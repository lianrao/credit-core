/**   
* @Description: 手机二要素聚合数据源
* @author nan.liu
* @date 2018年1月31日 下午3:32:10 
* @version V1.0   
*/
package com.wanda.credit.ds.client.juhe;

import java.sql.Timestamp;
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
import com.wanda.credit.base.util.ExceptionUtil;
import com.wanda.credit.base.util.IPUtils;
import com.wanda.credit.base.util.RequestHelper;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.common.util.GladDESUtils;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.client.ji_ao.bean.MobileLocation;
import com.wanda.credit.ds.client.ppxin.BasePPXDSRequestor;
import com.wanda.credit.ds.client.ppxin.bean.JuheMobileBean;
import com.wanda.credit.ds.client.zhengtong.BaseZTDataSourceRequestor;
import com.wanda.credit.ds.dao.iface.jiAo.IJiAoMobileCheckService;
import com.wanda.credit.ds.iface.IDataSourceRequestor;

@DataSourceClass(bindingDataSourceId="ds_juhe_mobileName")
public class JuheMobileNameDSRequestor extends BaseJuheDSRequestor implements
		IDataSourceRequestor {
	
	private Logger logger = LoggerFactory.getLogger(JuheMobileNameDSRequestor.class);
	
	@Autowired
	private IPropertyEngine propertyEngine;
	@Autowired
	private IJiAoMobileCheckService jiaoMobileCheck;
	public Map<String, Object> request(String trade_id, DataSource ds) {		
		String juhe_mobilename_url = propertyEngine.readById("ds_juhe_mobilename_url").trim();
		String juhe_mobilename_key = propertyEngine.readById("ds_juhe_mobilename_api_key");
		
		String juhe_url = propertyEngine.readById("ds_juhe_localMobile_url");
		String juhe_key = propertyEngine.readById("ds_juhe_localMobile_key");
		int date_num = Integer.valueOf(propertyEngine.readById("ds_jiAo_mobileName_cachedate"));
        boolean inCache = false;
        
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;

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
			logObj.setReq_url(juhe_mobilename_url);
			logObj.setBiz_code3(IPUtils.getLocalIP());
			logObj.setIncache("0");
			logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
			logObj.setState_msg("交易失败");
			try{
				logger.info("{} 开始解析传入的参数" , prefix);
				String name = ParamUtil.findValue(ds.getParams_in(), paramIds[0]).toString();
				String mobile = ParamUtil.findValue(ds.getParams_in(), paramIds[1]).toString();//账户号

				logger.info("{} 解析传入的参数成功" , prefix);
				reqparam.put("name", name);
				reqparam.put("mobile", mobile);
				//加密敏感信息
				String enMobile = GladDESUtils.encrypt(mobile);

				if(mobile.length() != 11){
					logger.info("{} 手机号码格式错误:{}" , prefix,mobile);
					logObj.setState_msg("手机号码格式错误");
					rets.clear();
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_MOBILE_NO_ERROR);
					rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_WARN_DS_MOBILE_NO_ERROR.getRet_msg());
					return rets;
				}
				if(!BaseZTDataSourceRequestor.isChineseWord(name)){
					logObj.setIncache("1");
					logger.warn("{} 姓名入参格式不符合要求:{}", prefix,name);
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_JIAO_NAME_ERROR);
					rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_WARN_DS_JIAO_NAME_ERROR.getRet_msg());
					return rets;
				}
				if(jiaoMobileCheck.inCachedCount(name, enMobile, date_num)){//使用缓存数据
					logger.info("{} 查询到缓存数据" , prefix);
					logObj.setIncache("1");
					Map<String,Object> mobile_map = jiaoMobileCheck.findGeoMobileCheck(name, enMobile);
					logger.info("{} 解析缓存数据" , prefix);
					resource_tag = resolveTag(prefix,String.valueOf(mobile_map.get("ATTRIBUTE")));
					retData.clear();
					retData.put("province", mobile_map.get("PROVINCE"));
					retData.put("city", mobile_map.get("CITY"));
					retData.put("attribute", mobile_map.get("ATTRIBUTE"));
					retData.put("checkresult", "0");
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
					rets.put(Conts.KEY_RET_MSG, "请求成功");
					rets.put(Conts.KEY_RET_DATA, retData);
					rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
					logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
					logObj.setState_msg("交易成功");
					return rets;
				}
				if (!inCache) {
	            	logger.info("{} 聚合实时手机二要素验证开始...", prefix);
	            	Map<String, String> header = new HashMap<>();
					Map<String, String> req_params = new HashMap<>();
					req_params.put("key", juhe_mobilename_key);
					req_params.put("realname", name);
					req_params.put("mobile", mobile);

					Map<String,Object> postRsp = RequestHelper.doGetRetFull(juhe_mobilename_url, req_params, header, true,null,
	                        "UTF-8");
					String httpresult = String.valueOf(postRsp.get("res_body_str"));
	            	logger.info("{} 聚合实时二要素验证调用成功:{}", prefix,httpresult);
	            	if(StringUtils.isEmpty(httpresult)){
						rets.clear();
						rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_POLICE_EXCEPTION);
						rets.put(Conts.KEY_RET_MSG, "数据源调用失败");
						rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
						logger.warn("{} 公安数据源厂商返回异常! ",prefix);
						return rets;
					}
	            	logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
	            	JSONObject json = JSONObject.parseObject(httpresult);
					String error_code = json.getString("error_code");
					logObj.setBiz_code1(error_code);
					Map<String, Object> result = new HashMap<String, Object>();
					if("0".equals(error_code)){
						JSONObject res = json.getJSONObject("result");
						if("1".equals(res.getString("res"))){
							result.put("checkresult", "0");
							retData.put("checkResult", "0");
						}else if("2".equals(res.getString("res"))){
							result.put("checkresult", "1");
							retData.put("checkResult", "1");
						}						
					}else if("225103".equals(error_code)){
						result.put("checkresult", "-1");
						retData.put("checkResult", "-1");
					}else if("225104".equals(error_code)){
						rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_MOBILE_NO_ERROR);
						rets.put(Conts.KEY_RET_MSG, "您输入的手机号码无效，请核对后重新输入");
						rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
						logger.warn("{}运营商查询失败 ",prefix);
						return rets;
					}else if("225108".equals(error_code)){
						rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_NOT_SUPPORT_VNO);
						rets.put(Conts.KEY_RET_MSG, "暂不支持该手机号查询");
						rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
						logger.warn("{}运营商查询失败 ",prefix);
						return rets;
					}else if("225102".equals(error_code)){
						rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_PARAM_INVALID);
						rets.put(Conts.KEY_RET_MSG, "同一手机号请求过于频繁");
						rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
						logger.warn("{}运营商查询同一手机号请求过于频繁 ",prefix);
						return rets;
					}else{
						rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
						rets.put(Conts.KEY_RET_MSG, "手机二要素查询失败");
						rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
						logger.warn("{}公安数据源厂商返回异常! ",prefix);
						return rets;
					}
					logger.info("{} 本地查询归属地" , prefix);	
					MobileLocation location = new MobileLocation();
					
					logger.info("{} 调用聚合归属地查询接口" , prefix);
					String pexfix = juhe_url+"phone="+mobile+"&key="+juhe_key;
					String juhe_rsp = BasePPXDSRequestor.getMsg(pexfix);
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
					resource_tag = buildTags(error_code,retData.get(ATTRIBUTE_EN).toString());
					result.put("province", retData.get("province"));
					result.put("city", retData.get("city"));
					result.put("attribute", retData.get("attribute"));
					//解析结果用于保存
					parseToSave(trade_id,name,null,enMobile,location,retData);
					//拼装返回信息
					rets.clear();
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
					rets.put(Conts.KEY_RET_MSG, "请求成功");
					rets.put(Conts.KEY_RET_DATA, result);
					rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
					//记录日志信息
					logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
					logObj.setState_msg("交易成功");
	            }
				
			}catch(Exception e){
				resource_tag = Conts.TAG_SYS_ERROR;
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION.getRet_msg());
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
			logger.info("{} 身份验证End",prefix);
			return rets;
	
	}
	private String buildTags(String queryStatus, String attributeEn) {
		String resource_tag = Conts.TAG_TST_FAIL;
		if("0".equals(queryStatus)){
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
	private String resolveTag(String trade_id,String yyinshang) {
		String resource_tag = Conts.TAG_TST_FAIL;
		if ("移动".equals(yyinshang)){
			resource_tag = "check_yd_incache_found";
		}else if ("联通".equals(yyinshang)){
			resource_tag = "check_lt_incache_found";
		}else if ("电信".equals(yyinshang)){
			resource_tag = "check_dx_incache_found";
		}else {
			resource_tag = "unfound";
			logger.error("{} 不能识别的运营商信息", trade_id);
		}
		return resource_tag;
	}
}
