/**   
* @Description: 手机二维验证 集奥数据源
* @author xiaobin.hou  
* @date 2016年11月1日 下午3:32:10 
* @version V1.0   
*/
package com.wanda.credit.ds.client.jiAoDS;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.conn.ConnectTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.api.iface.IExecutorSecurityService;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.client.ji_ao.bean.CheckRes;
import com.wanda.credit.ds.client.ji_ao.bean.CheckResDetail;
import com.wanda.credit.ds.client.ji_ao.bean.MobileLocation;
import com.wanda.credit.ds.client.ji_ao.bean.ResErrorInfo;
import com.wanda.credit.ds.client.ji_ao.bean.WSJiAoResBean;
import com.wanda.credit.ds.dao.iface.jiAo.IJiAoMobileCheckService;
import com.wanda.credit.ds.iface.IDataSourceRequestor;
import com.wanda.credit.dsconfig.main.ResolveContext;

/**
 * @author nan.liu
 *
 */
@DataSourceClass(bindingDataSourceId="ds_dmp_mobilenameChk_new")
public class JiAoMobileNameRequestor extends BaseJiaoDs implements
		IDataSourceRequestor {
	
	private Logger logger = LoggerFactory.getLogger(JiAoMobileNameRequestor.class);
	
	@Autowired
	private IPropertyEngine propertyEngine;
	@Autowired
	private IExecutorSecurityService synchExecutorService;
	@Autowired
	private IJiAoMobileCheckService jiaoMobileCheck;
	public final static String TAG_CHECK_YD_FOUND = "check_yd_found";
	public final static String TAG_CHECK_LT_FOUND = "check_lt_found";
	public final static String TAG_CHECK_DX_FOUND = "check_dx_found";
	
	public final static String TAG_CHECK_UNFOUND = "check_unfound";
	
	private String url;
	
	public Map<String, Object> request(String trade_id, DataSource ds) {
		url = propertyEngine.readById("ds_jiAoAi_mobileNew_url").trim();
		int date_num = Integer.valueOf(propertyEngine.readById("ds_jiAo_mobileName_cachedate"));
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		long start = System.currentTimeMillis();
		logger.info("{} 手机二要素验证-集奥Begin {}" , prefix ,start);	
		//初始化对象
		Map<String, Object> rets = initRets();
		TreeMap<String, Object> retData = new TreeMap<String, Object>();
		Map<String, Object> req_param = new HashMap<String,Object>();
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
			String mobile = ParamUtil.findValue(ds.getParams_in(), paramIds[1]).toString();
			logger.info("{} 解析传入的参数成功" , prefix);
			
			//加密敏感信息
			String encMobile = synchExecutorService.encrypt(mobile);
			//保存请求参数
			String authCode = getAutoCode();
			logger.info("{} authCode为:{}" , prefix,authCode);
			logObj.setBiz_code1(authCode);
			req_param.put("realName", name);
			req_param.put("cid", mobile);
			req_param.put("innerIfType", "A2");
			req_param.put("authCode", authCode);

			if(!(mobile.length() == 11 && StringUtil.isPositiveInt(mobile))){
				logger.info("{} 手机号码格式错误" , prefix);
				logObj.setState_msg("手机号码格式错误");
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_POLICE_PARAM_FAILED);
				rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_WARN_DS_POLICE_PARAM_FAILED.getRet_msg());
				return rets;
			}
		
			String params = buildReqParam(trade_id,req_param,false);
			//为了保证数据的时效性不查询
			boolean inCache = false;
			String content = jiaoMobileCheck.findDataByMobilename(encMobile, name, date_num);
			if(onCacheCondition(content)){//使用数据
				resolveTag(trade_id,content,tags);
				//返回值
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
				rets.put(Conts.KEY_RET_MSG, "请求成功");
				rets.put(Conts.KEY_RET_DATA, (JSONObject)JSON.parse(content));
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
				logObj.setState_msg("交易成功");
				return rets;
			}
			//不存在，从数据源获取数据
			if (!inCache) {
				WSJiAoResBean msgResObj = getData(trade_id,url,params);				
				if (!isSuccess(trade_id,msgResObj,logObj,req_param,url)) {
					switch( msgResObj.getCode()){
						case "1000000":
							rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_MOBILE_NO_SUPPORT);
							rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_FAILED_DS_MOBILE_NO_SUPPORT.getRet_msg());
							break;
						case "1000006":
							rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_NOT_SUPPORT_VNO);
							rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_FAILED_DS_NOT_SUPPORT_VNO.getRet_msg());
							break;
						case "1000009":
							rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_NOT_SUPPORT_VNO);
							rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_FAILED_DS_NOT_SUPPORT_VNO.getRet_msg());
							break;
						default:
							rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
							rets.put(Conts.KEY_RET_MSG, "调用远程数据源失败");
							break;
					}
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
					
					if("10000004".equals(eclCode)){
						//标签
						tags.clear();
						tags.add(TAG_CHECK_UNFOUND);
						//返回值
						rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
						rets.put(Conts.KEY_RET_MSG, "请求成功");
						rets.put(Conts.KEY_RET_DATA, (JSONObject)JSON.parse(JSON.toJSONString(msgResObj.getData())));
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
						rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_POLICE_PARAM_FAILED);
						rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_WARN_DS_POLICE_PARAM_FAILED.getRet_msg());
						return rets;
					}else if("100000001".equals(eclCode)){
						//手机号码格式错误
						rets.clear();
						rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_NOT_SUPPORT_VNO);
						rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_FAILED_DS_NOT_SUPPORT_VNO.getRet_msg());
						return rets;
					}else {
						rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
						rets.put(Conts.KEY_RET_MSG, "调用远程数据源失败");
						return rets;
					}
				}

				List<CheckRes> checkResList = msgResObj.getData().getRSL();
				if (checkResList != null && checkResList.size() > 0) {
					logger.info("{} 手机二维验证获取详细信息成功，开始解析",prefix );
					//解析返回的结果
					retData = parseRSLList(checkResList,retData);
					//构建返回标签
					tags = buildTags(checkResList,msgResObj.getData().getISPNUM(),tags);
					//解析结果用于保存
					parseToSave(trade_id,name,null,encMobile,location,retData);
					jiaoMobileCheck.saveMobileName(trade_id, encMobile, name, null, JSON.toJSONString(msgResObj.getData()));					
					//拼装返回信息
					rets.clear();
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
					rets.put(Conts.KEY_RET_MSG, "请求成功");
					rets.put(Conts.KEY_RET_DATA, (JSONObject)JSON.parse(JSON.toJSONString(msgResObj.getData())));
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
			DataSourceLogEngineUtil.writeParamIn(trade_id, req_param, logObj);
			logger.info("{} 保存ds Log成功,耗时：{}" ,prefix , System.currentTimeMillis() - dsLogStart);
		}
		logger.info("{} 手机二维验证End，交易时间为(ms):{}",prefix ,(System.currentTimeMillis() - start));
		return rets;	
	}
	/**
	 * @param checkResList
	 * @param tags
	 * @return
	 */
	private Set<String> buildTags(List<CheckRes> checkResList, MobileLocation isp,Set<String> tags) {
		CheckResDetail rs = checkResList.get(0).getRS();
		if (rs != null && !StringUtil.isEmpty(isp.getIsp())) {
			String code = rs.getCode();
			if("0".equals(code) || "1".equals(code) || "99".equals(code)){
				if("移动".equals(isp.getIsp())){
					tags.clear();
					tags.add(TAG_CHECK_YD_FOUND);
				}else if("电信".equals(isp.getIsp())){
					tags.clear();
					tags.add(TAG_CHECK_DX_FOUND);
				}else if("联通".equals(isp.getIsp())){
					tags.clear();
					tags.add(TAG_CHECK_LT_FOUND);
				}				
			}
		}
		return tags;
	}
	private void resolveTag(String trade_id,String content,Set<String> tags) {
		JSONObject rspData = JSONObject.parseObject(content);
		String yyinshang = rspData.getJSONObject("ISPNUM").getString("isp");
		if ("移动".equals(yyinshang)){
			tags.clear();
			tags.add("check_yd_incache_found");
		}else if ("联通".equals(yyinshang)){
			tags.clear();
			tags.add("check_lt_incache_found");
		}else if ("电信".equals(yyinshang)){
			tags.clear();
			tags.add("check_dx_incache_found");
		}else {
			tags.clear();
			tags.add("unfound");
			logger.error("{} 不能识别的运营商信息 {}", trade_id, rspData);
		}
	}
	/**
	 * @param checkResList
	 * @param retData
	 * @return
	 */
	private TreeMap<String, Object> parseRSLList(List<CheckRes> checkResList,
			TreeMap<String, Object> retData) {
		for (CheckRes checkRes : checkResList) {
			if ("B7".equals(checkRes.getIFT())|| "A2".equals(checkRes.getIFT())) {
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
	/** 只有code 0 or 1 or 99 时才查询 */
	private boolean onCacheCondition(String content) {
		if(StringUtil.isEmpty(content))
			return false;
		JSONObject rspData = null;
		try{
			rspData = JSONObject.parseObject(content);
		}catch(Exception e){
			return false;
		}
		if(rspData.getJSONObject("ISPNUM").isEmpty())
			return false;
		JSONArray rsl = rspData.getJSONArray("RSL");
		if (CollectionUtils.isNotEmpty(rsl)) {
			JSONObject rs = ((JSONObject) rsl.get(0)).getJSONObject("RS");
			if (rs != null) {
				Object code = rs.get("code");
				if ("0".equals(code) || "1".equals(code)|| "99".equals(code))
					return true;
			}
		}
		return false;
	}
}
