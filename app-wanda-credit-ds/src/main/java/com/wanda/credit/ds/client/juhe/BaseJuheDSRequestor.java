/**   
* @Description: 请求数据源 集奥 BASE Requestor 
* @author xiaobin.hou  
* @date 2016年11月1日 下午3:22:56 
* @version V1.0   
*/
package com.wanda.credit.ds.client.juhe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URLDecoder;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.counter.GlobalCounter;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.exception.ServiceException;
import com.wanda.credit.base.util.RequestHelper;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.ds.BaseDataSourceRequestor;
import com.wanda.credit.ds.client.ji_ao.bean.BigDataLogin;
import com.wanda.credit.ds.client.ji_ao.bean.MobileInfo;
import com.wanda.credit.ds.client.ji_ao.bean.MobileLocation;
import com.wanda.credit.ds.client.ji_ao.bean.WSJiAoReqBean;
import com.wanda.credit.ds.dao.domain.jiAo.GeoMobileCheck;
import com.wanda.credit.ds.dao.iface.IAllAuthCardService;
import com.wanda.credit.ds.dao.iface.IJuheAuthCardService;
import com.wanda.credit.ds.dao.iface.jiAo.IJiAoMobileCheckService;

/**
 * @author xiaobin.hou
 *
 */
public class BaseJuheDSRequestor extends BaseDataSourceRequestor {
	
	private Logger logger = LoggerFactory.getLogger(BaseJuheDSRequestor.class);
	@Autowired
	protected  IJuheAuthCardService juheAuthCardService;	
    @Autowired
    protected IAllAuthCardService allAuthCardService;
	@Autowired
	protected IPropertyEngine propertyEngine;
	@Autowired
	private IJiAoMobileCheckService mobileService;
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
    
	protected String loginUser;
	protected String loginPaw;
	protected String method;
	private final String JUHE_TOKEN_ID_REDIS = "juhe_multi_redisID";
	public WSJiAoReqBean buildReqBean(String name,String cardNo,String mobile,String type){
		
		loginUser = propertyEngine.readById("ds_jiAo_mobile_user");
		loginPaw = propertyEngine.readById("ds_jiAo_mobile_pwd");
		method = propertyEngine.readById("ds_jiAo_method");
		
		WSJiAoReqBean reqBean = new WSJiAoReqBean();
		
		MobileInfo info = new MobileInfo();
		info.setIdno(cardNo);
		info.setName(name);
		info.setMobile(mobile);
		info.setInnerIfType(type);
		
		BigDataLogin login = new BigDataLogin();
		login.setPassword(loginPaw);
		login.setUsername(loginUser);
		
		reqBean.setMethod(method);
		reqBean.setParams(info);
		reqBean.setLogin(login);

		return reqBean;
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
			if(!"8".equals(mobilePojo.getCheckResult())){
				mobileService.add(mobilePojo);
			}
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
	 * @param personName
	 * @param enccardNo
	 * @param encMobile
	 * @return
	 */
	protected boolean saveParamIn(String name, String cardNo, String mobile,
			String getInTime, String getStatus,
			String trade_id, DataSourceLogVO logObj) {
		boolean isSave = true;
		try {
			Map<String, Object> paramIn = new HashMap<String, Object>();
			paramIn.put("name", name);
			paramIn.put("cardNo", cardNo);
			paramIn.put("mobile", mobile);
			paramIn.put("getInTime", getInTime);
			paramIn.put("getStatus", getStatus);
			long start = System.currentTimeMillis();
			DataSourceLogEngineUtil.writeParamIn(trade_id, paramIn, logObj);
			logger.info("{} 保存请求参数成功,耗时为 {}", trade_id , System.currentTimeMillis() - start	);
		} catch (Exception e) {
			logger.info("{}保存入参信息异常{}", trade_id, e.getMessage());
			isSave = false;
		}

		return isSave;
	}
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
	
	public boolean doPrint(String testFlag) {

		if ("1".equals(testFlag)) {
			return true;
		}

		return false;

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
//			checkResMap.put("7", "");
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
    
    //================add by wj 银行卡三、四要素核验连接dmp改为直连聚合============
	protected Map<String, Object> doRequest(String trade_id, String url)
			throws Exception {
		Map<String, Object> header = new HashMap<String, Object>();
		Map<String, Object> rspMap = doGetForHttpAndHttps(url, trade_id, header, null);
		logger.info("{} end request", trade_id);
		return rspMap;
	}

	private RequestConfig requestConfig = null;

	protected Map<String, Object> doGetForHttpAndHttps(String url,
			String prefix, Map<String, Object> header, Map<String, Object> body)
			throws Exception {
		CloseableHttpClient httpClient = null;
		Map<String, Object> rspMap = new HashMap<String, Object>();
		StringBuffer result = new StringBuffer();
		if (httpClient == null) {
			if (url.indexOf("https") > -1) {
				SSLContext ctx = SSLContext.getInstance("TLS");
				X509TrustManager tm = new X509TrustManager() {
					public X509Certificate[] getAcceptedIssuers() {
						return null;
					}

					public void checkClientTrusted(X509Certificate[] arg0,
							String arg1) throws CertificateException {
					}

					public void checkServerTrusted(X509Certificate[] arg0,
							String arg1) throws CertificateException {
					}
				};
				ctx.init(null, new TrustManager[] { tm }, null);
				// 指定信任密钥存储对象和连接套接字工厂
				LayeredConnectionSocketFactory sslSF = new SSLConnectionSocketFactory(
						ctx, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
				RegistryBuilder<ConnectionSocketFactory> registryBuilder = 
						RegistryBuilder.<ConnectionSocketFactory> create();
				registryBuilder.register("https", sslSF);
				Registry<ConnectionSocketFactory> registry = registryBuilder.build();
				// 设置连接管理器
				PoolingHttpClientConnectionManager connManager = 
						new PoolingHttpClientConnectionManager(registry);
				// 构建客户端
				httpClient = HttpClientBuilder.create().setConnectionManager(connManager).build();
			} else {
				httpClient = HttpClientBuilder.create().build();
			}
		}
		/** build body as params */
		URIBuilder uriBuilder = new URIBuilder(url);
		if (null != body && body.size() > 0) {
			for (Entry<String, Object> entry : body.entrySet()) {
				Object v = entry.getValue();
				String value = (v == null) ? null : v.toString();
				uriBuilder.addParameter(entry.getKey(), value);
			}
		}
		URI uri = uriBuilder.build();
		HttpGet httpGet = new HttpGet(uri);

		if (requestConfig == null) {
			requestConfig = RequestConfig
					.custom()
					.setSocketTimeout(Integer.parseInt(propertyEngine.readById("req_read_timeout")))
					.setConnectTimeout(Integer.parseInt(propertyEngine.readById("req_conn_timeout")))
					.setConnectionRequestTimeout(Integer.parseInt(propertyEngine.readById("req_getconn_timeout")))
					.build();
		}

		// requestConfig =
		// RequestConfig.custom().setSocketTimeout(100).setConnectTimeout(100).setConnectionRequestTimeout(100)
		// .build();
		httpGet.setConfig(requestConfig);
		/** build header */
		if (header != null && header.size() > 0) {
			for (Entry<String, Object> entry : header.entrySet()) {
				Object v = entry.getValue();
				String value = (v == null) ? null : v.toString();
				httpGet.setHeader(entry.getKey(), value);
			}
		}
		CloseableHttpResponse response = httpClient.execute(httpGet);
		/** check rsp status */
		rspMap.put("httpstatus", response.getStatusLine() != null ? response.getStatusLine().getStatusCode() : 200);
		url = URLDecoder.decode(url, "UTF-8");
		HttpEntity entity = response.getEntity();
		InputStream in = entity.getContent();
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
		String line = "";
		while ((line = bufferedReader.readLine()) != null) {
			result.append(line);
		}
		in.close();
		response.close();
		rspMap.put("rspstr", result.toString());
		return rspMap;
	}

	protected Map<String, String> mapObjToMapStr(Map<String, Object> mapObj){
		Map<String, String> mapStr = new HashMap<String, String>();
//		for (String string : mapObj.keySet()) {
//			mapStr.put(string, String.valueOf(mapObj.get(string)));
//		}
		mapStr.put("key", String.valueOf(mapObj.get("key")));
		mapStr.put("realname", String.valueOf(mapObj.get("name")));
//		try {
//			mapStr.put("realname", URLEncoder.encode(String.valueOf(mapObj.get("name")),"utf-8"));
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
		mapStr.put("idcard", String.valueOf(mapObj.get("cardNo")));
		mapStr.put("bankcard", String.valueOf(mapObj.get("cardId")));
		mapStr.put("mobile", String.valueOf(mapObj.get("phone")));
		mapStr.put("uorderid", String.valueOf(mapObj.get("trade_id")));
		mapStr.put("isshow", String.valueOf(mapObj.get("isshow")));
		if(mapObj.get("hphm")!=null){
			mapStr.put("hphm", String.valueOf(mapObj.get("hphm")));
		}
		if(mapObj.get("province")!=null){
			mapStr.put("province", String.valueOf(mapObj.get("province")));
		}
		if(mapObj.get("hpzl")!=null){
			mapStr.put("hpzl", String.valueOf(mapObj.get("hpzl")));
		}
		return mapStr;
	}
	protected boolean isSuccess(com.alibaba.fastjson.JSONObject rspData) {
		boolean result = false;
		logger.info("返回error_code是 {}", rspData.getString("error_code"));
		if (rspData != null && "0".equals(rspData.getString("error_code"))) {
//			logger.info("返回code是 {}", rspData.getString("error_code"));
			result = true;
		}
		return result;
	}

	protected boolean isSupport(com.alibaba.fastjson.JSONObject rspData) {
		if (rspData != null && "220702".equals(rspData.getString("error_code"))) {
			return true;
		}
		return false;
	}
	
	protected String buildTag(String trade_id, com.alibaba.fastjson.JSONObject rspData) {
		Object res = rspData.get("res");
		String resstr = null;
		if(res != null ){
			resstr = res.toString();
			if("1".equals(resstr) || "2".equals(resstr)){
			    return Conts.TAG_TST_SUCCESS;
			}
		}
		return Conts.TAG_TST_FAIL;
	}
	
	protected Map<? extends String, ? extends Object> visitBusiData(
			String trade_id, com.alibaba.fastjson.JSONObject data) {
		Map<String,Object> ret = new HashMap<String,Object>();
		Object resObj = data.get("res");
		if(resObj == null){
			logger.error("{} res 字段值非法  ,",trade_id,data.toString());
			return ret;
		}
		String message = data.getString("message");
		String res = resObj.toString();
		String respCode = res; String resMsg = message;
		if("1".equals(res)){
			respCode = "2000" ;
			resMsg = "认证一致" ;
		}else if("2".equals(res)){
			respCode = "2001";
//			resMsg = "认证不一致";
		}
		ret.put("respCode", respCode);
		ret.put("respDesc", resMsg);
		return ret;
	}
	public String getToken(String trade_id,boolean isGetNewToken){
		if(!isGetNewToken){
			try {
				String token =  GlobalCounter.getString(JUHE_TOKEN_ID_REDIS);
				if(!StringUtil.isEmpty(token)){
					return token;
				}
			} catch (ServiceException e) {
				logger.error("{} 从redis获取token失败：{}",trade_id,e.getMessage());
			}
		}
		logger.info("{} 重新获取token获取开始...",trade_id);
		String juhe_url = propertyEngine.readById("juhe_multi_token_url");
		String juhe_clent = propertyEngine.readById("juhe_multi_token_client_id");
		String juhe_clent_key = propertyEngine.readById("juhe_multi_token_client_key");
		
		Map<String, String> req_params = new HashMap<>();
		req_params.put("grant_type","client_credentials");
		req_params.put("scopes","multiple");
		req_params.put("client_id", juhe_clent);
		req_params.put("client_secret", juhe_clent_key);
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Content-Type", "application/json");
		try {
			Map<String, Object> postRsp = RequestHelper.doPostRetFull(juhe_url, null,
					headers, req_params, null, null, true);
			JSONObject rspData = JSONObject.parseObject(String.valueOf(postRsp.get("res_body_str")));
			GlobalCounter.setString(JUHE_TOKEN_ID_REDIS, rspData.getString("access_token"));
			return rspData.getString("access_token");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
}
