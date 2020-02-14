package com.wanda.credit.ds.client.xiaohe;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.CardNoValidator;
import com.wanda.credit.base.util.ExceptionUtil;
import com.wanda.credit.base.util.IPUtils;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.common.util.GladDESUtils;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.client.xiaohe.utils.MD5Util;
import com.wanda.credit.ds.client.xiaohe.utils.XmlTool;
import com.wanda.credit.ds.client.xiaohe.utils.httpUtils;
import com.wanda.credit.ds.client.zhengtong.BaseZTDataSourceRequestor;
import com.wanda.credit.ds.dao.domain.Nciic_Check_Result;
import com.wanda.credit.ds.dao.iface.INciicCheckService;
import com.wanda.credit.ds.iface.IDataSourceRequestor;

@DataSourceClass(bindingDataSourceId="ds_xiaohe_policeNoid")
public class XiaoHePoliceNoidRequestor extends BasePSGDataSourceRequestor implements IDataSourceRequestor {
	private Logger logger = LoggerFactory.getLogger(XiaoHePoliceNoidRequestor.class);
	protected String CODE_EQUAL = "gajx_001";
	protected String CODE_NOEQUAL = "gajx_002";
	protected String CODE_NOEXIST = "gajx_003";
	
	private final  String STATUS_CHECK_NULL = "02";
	private final  String SOURCE_ID = "15";//聚合 
	@Autowired
	private IPropertyEngine propertyEngine;
	
	@Autowired
	private INciicCheckService nciicCheckService;
	@Override
	public Map<String, Object> request(String trade_id, DataSource ds) {
		String police_url = propertyEngine.readById("ds_xiaohe_police_url").trim();
		String police_api = propertyEngine.readById("ds_xiaohe_police_api");
		String police_api_key = propertyEngine.readById("ds_xiaohe_police_api_keynoid");
		String police_hashcode = propertyEngine.readById("ds_xiaohe_police_hashcodenoid");
		int incache_days = Integer.valueOf(propertyEngine.readById("ds_police_incacheTime"));//公安数据缓存时间(天)
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		long start = System.currentTimeMillis();
		//初始化对象
		Map<String, Object> rets = new HashMap<String, Object>();
		TreeMap<String, Object> retdata = new TreeMap<String, Object>();	
		Map<String, Object> reqparam = new HashMap<String, Object>();
		//计费标签
		String resource_tag = Conts.TAG_SYS_ERROR;
		//交易日志信息数据
		DataSourceLogVO logObj = new DataSourceLogVO();
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));
		logObj.setDs_id(ds.getId());
		logObj.setReq_url(police_url);
		logObj.setBiz_code3(IPUtils.getLocalIP());
		logObj.setIncache("0");
		logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
		logObj.setState_msg("交易失败");
		
		try{
			logger.info("{} 小河开始解析传入的参数" , prefix);
			String name = ParamUtil.findValue(ds.getParams_in(), paramIds[0]).toString();
			String cardNo = ParamUtil.findValue(ds.getParams_in(), paramIds[1]).toString().toUpperCase();
			String acct_id = ParamUtil.findValue(ds.getParams_in(), paramIds[2]).toString();//账户号

			logger.info("{} 小河解析传入的参数成功" , prefix);
			reqparam.put("name", name);
			reqparam.put("cardNo", cardNo);
			//加密敏感信息
			String encardNo = GladDESUtils.encrypt(cardNo);
			
			//参数校验 - 身份证号码
			String validate = CardNoValidator.validate(cardNo);
			if (!StringUtil.isEmpty(validate)) {
				logger.info("{} 身份证格式校验错误： {}" , prefix , validate);
				logObj.setState_msg("身份证格式校验错误");
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_IDCARD_ERROR);
				rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_IDCARD_ERROR.getRet_msg());
				return rets;
			}	
			if(!BaseZTDataSourceRequestor.isChineseWord(name)){				
				logger.warn("{} 姓名入参格式不符合要求:{}", prefix,name);
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_PARAM_INVALID);
				rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_FAILED_SYS_PARAM_INVALID.getRet_msg());
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				return rets;
			}
			String cardNo_check = "一致";
			String name_check = "不一致";
			boolean ds_incache = dsIncache(acct_id,ds.getId());
			if(!nciicCheckService.inCachedCountJuHe(name, encardNo,incache_days) || ds_incache){
				logger.info("{} 小河直连请求开始..." , prefix);
				Date now = new Date(); 
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");//可以方便地修改日期格式
				String sdate = dateFormat.format(now); 

				Map<String, String> paramMap = new HashMap<String, String>();
				paramMap.put("Hashcode", police_hashcode);
				paramMap.put("passName", name);
				paramMap.put("pid", cardNo);

				String sign=paramMap.get("Hashcode")+paramMap.get("passName")+
						paramMap.get("pid")+police_api_key+sdate;
				sign=MD5Util.MD5(sign);
				paramMap.put("sign",sign);//姓名
				Nciic_Check_Result nciic_check = new Nciic_Check_Result();
				nciic_check.setTrade_id(trade_id);
				nciic_check.setCardno(encardNo);
				nciic_check.setName(name);
				nciic_check.setSourceid(SOURCE_ID);
				nciic_check.setStatus(STATUS_CHECK_NULL);
				//开始请求，获取的是XML格式
				String httpresult=httpUtils.httpsGet(trade_id,"https", police_url, "443",police_api, paramMap);
				if(StringUtils.isEmpty(httpresult)){
					rets.clear();
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_POLICE_EXCEPTION);
					rets.put(Conts.KEY_RET_MSG, "数据源调用失败");
					rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
					logger.warn("{} 公安数据源厂商返回异常! ",prefix);
					return rets;
				}
				//转成json对象
				JSONObject json=XmlTool.documentToJSONObject(httpresult);
				JSONObject police = (JSONObject) json.getJSONArray("ErrorRes").get(0);
				String code = police.getString("Err_code");
				if("200".equals(code)){
					logObj.setState_msg("交易成功");
					resource_tag = Conts.TAG_MATCH;
					name_check = "一致";	
					nciic_check.setStatus("04");
					retdata.put("resultGmsfhm", cardNo_check);
					retdata.put("resultXm", name_check);
					retdata.put("xp_content", "");
					rets.clear();
					rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
					rets.put(Conts.KEY_RET_DATA, retdata);
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
					rets.put(Conts.KEY_RET_MSG, "采集成功!");
				}else if("401".equals(code)){
					nciic_check.setStatus("05");
					logObj.setBiz_code1(CODE_NOEQUAL);
					resource_tag = Conts.TAG_UNMATCH;
					rets.clear();
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_POLICE_INVALID);
					rets.put(Conts.KEY_RET_MSG, "公安数据源厂商返回身份证号码，姓名校验不一致!");
					rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				}else if("404".equals(code)){
					nciic_check.setStatus("06");
					logObj.setBiz_code1(CODE_NOEXIST);
					resource_tag = Conts.TAG_UNMATCH;
					rets.clear();
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_POLICE_NOTEXISTS);
					rets.put(Conts.KEY_RET_MSG, "申请人身份证号码校验不存在!"); 
					rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				}else if("405".equals(code)){
					nciic_check.setStatus("06");
					resource_tag = Conts.TAG_SYS_ERROR;
					rets.clear();
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_PARAM_INVALID);
					rets.put(Conts.KEY_RET_MSG, "传入参数格式有误");
					rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				}else{
					rets.clear();
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_POLICE_EXCEPTION);
					rets.put(Conts.KEY_RET_MSG, "公安数据源厂商返回异常!");
					rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
					logger.warn("{}公安数据源厂商返回异常! ",prefix);
				}
				
				nciic_check.setCard_check(cardNo_check);
				nciic_check.setName_check(name_check);
				nciicCheckService.add(nciic_check);	
				return rets;
			}else{
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
				logObj.setIncache("1");
				logObj.setBiz_code1(CODE_EQUAL);
				resource_tag = Conts.TAG_INCACHE_MATCH;
				logger.info("{}缓存数据中存在此公安查询数据!", prefix);
				Map<String,Object> getResultMap = nciicCheckService.inCachedJuHe(name, encardNo);
				if(getResultMap.get("CARD_CHECK") != null){
					cardNo_check = getResultMap.get("CARD_CHECK").toString();
				}
				if(getResultMap.get("NAME_CHECK") != null){
					name_check  = getResultMap.get("NAME_CHECK").toString();
				}
				if("不一致".equals(cardNo_check) || 
						"不一致".equals(name_check)){
					logObj.setBiz_code1(CODE_NOEQUAL);
					logObj.setState_msg("交易成功");
					resource_tag = Conts.TAG_INCACHE_UNMATCH;
					rets.clear();
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_POLICE_INVALID);
					rets.put(Conts.KEY_RET_MSG, "公安数据源厂商返回身份证号码，姓名校验不一致!");
					rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
					return rets;
				}
			}
			logObj.setState_msg("交易成功");
			retdata.put("resultGmsfhm", cardNo_check);
			retdata.put("resultXm", name_check);
			retdata.put("xp_content", "");
			rets.clear();
			rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
			rets.put(Conts.KEY_RET_DATA, retdata);
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
			rets.put(Conts.KEY_RET_MSG, "采集成功!");
		}catch(Exception e){
			resource_tag = Conts.TAG_SYS_ERROR;
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, "数据源处理时异常!");
			logger.error("{} 数据源处理时异常：{}",prefix,ExceptionUtil.getTrace(e));
			if (ExceptionUtil.isTimeoutException(e)) {
				resource_tag = Conts.TAG_SYS_TIMEOUT;
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_TIMEOUT);
			} else {
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
				logObj.setState_msg("数据源处理时异常! 详细信息:" + e.getMessage());
			}
			rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
		}finally{
			//保存日志信息
			logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
			logObj.setTag(resource_tag);
			logger.info("{} 保存ds Log开始..." ,prefix);
			executorDtoService.writeDsLog(trade_id,logObj,false);
			executorDtoService.writeDsParamIn(trade_id, reqparam, logObj,false);
//			DataSourceLogEngineUtil.writeLog(trade_id,logObj);
//			DataSourceLogEngineUtil.writeParamIn(trade_id, reqparam, logObj);
			logger.info("{} 保存ds Log成功" ,prefix);
		}
		logger.info("{} 身份验证End，交易时间为(ms):{}",prefix ,(System.currentTimeMillis() - start));
		return rets;
	}
}
