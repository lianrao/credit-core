package com.wanda.credit.ds.client.xinhua;

import java.sql.Timestamp;
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
import com.wanda.credit.ds.client.zhengtong.BaseZTDataSourceRequestor;
import com.wanda.credit.ds.dao.domain.Nciic_Check_Result;
import com.wanda.credit.ds.dao.iface.INciicCheckService;
import com.wanda.credit.ds.iface.IDataSourceRequestor;

@DataSourceClass(bindingDataSourceId="ds_xinhua_police")
public class XinHuaPoliceRequestor extends BaseXHuaSourceRequestor implements IDataSourceRequestor {
	private Logger logger = LoggerFactory.getLogger(XinHuaPoliceRequestor.class);

	
	private final  String STATUS_CHECK_NULL = "02";
	private final  String SOURCE_ID = "15";//聚合 
	@Autowired
	private IPropertyEngine propertyEngine;
	
	@Autowired
	private INciicCheckService nciicCheckService;
	@Override
	public Map<String, Object> request(String trade_id, DataSource ds) {
		String xinhua_key = propertyEngine.readById("ds_xinhua_accountkey");
		String xinhua_username = propertyEngine.readById("ds_xinhua_username");
		String request_url = propertyEngine.readById("ds_xinhua_facePhoto_url");
		String face_prod = propertyEngine.readById("ds_xinhua_card_prod");
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
		DataSourceLogVO logObj = new DataSourceLogVO(trade_id);
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));
		logObj.setDs_id(ds.getId());
		logObj.setReq_url(request_url);
		logObj.setBiz_code3(IPUtils.getLocalIP());
		logObj.setIncache("0");
		logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
		logObj.setState_msg("交易成功");
		
		try{
			logger.info("{} 新华开始解析传入的参数" , prefix);
			String name = ParamUtil.findValue(ds.getParams_in(), paramIds[0]).toString();
			String cardNo = ParamUtil.findValue(ds.getParams_in(), paramIds[1]).toString().toUpperCase();
			String acct_id = ParamUtil.findValue(ds.getParams_in(), paramIds[2]).toString();//账户号

			logger.info("{} 新华解析传入的参数成功" , prefix);
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
				logger.info("{} 新华直连请求开始..." , prefix);
				
				Nciic_Check_Result nciic_check = new Nciic_Check_Result();
				nciic_check.setTrade_id(trade_id);
				nciic_check.setCardno(encardNo);
				nciic_check.setName(name);
				nciic_check.setSourceid(SOURCE_ID);
				nciic_check.setStatus(STATUS_CHECK_NULL);
				
				Map<String, String> params = new TreeMap<String, String>();
				params.put("name", name);// ds入参 必填
				params.put("identityCard",cardNo);
				//开始请求，获取的是XML格式
				logger.info("{} 新华直连http请求开始..." , prefix);
				String res = Service(trade_id,xinhua_key,face_prod,request_url,xinhua_username,params);
				logger.info("{} 新华直连http请求完成:{}" , prefix,res);
				if(StringUtils.isEmpty(res)){
					rets.clear();
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_POLICE_EXCEPTION);
					rets.put(Conts.KEY_RET_MSG, "数据源调用失败");
					rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
					logger.warn("{} 公安数据源厂商返回异常! ",prefix);
					return rets;
				}
				//转成json对象
				JSONObject result_obj = JSONObject.parseObject(res);
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
				logObj.setBiz_code1(result_obj.getString("serialno"));
				if("0".equals(result_obj.getString("code"))){				
					JSONObject data = result_obj.getJSONObject("data");
					String compStatus = data.getString("compStatus");
					if("1".equals(compStatus)){
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
					}else if("2".equals(compStatus)){
						nciic_check.setStatus("05");

						resource_tag = Conts.TAG_UNMATCH;
						rets.clear();
						rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_POLICE_INVALID);
						rets.put(Conts.KEY_RET_MSG, "公安数据源厂商返回身份证号码，姓名校验不一致!");
						rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
					}else if("3".equals(compStatus)){
						nciic_check.setStatus("06");

						resource_tag = Conts.TAG_NOMATCH;
						rets.clear();
						rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_POLICE_NOTEXISTS);
						rets.put(Conts.KEY_RET_MSG, "申请人身份证号码校验不存在!"); 
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
					logger.info("{} 新华直连插入police表完成" , prefix);
				}else if("1002".equals(result_obj.getString("code"))){
					rets.clear();
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_PARAM_INVALID);
					rets.put(Conts.KEY_RET_MSG, "校验不通过:同一身份证请求过于频繁!");
					rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
					logger.warn("{}公安数据源厂商返回失败:{}",prefix,result_obj.getString("msg"));
				}else{
					rets.clear();
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_POLICE_EXCEPTION);
					rets.put(Conts.KEY_RET_MSG, "公安数据源厂商返回异常!");
					rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
					logger.warn("{}公安数据源厂商返回失败:{}",prefix,result_obj.getString("msg"));
				}
				
				nciic_check.setCard_check(cardNo_check);
				nciic_check.setName_check(name_check);
				nciicCheckService.add(nciic_check);	
				return rets;
			}else{
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
				logObj.setIncache("1");
	
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
			executorDtoService.writeDsLog(trade_id,logObj,true);
			executorDtoService.writeDsParamIn(trade_id, reqparam, logObj,true);
//			DataSourceLogEngineUtil.writeLog(trade_id,logObj);
//			DataSourceLogEngineUtil.writeParamIn(trade_id, reqparam, logObj);
			logger.info("{} 保存ds Log成功" ,prefix);
		}
		logger.info("{} 身份验证End，交易时间为(ms):{}",prefix ,(System.currentTimeMillis() - start));
		return rets;
	}
}
