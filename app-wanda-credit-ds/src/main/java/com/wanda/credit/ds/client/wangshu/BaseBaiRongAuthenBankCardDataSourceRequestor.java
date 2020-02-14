package com.wanda.credit.ds.client.wangshu;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.wanda.credit.base.Conts;
import com.wanda.credit.common.template.PropertyEngine;
import com.wanda.credit.ds.dao.iface.IBairongAuthCardService;
import com.wanda.credit.ds.iface.IDataSourceRequestor;

abstract public class BaseBaiRongAuthenBankCardDataSourceRequestor extends BaseWDWangShuDataSourceRequestor
implements IDataSourceRequestor {
	private final  Logger logger = LoggerFactory.getLogger(BaseBaiRongAuthenBankCardDataSourceRequestor.class);
	
	@Autowired
	protected  WDWangShuTokenService tokenService;

	@Autowired
	protected  IBairongAuthCardService bairongAuthCardService;		
	
	protected Map<? extends String, ? extends Object> visitBusiData(
			String trade_id, JSONObject rspData) {
		Map<String,Object> ret = new HashMap<String,Object>();
		JSONObject product = rspData.getJSONObject("product");
		ret.put("respCode", product.get("respCode"));
		ret.put("respDesc", product.get("resMsg"));
		return ret;
	}
	
	protected String buildTag(String trade_id, JSONObject rspData) {
		JSONObject flag = rspData.getJSONObject("flag");
		if(flag != null && flag.get("flag_bankthree") != null && 
				"1".equals(flag.get("flag_bankthree").toString())){
			return Conts.TAG_TST_SUCCESS;
		}
		return Conts.TAG_TST_FAIL;
	}
	 
	protected boolean isSuccess(JSONObject rspData) {
		if("2001".equals(rspData.getString("code"))){
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
		String token = PropertyEngine.get("tmp_tokenid");
//		String token = tokenService.getToken();
		header.put("X-Access-Token",token);
		logger.info("{} tokenid {}",trade_id,token);
		logger.info("{} start request",trade_id);
		Map<String,Object> rspMap = doGetForHttpAndHttps(url,trade_id,header,null);
		logger.info("{} end request",trade_id);
		return rspMap;
	}

}