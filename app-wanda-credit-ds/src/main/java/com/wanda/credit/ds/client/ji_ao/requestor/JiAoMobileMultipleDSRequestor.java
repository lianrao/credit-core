/**   
* @Description: 手机多个功能组合接口，可用于在网时长和当前手机号状态的自由组合
* @author xiaobin.hou  
* @date 2016年11月1日 下午3:32:10 
* @version V1.0   
*/
package com.wanda.credit.ds.client.ji_ao.requestor;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.apache.http.conn.ConnectTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.api.iface.IExecutorSecurityService;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.CardNoValidator;
import com.wanda.credit.base.util.RequestHelper;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.client.ji_ao.bean.CheckRes;
import com.wanda.credit.ds.client.ji_ao.bean.CheckResDetail;
import com.wanda.credit.ds.client.ji_ao.bean.MobileLocation;
import com.wanda.credit.ds.client.ji_ao.bean.ResData;
import com.wanda.credit.ds.client.ji_ao.bean.ResErrorInfo;
import com.wanda.credit.ds.client.ji_ao.bean.WSJiAoReqBean;
import com.wanda.credit.ds.client.ji_ao.bean.WSJiAoResBean;
import com.wanda.credit.ds.iface.IDataSourceRequestor;

/**
 * @author xiaobin.hou
 *
 */
@DataSourceClass(bindingDataSourceId="ds_jiAo_multiple")
public class JiAoMobileMultipleDSRequestor extends BaseJiAoDSRequestor implements
		IDataSourceRequestor {
	
	private Logger logger = LoggerFactory.getLogger(JiAoMobileMultipleDSRequestor.class);
	
	@Autowired
	private IPropertyEngine propertyEngine;
	@Autowired
	private IExecutorSecurityService synchExecutorService;
	
	public final static String NOT_FOUND_CODE = "5";
	
	public final static String TAG_INTIME_FOUND_CMCC = "intime_yd_found";
	public final static String TAG_INTIME_FOUND_CUCC = "intime_lt_found";
	public final static String TAG_INTIME_FOUND_CTCC = "intime_dx_found";
	public final static String TAG_INTIME_FOUND_OTHERS = "intime_found_others";
	public final static String TAG_INTIME_UNFOUND = "intime_unfound";
	
	
	public final static String TAG_STATE_FOUND_CMCC = "state_yd_found";
	public final static String TAG_STATE_FOUND_CUCC = "state_lt_found";
	public final static String TAG_STATE_FOUND_CTCC = "state_dx_found";
	public final static String TAG_STATE_FOUND_OTHERS = "state_found_others";
	public final static String TAG_STATE_UNFOUND = "state_unfound";
	
	private String url;
	private String testFlag;
	
	public Map<String, Object> request(String trade_id, DataSource ds) {

		url = propertyEngine.readById("ds_jiAo_mobile_url").trim();
		
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		long start = System.currentTimeMillis();
		logger.info("{} 多功能接口查询手机信息-网数-集奥Begin {}" , prefix ,start);
		
		//组织返回对象
		Map<String, Object> rets = new HashMap<String, Object>();
		TreeMap<String, Object> retData = new TreeMap<String, Object>();
		rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED);
		rets.put(Conts.KEY_RET_MSG, "交易失败");
		//计费标签
		Set<String> tags = new HashSet<String>();
		tags.add(Conts.TAG_SYS_ERROR);
		//交易日志信息数据
		DataSourceLogVO logObj = new DataSourceLogVO();
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));
		logObj.setDs_id(ds.getId());
		logObj.setReq_url(url);
		logObj.setIncache("0");
		logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
		logObj.setState_msg("交易失败");
		
		try{
			logger.info("{} 开始解析传入的参数" , prefix);
			String name = ParamUtil.findValue(ds.getParams_in(), paramIds[0]).toString();
			String cardNo = ParamUtil.findValue(ds.getParams_in(), paramIds[1]).toString().toUpperCase();
			String mobile = ParamUtil.findValue(ds.getParams_in(), paramIds[2]).toString();
			String getInTime = ParamUtil.findValue(ds.getParams_in(), paramIds[3]).toString();
			String getStatus = ParamUtil.findValue(ds.getParams_in(), paramIds[4]).toString();
			String multipleType = getInTime + getStatus;
			logger.info("{} 解析传入的参数成功" , prefix);
			
			//加密敏感信息
			String enccardNo = synchExecutorService.encrypt(cardNo);
			String encMobile = synchExecutorService.encrypt(mobile);
			//保存请求参数
			saveParamIn(name,cardNo,mobile,getInTime,getStatus,trade_id,logObj);
			//验证综合参数
			String type = validateMultipleType(multipleType , prefix);
			//参数校验 - 身份证号码和手机号
			String validate = CardNoValidator.validate(cardNo);
			if (StringUtil.isEmpty(type)) {
				logObj.setState_msg("getInTime,getStatus参数错误");
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_PARAM_INVALID);
				rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_FAILED_SYS_PARAM_INVALID.getRet_msg());
				return rets;
			}
			if (!StringUtil.isEmpty(validate)) {
				logger.info("{} 身份证格式校验错误： {}" , prefix , validate);
				logObj.setState_msg("身份证格式校验错误");
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_IDCARD_ERROR);
				rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_IDCARD_ERROR.getRet_msg());
				return rets;
			}
			if(!(mobile.length() == 11 && StringUtil.isPositiveInt(mobile))){
				logger.info("{} 手机号码格式错误" , prefix);
				logObj.setState_msg("手机号码格式错误");
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_MOBILE_NO_ERROR);
				rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_WARN_DS_MOBILE_NO_ERROR.getRet_msg());
				return rets;
			}
			
			
			//为了保证数据的时效性不查询缓存
			boolean inCache = false;
			
			//缓存数据不存在直接从天翼征信获取数据
			if (!inCache) {

				WSJiAoReqBean reqBean = buildReqBean(name, cardNo, mobile, type);
				
				long postStart = System.currentTimeMillis();
				String postResult = RequestHelper.doPost(url, null,
						new HashMap<String, String>(), reqBean, null,
						doPrint(testFlag));
				logger.info("{} http请求耗时(ms)为：{}",prefix , System.currentTimeMillis() - postStart);
				logger.info("{} http请求结果为：{}",prefix , postResult);
				
				if (StringUtil.isEmpty(postResult)) {
					logger.info("{} http返回结果为空" , prefix);
					logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_TIMEOUT);
					logObj.setState_msg("请求超时");
					return rets;
				}
				
				WSJiAoResBean msgResObj = JSONObject.parseObject(postResult, WSJiAoResBean.class);
				
				if (StringUtil.isEmpty(msgResObj)) {
					logObj.setState_msg("返回报文解析成对象错误");
					return rets;
				}
				//接口调用返回码
				String resCode = msgResObj.getCode();
				String resMsg = msgResObj.getMsg();
				logObj.setBiz_code2(resCode);
				logObj.setState_msg(resMsg);
				//初始化标签
				tags.clear();
				if ("A3".equals(type)) {
					tags.add(TAG_INTIME_UNFOUND);
				}else if("A4".equals(type)){
					tags.add(TAG_STATE_UNFOUND);
				}else{
					tags.add(TAG_INTIME_UNFOUND);
					tags.add(TAG_STATE_UNFOUND);
				}
				//解析系统返回码判断远程调用是否异常
				if (!"200".equals(resCode)) {
					logger.info("{} 调用远程接口失败返回错误码为 {}" , prefix , resCode);
					rets.clear();
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
					rets.put(Conts.KEY_RET_MSG, "调用远程数据源失败");
					return rets;
				}				
				//判断data节点是否有内容
				ResData dataNode = msgResObj.getData();
				if (StringUtil.isEmpty(dataNode)) {
					logger.info("{} 返回报文信息中对应data节点为空" , prefix );
					rets.clear();
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
					rets.put(Conts.KEY_RET_MSG, "调用远程数据源失败");
					return rets;
				}
				//判断ECL节点内容
				List<ResErrorInfo> eclList = dataNode.getECL();				
				if (eclList != null && eclList.size() > 0) {
					logger.info("{} 数据源返回异常个数为{}" , prefix ,eclList.size());
					logger.info("{} 数据源ECL节点内容为：{} " , prefix ,JSONObject.toJSONString(eclList));
					for (ResErrorInfo resError : eclList) {
						String errorIFT = resError.getIFT();
						String errorCode = resError.getCode();
						if("10000002".equals(errorCode)){
							//制定返回码 暂不支持此运营商
							rets.clear();
							rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_MOBILE_NO_SUPPORT);
							rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_FAILED_DS_MOBILE_NO_SUPPORT.getRet_msg());
							return rets;
						}else if("10000006".equals(errorCode)){
							//手机号码格式错误
							rets.clear();
							rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_MOBILE_NO_ERROR);
							rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_WARN_DS_MOBILE_NO_ERROR.getRet_msg());
							return rets;
						}else if ("10000004".equals(errorCode)) {
							if ("A3".equals(errorIFT)) {
								retData.put(MOBILE_IN_TIME, NOT_FOUND_CODE);
							}else if("A4".equals(errorIFT)){
								retData.put(MOBILE_STATUS, NOT_FOUND_CODE);
							}
						}else {
							rets.clear();
							rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
							rets.put(Conts.KEY_RET_MSG, "调用远程数据源失败");
							return rets;
						}
					}
				}
				
				//获取号码归属地信息 ISPNUM
				MobileLocation location = dataNode.getISPNUM();
				retData = parseLocatin(location,retData);
				String attriEn = retData.get(ATTRIBUTE_EN).toString();
				//判断RSL节点内容
				List<CheckRes> rslList = dataNode.getRSL();
				if (rslList !=null && rslList.size() > 0) {
					logger.info("{} 数据源RSL节点内容为：{} " , prefix ,JSONObject.toJSONString(rslList));
					//解析返回结果用于输出
					retData = parseRslList(rslList,retData);
					//处理标签
					tags = buildTags(rslList,tags,getInTime,getStatus,attriEn);
					//解析结果用于保存
					parseToSave(trade_id,name,enccardNo,encMobile,location,retData);
					//拼装返回信息
					rets.clear();
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
					rets.put(Conts.KEY_RET_MSG, "请求成功");
					rets.put(Conts.KEY_RET_DATA, retData);
					//记录日志信息
					logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
					logObj.setState_msg("交易成功");					
				}else{
					//RSL节点为空
					rets.clear();
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
					rets.put(Conts.KEY_RET_MSG, "调用远程数据源失败");
					return rets;
				}
				
			}
			
		}catch(Exception e){
			logger.error("{} 手机号码核查交易处理异常：{}" , prefix , e.getMessage());
			
			if (e instanceof ConnectTimeoutException) {
				
				logger.error("{} 连接远程数据源超时" , prefix);
				
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_TIMEOUT);
				logObj.setState_msg("请求超时");
				//设置标签
				tags.clear();
				tags.add(Conts.TAG_SYS_TIMEOUT);
			}
			e.printStackTrace();
		}finally{
			rets.put(Conts.KEY_RET_TAG,tags.toArray(new String[tags.size()]));
			logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
			logObj.setTag(StringUtils.join(tags, ";"));
			long dsLogStart = System.currentTimeMillis();
			DataSourceLogEngineUtil.writeLog(trade_id,logObj);
			logger.info("{} 保存ds Log成功,耗时：{}" ,prefix , System.currentTimeMillis() - dsLogStart);
		}
		logger.info("{} 手机三维验证End，交易时间为(ms):{}",prefix ,(System.currentTimeMillis() - start));
		return rets;
	
	}

	/**
	 * 解析返回信息拼装标签
	 * @param rslList
	 * @param tags
	 * @param getInTime
	 * @param getStatus
	 * @return
	 */
	private Set<String> buildTags(List<CheckRes> rslList, Set<String> tags,
			String getInTime, String getStatus,String attribute_en) {
		
		if ("1".equals(getInTime) && "0".equals(getStatus) ) {
			if ("A3".equals(rslList.get(0).getIFT())) {
				tags.clear();
				tags.add(getInTimeFoundTag(attribute_en));
			}
		}else if("0".equals(getInTime) && "1".equals(getStatus)){
			if ("A4".equals(rslList.get(0).getIFT())) {
				tags.clear();
				tags.add(getMobileStateFoundTag(attribute_en));
			}
		}else if ("1".equals(getInTime) && "1".equals(getStatus)) {
			if (rslList.size() >=2) {
				tags.clear();
				tags.add(getInTimeFoundTag(attribute_en));
				tags.add(getMobileStateFoundTag(attribute_en));
			}else{
				String rslIft = rslList.get(0).getIFT();
				if ("A3".equals(rslIft)) {
					tags.clear();
					tags.add(getInTimeFoundTag(attribute_en));
					tags.add(TAG_STATE_UNFOUND);
				}else{
					tags.clear();
					tags.add(TAG_INTIME_UNFOUND);
					tags.add(getMobileStateFoundTag(attribute_en));
				}
			}
		}
		return tags;
	}

	/**
	 * 拼装在网时长查询到结果的标签
	 * @param attribute_en
	 * @return
	 */
	private String getMobileStateFoundTag(String attribute_en) {
		if (CHINA_MOBILE.equals(attribute_en)) {
			return TAG_STATE_FOUND_CMCC;
		}else if(CHINA_UNICOM.equals(attribute_en)){
			return TAG_STATE_FOUND_CUCC;
		}else if(CHINA_TELECOM.equals(attribute_en)){
			return TAG_STATE_FOUND_CTCC;
		}else{
			return TAG_STATE_FOUND_OTHERS;
		}
	}

	/**
	 * 拼装在网时长查询到结果的标签
	 * @param attribute_en
	 * @return
	 */
	private String getInTimeFoundTag(String attribute_en) {
		if (CHINA_MOBILE.equals(attribute_en)) {
			return TAG_INTIME_FOUND_CMCC;
		}else if(CHINA_UNICOM.equals(attribute_en)){
			return TAG_INTIME_FOUND_CUCC;
		}else if(CHINA_TELECOM.equals(attribute_en)){
			return TAG_INTIME_FOUND_CTCC;
		}else{
			return TAG_INTIME_FOUND_OTHERS;
		}
		
	}

	/**
	 * 解析返回的详细信息
	 * @param rslList
	 * @param getInTime
	 * @param getStatus
	 * @param retData
	 * @return
	 */
	private TreeMap<String, Object> parseRslList(List<CheckRes> rslList, TreeMap<String, Object> retData) {
		
		for (CheckRes checkRes : rslList) {
			String ift = checkRes.getIFT();
			CheckResDetail rs = checkRes.getRS();
			String code = rs.getCode();
			if ("A3".equals(ift)) {
				retData.put(MOBILE_IN_TIME, NOT_FOUND_CODE);
				Map<String, String> inTimeCodeMap = getInTimeMap();
				if (inTimeCodeMap.containsKey(code)) {
					retData.put(MOBILE_IN_TIME, inTimeCodeMap.get(code));
				}
			}else if("A4".equals(ift)){
				retData.put(MOBILE_STATUS, NOT_FOUND_CODE);
				Map<String, String> statusCodeMap = getStatusMap();
				if (statusCodeMap.containsKey(code)) {
					retData.put(MOBILE_STATUS, statusCodeMap.get(code));
				}
			}
		}
		return retData;
	}

	/**
	 * @param multipleType
	 * @return
	 */
	private String validateMultipleType(String multipleType,String prefix) {
		logger.info("{} 参数getInTime和getStatus为：{}" , prefix , multipleType);
		if ("00".equals(multipleType)) {
			return null;
		}else if("01".equals(multipleType)){
			return "A4";
		}else if("10".equals(multipleType)){
			return "A3";
		}else if ("11".equals(multipleType)) {
			return "A3,A4";
		}else {		
			return null;
		}
	}

//	public void setUrl(String url) {
//		this.url = url;
//	}

	public void setTestFlag(String testFlag) {
		this.testFlag = testFlag;
	}

}
