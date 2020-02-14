/**   
 * @Description: TODO(用一句话描述该文件做什么) 
 * @author xiaobin.hou  
 * @date 2017年2月13日 下午8:16:13 
 * @version V1.0   
 */
package com.wanda.credit.ds.client.trulioo;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ConnectTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.iface.IDataSourceRequestor;

/**
 * @author xiaobin.hou
 *
 */
@DataSourceClass(bindingDataSourceId="ds_trulioo_verify")
public class VerifyForeignerDSRequestor extends BaseTruliooDSRequestor implements
		IDataSourceRequestor {

	private Logger logger = LoggerFactory
			.getLogger(VerifyForeignerDSRequestor.class);

	@Autowired
	public IPropertyEngine propertyEngine;
	
	private final static String TRULIOO_CONF_NAME = "Identity Verification";

	public Map<String, Object> request(String trade_id, DataSource ds) {

		long start = System.currentTimeMillis();
		String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		// 组织返回对象
		Map<String, Object> rets = new HashMap<String, Object>();
		TreeMap<String, Object> retData = new TreeMap<String, Object>();
		// 计费标签
		Set<String> tags = new HashSet<String>();
		tags.add(Conts.TAG_SYS_ERROR);
		
		String url = propertyEngine.readById("ds_trulioo_verify_url");
		String authInfo = propertyEngine.readById("ds_trulioo_authInfo");
		int timeOut = Integer.valueOf(propertyEngine.readById("ds_trulioo_timeout"));
		// 交易日志信息数据
		DataSourceLogVO logObj = new DataSourceLogVO();
		logObj.setDs_id(ds.getId());
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));
		logObj.setReq_url(url);
		logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL); // 初始值-失败
		logObj.setIncache("0");
		try {			
			logger.info("{} 开始解析传入的参数" , prefix);
			//Location,Communication,DriverLicence,NationalIds,Passport,CountrySpecific
			String countryCode = ParamUtil.findValue(ds.getParams_in(), paramIds[0]).toString();
			String verify_data = ParamUtil.findValue(ds.getParams_in(), paramIds[1]).toString();
//			Object Location = ParamUtil.findValue(ds.getParams_in(), "Location");
//			Object Communication = ParamUtil.findValue(ds.getParams_in(), "Communication");
//			Object driverLicence = ParamUtil.findValue(ds.getParams_in(), "DriverLicence");
//			Object nationalIds = ParamUtil.findValue(ds.getParams_in(), "NationalIds");
//			Object passport = ParamUtil.findValue(ds.getParams_in(), "Passport");
//			Object countrySpecific = ParamUtil.findValue(ds.getParams_in(), "CountrySpecific");
			logger.info("{} 解析传入的参数成功" , prefix);		

			Map<String, String> headerMap = new HashMap<String, String>();
			headerMap.put("Content-Type", "application/json");
			headerMap.put("Authorization", authInfo);
			RequestConfig requestConfig = RequestConfig.custom()
					.setConnectionRequestTimeout(timeOut).setSocketTimeout(timeOut)
					.setConnectTimeout(timeOut).build();
			Map<String, Object> verifyMap = new HashMap<String, Object>();
			List<String> list = new ArrayList<String>();
			if (countryCode.equals("HK")) {
				list.add("TransUnion Hong Kong");
				verifyMap.put("ConsentForDataSources", list);
			}
			verifyMap.put("AcceptTruliooTermsAndConditions", true);
			verifyMap.put("Timeout", 100000);
			verifyMap.put("CleansedAddress", false);
			verifyMap.put("ConfigurationName", TRULIOO_CONF_NAME);
			verifyMap.put("CountryCode", countryCode);
			verifyMap.put("VerboseMode", true);
//			verifyMap.put("DataFields", value);
//			VerifyReqBean verify = new VerifyReqBean();
//			verify.setAcceptTruliooTermsAndConditions(true);
//			verify.setDemo(true);
//			verify.setCleansedAddress(false);
//			verify.setConfigurationName(TRULIOO_CONF_NAME);
//			verify.setCountryCode(countryCode);//国家编码
//			verify.setVerboseMode(false);
//			Map<String, Object> dataFields = buildReqBody(personInfo,Location,Communication,driverLicence,nationalIds,passport,countrySpecific);
//			verify.setDataFields(dataFields);
			Map dataFields = null;
			if(isGoodJson(verify_data)){
				dataFields = JSONObject.parseObject(verify_data);
			}else{
				logger.info("{} 传入的参数错误:{}" , prefix,verify_data);
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_PARAM_INVALID);
				rets.put(Conts.KEY_RET_MSG, "校验不通过:传入参数不正确");
				return rets;
			}
			verifyMap.put("DataFields", dataFields);
			logger.info("{} 传入的入参信息:{}" , prefix,verifyMap);
			Map<String,String> res = doPost(url, null, headerMap, verifyMap, null, requestConfig, true);
			net.sf.json.JSONObject respMap = new net.sf.json.JSONObject();
			if (StringUtil.isEmpty(res.get(http_resp_str))) {
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_TIMEOUT);
				rets.put(Conts.KEY_RET_MSG, "数据源请求超时");
				return rets;
			}
			
			String respStatus = res.get(http_resp_status);
			
			if (!"200".equals(respStatus)) {
				logger.info("{} 请求trulioo数据源失败 {}" , prefix, respStatus + res.get(http_resp_str));
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED);
				rets.put(Conts.KEY_RET_MSG, "请求失败");
			}else{
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC); // 成功
				respMap = net.sf.json.JSONObject.fromObject(res.get(http_resp_str));
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
				rets.put(Conts.KEY_RET_MSG, "请求成功");
				retData.put("verify_result", respMap);
				rets.put(Conts.KEY_RET_DATA, retData);
				tags.clear();
				tags.add(Conts.TAG_TST_SUCCESS + "_" + countryCode);
			}
			return rets;
			
		} catch (Exception e) {
			logger.error("{} 获取trulioo支持的城市列表异常：{}", prefix, e.getMessage());
			// 设置标签
			tags.clear();
			tags.add(Conts.TAG_TST_FAIL);
			if (e instanceof ConnectTimeoutException) {
				logger.error("{} 连接远程数据源超时", prefix);
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_TIMEOUT);
				logObj.setState_msg("请求超时");
				// 设置标签
				tags.clear();
				tags.add(Conts.TAG_SYS_TIMEOUT);
			}
			e.printStackTrace();
		} finally {
			rets.put(Conts.KEY_RET_TAG,tags.toArray(new String[tags.size()]));
			// 保存日志信息
			logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
			logObj.setTag(StringUtils.join(tags, ";"));
			DataSourceLogEngineUtil.writeLog(trade_id, logObj);

		}
		logger.info("{} 手机三维验证End，交易时间为(ms):{}", prefix,
				(System.currentTimeMillis() - start));
		return rets;
	}

	/**
	 * @param personInfo
	 * @return
	 */
	private Map<String, Object> buildReqBody(String personInfo,Object... others) {
		//装在数据
		Map<String, Object> dataFields = new HashMap<String, Object>();
		Map personInfoMap = JSONObject.parseObject(personInfo, Map.class);
		dataFields.put("PersonInfo", personInfoMap);
		if (others != null) {
			if (others.length > 0 && others[0] != null) {
				Map locationMap = JSONObject.parseObject(others[0].toString(), Map.class);
				dataFields.put("Location", locationMap);						
			}
			if (others.length > 1 && others[1] != null) {
				Map communicationMap = JSONObject.parseObject(others[1].toString(), Map.class);
				dataFields.put("Communication", communicationMap);						
			}
			if (others.length > 2 && others[2] != null) {
				
				Map driverLicenceMap = JSONObject.parseObject(others[2].toString(), Map.class);
				dataFields.put("DriverLicence", driverLicenceMap);						
			}
			if (others.length > 3 && others[3] != null) {
				List nationalIdsList = JSON.parseArray(others[3].toString(), List.class);
//				List nationalIdsMap = JSONObject.parseArray(others[3].toString(), List.class);
				dataFields.put("NationalIds", nationalIdsList);						
			}
			if (others.length > 4 && others[4] != null) {
				Map passportMap = JSONObject.parseObject(others[4].toString(), Map.class);
				dataFields.put("Passport", passportMap);						
			}
			if (others.length > 5 && others[5] != null) {
				Map specificMap = JSONObject.parseObject(others[5].toString(), Map.class);
				dataFields.put("CountrySpecific", specificMap);						
			}
		}
		return dataFields;
	}
}
