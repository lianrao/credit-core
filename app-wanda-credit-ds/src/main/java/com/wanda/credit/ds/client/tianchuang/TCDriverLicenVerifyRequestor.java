package com.wanda.credit.ds.client.tianchuang;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.ExceptionUtil;
import com.wanda.credit.base.util.JsonFilter;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.iface.IDataSourceRequestor;

/**
 * 天创-验证驾驶证信息接口
 * */
@DataSourceClass(bindingDataSourceId="ds_tianchuang_driverVerify")
public class TCDriverLicenVerifyRequestor extends BaseTianChSourceRequestor
		implements IDataSourceRequestor {
	private final  Logger logger = LoggerFactory.getLogger(TCDriverLicenVerifyRequestor.class);
	private static final String BIZ_CODE_1_SUCCESS = "1";//查询成功，有记录
	private static final String BIZ_CODE_1_SUCCESS_UNMATCH = "2";//查询成功，不一致	
	private static final String BIZ_CODE_1_SUCCESS_NO = "3";//查询成功，库无
	private static Map<String, String> checkResultMap = new HashMap<String, String>();
	private static Map<String, String> jsonResultMap = new HashMap<String, String>();
	private static Map<String, String> jsonValueMap = new HashMap<String, String>();
	@Autowired
	public IPropertyEngine propertyEngine;
	@Override
	public Map<String, Object> request(String trade_id, DataSource ds){
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		logger.info("{}天创信用数据源请求开始...", prefix);
		String url = propertyEngine.readById("ds_tcdriver_verify_url");
		Map<String, Object> rets = null;
		Map<String, Object> retdata = new HashMap<String, Object>();
		DataSourceLogVO logObj = new DataSourceLogVO();
		Map<String, Object> reqparam = new HashMap<String, Object>();
		Map<String, String> params = new HashMap<String, String>();
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));//log请求时间
		String resource_tag = Conts.TAG_SYS_ERROR;
		try{	
			String name = ParamUtil.findValue(ds.getParams_in(), paramIds[0]).toString(); 
			String licenseNo = ParamUtil.findValue(ds.getParams_in(), paramIds[1]).toString();//驾驶证号
			String fileNo = "";//档案编号(暂不支持验证)
			String classType = "";//准驾车型
			String issueDate = "";//签发日期
			String startDate = "";//起始有效日期
			String expiryDate = "";//结束有效日期
			if(ParamUtil.findValue(ds.getParams_in(), "fileNo")!=null){
				fileNo = ParamUtil.findValue(ds.getParams_in(), "fileNo").toString();
				if(!StringUtils.isEmpty(fileNo)){
					reqparam.put("fileNo", fileNo);
					params.put("fileNo", fileNo);
				}				
			}
			if(ParamUtil.findValue(ds.getParams_in(), "carModels")!=null){
				classType = ParamUtil.findValue(ds.getParams_in(), "carModels").toString();
				if(!StringUtils.isEmpty(classType)){
					reqparam.put("carModels", classType);
					params.put("class", classType);
				}
				
			}
			if(ParamUtil.findValue(ds.getParams_in(), "issueDate")!=null){
				issueDate = ParamUtil.findValue(ds.getParams_in(), "issueDate").toString();
				if(!StringUtils.isEmpty(issueDate)){
					reqparam.put("issueDate", issueDate);
					params.put("issueDate", issueDate);
				}
				
			}
			if(ParamUtil.findValue(ds.getParams_in(), "startDate")!=null){
				startDate = ParamUtil.findValue(ds.getParams_in(), "startDate").toString();
				if(!StringUtils.isEmpty(startDate)){
					reqparam.put("startDate", startDate);
					params.put("startDate", startDate);
				}				
			}
			if(ParamUtil.findValue(ds.getParams_in(), "expiryDate")!=null){
				expiryDate = ParamUtil.findValue(ds.getParams_in(), "expiryDate").toString();
				if(!StringUtils.isEmpty(expiryDate)){
					reqparam.put("expiryDate", expiryDate);
					params.put("expiryDate", expiryDate);
				}				
			}
			logObj.setDs_id(ds.getId());
			logObj.setReq_url(url);
			logObj.setTrade_id(trade_id);
			reqparam.put("name", name);
			reqparam.put("licenseNo", licenseNo);
			
			params.put("name", name);
			params.put("licenseNo", licenseNo);
			rets = new HashMap<String, Object>();	
			
			logObj.setIncache("0");
			logger.info("{} 天创信用车辆核验http调用开始...", prefix);
			String res = verifyDriverLicensce(trade_id,url,params);
//			String res = "{\"data\":{\"code\":\"2\",\"class\":\"0\"},\"seqNum\":\"7619031400079984\",\"message\":\"成功\",\"status\":0}";
			logger.info("{} 天创信用车辆核验http调用结束:{}", prefix,res);
			if(StringUtil.isEmpty(res)){
				logger.error("{} 天创信用车辆核验返回异常！", prefix);
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "数据源查询异常");
				return rets;
			}
			JSONObject json = (JSONObject) JSONObject.parse(res);
			if("0".equals(json.getString("status"))){
				JSONObject data = (JSONObject) JSONObject.parse(json.getString("data"));
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
				logObj.setBiz_code1(data.getString("code"));
				logObj.setBiz_code2(data.getString("seqNum"));
				if (BIZ_CODE_1_SUCCESS.equals(data.getString("code"))) {
					resource_tag = Conts.TAG_TST_SUCCESS;
					getRetData(reqparam,retdata,JsonFilter.getJsonKeys(data, "code"),"1");
				} else if (BIZ_CODE_1_SUCCESS_UNMATCH.equals(data.getString("code"))) {
					rets.clear();
					resource_tag = Conts.TAG_TST_SUCCESS;
					getRetData(reqparam,retdata,JsonFilter.getJsonKeys(data, "code"),"2");
				} else if (BIZ_CODE_1_SUCCESS_NO.equals(data.getString("code"))) {
					logger.info("{} 天创信用驾驶证核验查询未查得", prefix);
					rets.clear();
					resource_tag = Conts.TAG_TST_SUCCESS;
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_PY_DRIVERLICENSE_NOMATCH);
					rets.put(Conts.KEY_RET_MSG, "未查得驾驶证信息");
					rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
					return rets;
				} else {
					logger.info("{} 天创信用驾驶证核验查询失败", prefix);
					rets.clear();
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_PY_DRIVERLICENSE_EXCEPTION);
					rets.put(Conts.KEY_RET_MSG, "驾驶证信息核查失败!");
					rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
					return rets;
				}
			}else if("2".equals(json.getString("status"))){
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
				logger.info("{} 天创信用驾驶证核验查询失败:{}", prefix,json.getString("message"));
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_PARAM_INVALID);
				rets.put(Conts.KEY_RET_MSG, "传入参数格式有误");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				return rets;
			}else{
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
				logger.info("{} 天创信用驾驶证核验查询失败:{}", prefix,json.getString("message"));
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_PY_DRIVERLICENSE_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "驾驶证信息核查失败!");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				return rets;
			}			
			rets.put(Conts.KEY_RET_DATA, retdata);
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
			rets.put(Conts.KEY_RET_MSG, "采集成功!");
			rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
		}catch(Exception ex){
			ex.printStackTrace();
			resource_tag = Conts.TAG_SYS_ERROR;
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, "数据源处理时异常!");
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
			logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
			logObj.setTag(resource_tag);
			DataSourceLogEngineUtil.writeLog(trade_id, logObj);
			DataSourceLogEngineUtil.writeParamIn(trade_id, reqparam, logObj);
		}
		return rets;
	}
	//包装出参
	public void getRetData(Map<String, Object> reqparam,Map<String, Object> retdata,
			JSONObject json,String flag){
		if("1".equals(flag)){
			for (Map.Entry<String, Object> entry : reqparam.entrySet()) { 
				retdata.put(checkResultMap.get(entry.getKey()), "一致");
			}
		}else if("2".equals(flag)){
			for (Map.Entry<String, Object> entry : reqparam.entrySet()) { 
				for (Map.Entry<String, Object> entry1 : json.entrySet()) {
					String nomatch_entry = entry1.getKey();
					if(nomatch_entry.equals("class")){
						nomatch_entry = "carModels";
					}
//					logger.info("reqparam的key:{},json的key:{}",entry.getKey(),entry1.getKey());
					if(!nomatch_entry.equals(entry.getKey())){
						retdata.put(checkResultMap.get(entry.getKey()), "一致");
					}else{
						retdata.put(jsonResultMap.get(entry1.getKey()), 
								jsonValueMap.get(entry1.getValue()));
					}
					
		        }
				
			}
			
		}else if("3".equals(flag)){
			for (Map.Entry<String, Object> entry : reqparam.entrySet()) { 
				retdata.put(checkResultMap.get(entry.getKey()), "未查得");
			}
		}	
	}
	static {
		checkResultMap.put("name", "nameCheckResult");
		checkResultMap.put("licenseNo", "cardNoCheckResult");
		checkResultMap.put("fileNo", "archviesNoCheckResult");
		checkResultMap.put("carModels", "carModelsCheckResult");
		checkResultMap.put("issueDate", "firstGetDocDateCheckResult");
		checkResultMap.put("startDate", "startDateCheckResult");
		checkResultMap.put("expiryDate", "expiryDateCheckResult");
		
		jsonResultMap.put("name", "nameCheckResult");
		jsonResultMap.put("licenseStatus", "cardNoCheckResult");
		jsonResultMap.put("fileNo", "archviesNoCheckResult");
		jsonResultMap.put("class", "carModelsCheckResult");
		jsonResultMap.put("issueDate", "firstGetDocDateCheckResult");
		jsonResultMap.put("startDate", "startDateCheckResult");
		jsonResultMap.put("expiryDate", "expiryDateCheckResult");
		
		jsonValueMap.put("0", "不一致");
		jsonValueMap.put("1", "一致");
	}
}
