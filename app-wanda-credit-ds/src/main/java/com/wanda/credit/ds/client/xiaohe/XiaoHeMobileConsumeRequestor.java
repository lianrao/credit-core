package com.wanda.credit.ds.client.xiaohe;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import com.wanda.credit.base.util.IPUtils;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.common.util.GladDESUtils;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.client.ji_ao.bean.MobileLocation;
import com.wanda.credit.ds.client.ppxin.BasePPXDSRequestor;
import com.wanda.credit.ds.client.ppxin.bean.JuheMobileBean;
import com.wanda.credit.ds.client.xiaohe.utils.MD5Util;
import com.wanda.credit.ds.client.xiaohe.utils.XmlTool;
import com.wanda.credit.ds.client.xiaohe.utils.httpUtils;
import com.wanda.credit.ds.client.zhengtong.BaseZTDataSourceRequestor;
import com.wanda.credit.ds.dao.iface.jiAo.IJiAoMobileCheckService;
import com.wanda.credit.ds.iface.IDataSourceRequestor;

/**
 * 手机月度消费
 * @author Administrator
 *
 */
@DataSourceClass(bindingDataSourceId="ds_xiaohe_mobileconsume")
public class XiaoHeMobileConsumeRequestor extends BasePPXDSRequestor implements IDataSourceRequestor {
	private Logger logger = LoggerFactory.getLogger(XiaoHeMobileConsumeRequestor.class);

	@Autowired
	private IPropertyEngine propertyEngine;
	@Override
	public Map<String, Object> request(String trade_id, DataSource ds) {
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		logger.info("{} 小河实时手机月度消费调用开始...", prefix);
		long start = System.currentTimeMillis();
		String mobile_api = propertyEngine.readById("ds_xiaohe_mobileconsume_api");
		String police_url = propertyEngine.readById("ds_xiaohe_police_url");
		String police_api_key = propertyEngine.readById("ds_xiaohe_police_api_key");
		String police_hashcode = propertyEngine.readById("ds_xiaohe_police_hashcode");
		
		String juhe_url = propertyEngine.readById("ds_juhe_localMobile_url");
		String juhe_key = propertyEngine.readById("ds_juhe_localMobile_key");

		//初始化对象
		Map<String, Object> rets = new HashMap<String, Object>();
		TreeMap<String, Object> retData = new TreeMap<String, Object>();	
		Map<String, Object> reqparam = new HashMap<String, Object>();
		//计费标签
		String resource_tag = Conts.TAG_SYS_ERROR;
		//交易日志信息数据
		DataSourceLogVO logObj = new DataSourceLogVO(trade_id);
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));
		logObj.setDs_id(ds.getId());
		logObj.setReq_url(mobile_api);
		logObj.setBiz_code3(IPUtils.getLocalIP());
		logObj.setIncache("0");
		logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
		logObj.setState_msg("交易失败");
		try{
			logger.info("{} 开始解析传入的参数" , prefix);
			String mobile = ParamUtil.findValue(ds.getParams_in(), "mobile").toString();
			String name = "";
			if(ParamUtil.findValue(ds.getParams_in(), "name")!=null){
				name = ParamUtil.findValue(ds.getParams_in(), "name").toString();
			}
			String cardNo = "";
			if(ParamUtil.findValue(ds.getParams_in(), "cardNo")!=null){
				cardNo = ParamUtil.findValue(ds.getParams_in(), "cardNo").toString();
			}
			logger.info("{} 解析传入的参数成功" , prefix);
			reqparam.put("mobile", mobile);

			if(mobile.length() != 11 && StringUtil.isPositiveInt(mobile)){
				logger.info("{} 手机号码格式错误:{}" , prefix,mobile);
				logObj.setState_msg("手机号码格式错误");
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_MOBILE_NO_ERROR);
				rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_WARN_DS_MOBILE_NO_ERROR.getRet_msg());
				return rets;
			}
			if(!StringUtil.isEmpty(name)){
				if(!BaseZTDataSourceRequestor.isChineseWord(name)){
					logObj.setIncache("1");
					logger.warn("{} 姓名入参格式不符合要求:{}", prefix,name);
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_JIAO_NAME_ERROR);
					rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_WARN_DS_JIAO_NAME_ERROR.getRet_msg());
					return rets;
				}
			}
			if(!StringUtil.isEmpty(cardNo)){
				String validate = CardNoValidator.validate(cardNo);
				if (!StringUtil.isEmpty(validate)) {
					logger.info("{} 身份证格式校验错误： {}" , prefix , validate);
					logObj.setState_msg("身份证格式校验错误");
					rets.clear();
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_IDCARD_ERROR);
					rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_IDCARD_ERROR.getRet_msg());
					return rets;
				}
			}
			logger.info("{} 小河手机月度消费http请求开始...", prefix);
        	Date now = new Date(); 
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");//可以方便地修改日期格式
			String sdate = dateFormat.format(now);
			
        	Map<String, String> paramMap = new HashMap<String, String>();
    		paramMap.put("Hashcode", police_hashcode);
    		paramMap.put("passname", name);
    		paramMap.put("pid", cardNo);
    		paramMap.put("mobile", mobile);

    		String sign=paramMap.get("Hashcode")+paramMap.get("mobile")+paramMap.get("passname")+paramMap.get("pid")
    		+police_api_key+sdate;
			sign=MD5Util.MD5(sign);
			paramMap.put("sign",sign);//姓名
        	String httpresult=httpUtils.httpsGet(trade_id,"https", police_url, "443",mobile_api, paramMap);
        	logger.info("{} 小河手机月度消费http请求成功:{}", prefix,httpresult);
        	if(StringUtils.isEmpty(httpresult)){
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_POLICE_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "数据源调用失败");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				logger.warn("{} 数据源厂商返回异常! ",prefix);
				return rets;
			}
        	JSONObject json=XmlTool.documentToJSONObject(httpresult);
			JSONObject police = (JSONObject) json.getJSONArray("ErrorRes").get(0);
			String code = police.getString("Err_code");
			logObj.setBiz_code1(code);
			Map<String, Object> result = new HashMap<String, Object>();
			if("200".equals(code)){
				JSONObject mobileresult = (JSONObject) json.getJSONArray("mobileresult").get(0);
				result.put("desc", mobileresult.getString("desc"));
				retData.put("checkResult", "0");
			}else if("404".equals(code)){
				resource_tag = Conts.TAG_SYS_ERROR;
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_ONLINE_FALI01);
				rets.put(Conts.KEY_RET_MSG, "手机号码不存在");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				return rets;
			}else if("503".equals(code)){
				resource_tag = Conts.TAG_SYS_ERROR;
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_ONLINE_FALI02);
				rets.put(Conts.KEY_RET_MSG, "无法验证");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				return rets;
			}else{
				resource_tag = Conts.TAG_SYS_ERROR;
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_POLICE_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "运营商调用失败!");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				return rets;
			}
			logger.info("{} 本地查询归属地" , prefix);	
			MobileLocation location = new MobileLocation();
			
			logger.info("{} 调用聚合归属地查询接口" , prefix);
			String pexfix = juhe_url+"phone="+mobile+"&key="+juhe_key;
			String juhe_rsp = getMsg(pexfix);
			logger.info("{} 调用聚合归属地查询返回信息:{}" , prefix,juhe_rsp);
			if(!StringUtil.isEmpty(juhe_rsp)){
				JuheMobileBean juhe_json = JSONObject.parseObject(juhe_rsp, JuheMobileBean.class);
				if(juhe_json.getError_code()==0){
					location.setProvince(juhe_json.getResult().getProvince());
	                location.setCity(juhe_json.getResult().getCity());
	                location.setIsp(juhe_json.getResult().getCompany());
				}else{
					logger.info("{} 调用聚合归属地查询失败" , prefix);
				}
			}
			retData = parseLocatin(location, retData);
			//构建返回标签
			resource_tag = buildTags(code,retData.get(ATTRIBUTE_EN).toString());
			result.put("province", retData.get("province"));
			result.put("city", retData.get("city"));
			result.put("isp", retData.get("attribute"));
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
		logger.info("{} 身份验证End，交易时间为(ms):{}",prefix ,(System.currentTimeMillis() - start));
		return rets;
	}
	private String buildTags(String queryStatus, String attributeEn) {
		String resource_tag = Conts.TAG_TST_FAIL;
		if("200".equals(queryStatus)){
			if (CHINA_MOBILE.equals(attributeEn)) {
				resource_tag = "check_yd_found";
			}else if(CHINA_UNICOM.equals(attributeEn)){
				resource_tag = "check_lt_found";
			}else if(CHINA_TELECOM.equals(attributeEn)){
				resource_tag = "check_dx_found";
			}else {
				resource_tag = "check_found_others";
			}
		}else{
			resource_tag = Conts.TAG_TST_FAIL;
		}
		return resource_tag;
	}
}
