package com.wanda.credit.ds.client.xinyada;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
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
import com.wanda.credit.ds.client.ppxin.BasePPXDSRequestor;
import com.wanda.credit.ds.client.xinyada.bean.XinYaDResultBean;
import com.wanda.credit.ds.iface.IDataSourceRequestor;
/**
 * 信雅达在网状态
 * */
@DataSourceClass(bindingDataSourceId="ds_xinyada_mobileState")
public class XinYDMobileStateRequestor extends BasePPXDSRequestor implements IDataSourceRequestor {
	private Logger logger = LoggerFactory.getLogger(XinYDMobileStateRequestor.class);

	@Autowired
	private IPropertyEngine propertyEngine;
	@Override
	public Map<String, Object> request(String trade_id, DataSource ds) {
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		logger.info("{} 信雅达实时手机在网状态开始...", prefix);
		long start = System.currentTimeMillis();
		String xinyada_url = propertyEngine.readById("ds_xinyada_link_url");
		String xinyada_apaid = propertyEngine.readById("ds_xinyada_apaid");
		String xinyada_appkey = propertyEngine.readById("ds_xinyada_appkey");
		String onlineOfcode = propertyEngine.readById("ds_xinyada_status_code");
		//初始化对象
		Map<String, Object> rets = new HashMap<String, Object>();	
		Map<String, Object> reqparam = new HashMap<String, Object>();
		//计费标签
		String resource_tag = Conts.TAG_SYS_ERROR;
		//交易日志信息数据
		DataSourceLogVO logObj = new DataSourceLogVO(trade_id);
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));
		logObj.setDs_id(ds.getId());
		logObj.setReq_url(xinyada_url);
		logObj.setBiz_code3(IPUtils.getLocalIP());
		logObj.setIncache("0");
		logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
		logObj.setState_msg("交易失败");
		try{
			logger.info("{} 开始解析传入的参数" , prefix);			
			String mobile = ParamUtil.findValue(ds.getParams_in(), paramIds[0]).toString();
			String attributeEn = ParamUtil.findValue(ds.getParams_in(), "attributeEn").toString();
			String name = "";
			if(ParamUtil.findValue(ds.getParams_in(), "name")!=null){
				name = ParamUtil.findValue(ds.getParams_in(), "name").toString();
			}
			String cardNo = "";
			if(ParamUtil.findValue(ds.getParams_in(), "cardNo")!=null){
				cardNo = ParamUtil.findValue(ds.getParams_in(), "cardNo").toString();
			}
			logger.info("{} 解析传入的参数成功" , prefix);
			reqparam.put("cardNo", cardNo);
			reqparam.put("name", name);
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
			JSONObject jtr=new JSONObject();//参数包装
			jtr.put("mobile",mobile);//参数包装
			String ss=jtr.toString();
			Map<String, String> params=new HashMap<String, String>();
			params.put("appid", xinyada_apaid);
			params.put("api", "1430");
			params.put("args",ss);
			StringBuffer sb = new StringBuffer(xinyada_appkey);//提供的appkey
			sb.append("|").append(params.get("appid")).append("|").append(params.get("api")).append("|").append(params.get("args"));
			String localSign = DigestUtils.md5Hex(sb.toString());
			params.put("sign",localSign);
        	String httpresult=RequestHelper.doGet(xinyada_url, params, false);
        	logger.info("{} 信雅达实时手机在网状态调用成功:{}", prefix,httpresult);
        	if(StringUtils.isEmpty(httpresult)){
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_POLICE_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "数据源厂商返回异常!");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				logger.warn("{} 信雅达数据源厂商返回异常! ",prefix);
				return rets;
			}
        	XinYaDResultBean xinydBean = JSONObject.parseObject(httpresult,XinYaDResultBean.class);
        	logObj.setBiz_code1(xinydBean.getCode());
        	logObj.setBiz_code2(xinydBean.getOrderNo());
			Map<String, Object> result = new HashMap<String, Object>();
			if("0000".equals(xinydBean.getCode())){
				resource_tag = buildTags(attributeEn);
				result.put("mobileState", getOnlineCode(onlineOfcode,xinydBean.getData().getValue()));
			}else if("1203".equals(xinydBean.getCode())){
				result.put("mobileState", "5");
			}else{
				result.put("mobileState", "-1");
			}			
			//拼装返回信息
			rets.clear();
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
			logger.info("{} 保存ds Log成功" ,prefix);
		}
		logger.info("{} 信雅达End，交易时间为(ms):{}",prefix ,(System.currentTimeMillis() - start));
		return rets;
	}
	private String buildTags(String attributeEn) {
		String resource_tag = Conts.TAG_TST_FAIL;
		if (CHINA_MOBILE.equals(attributeEn)) {
			resource_tag = "state_yd_found";
		}else if(CHINA_UNICOM.equals(attributeEn)){
			resource_tag = "state_lt_found";
		}else if(CHINA_TELECOM.equals(attributeEn)){
			resource_tag = "state_dx_found";
		}else {
			resource_tag = "state_found_others";
		}
		return resource_tag;
	}
	private String getOnlineCode(String modelCode,String xiaohe_code){
		for(String tmp:modelCode.split(",")){
			String[] tmp1 = tmp.split(":");
			if(xiaohe_code.equals(tmp1[0])){
				return tmp1[1];
			}				
		}
		return "5";
	}
}
