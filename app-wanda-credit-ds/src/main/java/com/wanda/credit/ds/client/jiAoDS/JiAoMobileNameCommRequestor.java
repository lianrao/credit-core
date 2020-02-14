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
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.ExceptionUtil;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.common.util.GladDESUtils;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.client.ji_ao.bean.CheckRes;
import com.wanda.credit.ds.client.ji_ao.bean.CheckResDetail;
import com.wanda.credit.ds.client.ji_ao.bean.MobileLocation;
import com.wanda.credit.ds.client.ji_ao.bean.ResErrorInfo;
import com.wanda.credit.ds.client.ji_ao.bean.WSJiAoResBean;
import com.wanda.credit.ds.client.zhengtong.BaseZTDataSourceRequestor;
import com.wanda.credit.ds.dao.iface.jiAo.IJiAoMobileCheckService;
import com.wanda.credit.ds.iface.IDataSourceRequestor;

/**
 * @author nan.liu
 *
 */
@DataSourceClass(bindingDataSourceId="ds_jiao_mobilename_comm")
public class JiAoMobileNameCommRequestor extends BaseJiaoDs implements
		IDataSourceRequestor {
	
	private Logger logger = LoggerFactory.getLogger(JiAoMobileNameCommRequestor.class);
	
	@Autowired
	private IPropertyEngine propertyEngine;
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
		DataSourceLogVO logObj = new DataSourceLogVO(trade_id);
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
			String encMobile = GladDESUtils.encrypt(mobile);
			//保存请求参数
			String authCode = getAutoCode();
			logger.info("{} authCode为:{}" , prefix,authCode);
			logObj.setBiz_code1(authCode);
			req_param.put("realName", name);
			req_param.put("cid", mobile);
			req_param.put("innerIfType", "A2");
			req_param.put("authCode", authCode);

			if(mobile.length() != 11){
				logger.info("{} 手机号码格式错误" , prefix);
				logObj.setState_msg("手机号码格式错误");
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_POLICE_PARAM_FAILED);
				rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_WARN_DS_POLICE_PARAM_FAILED.getRet_msg());
				return rets;
			}
			if(!BaseZTDataSourceRequestor.isChineseWord(name)){
				logObj.setIncache("1");
				logger.warn("{} 姓名入参格式不符合要求:{}", prefix,name);
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_JIAO_NAME_ERROR);
				rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_WARN_DS_JIAO_NAME_ERROR.getRet_msg());
				return rets;
			}
			String params = buildReqParam(trade_id,req_param,false);
			//为了保证数据的时效性不查询
			boolean inCache = false;

			if(jiaoMobileCheck.inCachedCount(name, encMobile, date_num)){//使用数据
				logger.info("{} 查询到数据" , prefix);
				logObj.setIncache("1");
				Map<String,Object> mobile_map = jiaoMobileCheck.findGeoMobileCheck(name, encMobile);
				logger.info("{} 解析数据" , prefix);
				resolveTag(prefix,String.valueOf(mobile_map.get("ATTRIBUTE")),tags);
				retData.clear();
				retData.put("province", mobile_map.get("PROVINCE"));
				retData.put("city", mobile_map.get("CITY"));
				retData.put("attribute", mobile_map.get("ATTRIBUTE"));
				retData.put("checkresult", "0");
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
				rets.put(Conts.KEY_RET_MSG, "请求成功");
				rets.put(Conts.KEY_RET_DATA, retData);
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
							rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_POLICE_PARAM_FAILED);
							rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_WARN_DS_POLICE_PARAM_FAILED.getRet_msg());
							break;
						case "100000802":
							rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_JIAO_NAME_ERROR);
							rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_WARN_DS_JIAO_NAME_ERROR.getRet_msg());
							break;
						case "100000001":
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
						retData.put("checkresult", "-1");
						//返回值
						rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
						rets.put(Conts.KEY_RET_MSG, "请求成功");
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
					rets.put(Conts.KEY_RET_DATA, buildOutParam((JSONObject)JSON.parse(JSON.toJSONString(msgResObj.getData()))));
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
			logger.error("{} 手机号码核查交易处理异常：{}" , prefix ,ExceptionUtil.getTrace(e));			
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
		}finally{			
			rets.put(Conts.KEY_RET_TAG,tags.toArray(new String[tags.size()]));
			//保存日志信息
			logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
			logObj.setTag(StringUtils.join(tags, ";"));
			logger.info("{} 保存ds Log开始..." ,prefix);
			executorDtoService.writeDsLog(trade_id,logObj,true);
			executorDtoService.writeDsParamIn(trade_id, req_param, logObj,true);
			logger.info("{} 保存ds Log结束" ,prefix);
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
	private void resolveTag(String trade_id,String yyinshang,Set<String> tags) {
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
			logger.error("{} 不能识别的运营商信息", trade_id);
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
			if ("B7".equals(checkRes.getIFT()) || "A2".equals(checkRes.getIFT())) {
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
	
	public Map<String,Object> buildOutParam(JSONObject data){
		Map<String,Object> retdata = new HashMap<String,Object>();
		JSONArray ecl = (JSONArray) data.get("ECL");
		if(CollectionUtils.isNotEmpty(ecl)){
			if(ecl.getJSONObject(0) != null &&
					"10000004".equals(ecl.getJSONObject(0).getString("code"))){
				retdata.put("checkresult", -1);			
			}
		}else{
			String jsonString = JSONObject.toJSONString(data.get("RSL"));
			com.alibaba.fastjson.JSONArray parseArray = com.alibaba.fastjson.JSONArray.parseArray(jsonString);
			String jsonString1 = JSONObject.toJSONString(parseArray.get(0));
			JSONObject parseObject = JSON.parseObject(jsonString1);
			String jsonString2 = JSONObject.toJSONString(parseObject.get("RS"));
			Map parseObject2 = JSON.parseObject(jsonString2, Map.class);
			
			String jsonSring3 = JSONObject.toJSONString(data.get("ISPNUM"));
			Map parseObject3 = JSON.parseObject(jsonSring3, Map.class);

			retdata.put("province", parseObject3.get("province"));
			retdata.put("city", parseObject3.get("city"));
			retdata.put("attribute", parseObject3.get("isp"));
			//新加返回码99判断
			if("99".equals(parseObject2.get("code"))){
				retdata.put("checkresult", 2);
			}else{
				retdata.put("checkresult", parseObject2.get("code"));
			}
		}
		return retdata;
	}
}
