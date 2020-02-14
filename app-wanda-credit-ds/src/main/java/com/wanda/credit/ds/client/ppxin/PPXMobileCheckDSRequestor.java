/**   
* @Description: 手机三维验证拍拍信数据源
* @author nan.liu
* @date 2018年1月31日 下午3:32:10 
* @version V1.0   
*/
package com.wanda.credit.ds.client.ppxin;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.apache.http.conn.ConnectTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.api.iface.IExecutorSecurityService;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.CardNoValidator;
import com.wanda.credit.base.util.MobileCarrier;
import com.wanda.credit.base.util.RequestHelper;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.client.bairong.service.IMobileSrcLocService;
import com.wanda.credit.ds.client.ji_ao.bean.MobileLocation;
import com.wanda.credit.ds.client.ppxin.bean.JuheMobileBean;
import com.wanda.credit.ds.client.ppxin.bean.PPXinResBean;
import com.wanda.credit.ds.dao.domain.bairong.MobileSrcLocVo;
import com.wanda.credit.ds.dao.domain.jiAo.GeoMobileCheck;
import com.wanda.credit.ds.iface.IDataSourceRequestor;

@DataSourceClass(bindingDataSourceId="ds_ppx_mobileCheck")
public class PPXMobileCheckDSRequestor extends BasePPXDSRequestor implements
		IDataSourceRequestor {
	
	private Logger logger = LoggerFactory.getLogger(PPXMobileCheckDSRequestor.class);
	
	@Autowired
    private IMobileSrcLocService mobileSrcLocService;
	@Autowired
	private IPropertyEngine propertyEngine;
	@Autowired
	private IExecutorSecurityService synchExecutorService;
	
	public final static String ppx_method = "ppc.certifyuser.query";
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
	private Long cacheTime = 0L;
	
	public Map<String, Object> request(String trade_id, DataSource ds) {		
		String ppxin_url = propertyEngine.readById("ds_ppxin_mobile_url").trim();
		String ppxin_key = propertyEngine.readById("ds_ppxin_api_key");
		String ppxin_secret = propertyEngine.readById("ds_ppxin_api_secret");
		String juhe_url = propertyEngine.readById("ds_juhe_localMobile_url");
		String juhe_key = propertyEngine.readById("ds_juhe_localMobile_key");
		
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		long start = System.currentTimeMillis();
		logger.info("{} 手机三要素验证-拍拍信Begin {}" , prefix ,start);
		boolean inCache = false;
		String cacheTimeStr = propertyEngine.readById("ds_ja_cacheSec");
        String isCache = propertyEngine.readById("ds_ja_isCache");
        String exception = propertyEngine.readById("ds_ja_exce");

        logger.info("{} 缓存开关状态：{}", prefix, isCache);
        logger.info("{} 缓存时间：{}", prefix, cacheTimeStr);
        logger.info("{} 异常模拟开关状态：{}", prefix, exception);

        try{
            if(StringUtil.isEmpty(cacheTimeStr)){
                cacheTime = 60*60*24*30L; //30天默认
            }else {
                cacheTime = Long.parseLong(cacheTimeStr);
            }
        }catch (Exception e){
            logger.error("{} 解析cacheTime失败，使用默认值", prefix);
            cacheTime = 60*60*24*30L;
        }
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
		logObj.setReq_url(ppxin_url);
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
			if("1".equals(isCache)){
                logger.info("{} 开启缓存规则", prefix);
                GeoMobileCheck cacheData = isIncache(name, enccardNo, encMobile, cacheTime, prefix);
                if(cacheData != null){
                    logger.info("{} 匹配到缓存数据{}", prefix , cacheData.getCheckResult());
                    String cacheAttr = cacheData.getAttribute();
                    if(StringUtils.isNotEmpty(cacheAttr)){
                        retData.put(ATTRIBUTE, cacheAttr);
                        if(cacheAttr.contains("移动")){
                            retData.put(ATTRIBUTE_EN, CHINA_MOBILE);
                        }else if(cacheAttr.contains("联通")){
                            retData.put(ATTRIBUTE_EN, CHINA_UNICOM);
                        }else if(cacheAttr.contains("电信")){
                            retData.put(ATTRIBUTE_EN, CHINA_TELECOM);
                        }else {
                            retData.put(ATTRIBUTE_EN, CHINA_OTHERS);
                        }
                    }else {
                    	String attr = MobileCarrier.getIspGroupByCID(mobile);
						if (MobileCarrier.CHINA_MOBILE.equals(attr)) {
							retData.put(ATTRIBUTE_EN, CHINA_MOBILE);
							retData.put(ATTRIBUTE,"移动");
						}else if(MobileCarrier.CHINA_UNICOM.equals(attr)){
							retData.put(ATTRIBUTE_EN, CHINA_UNICOM);
							retData.put(ATTRIBUTE,"联通");
						}else if(MobileCarrier.CHINA_TELECOM.equals(attr)){
							retData.put(ATTRIBUTE_EN, CHINA_TELECOM);
							retData.put(ATTRIBUTE,"电信");
						}else{
							retData.put(ATTRIBUTE,"" );
							retData.put(ATTRIBUTE_EN, "");	
						}

                    }
                 
                    String cacheCheckRes = cacheData.getCheckResult();
                    tags = bulidCacheTag(retData.get(ATTRIBUTE_EN).toString(), cacheCheckRes, tags);
                    retData.put(PROVICE, cacheData.getProvince()==null?"":cacheData.getProvince());
                    retData.put(CITY, cacheData.getCity()==null?"":cacheData.getCity());
                    retData.put(CHECK_RESULT, cacheCheckRes);
                    rets.put(Conts.KEY_RET_DATA, retData);

                    rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
                    rets.put(Conts.KEY_RET_MSG, "请求成功");
                    logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
                    logObj.setState_msg("交易成功");
                    logObj.setIncache("1");
                    logObj.setBiz_code2(cacheData.getTrade_id());

                    return rets;
                }
            }
			
			//不存在缓存，从数据源获取数据
			if (!inCache) {
				long postStart = System.currentTimeMillis();
				Map<String, Object> req_params = new HashMap<>();
				req_params.put("name", name);
				req_params.put("pid", cardNo);
				req_params.put("mobile", mobile);
				req_params.putAll(VerifyUtil.signinMap(trade_id,ppx_method,ppxin_key,ppxin_secret));
				String postResult = RequestHelper.sendPostRequest(ppxin_url,
						req_params, "UTF-8",true);
				logger.info("{} http请求耗时(ms)为：{}",prefix , System.currentTimeMillis() - postStart);
				logger.info("{} http请求结果为：{}",prefix , postResult);
				
				if (StringUtil.isEmpty(postResult) || "null".equals(postResult)) {
					logger.info("{} http请求返回结果为空" , prefix);
					logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_TIMEOUT);
					logObj.setState_msg("请求超时");					
					return rets;
				}				
				PPXinResBean ppxObj = JSONObject.parseObject(postResult, PPXinResBean.class);
				MobileLocation location = new MobileLocation();
				logger.info("{} 返回信息bean包装:{}" , prefix,JSON.toJSONString(ppxObj));
				logObj.setBiz_code1(ppxObj.getResp_serial());
				if(PPX_RESP_CODE.equals(ppxObj.getResp_code())){//调用成功					
					if(PPX_RESP_RESULT.equals(ppxObj.getResp_body().getResult())){
						if(PPX_QUERY_STATUS.equals(ppxObj.getResp_body().getMsg().getQueryStatus())){
							rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED);
							rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_FAILED.getRet_msg());
							return rets;
						}else{
							if("0".equals(ppxObj.getResp_body().getMsg().getData().getIsCertifyUser())){
								retData.put(CHECK_RESULT, "8");
							}else if("1".equals(ppxObj.getResp_body().getMsg().getData().getIsCertifyUser())){
								retData.put(CHECK_RESULT, "0");
							}else if("2".equals(ppxObj.getResp_body().getMsg().getData().getIsCertifyUser())){
								retData.put(CHECK_RESULT, "1");
							}
							logger.info("{} 本地查询归属地" , prefix);	
							MobileSrcLocVo mobileSrcLocVo = mobileSrcLocService.findByMobileNo(mobile);
							if(mobileSrcLocVo != null){
				                location.setProvince(mobileSrcLocVo.getProvince());
				                location.setCity(mobileSrcLocVo.getCity());
				                location.setIsp(mobileSrcLocVo.getAttribute());
							}else{
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
							}
							retData = parseLocatin(location, retData);
							//构建返回标签
							tags = buildTags(ppxObj.getResp_body().getMsg().getQueryStatus(),retData.get(ATTRIBUTE_EN).toString(),tags);
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
						}						
					}else{
						rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED);
						rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_FAILED.getRet_msg());
						return rets;
					}					
				}else{
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED);
					rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_FAILED.getRet_msg());
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
	private Set<String> buildTags(String queryStatus, String attributeEn,Set<String> tags) {
		tags.clear();
		if("2".equals(queryStatus)){
			tags.add(TAG_CHECK_UNFOUND);
		}else if("1".equals(queryStatus)){
			if (CHINA_MOBILE.equals(attributeEn)) {
				tags.add(TAG_CHECK_CMCC_FOUND1);
			}else if(CHINA_UNICOM.equals(attributeEn)){
				tags.add(TAG_CHECK_CUCC_FOUND1);
			}else if(CHINA_TELECOM.equals(attributeEn)){
				tags.add(TAG_CHECK_CTCC_FOUND1);
			}else {
				tags.add(TAG_CHECK_FOUND1_OTHERS);
			}
		}else{
			tags.add(Conts.TAG_TST_FAIL);
		}
		return tags;
	}
	/**
	 * @param location
	 * @param retData
	 * @return
	 */
	protected TreeMap<String, Object> parseLocatin(MobileLocation location,
			TreeMap<String, Object> retData) {

		if (location != null) {
			retData.put(PROVICE, location.getProvince());
			retData.put(CITY, location.getCity());
			String attribute = location.getIsp();
			retData.put(ATTRIBUTE, attribute);
			
			if (attribute != null && attribute.trim().length() > 0) {
				if (attribute.contains("移动")) {
					retData.put(ATTRIBUTE_EN, CHINA_MOBILE);
				}else if(attribute.contains("联通")){
					retData.put(ATTRIBUTE_EN, CHINA_UNICOM);
				}else if(attribute.contains("电信")){
					retData.put(ATTRIBUTE_EN, CHINA_TELECOM);
				}else{
					retData.put(ATTRIBUTE_EN, CHINA_OTHERS);
				}
			}else{
				retData.put(ATTRIBUTE_EN, CHINA_OTHERS);
			}
		}else{
			retData.put(PROVICE, "");
			retData.put(CITY, "");
			retData.put(ATTRIBUTE, "");
			retData.put(ATTRIBUTE_EN, CHINA_OTHERS);
		}
		return retData;
	}
	private Set<String> bulidCacheTag(String attri, String checkResult, Set<String> tags){
        Map<String, String> found1Map = tagCacheFound1Map();
        Map<String, String> found2Map = tagCacheFound2Map();
        tags.clear();
        StringBuffer tagBf = new StringBuffer();
        if(CHINA_MOBILE.equals(attri)){
            tagBf.append("check_yd_incache_");
        }else if(CHINA_UNICOM.equals(attri)){
            tagBf.append("check_lt_incache_");
        }else if(CHINA_TELECOM.equals(attri)){
            tagBf.append("check_dx_incache_");
        }else {
            tagBf.append("check_other_incache_");
        }
        if(found1Map.containsKey(checkResult)){
            tagBf.append("found1");
        }else if(found2Map.containsKey(checkResult)){
            tagBf.append("found2");
        }

        tags.add(tagBf.toString());

        return tags;
    }
}
