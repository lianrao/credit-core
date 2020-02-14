package com.wanda.credit.ds.client.jiAoDS;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.apache.http.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.thinkive.base.util.UUID;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.counter.GlobalCounter;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.exception.ServiceException;
import com.wanda.credit.base.util.RequestHelper;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.ds.BaseDataSourceRequestor;
import com.wanda.credit.ds.client.jiAoDS.baseUtil.Secret;
import com.wanda.credit.ds.client.jiAoDS.beans.TokenBean;
import com.wanda.credit.ds.client.ji_ao.bean.MobileLocation;
import com.wanda.credit.ds.client.ji_ao.bean.WSJiAoResBean;
import com.wanda.credit.ds.dao.domain.jiAo.GeoMobileCheck;
import com.wanda.credit.ds.dao.iface.jiAo.IJiAoMobileCheckService;

public class BaseJiaoDs extends BaseDataSourceRequestor{
	private Logger logger = LoggerFactory.getLogger(BaseJiaoDs.class);
	@Autowired
	private IPropertyEngine propertyEngine;
	@Autowired
	private IJiAoMobileCheckService mobileService;
	
	public final String TOKEN_ID_REDIS = "jiaoai_mobile_redisID";
	//号码归属地-省
	public final static String PROVICE = "province";
	//号码归属地-市
	public final static String CITY = "city";
	//号码所属运营商-中文
	public final static String ATTRIBUTE = "attribute";
	//号码所属运营商-英文缩写
	public final static String ATTRIBUTE_EN = "attribute_en";
	//三维验证结果
	public final static String CHECK_RESULT = "checkResult";
	//在网时长
	public final static String MOBILE_IN_TIME = "inTime";
	//手机号码状态
	public final static String MOBILE_STATUS = "mobileState";
	//常量-三大运营商缩写
	public final static String CHINA_MOBILE = "CMCC";
	public final static String CHINA_UNICOM = "CUCC";
	public final static String CHINA_TELECOM = "CTCC";
	public final static String CHINA_OTHERS = "others";
	public Map<String, String> inTimeMap = null;
	public Map<String, String> statusMap = null;
	public Map<String, String> checkResMap = null;
	
	public Map<String, String> tagFound1Map = null;
	public Map<String, String> tagFound2Map = null;
	
	public Map<String, String> tagCacheFound1Map = null;
    public Map<String, String> tagCacheFound2Map = null;
	//------------------------获取集奥token及校验数据信息------------------------------------------------------
	private String server = "http://yz.geotmtai.com" ; // http://yz.geotmt.com、https://yz.geotmt.com
	private int encrypted = 1 ; // 是否加密传输
	private String encryptionType = "AES" ; // AES(秘钥长度不固定)、DES(秘钥长度8)、DESede(秘钥长度24)
	private String encryptionKey = "glad@geo6688" ; // 加密类型和加密秘钥向GEO索取(如果是获取数据的时候传的是RSA那么这里自己定义即可)

	private String username = "gladtrust" ; // 账户向GEO申请开通
	private String password = "GLAD@geo0320" ;
	protected String uno = "200554" ;
	private int dsign = 0 ; // 是否进行数字签名 1是0否
	public String getToken(String trade_id,boolean isGetNewToken) throws ParseException, IOException{//isGetNewToken=true重新获取token
		if(!isGetNewToken){
			try {
				String token =  GlobalCounter.getString(TOKEN_ID_REDIS);
				if(!StringUtil.isEmpty(token)){
					return token;
				}
			} catch (ServiceException e) {
				logger.error("{} 从redis获取token失败：{}",trade_id,e.getMessage());
			}
		}
		logger.info("{} 重新获取token获取开始...",trade_id);
		String path = server+"/civp/getview/api/o/login" ;
		// 加密用户名密码
		String eUsername = username ;  
		String ePassword = password ;
		String eDsign = dsign+"" ;
		if(encrypted == 1){
			eUsername = Secret.encrypt(encryptionType,username, encryptionKey);
			ePassword = Secret.encrypt(encryptionType,password, encryptionKey);
			eDsign = Secret.encrypt(encryptionType,dsign+"", encryptionKey);
		}
		String params = "username="+eUsername+"&password="+ePassword+"&uno="+uno+"&encrypted="+encrypted+"&dsign="+eDsign;
		Map<String, Object> rspMsg = RequestHelper.doGetRetFull(path+"?"+params, null, null, true,null,
                "UTF-8");
		String tokenBean = String.valueOf(rspMsg.get("res_body_str"));
		if (StringUtil.isEmpty(tokenBean) || "null".equals(tokenBean)) {
			logger.error("{} token获取失败,返回信息为空",trade_id);
			return "";
		}
		TokenBean token_bean = JSONObject.parseObject(Secret.decrypt(encryptionType,tokenBean, encryptionKey), TokenBean.class);
		if("200".equals(token_bean.getCode())){
			try {
				GlobalCounter.setString(TOKEN_ID_REDIS, token_bean.getTokenId());
			} catch (ServiceException e) {
				logger.error("{} token存储redis失败：{}",trade_id,e.getMessage());
			}
			return token_bean.getTokenId();
		}	
		return "";
	}
	public WSJiAoResBean getData(String trade_id,String url,String params) throws ParseException, IOException{
		Map<String, Object> rspMsg = RequestHelper.doGetRetFull(url+"?"+params, null, null, true,null,
                "UTF-8");
		String jiaoBean = String.valueOf(rspMsg.get("res_body_str"));
		logger.error("{} 集奥返回信息:{}",trade_id,Secret.decrypt(encryptionType,jiaoBean, 
				encryptionKey));
		if (StringUtil.isEmpty(jiaoBean) || "null".equals(jiaoBean)) {
			logger.error("{} 集奥信息获取失败,返回信息为空",trade_id);
			return null;
		}
		return JSONObject.parseObject(Secret.decrypt(encryptionType,jiaoBean, 
				encryptionKey), WSJiAoResBean.class);
	}
	public String getAutoCode(){
		return StringUtil.formatDate(new Date(), "YYYYMMDDHH")+uno+UUID.randomUUID().toString().replace("-", "");
	}
	//------------------------获取集奥token及校验数据信息------------------------------------------------------
	/**
	 * 初始化数据源返回的初始化对象 Map<String,Object>
	 * @return
	 */
	protected Map<String, Object> initRets(){		
		Map<String, Object> rets = new HashMap<String, Object>();
		rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED);
		rets.put(Conts.KEY_RET_MSG, "交易失败");		
		return rets;		
	}
	/**
	 * 构建集奥请求入参
	 * @param trade_id
	 * @param params
	 * @param isGetNewToken
	 * @return
	 * @throws ParseException
	 * @throws IOException
	 */
	public String buildReqParam(String trade_id,Map<String,Object> params,boolean isGetNewToken)
			throws ParseException, IOException{
		String tokenId = getToken(trade_id,isGetNewToken);
		StringBuilder sb = new StringBuilder() ;
		if (params != null) {
			Set<String> set = params.keySet() ;
			for (String k : set) {
				String value = (String)params.get(k) ;
				if(encrypted == 1){
					value = Secret.encrypt(encryptionType,value, encryptionKey)  ; // 加密
				}
				sb.append(k).append("=").append(value).append("&") ;
			}
		}
		return sb.toString() + "&tokenId=" + tokenId;
	}
	public boolean isSuccess(String trade_id,WSJiAoResBean msgResObj,
			DataSourceLogVO logObj,Map<String,Object> req_param,String url) throws ParseException, IOException{
		if (msgResObj==null) {
			return false;
		}
		if("-100".equals(msgResObj.getCode()) || "-200".equals(msgResObj.getCode()) || "-300".equals(msgResObj.getCode())){
			logger.info("{} token已失效返回错误码为 {},重新获取token" , trade_id , msgResObj.getCode());
			req_param.put("authCode", getAutoCode());
			msgResObj = getData(trade_id,url,buildReqParam(trade_id,req_param,true));
		}
		logObj.setBiz_code1(String.valueOf(req_param.get("authCode")));
		logObj.setBiz_code2(msgResObj.getCode());
		logObj.setState_msg(msgResObj.getMsg());
		if(!"200".equals(msgResObj.getCode())){
			logger.info("{} 返回结果错误:{}" , trade_id , msgResObj.getCode());
			return false;
		}
		logger.info("{} 集奥返回结果成功" , trade_id);
		return true;
	}
	public Map<String,String> getInTimeMap(){		
		if (inTimeMap == null) {
			inTimeMap = new HashMap<String, String>();
			inTimeMap.put("03", "0");
			inTimeMap.put("04", "1");
			inTimeMap.put("1", "2");
			inTimeMap.put("2", "3");
			inTimeMap.put("3", "4");
		}		
		return inTimeMap;
	}
	
	public Map<String,String> getStatusMap(){
		
		if (statusMap == null) {
			statusMap = new HashMap<String, String>();
			statusMap.put("0", "0");
			statusMap.put("1", "1");
			statusMap.put("2", "2");
			statusMap.put("3", "3");
			statusMap.put("4", "4");
		}
		
		return statusMap;
	}
	
	public Map<String, String> getCheckResMap(){
		
		if (checkResMap == null) {
			checkResMap = new HashMap<String, String>();
			checkResMap.put("0", "0");
			checkResMap.put("1", "1");
			checkResMap.put("4", "2");
			checkResMap.put("5", "3");
			checkResMap.put("6", "4");
			checkResMap.put("2", "5");
			checkResMap.put("3", "6");
		}
		
		return checkResMap;
	}
	
	public Map<String,String> tagFound1Map(){
		if (tagFound1Map == null) {
			tagFound1Map = new HashMap<String, String>();
			tagFound1Map.put("0","三维验证一致");
			tagFound1Map.put("1","三维验证不一致");
			tagFound1Map.put("4","手机号身份证号验证一致；手机号姓名验证不一致");
			tagFound1Map.put("5","手机号身份证号验证不一致，手机号姓名验证一致");
		}		
		return tagFound1Map;
	}
	
	public Map<String,String> tagFound2Map(){
		if (tagFound2Map == null) {
			tagFound2Map = new HashMap<String, String>();
			tagFound2Map.put("6", "手机号证件类型不匹配，不再进行验证");
			tagFound2Map.put("2", "手机号身份证号验证一致；手机号姓名验证结果未知");
			tagFound2Map.put("3", "手机号身份证号验证不一致；手机号姓名验证结果未知");
		}		
		return tagFound2Map;
	}
	/**
	 * @param name
	 * @param enccardNo
	 * @param encMobile
	 * @param location
	 * @param retData
	 * @return
	 */
	protected GeoMobileCheck parseToSave(String tradeId,String name, String enccardNo,
			String encMobile, MobileLocation location,
			TreeMap<String, Object> retData) {
		GeoMobileCheck mobilePojo = new GeoMobileCheck();
		Date nowTime = new Date();
		try{
			mobilePojo.setTrade_id(tradeId);
			mobilePojo.setName(name);
			mobilePojo.setCardNo(enccardNo);
			mobilePojo.setMobileNo(encMobile);
			if (location != null) {
				mobilePojo.setProvince(location.getProvince());
				mobilePojo.setCity(location.getCity());
				mobilePojo.setAttribute(location.getIsp());
			}
			
			if (retData.containsKey(CHECK_RESULT)) {
				mobilePojo.setCheckResult(retData.get(CHECK_RESULT).toString());
			}
			if (retData.containsKey(MOBILE_IN_TIME)) {
				mobilePojo.setIntime(retData.get(MOBILE_IN_TIME).toString());
			}
			if (retData.containsKey(MOBILE_STATUS)) {
				mobilePojo.setMobileState(retData.get(MOBILE_STATUS).toString());
			}
			mobilePojo.setCreate_time(nowTime);
			mobilePojo.setUpdate_time(nowTime);
			mobileService.add(mobilePojo);
		}catch(Exception e){
			logger.info("{} 信息保存数据库异常" , tradeId);
		}
		
		return mobilePojo;
	}
	protected GeoMobileCheck isIncache(String name, String cardNo, String mobile, long cacheTime, String prefix){
        GeoMobileCheck findGeoMobileCheck = mobileService.findGeoMobileCheck(name, cardNo, mobile);

        if(findGeoMobileCheck != null){
            if(StringUtils.isEmpty(findGeoMobileCheck.getCheckResult())){
                logger.info("{} 缓存中没有对应数据", prefix);
                return null;
            }
            long updateTime = findGeoMobileCheck.getUpdate_time().getTime();
            if(cacheTime > (System.currentTimeMillis() - updateTime) / 1000){
                return findGeoMobileCheck;
            }else {
                logger.info("{} 缓存中有数据，但不在缓存有效期", prefix);
                return null;
            }
        }else {
            logger.info("{} 缓存中没有对应数据", prefix);
            return null;
        }
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
	public Map<String,String> tagCacheFound1Map(){
        if (tagCacheFound1Map == null) {
            tagCacheFound1Map = new HashMap<String, String>();
            tagCacheFound1Map.put("0","三维验证一致");
            tagCacheFound1Map.put("1","三维验证不一致");
            tagCacheFound1Map.put("2","手机号身份证号验证一致；手机号姓名验证不一致");
            tagCacheFound1Map.put("3","手机号身份证号验证不一致，手机号姓名验证一致");
			tagCacheFound1Map.put("99","手机号T-1月前已离网");
        }

        return tagCacheFound1Map;
    }

    public Map<String,String> tagCacheFound2Map(){
        if (tagCacheFound2Map == null) {
            tagCacheFound2Map = new HashMap<String, String>();
            tagCacheFound2Map.put("4", "手机号证件类型不匹配，不再进行验证");
            tagCacheFound2Map.put("5", "手机号身份证号验证一致；手机号姓名验证结果未知");
            tagCacheFound2Map.put("6", "手机号身份证号验证不一致；手机号姓名验证结果未知");
        }

        return tagCacheFound2Map;
    }
	public static void main(String[] args) throws ParseException, IOException {
		String bean = "{\"code\":\"200\",\"data\":{\"ISPNUM\":{\"province\":\"上海\",\"city\":\"上海\",\"isp\":\"移动\"},\"RSL\":[{\"RS\":{\"code\":\"0\",\"desc\":\"三维验证一致\"},\"IFT\":\"B7\"}],\"ECL\":[]},\"msg\":\"成功\"}";
		WSJiAoResBean ws = JSONObject.parseObject(bean, WSJiAoResBean.class);
		System.out.println("ws"+ws);
	}
}
