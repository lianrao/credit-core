package com.wanda.credit.ds.client.chuanglan;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.ExceptionUtil;
import com.wanda.credit.base.util.IPUtils;
import com.wanda.credit.base.util.RequestHelper;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.client.chuanglan.bean.CLMobileDataBean;
import com.wanda.credit.ds.client.chuanglan.bean.CLMobileResultBean;
import com.wanda.credit.ds.client.ppxin.BasePPXDSRequestor;
import com.wanda.credit.ds.iface.IDataSourceRequestor;
/**
 * 创蓝空号检测
 * */
@DataSourceClass(bindingDataSourceId="ds_chuanglan_mobileEmpty")
public class CLMobileEmptyRequestor extends BasePPXDSRequestor implements IDataSourceRequestor {
	private Logger logger = LoggerFactory.getLogger(CLMobileEmptyRequestor.class);

	@Autowired
	private IPropertyEngine propertyEngine;
	@Override
	public Map<String, Object> request(String trade_id, DataSource ds) {
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		logger.info("{} 创蓝空号检测开始...", prefix);
		long start = System.currentTimeMillis();
		String chuanglan_url = propertyEngine.readById("ds_chuanglan_mobile_url");
		String chuanglan_appid = propertyEngine.readById("ds_chuanglan_mobile_appid");
		String chuanglan_appkey = propertyEngine.readById("ds_chuanglan_mobile_appkey");
		int time_out = Integer.valueOf(propertyEngine.readById("sys_http_send_timeout"));
		//初始化对象
		Map<String, Object> rets = new HashMap<String, Object>();	
		Map<String, Object> reqparam = new HashMap<String, Object>();
		//计费标签
		String resource_tag = Conts.TAG_SYS_ERROR;
		//交易日志信息数据
		DataSourceLogVO logObj = new DataSourceLogVO(trade_id);
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));
		logObj.setDs_id(ds.getId());
		logObj.setReq_url(chuanglan_url);
		logObj.setBiz_code3(IPUtils.getLocalIP());
		logObj.setIncache("0");
		logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
		logObj.setState_msg("交易失败");
		try{
			logger.info("{} 开始解析传入的参数" , prefix);			
			String mobile = ParamUtil.findValue(ds.getParams_in(), paramIds[0]).toString();
			
			logger.info("{} 解析传入的参数成功" , prefix);
			reqparam.put("mobile", mobile);
			
			if(!(mobile.length() == 11 && StringUtil.isPositiveInt(mobile))){
				logger.info("{} 手机号码格式错误" , prefix);
				logObj.setState_msg("手机号码格式错误");
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_MOBILE_NO_ERROR);
				rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_WARN_DS_MOBILE_NO_ERROR.getRet_msg());
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				return rets;
			}
			Map<String, String> params =new HashMap<String,String>();
			params.put("appId", chuanglan_appid);
	        params.put("appKey", chuanglan_appkey);
	        params.put("mobiles", mobile);
			String httpresult = "";
	        if(!chuanglan_url.startsWith("https:")){
	        	httpresult = RequestHelper.doPost(chuanglan_url,params,false,time_out);
	        }else{
	        	httpresult = RequestHelper.doPost(chuanglan_url,params,true,time_out);
	        }
        	logger.info("{} 创蓝空号调用成功:{}", prefix,httpresult);
        	if(StringUtils.isEmpty(httpresult)){
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_POLICE_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "数据源厂商返回异常!");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				logger.warn("{} 创蓝空号厂商返回异常! ",prefix);
				return rets;
			}
        	CLMobileResultBean clBean = JSONObject.parseObject(httpresult,CLMobileResultBean.class);
        	logObj.setBiz_code1(clBean.getCode()+"-"+clBean.getChargeStatus());
        	
			Map<String, Object> result = new HashMap<String, Object>();
			if("200000".equals(clBean.getCode()) && clBean.getData().size()>0){
				CLMobileDataBean clData = clBean.getData().get(0);
				String[] areas = clData.getArea().split("-");
				String attributeEn = getAttributeEn(clData.getNumberType());
				resource_tag = buildTags(attributeEn,clData.getChargesStatus());
				result.put("attribute", attributeEn);
				result.put("status", clData.getStatus());
				result.put("province", areas[0]);
				result.put("city", areas[1]);
				logObj.setBiz_code2(clData.getStatus());
			}else if("400001".equals(clBean.getCode())){
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_MOBILE_NO_ERROR);
				rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_WARN_DS_MOBILE_NO_ERROR.getRet_msg());
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				return rets;
			}else{
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_POLICE_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "数据源厂商返回异常!");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				logger.warn("{} 创蓝空号厂商返回异常! ",prefix);
				return rets;
			}			
			//拼装返回信息
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
			rets.put(Conts.KEY_RET_MSG, "请求成功");
			rets.put(Conts.KEY_RET_DATA, result);
			rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
			//记录日志信息
			logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
			logObj.setState_msg("交易成功");
		}catch(Exception e){
			resource_tag = Conts.TAG_SYS_ERROR;
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, "数据源查询异常!");
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
			logger.info("{} 保存ds Log成功" ,prefix);
		}
		logger.info("{} 创蓝空号End，交易时间为(ms):{}",prefix ,(System.currentTimeMillis() - start));
		return rets;
	}
	private String buildTags(String attributeEn,String chargeStatus) {
		String resource_tag = Conts.TAG_TST_FAIL;
		if("1".equals(chargeStatus)){
			if (CHINA_MOBILE.equals(attributeEn)) {
				resource_tag = "check_yd_found";
			}else if(CHINA_UNICOM.equals(attributeEn)){
				resource_tag = "check_lt_found";
			}else if(CHINA_TELECOM.equals(attributeEn)){
				resource_tag = "check_dx_found";
			}else {
				resource_tag = "check_found_others";
			}
		}		
		return resource_tag;
	}
	private String getAttributeEn(String attributeEn){
		String result = "UNKNOWN";
		if (attributeEn.contains("移动")) {
			result = "CMCC";
		}else if(attributeEn.contains("联通")){
			result = "CUCC";
		}else if(attributeEn.contains("电信")){
			result = "CTCC";
		}else {
			result = "UNKNOWN";
		}
		return result;
	}
}
