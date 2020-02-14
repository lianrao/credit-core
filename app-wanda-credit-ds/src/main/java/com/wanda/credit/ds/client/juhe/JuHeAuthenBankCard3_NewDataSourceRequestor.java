package com.wanda.credit.ds.client.juhe;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.CardNoValidator;
import com.wanda.credit.base.util.ExceptionUtil;
import com.wanda.credit.base.util.RequestHelper;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.client.dsconfig.commonfunc.CryptUtil;
import com.wanda.credit.ds.iface.IDataSourceRequestor;
@DataSourceClass(bindingDataSourceId="ds_juhe_AuthenBankCard3_new")
public class JuHeAuthenBankCard3_NewDataSourceRequestor extends BaseJuheDSRequestor
implements IDataSourceRequestor {
	private final  Logger logger = LoggerFactory.getLogger(JuHeAuthenBankCard3_NewDataSourceRequestor.class);
	@Autowired
	private IPropertyEngine propertyEngine;
	@Override
	public Map<String, Object> request(String trade_id, DataSource ds){
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		TreeMap<String, Object> retdata = new TreeMap<String, Object>();
		Map<String, Object> rets = new HashMap<String, Object>();
		DataSourceLogVO logObj = new DataSourceLogVO();
		Map<String, Object> reqparam = new HashMap<String, Object>();
		String resource_tag = Conts.TAG_SYS_ERROR;
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));
		try{
			String url = propertyEngine.readById("ds_juhe_verifybankcard3_url");
			logObj.setDs_id(ds.getId());
			logObj.setReq_url(url);
			logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
	 		String name = ParamUtil.findValue(ds.getParams_in(), "name").toString();   //姓名 
			String cardNo = ParamUtil.findValue(ds.getParams_in(), "cardNo").toString(); //身份证号码
			String cardId = ParamUtil.findValue(ds.getParams_in(), "cardId").toString(); //银行卡号
 
            reqparam.put("key", propertyEngine.readById("ds_juhe_verifybankcard3_key"));
			reqparam.put("name", name);
			reqparam.put("cardNo", cardNo);
			reqparam.put("cardId", cardId);
			reqparam.put("isshow", 1);
			reqparam.put("trade_id", trade_id);
			if(StringUtils.isNotEmpty(CardNoValidator.validate(cardNo))){
				logObj.setIncache("1");
				logObj.setState_msg("身份证号码不符合规范");
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_IDCARD_ERROR);
				rets.put(Conts.KEY_RET_MSG, "您输入的为无效身份证号码，请核对后重新输入!");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				return rets;
			}			
			logObj.setIncache("0");
			
			Map<String,Object> rspDataMap = 
					RequestHelper.doGetRetFull(url, mapObjToMapStr(reqparam), 
							new HashMap<String, String>(), true, null, "UTF-8");
			logger.info("{} 返回数据 {}",trade_id, rspDataMap.get("res_body_str"));
			
			JSONObject rspData = JSONObject.parseObject(String.valueOf(rspDataMap.get("res_body_str")));
			
			if (StringUtil.isEmpty(rspData) || "null".equals(rspData)) {
				logger.info("{} http请求返回结果为空" , prefix);
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_TIMEOUT);
				logObj.setState_msg("请求超时");					
				return rets;
			}
			
			JSONObject result = rspData.getJSONObject("result");
			logObj.setBiz_code2(result.getString("jobid"));
			logObj.setBiz_code1(rspData.getString("error_code") + "-" + rspData.getString("reason"));
			
			if(isSuccess(rspData)){
				long saveStart = System.currentTimeMillis();

                String req_values = cardNo+"_"+cardId;
    			String isEnc = propertyEngine.readById("encrypt_switch_142");
    			String encCardNo = cardNo;
    			String encCardId = cardId;
    			if ("1".equals(isEnc)) {
    				encCardNo = CryptUtil.encrypt(cardNo);
    				encCardId = CryptUtil.encrypt(cardId);
    			}
				allAuthCardService.savaJuHeAuthCard(ds.getId(), trade_id, name,
						encCardNo, encCardId,null, result, req_values);
				logger.info("{} 保存请求结果到数据库耗时为 {}" , prefix ,System.currentTimeMillis() - saveStart);
				resource_tag = buildTag(trade_id, result);
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
				retdata.putAll(visitBusiData(trade_id, result));
				retdata.put("detailRespCode", "");
				retdata.put("respDetail", "");
				logger.info("处理成功" , prefix);
			}else if(isSupport(rspData)){
				logger.info("返回不支持" , prefix);
				resource_tag = Conts.TAG_TST_FAIL;
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
				retdata.put("respCode", "2003");
				retdata.put("respDesc", "不支持验证");
				retdata.put("detailRespCode", "");
				retdata.put("respDetail", "");
			}else if("221306".equals(rspData.getString("error_code"))){
				logger.info("返回不支持" , prefix);
				resource_tag = Conts.TAG_TST_FAIL;
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
				rets.put(Conts.KEY_RET_MSG, "银行卡鉴权失败:请求频繁,验证次数超限");
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_ZT_BANKCARD_AUTHEN_EXCEPTION);
				rets.put(Conts.KEY_RET_TAG, new String[] { resource_tag });
				logger.error("{} 验证次数超限", trade_id);
				return rets;
			}else if("221303".equals(rspData.getString("error_code"))){
				logger.info("返回不支持" , prefix);
				resource_tag = Conts.TAG_TST_FAIL;
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_INVALID_CARD);
				rets.put(Conts.KEY_RET_MSG, "您输入的银行卡号无效，请核对后重新输入!");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				return rets;
			}else if("220704".equals(rspData.getString("error_code"))){
				logger.info("返回不支持" , prefix);
				resource_tag = Conts.TAG_TST_FAIL;
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_PARAM_INVALID);
				rets.put(Conts.KEY_RET_MSG, "传入参数格式有误:卡号错误");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				return rets;
			}else{
				resource_tag = Conts.TAG_SYS_ERROR;
				if(rspData.getString("reason").contains("CardNumber(银行卡号)")){
					rets.clear();
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_PARAM_INVALID);
					rets.put(Conts.KEY_RET_MSG, "传入参数格式有误:卡号错误");
					rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
					logger.warn("{} 数据源厂商卡号错误",trade_id);
				}else{
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_ZT_BANKCARD_AUTHEN_EXCEPTION);
					rets.put(Conts.KEY_RET_MSG, "银行卡鉴权失败:"+rspData.getString("reason"));
					rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
					logger.warn("{}数据源厂商返回异常!",trade_id);
				}
				return rets;
			}			
			retdata.put("name", name);
            retdata.put("cardNo", cardNo);
            retdata.put("cardId", cardId);
            
            
			rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
			rets.put(Conts.KEY_RET_DATA, retdata);
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
			rets.put(Conts.KEY_RET_MSG, "采集成功!");			
		}catch(Exception ex){
			resource_tag = Conts.TAG_SYS_ERROR;
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, "数据源查询异常!");
			logger.error(prefix+" 数据源处理时异常：{}",ex);
			if (ExceptionUtil.isTimeoutException(ex)) {
				resource_tag = Conts.TAG_SYS_TIMEOUT;
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_TIMEOUT);
			} else {
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
				logObj.setState_msg("数据源处理时异常! 详细信息:" + ex.getMessage());
			}
			rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
		}finally {
			logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
			logObj.setTag(resource_tag);
			DataSourceLogEngineUtil.writeLog(trade_id, logObj);
			DataSourceLogEngineUtil.writeParamIn(trade_id, reqparam, logObj);
		}
		return rets;
	}
}
