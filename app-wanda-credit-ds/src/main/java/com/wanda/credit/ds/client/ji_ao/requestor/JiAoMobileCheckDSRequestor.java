/**   
* @Description: 手机三维验证 集奥数据源
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
import com.wanda.credit.ds.client.ji_ao.bean.ResErrorInfo;
import com.wanda.credit.ds.client.ji_ao.bean.WSJiAoReqBean;
import com.wanda.credit.ds.client.ji_ao.bean.WSJiAoResBean;
import com.wanda.credit.ds.iface.IDataSourceRequestor;

/**
 * @author xiaobin.hou
 *
 */
@DataSourceClass(bindingDataSourceId="ds_jiAo_mobileCheck")
public class JiAoMobileCheckDSRequestor extends BaseJiAoDSRequestor implements
		IDataSourceRequestor {
	
	private Logger logger = LoggerFactory.getLogger(JiAoMobileCheckDSRequestor.class);
	
	@Autowired
	private IPropertyEngine propertyEngine;
	@Autowired
	private IExecutorSecurityService synchExecutorService;
	
	public final static String TAG_CHECK_CMCC_FOUND1 = "check_yd_found1";
	public final static String TAG_CHECK_CMCC_FOUND2 = "check_yd_found2";
	public final static String TAG_CHECK_CMCC_FOUND3 = "check_yd_found3";
	
	public final static String TAG_CHECK_CUCC_FOUND1 = "check_lt_found1";
	public final static String TAG_CHECK_CUCC_FOUND2 = "check_lt_found2";
	public final static String TAG_CHECK_CUCC_FOUND3 = "check_lt_found3";
	
	public final static String TAG_CHECK_CTCC_FOUND1 = "check_dx_found1";
	public final static String TAG_CHECK_CTCC_FOUND2 = "check_dx_found2";
	public final static String TAG_CHECK_CTCC_FOUND3 = "check_dx_found3";
	
	public final static String TAG_CHECK_FOUND1_OTHERS = "check_found1_others";
	public final static String TAG_CHECK_FOUND2_OTHERS = "check_found2_others";
	public final static String TAG_CHECK_FOUND3_OTHERS = "check_found3_others";
	
	public final static String TAG_CHECK_UNFOUND = "check_unfound";
	
	public String[] found1Arr = new String[]{"0","1","4","5"};
	public String[] found2Arr = new String[]{"2","3","6"};
	
	private String url;
	private String type;
	private String testFlag;
	
	public Map<String, Object> request(String trade_id, DataSource ds) {
		
		url = propertyEngine.readById("ds_jiAo_mobile_url").trim();
		type = propertyEngine.readById("ds_jiAo_check");

		
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		long start = System.currentTimeMillis();
		logger.info("{} 手机三要素验证-网数-集奥Begin {}" , prefix ,start);
		
		//初始化对象
		Map<String, Object> rets = initRets();
		TreeMap<String, Object> retData = new TreeMap<String, Object>();
		
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
			logger.info("{} 解析传入的参数成功" , prefix);
			
			//加密敏感信息
			String enccardNo = synchExecutorService.encrypt(cardNo);
			String encMobile = synchExecutorService.encrypt(mobile);
			//保存请求参数
			saveParamIn(name,cardNo,mobile,null,null,trade_id,logObj);
			
			//参数校验 - 身份证号码和手机号
			String validate = CardNoValidator.validate(cardNo);
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
			
			//不存在缓存，从数据源获取数据
			if (!inCache) {

				WSJiAoReqBean reqBean = buildReqBean(name, cardNo, mobile, type);
				
				long postStart = System.currentTimeMillis();
				String postResult = RequestHelper.doPost(url, null,
						new HashMap<String, String>(), reqBean, null,
						doPrint(testFlag));
				logger.info("{} http请求耗时(ms)为：{}",prefix , System.currentTimeMillis() - postStart);
				logger.info("{} http请求结果为：{}",prefix , postResult);
				
				if (StringUtil.isEmpty(postResult)) {
					logger.info("{} http请求返回结果为空" , prefix);
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
				if (!"200".equals(resCode)) {
					logger.info("{} 调用远程接口失败返回错误码为 {}" , prefix , resCode);
					
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
					rets.put(Conts.KEY_RET_MSG, "调用远程数据源失败");
					return rets;
				}
				
				if (StringUtil.isEmpty(msgResObj.getData())) {
					logger.info("{} 返回报文信息中对应data节点为空" , prefix );
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
					rets.put(Conts.KEY_RET_MSG, "调用远程数据源失败");
					return rets;
				}
				
				MobileLocation location = msgResObj.getData().getISPNUM();
				retData = parseLocatin(location, retData);
				logger.info("{} 解析号码归属地成功" , prefix);
				
				List<ResErrorInfo> eclList = msgResObj.getData().getECL();
				//判断ECL节点内容
				if (eclList != null && eclList.size() > 0) {
					logger.info("{} 数据源返回异常个数为{}" , prefix ,eclList.size());
					logger.info("{} 数据源ECL节点内容为 ：{}" , prefix ,JSONObject.toJSONString(eclList));
					//获取ECL中code
					String eclCode = eclList.get(0).getCode();					
					logObj.setBiz_code3(eclCode);
					
					if ("100000016".equals(eclCode)) {
						//标签
						tags.clear();
						String attributeEn = retData.get(ATTRIBUTE_EN).toString();
						if (attributeEn.contains("移动")) {
							tags.add(TAG_CHECK_CMCC_FOUND3);
						}else if(attributeEn.contains("联通")){
							tags.add(TAG_CHECK_CUCC_FOUND3);
						}else if (attributeEn.contains("电信")) {
							tags.add(TAG_CHECK_CTCC_FOUND3);
						}else{
							tags.add(TAG_CHECK_FOUND3_OTHERS);
						}
						//返回值
						rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
						rets.put(Conts.KEY_RET_MSG, "请求成功");
						retData.put(CHECK_RESULT, "7");
						rets.put(Conts.KEY_RET_DATA, retData);
						logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
						logObj.setState_msg("交易成功");
						return rets;
					}else if("10000004".equals(eclCode)){
						//标签
						tags.clear();
						tags.add(TAG_CHECK_UNFOUND);
						//返回值
						rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
						rets.put(Conts.KEY_RET_MSG, "请求成功");
						retData.put(CHECK_RESULT, "8");
						rets.put(Conts.KEY_RET_DATA, retData);
						logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
						logObj.setState_msg("交易成功");
						return rets;
					}else if("10000002".equals(eclCode)){
						//制定返回码 暂不支持此运营商
						rets.clear();
						rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_MOBILE_NO_SUPPORT);
						rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_FAILED_DS_MOBILE_NO_SUPPORT.getRet_msg());
						return rets;
					}else if("10000006".equals(eclCode)){
						//手机号码格式错误
						rets.clear();
						rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_MOBILE_NO_ERROR);
						rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_WARN_DS_MOBILE_NO_ERROR.getRet_msg());
						return rets;
					}else {
						rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
						rets.put(Conts.KEY_RET_MSG, "调用远程数据源失败");
						return rets;
					}
				}
				
				
				List<CheckRes> checkResList = msgResObj.getData().getRSL();
				if (checkResList != null && checkResList.size() > 0) {
					//解析返回的结果
					retData = parseRSLList(checkResList,retData);
					//构建返回标签
					tags = buildTags(checkResList,retData.get(ATTRIBUTE_EN).toString(),tags);
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
					logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
					rets.clear();
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
					rets.put(Conts.KEY_RET_MSG, "调用远程数据源失败");
					return rets;
				}
				
				
				
			}
			
		}catch(Exception e){
			e.printStackTrace();
			logger.error("{} 手机号码核查交易处理异常：{}" , prefix , e.getMessage());
			
			//设置标签
			tags.clear();
			tags.add(Conts.TAG_TST_FAIL);
			
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
			//保存日志信息
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
	 * @param checkResList
	 * @param tags
	 * @return
	 */
	private Set<String> buildTags(List<CheckRes> checkResList, String attributeEn,Set<String> tags) {
		CheckResDetail rs = checkResList.get(0).getRS();
		
		Map<String, String> found1Map = tagFound1Map();
		Map<String, String> found2Map = tagFound2Map();
		if (rs != null) {
			String code = rs.getCode();
			if (found1Map.containsKey(code)) {
				tags.clear();
				if (CHINA_MOBILE.equals(attributeEn)) {
					tags.add(TAG_CHECK_CMCC_FOUND1);
				}else if(CHINA_UNICOM.equals(attributeEn)){
					tags.add(TAG_CHECK_CUCC_FOUND1);
				}else if(CHINA_TELECOM.equals(attributeEn)){
					tags.add(TAG_CHECK_CTCC_FOUND1);
				}else {
					tags.add(TAG_CHECK_FOUND1_OTHERS);
				}
				
			}
			if (found2Map.containsKey(code)) {
				tags.clear();
				if (CHINA_MOBILE.equals(attributeEn)) {
					tags.add(TAG_CHECK_CMCC_FOUND2);
				}else if(CHINA_UNICOM.equals(attributeEn)){
					tags.add(TAG_CHECK_CUCC_FOUND2);
				}else if(CHINA_TELECOM.equals(attributeEn)){
					tags.add(TAG_CHECK_CTCC_FOUND2);
				}else {
					tags.add(TAG_CHECK_FOUND2_OTHERS);
				}
			}
		}
		return tags;
	}

	/**
	 * @param checkResList
	 * @param retData
	 * @return
	 */
	private TreeMap<String, Object> parseRSLList(List<CheckRes> checkResList,
			TreeMap<String, Object> retData) {
		for (CheckRes checkRes : checkResList) {
			if ("B7".equals(checkRes.getIFT())) {
				CheckResDetail rs = checkRes.getRS();
				
				Map<String, String> resMap = getCheckResMap();
				String checkCode = rs.getCode();
				if (resMap.containsKey(checkCode)) {
					retData.put(CHECK_RESULT, resMap.get(checkCode));
				}else{
					retData.put(CHECK_RESULT, "8");
				}
		
			}
		}
		return retData;
	}

//	public void setUrl(String url) {
//		this.url = url;
//	}
//
//	public void setType(String type) {
//		this.type = type;
//	}

	public void setTestFlag(String testFlag) {
		this.testFlag = testFlag;
	}

}
