package com.wanda.credit.ds.client.wangshu;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.wanda.credit.base.Conts;
import com.wanda.credit.ds.dao.iface.IAllAuthCardService;
import com.wanda.credit.ds.dao.iface.IJuheAuthCardService;
import com.wanda.credit.ds.iface.IDataSourceRequestor;

abstract public class BaseJuHeAuthenBankCardDataSourceRequestor extends BaseWDWangShuDataSourceRequestor
implements IDataSourceRequestor {
	private final  Logger logger = LoggerFactory.getLogger(BaseJuHeAuthenBankCardDataSourceRequestor.class);
	
	@Autowired
	protected  WDWangShuTokenService tokenService;

	@Autowired
	protected  IJuheAuthCardService juheAuthCardService;	
	
    @Autowired
    protected IAllAuthCardService allAuthCardService;
	
	protected Map<? extends String, ? extends Object> visitBusiData(
			String trade_id, JSONObject data) {
		Map<String,Object> ret = new HashMap<String,Object>();
		Object resObj = data.get("res");
		if(resObj == null){
			logger.error("{} res 字段值非法  ,",trade_id,data.toJSONString());
			return ret;
		}
		String message = data.getString("message");
		String res = resObj.toString();
		String respCode = res; String resMsg = message;
		if("1".equals(res)){
			respCode = "00" ;
			resMsg = "验证匹配" ;
		}else if("2".equals(res)){
			respCode = "01";
			resMsg = "验证不匹配";
		}
		ret.put("respCode", respCode);
		ret.put("respDesc", resMsg);
		return ret;
	}
	
	protected String buildTag(String trade_id, JSONObject rspData) {
		Object res = rspData.get("res");
		String resstr = null;
		if(res != null ){
			resstr = res.toString();
			if("1".equals(resstr) || "2".equals(resstr)){
			    return Conts.TAG_TST_SUCCESS;
			}
		}
		return Conts.TAG_TST_FAIL;
	}
	 
	protected boolean isSuccess(JSONObject rspData) {
		boolean result = false;
		logger.info("返回code是 {}",rspData.getString("code"));
		if(rspData != null && "2001".equals(rspData.getString("code"))){
			logger.info("返回code是 {}",rspData.getString("code"));
			result = true;
		}
		return result;
	}
	
	protected boolean isSupport(JSONObject rspData) {
		if(rspData != null && "220702".equals(rspData.getString("code"))){
			return true;
		}
		return false;
	}
	
	protected boolean needRetry(int httpstatus, JSONObject rsponse) {		
		if(httpstatus == 401){
			return true;
		}
		String rspcode = rsponse.get("code") !=null ?
				rsponse.get("code").toString() : null;
        if("100007".equals(rspcode)){
           return true;
        }
		return false;
	}
	
	protected Map<String,Object> doRequest(String trade_id, String url,boolean forceRefresh) throws Exception {
		Map<String,Object> header = new HashMap<String,Object>();
		if(forceRefresh){
			logger.info("{} 强制刷新token",trade_id);
			tokenService.setToken(tokenService.getNewToken());
			logger.info("{} 强制刷新token结束",trade_id);
		}else if(tokenService.getToken() == null){
			logger.info("{} 发起token请求",trade_id);
			tokenService.setToken(tokenService.getNewToken());
			logger.info("{} 发起token请求结束",trade_id);

		}
		String token = tokenService.getToken();
		header.put("X-Access-Token",token);
/*		logger.info("{} tokenid {}",trade_id,token);
		logger.info("{} start request",trade_id);
*/		Map<String,Object> rspMap = doGetForHttpAndHttps(url,trade_id,header,null);
		logger.info("{} end request",trade_id);
		return rspMap;
	}

}