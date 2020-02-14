package com.wanda.credit.ds.client.maike;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import net.sf.json.JSONObject;

import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.ExceptionUtil;
import com.wanda.credit.base.util.RequestHelper;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.BaseDataSourceRequestor;
import com.wanda.credit.ds.iface.IDataSourceRequestor;

/**迈科短信发送（自定义短信/模板短信） add by wj 20180702*/
@DataSourceClass(bindingDataSourceId="ds_maike_sms")
public class MaikeZDYDataSourceRequestor extends BaseDataSourceRequestor
		implements IDataSourceRequestor {
	private final  Logger logger = LoggerFactory.getLogger(MaikeZDYDataSourceRequestor.class);
	@Autowired
	public IPropertyEngine propertyEngine;
	@Override
	public Map<String, Object> request(String trade_id, DataSource ds){
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		logger.info("{}迈科请求开始...",  prefix);
		TreeMap<String, Object> retdata = new TreeMap<String, Object>();
		Map<String, Object> rets = null;
		DataSourceLogVO logObj = new DataSourceLogVO();
		Map<String, String> reqparam = new HashMap<String, String>();
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));//log请求时间
		String url = propertyEngine.readById("ds_maike_url");//迈科调用连接
		String clientid = propertyEngine.readById("ds_maike_clientid");//迈科调用连接
		String password = propertyEngine.readById("ds_maike_password");//迈科调用账户
		String resource_tag = Conts.TAG_SYS_ERROR;
		try{
			String reqTyep = ParamUtil.findValue(ds.getParams_in(), paramIds[0]).toString();   
			String mobile = ParamUtil.findValue(ds.getParams_in(), paramIds[1]).toString();  
			String sendtime = ParamUtil.findValue(ds.getParams_in(), paramIds[2]).toString(); 
			String extend = ParamUtil.findValue(ds.getParams_in(), paramIds[3]).toString();  
//			String uid = ParamUtil.findValue(ds.getParams_in(), paramIds[4]).toString();  
			String smstype = ParamUtil.findValue(ds.getParams_in(), paramIds[4]).toString();  
			String content = ParamUtil.findValue(ds.getParams_in(), paramIds[5]).toString();  
			String templateid = ParamUtil.findValue(ds.getParams_in(), paramIds[6]).toString(); 
			String param = ParamUtil.findValue(ds.getParams_in(), paramIds[7]).toString(); 
			
			logObj.setDs_id(ds.getId());
			rets = new HashMap<String, Object>();
			reqparam.put("clientid", clientid);
			reqparam.put("password", password);
			reqparam.put("mobile", mobile);
			reqparam.put("extend", extend);
			reqparam.put("uid", trade_id);
			reqparam.put("sendtime", sendtime);
			
			String path = null;
			if("1".equals(reqTyep)) {//自定义短信发送
				reqparam.put("smstype", smstype);
				reqparam.put("content", content);
				path = propertyEngine.readById("ds_maike_pathZDY");
			} else {                 //模板短信发送
				reqparam.put("templateid", templateid);
				reqparam.put("param", param);
				path = propertyEngine.readById("ds_maike_pathTEMPLATE");
			}
			url = url + clientid + path;

			logObj.setReq_url(url);
			logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
			Map<String, String> headers = new HashMap<String, String>();
			headers.put("Accept", "application/json;");
			headers.put("Content-Type", "application/json;charset=utf-8;");
//			Headers: { "Accept":"application/json;", "Content-Type":"application/json;charset=utf-8;"}
			String isPrint = propertyEngine.readById("ds_maike_isPrint");
			String timeout = propertyEngine.readById("ds_maike_timeout");
			logger.info("{} 迈科开始请求", trade_id);
//			String res = RequestHelper.doPost(url, reqparam, true);
			String res = RequestHelper.doPost(url,null, headers, reqparam, 
					ContentType.APPLICATION_JSON,"1".equals(isPrint), Integer.valueOf(timeout));
			logger.info("{} 迈科结束请求", trade_id);
	        JSONObject json = null;
	        try {
	        	json = JSONObject.fromObject(res);
	        	retdata.put("result", json);
	        	resource_tag = Conts.TAG_TOTAL_FEE + json.getString("total_fee");
			} catch (Exception e) {
				retdata.put("result", res);
			}
	        
	        rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
			rets.put(Conts.KEY_RET_DATA, retdata);
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
			rets.put(Conts.KEY_RET_MSG, "采集成功!");
		}catch(Exception ex){
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, "数据源处理时异常! 详细信息:"+ex.getMessage());
			logger.error("{} 数据源处理时异常：{}",prefix,ExceptionUtil.getTrace(ex));
			if (ExceptionUtil.isTimeoutException(ex)) {
				resource_tag = Conts.TAG_SYS_TIMEOUT;
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_TIMEOUT);
			} else {
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
				logObj.setState_msg("数据源处理时异常! 详细信息:" + ex.getMessage());
			}
			rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
		}finally {
			logObj.setIncache("0");
			logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
			logObj.setTag(resource_tag);
			DataSourceLogEngineUtil.writeLog(trade_id, logObj);
			DataSourceLogEngineUtil.writeParamIn(trade_id, JSONObject.fromObject(reqparam), logObj);
		}
		return rets;
	}

}
