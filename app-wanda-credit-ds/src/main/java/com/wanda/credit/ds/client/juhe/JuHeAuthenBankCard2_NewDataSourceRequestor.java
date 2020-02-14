package com.wanda.credit.ds.client.juhe;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.ExceptionUtil;
import com.wanda.credit.base.util.RequestHelper;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.iface.IDataSourceRequestor;
@DataSourceClass(bindingDataSourceId="ds_juhe_AuthenBankCard2_new")
public class JuHeAuthenBankCard2_NewDataSourceRequestor extends BaseJuheDSRequestor
implements IDataSourceRequestor {
	private final  Logger logger = LoggerFactory.getLogger(JuHeAuthenBankCard2_NewDataSourceRequestor.class);
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
			String url = propertyEngine.readById("ds_juhe_verifybankcard2_url");
			logObj.setDs_id(ds.getId());
			logObj.setReq_url(url);
			logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
	 		String name = ParamUtil.findValue(ds.getParams_in(), "name").toString();   //姓名 
			String cardId = ParamUtil.findValue(ds.getParams_in(), "cardId").toString(); //银行卡号
 
            reqparam.put("key", propertyEngine.readById("ds_juhe_verifybankcard2_key"));
			reqparam.put("name", name);
			reqparam.put("cardId", cardId);
			reqparam.put("isshow", 1);
			reqparam.put("trade_id", trade_id);			
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
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "数据源查询异常!");
				return rets;
			}
			if (rspData.getJSONObject("result")==null) {
				logger.info("{} http请求返回结果异常" , prefix);
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_TIMEOUT);
				logObj.setState_msg("请求超时");
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "数据源查询异常!");
				return rets;
			}
			JSONObject result = rspData.getJSONObject("result");
			logObj.setBiz_code2(result.getString("jobid"));
			logObj.setBiz_code1(rspData.getString("error_code") + "-" + rspData.getString("reason"));
			
			if(isSuccess(rspData)){
				resource_tag = buildTag(trade_id, result);
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
				retdata.putAll(visit2BusiData(trade_id, result));
				logger.info("处理成功" , prefix);
			}else if("218804".equals(rspData.getString("error_code"))){
				logger.info("返回不支持" , prefix);
				resource_tag = Conts.TAG_SYS_ERROR;
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_CARDID_ERROR);
				rets.put(Conts.KEY_RET_MSG, "银行卡卡号格式错误");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				return rets;
			}else if("218805".equals(rspData.getString("error_code"))){
				logger.info("返回不支持" , prefix);
				resource_tag = Conts.TAG_SYS_ERROR;
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_JIAO_NAME_ERROR);
				rets.put(Conts.KEY_RET_MSG, "姓名格式错误");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				return rets;
			}else{
				resource_tag = Conts.TAG_SYS_ERROR;
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_ZT_BANKCARD_AUTHEN_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "银行卡鉴权失败:"+rspData.getString("reason"));
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				logger.warn("{}数据源厂商返回异常!",trade_id);
				return rets;
			}			
			retdata.put("name", name);
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
	protected Map<? extends String, ? extends Object> visit2BusiData(
			String trade_id, com.alibaba.fastjson.JSONObject data) {
		Map<String,Object> ret = new HashMap<String,Object>();
		Object resObj = data.get("res");
		if(resObj == null){
			logger.error("{} res 字段值非法  ,",trade_id,data.toString());
			return ret;
		}
		String res = resObj.toString();
		String respCode = res; 
		String resMsg = "";
		if("1".equals(res)){
			respCode = "2000" ;
			resMsg = "认证一致" ;
		}else if("2".equals(res)){
			respCode = "2001";
			resMsg = "认证不一致";
		}
		ret.put("respCode", respCode);
		ret.put("respDesc", resMsg);
		return ret;
	}
}
