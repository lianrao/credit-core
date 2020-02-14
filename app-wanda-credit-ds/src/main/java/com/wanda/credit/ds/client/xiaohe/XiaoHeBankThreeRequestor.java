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
import com.wanda.credit.ds.dao.iface.IAllAuthCardService;
import com.wanda.credit.ds.iface.IDataSourceRequestor;

@DataSourceClass(bindingDataSourceId="ds_xiaohe_bankCard3")
public class XiaoHeBankThreeRequestor extends BasePSGDataSourceRequestor implements IDataSourceRequestor {
	private Logger logger = LoggerFactory.getLogger(XiaoHeBankThreeRequestor.class);
	protected String CODE_EQUAL = "gajx_001";
	protected String CODE_NOEQUAL = "gajx_002";
	protected String CODE_NOEXIST = "gajx_003";
	
	@Autowired
	private IPropertyEngine propertyEngine;
	@Autowired
    protected IAllAuthCardService allAuthCardService;
	@Override
	public Map<String, Object> request(String trade_id, DataSource ds) {
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		logger.info("{} 小河-银行卡三要素调用开始..." , prefix);
		long start = System.currentTimeMillis();
		//初始化对象
		Map<String, Object> rets = new HashMap<String, Object>();
		TreeMap<String, Object> retdata = new TreeMap<String, Object>();	
		Map<String, Object> reqparam = new HashMap<String, Object>();
		//计费标签
		String resource_tag = Conts.TAG_SYS_ERROR;
		String police_url = propertyEngine.readById("ds_xiaohe_police_url").trim();
		String police_api = propertyEngine.readById("ds_xiaohe_bank3_api");
		String police_api_key = propertyEngine.readById("ds_xiaohe_police_api_key");
		String police_hashcode = propertyEngine.readById("ds_xiaohe_police_hashcode");
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
			logger.info("{} 开始解析传入的参数" , prefix);
			String name = ParamUtil.findValue(ds.getParams_in(), paramIds[0]).toString();
			String cardNo = ParamUtil.findValue(ds.getParams_in(), paramIds[1]).toString().toUpperCase();
			String cardId = ParamUtil.findValue(ds.getParams_in(), paramIds[2]).toString();//账户号

			logger.info("{} 解析传入的参数成功" , prefix);
			reqparam.put("name", name);
			reqparam.put("cardNo", cardNo);
			reqparam.put("cardId", cardId);

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
			Date now = new Date(); 
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");//可以方便地修改日期格式
			String sdate = dateFormat.format(now);
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("Hashcode", police_hashcode);
			paramMap.put("idname", name);
			paramMap.put("idcard", cardNo);
			paramMap.put("bankcard", cardId);

			String sign = paramMap.get("bankcard")+paramMap.get("Hashcode")+paramMap.get("idcard")
			+paramMap.get("idname")+police_api_key+sdate;
			sign=MD5Util.MD5(sign);
			paramMap.put("sign",sign);//姓名
			//开始请求，获取的是XML格式
			String httpresult=httpUtils.httpsGet(trade_id,"https", police_url, "443",police_api, paramMap);
			if(StringUtils.isEmpty(httpresult)){
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "数据源查询异常");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				logger.warn("{}数据源厂商返回异常! ",prefix);
				return rets;
			}
			//转成json对象
			JSONObject json=XmlTool.documentToJSONObject(httpresult);
			JSONObject police = (JSONObject) json.getJSONArray("ErrorRes").get(0);
			JSONObject result = (JSONObject) json.getJSONArray("result").get(0);
			String code = police.getString("Err_code");
			String result_code = result.getString("code");
			logObj.setState_msg("交易成功");
			logObj.setBiz_code1(code);
			logger.info("{} 小河-银行卡三要素出参包装开始..." , prefix);
			if("200".equals(code)){
				resource_tag = Conts.TAG_TST_SUCCESS;

				retdata.put("respCode", "2000");
				retdata.put("respDesc", "认证一致");
				retdata.put("detailRespCode", "");
				retdata.put("respDetail", "");
			}else if("404".equals(code)){
				retdata.put("respCode", "2001");
				retdata.put("respDesc", "认证不一致");
				retdata.put("detailRespCode", "");
				retdata.put("respDetail", "");
				resource_tag = Conts.TAG_TST_SUCCESS;
			}else if("405".equals(code)){
				retdata.put("respCode", "2003");
				retdata.put("respDesc", "不支持验证");
				retdata.put("detailRespCode", "");
				retdata.put("respDetail", "");
				resource_tag = Conts.TAG_TST_FAIL;
			}else{
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_ZT_BANKCARD_AUTHEN_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "银行卡鉴权失败");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				logger.warn("{}公安数据源厂商返回异常! ",prefix);
				return rets;
			}
			retdata.put("name", name);
            retdata.put("cardNo", cardNo);
            retdata.put("cardId", cardId);
            retdata.put("detailRespCode", "");
            
			rets.clear();
			rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
			rets.put(Conts.KEY_RET_DATA, retdata);
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
			rets.put(Conts.KEY_RET_MSG, "采集成功!");
			
			String req_values = cardNo+"_"+cardId;
			allAuthCardService.saveAuthCard(ds.getId(), trade_id, name, GladDESUtils.encrypt(cardNo), GladDESUtils.encrypt(cardId), "",
					retdata, req_values);
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
			logger.info("{} 保存ds Log成功" ,prefix);
		}
		logger.info("{} 银行卡三要素完成，交易时间为(ms):{}",prefix ,(System.currentTimeMillis() - start));
		return rets;
	}
}
