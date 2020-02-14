package com.wanda.credit.ds.client.yixin;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.interfaces.RSAPublicKey;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSONObject;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.LoggingEvent;
import com.wanda.credit.ds.BaseDataSourceRequestor;

public abstract class BaseYiXinDataSourceRequestor extends BaseDataSourceRequestor {
	private final Logger logger = LoggerFactory.getLogger(BaseYiXinDataSourceRequestor.class);

	// 查询成功返回码
	protected final String SUCC_CODE = "0000";
	protected final String SUCC_CODE2 = "0001";

	// 默认证件类型:身份证
	protected final String DEFAULT_IDTYPE = "101";
   // 默认查询原因
	protected final String DEFAULT_QUERYREASON = "10";

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	protected final String TRADE_ID = "trade_id";

	/**用户id*/
	protected String  userId;
    /**rc4 秘钥*/
    protected String  key;

	protected String url;

	// 公私钥的路径等信息
	private static String publicKeyPath = "/depends/ds/yixin/ZC_PublicKey_V2.crt";

	protected static RSAPublicKey rsaPublicKey;

	/**
	 * 初始化连接资源
	 * 
	 * @throws Exception
	 */
	public static void init() throws Exception {
		if (rsaPublicKey == null) {
			rsaPublicKey = RSA_1024_V2.gainRSAPublicKeyFromCrtFile(getResPathFromCP(publicKeyPath));
		}
	}

	/**
	 * 发送请求
	 * */
	protected JSONObject executeClient(Map<String, Object> context,String yixin_url) throws Exception {
		String tradeId = (String)context.get(TRADE_ID);
		// 发送请求
		RestTemplate restTemplate = new RestTemplate();
		MultiValueMap<String, String> paraMap  = buildRequestParams(context);
	 	String rspStrData = restTemplate.postForObject(yixin_url, paraMap,String.class);
	 	if(StringUtils.isBlank(rspStrData))throw new Exception("error:返回数据为空");
	 	JSONObject rspJsnData = JSONObject.parseObject(rspStrData);	 	 	
	 	if (rspJsnData.containsKey("errorcode") && SUCC_CODE.equals(rspJsnData.getString("errorcode"))) {
			/**开始解密data数据*/
			String decryptResult = RC4_128_V2.decode(rspJsnData.getString("data"), key);
			decryptResult = URLDecoder.decode(decryptResult, "utf-8");
			rspJsnData.put("data", JSONObject.parse(decryptResult));
		}
	 	
		DataSourceLogEngineUtil.writeLog2LogSys(
				new LoggingEvent(tradeId, rspJsnData.toJSONString(), new String[]{tradeId}));
	 	return rspJsnData;
	}

	/**
	 * 构建加密后的请求参数
	 * @throws UnsupportedEncodingException */
	private MultiValueMap<String, String> buildRequestParams(Map<String, Object> context) throws UnsupportedEncodingException {
		String tradeId = (String)context.get(TRADE_ID);
		MultiValueMap<String, String> paraMap = new LinkedMultiValueMap<String, String>();
		String encryptedUserID = RSA_1024_V2.encodeByPublicKey(rsaPublicKey, userId);
		paraMap.add("userid", encryptedUserID);
		JSONObject jsnData = buildRequestBody(context);
        /**记录请求参数*/
		DataSourceLogEngineUtil.writeLog2LogSys(
				new LoggingEvent(tradeId, jsnData.toString(), new String[]{tradeId}));

		String encryptedParams = RC4_128_V2.encode(URLEncoder.encode(jsnData.toString(), "utf-8"), key);
		paraMap.add("params", encryptedParams);	
		return paraMap;
	}

	/**交易是否成功*/
	protected boolean isSuccessful(JSONObject rspJsnData){
		if (rspJsnData.containsKey("errorcode") && 
				(SUCC_CODE.equals(rspJsnData.getString("errorcode")) || 
				 SUCC_CODE2.equals(rspJsnData.getString("errorcode")))) {
			return true;
		}
        return false;  
	}
	
	protected final static Map<String, String> errorCode = new HashMap<String, String>();
	static {
		errorCode.put("0000", "查询成功");
		errorCode.put("0001", "查询成功无结果");
		errorCode.put("0002", "查询服务器端的缓存没有匹配的结果");
		errorCode.put("4001", "用户名解密出现异常");
		errorCode.put("4002", "参数解密出现异常");
		errorCode.put("4005", "请求的接口不存在");
		errorCode.put("4006", "字符集解码失败");
		errorCode.put("4008", "输入的参数部分格式不正确，需json格式");
		errorCode.put("4009", "用户调用参数不正确");
		errorCode.put("4010", "身份证号不合法");
		errorCode.put("4011", "参数长度不正确");
		errorCode.put("4012", "传入的参数存在空值");
		errorCode.put("4101", "用户信息不存在");
		errorCode.put("4102", "用户已停用");
		errorCode.put("4103", "用户所在机构信息不存在");
		errorCode.put("4104", "用户所在机构已停用");
		
	}
	
	
	/**
	 * 构建jsonObject格式业务数据信息 
	 * */
	protected abstract JSONObject buildRequestBody(Map<String, Object> ctx);

	
	public String getPublicKeyPath() {
		return publicKeyPath;
	}

	public void setPublicKeyPath(String publicKeyPath) {
		BaseYiXinDataSourceRequestor.publicKeyPath = publicKeyPath;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public static String getResPathFromCP(String relatedPath) {
		URL url = BaseYiXinDataSourceRequestor.class.getResource(relatedPath);
		if (url == null) {
			throw new RuntimeException("资源文件[" + relatedPath + "]不存在");
		}
		return url.getPath();
	}
	
}
