package com.wanda.credit.ds.client.unionpay;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.ds.BaseDataSourceRequestor;
import com.wanda.credit.ds.client.trulioo.BaseTruliooDSRequestor;
import com.wanda.credit.ds.client.unionpay.util.MD5Util;
import com.wanda.credit.ds.client.unionpay.util.RSAUtil;

public class BaseUnionpayPaintDsRequestor extends BaseDataSourceRequestor {
	
	private static Logger logger = LoggerFactory.getLogger(BaseTruliooDSRequestor.class);
	
	protected static final String privateKeyStr = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQC2jXLd6WWj+C9ixIhNarSwzOPDGgxRezHcOlQ252h+OtCqX0ncDjF2BXIT3js+0jScUNgIgMs5qkTooPZCBsfrjRVM5C8s2vonwQPiVyjyed+8uY5tWwmf94Ywv/iiXegP+wSVsSLLnH9fqZsP6rE/lomIa0bOD5AciEmLI8jYt0FONDgPerIRGHbA7K+F3FBdrW0VupvECPgWXmcf3bbLrRb30GWwLlwh6C7OaC1XjrM2SyxO5UQ3C4O3BrM+lnB46jbShWuHi1unokolRNpnF4swmuvgiF7D01GZVdFjW921jQhnQ+YbRqLcYYpKN2JfkgFyBzuRktAUG1cAIZXvAgMBAAECggEBAJqGY0wqy8mfROUy/Tmi8G6UENvOiczTHmKP0UdkXJQ1TvpYjJnEPePsOiNNQxMrNjN3T1brRpt6YLgVwD4lpUbjdrtOkAxFE+lgvdvy8YtG1LfYzhINNLl++cz8QweVu+EDF19qwMxfnYYpeENh40WzNJoQTLAShmdQighIczPVhWtiq0Vnhu9UlGCtD8wUfmD/RGNr59fKuhtDtTOr116h+suzZ3ZaKpn4pgZtL7lNm5jkvegtECutjLKl59PncfvEzNZMImz5fBmHksoSakaKkX4S8Uax8ab6QK8Lc/ilaH1e5ZeXXnqwBal6r/fVxnhr3sddWfLCuOjxpDkuvgkCgYEA2EjfCYtv3mqKLbB2PtGcLl7UStjRHFr7/wd1YfOIFeIinNHqikOPgbkmGw+i+7rgS4eGqLLSFeO3Y0wwe9FwkNlpso0h6dXE7YaIIWIo+8BDtwxi9sIY79wWI5DP68sVayHPqLCIRlMHSuC8rHMj1P+1xOSarjrN5xnumvCs2zsCgYEA2BLlGNhIz0pWipGUZrjMnHGuidIbu2S86JC8d8NxPiDEl7O2BmzUc/i+m12zBA21Fmk6L15taj+bUgKwjJiSKGQhYcyByhy+e7zrMtsqEBuGRPe8TiNRDZh5DgBYXRZRsWSvwr4pqUBVTtkjavR6TmDejVv1Y92+7SQoGvUrvN0CgYA5BHTv8jdqnhGRW+zAQMf4nX0/wgrWe9Hk+JVkXiwpALV5PAmlq4vgeevmxGeGD0zj/HVucb3akW6eGc7KRXt0ZCxuKrCfEYDZ0VZ/3n3JaHUxg21vednTGeUoORvnIGGkHYS+BtPWHjtU7QwwNiZjF4pWvqFcfxoa64YwiHQ5UwKBgDSt1qEKCiZPr221NIMexvPVW4JKcWxskQn6T5i7U56cCpIZxGUon1tu57mlTIHOfbnrCb6GvRJMU6fY9AYzJF8ommLnozUNcPtmAcmyWTz4GPIzm/tI7PftloXtW53GVUB0lc1a43laL/I7SkFHPZJiV6ALLXadl3DlgcmgwHsZAoGAHI408yNQuvaktt6xo7ZXtIi9u7KLLRUKVyYZb40pg6yEIV3K3pmzBEWIGCNTVV0zwkPVRE73IzE15vZl1eFOLjsSQXOeN+X6RfriJrwQM4nbHj0GHeCZNcD1ay6O3iceaHcr2Erz9G/mpLoRXW9pMVwbUpkiZIyXwEEHaO1Vo18=";
	protected static final String publicKeyStr = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA0Sj6GHCdYxM9FDDvrncN5MkI0WfqXq17BMZW401WfILhuI+7w8cgW1dSR9AORFLWxrGPaUMnpSjguiZlDTCinFyugPIqiLUEjxXrvlhCfelQpHRGKAWaNJ84BPww1G0EaIasNe10Yx+FRYMqZzVY+eePHobvMTFbU5cIOeYikGHVyh+C/pCVMw0CDh3hGhWW4ZuHv1VUIOIoWpo9Np+ngYGAhpUsg64VZCuwO+JCBNO9B7K5bjWeO9vOoiqhA9TcLA4fLWcKhOVz/YWiHKZGFGZ/nlmvWgtLr4iFT2qsnznUrZnCBPvrqLfyxeTpYxZuZn0NjaLcWVknHjqLKOmKSQIDAQAB";
	//参数节点名称
	public final String ACCOUNT_ID_FLAG = "account";
	public final String ORDER_ID_FLAG = "orderId";
	public final String INDEX_FLAG = "indices";
	public final String MID_FLAG = "mid";
	public final String REGNO_FLAG = "regNo";
	
	//请求银联商户画像关键字
	public final String KEY_ACCOUNT = "account";
	public final String KEY_ENCRYPT = "encrypt";
	public final String KEY_CODE = "code";
	public final String KEY_SIGN = "sign";
	public final String KEY_MESSAGE = "message";
	public final String KEY_STATUS = "status";
	public final String KEY_SMARTID = "smartId";
	public final String KEY_ORDERID = "orderId";
	//
	public final String PARSE_CODE = "parse_code";
	public final String PARSE_MSG = "parse_msg";
	
	@Autowired
	protected IPropertyEngine propertyEngine;
	
	/**
	 * 将入参信息转化成银联HTTP请求入参信息
	 * @param prefix
	 * @param account
	 * @param paramIn
	 * @return
	 * @throws Exception
	 */
	protected Map<String,String> buildReqMap(String prefix,String account,Map<String,String> paramIn) throws Exception{		
		//加密加签名
		if (paramIn == null || paramIn.isEmpty()) {
			logger.info("{} 请求参数为空" , prefix);
			return null;
		}
		boolean doPrint = "1".equals(propertyEngine.readById("sys_log_print_switch"));
	 	String reqParamJson = JSONObject.toJSONString(paramIn);
	 	if (doPrint) {
		 	logger.info("{} 请求数据转化为json为 {}" , prefix ,reqParamJson);
		}
	 	//请求参数RSA加密
	    String encReqJson = RSAUtil.encryptByPrivateKey(privateKeyStr, reqParamJson);
	    if (doPrint) {
	    	logger.info("{} 加密结果为 {}" , prefix ,encReqJson);
		}
	    // 生成sign：account+encrypt MD5方式生成签文
        StringBuilder buf = new StringBuilder(account).append(encReqJson);
        String sign = MD5Util.encrypt(buf.toString());
        logger.info("{} 签名结果为 {}" , prefix ,sign);
        // 封装map中参数：account，encrypt，sign
        Map<String, String> reqMap = new HashMap<String, String>();
        reqMap.put(KEY_ACCOUNT, account);
        reqMap.put(KEY_ENCRYPT, encReqJson);
        reqMap.put(KEY_SIGN, sign);
        logger.info("{} 签名结果为 {}" , prefix ,reqMap);
		
		return reqMap;
	}
	
	/**
	 * 解析并解密HTTP请求结果
	 * 
	 * @param prefix
	 * @param respRes
	 * @return	Map<String,String>
	 * 	parse_code=000 解析成功
	 *  parse_msg 未解析结果
	 * @throws Exception
	 */
	public Map<String,String> decResInfo(String prefix,String respRes) throws Exception{
		Map<String,String> resMap = new HashMap<String, String>();
		resMap.put(PARSE_CODE, "-999");
		resMap.put(PARSE_MSG, "解析失败");
		if (StringUtil.isEmpty(respRes)) {
			logger.info("{} 传入参数HTTP请求结果为空 {}" , prefix);
			resMap.put(PARSE_CODE, "001");
			resMap.put(PARSE_MSG, "HTTP返回结果为空");
			return resMap;
		}
		JSONObject resJsonObj = JSONObject.parseObject(respRes);
		if (resJsonObj == null) {
			logger.info("{} http返回结果 {} 解析成JSONObject失败" , prefix , respRes);
			resMap.put(PARSE_CODE, "002");
			resMap.put(PARSE_MSG, "HTTP返回结果转化成对象失败");
			return resMap;
		}
		if (!resJsonObj.containsKey(KEY_SIGN)) {
    		logger.info("{} 银联返回节点中没有sign节点 {}" , prefix ,respRes);
    		resMap.put(PARSE_CODE, "003");
			resMap.put(PARSE_MSG, "HTTP返回结果没有sign节点");
			return resMap;
		}
    	// 返回数据验签
        String responseSign = resJsonObj.getString(KEY_SIGN);
        String responseSignData = resJsonObj.getString(KEY_ACCOUNT) + resJsonObj.getString(KEY_ENCRYPT);
        boolean verify = MD5Util.encrypt(responseSignData).equalsIgnoreCase(responseSign);
        logger.info("{} 验签结果{}", prefix,verify ? "成功" : "失败");
        if (!verify) {
        	resMap.put(PARSE_CODE, "004");
			resMap.put(PARSE_MSG, "HTTP返回结果验签失败");
			return resMap;
        }
        String encrypt = resJsonObj.getString(KEY_ENCRYPT);
        if (StringUtil.isEmpty(encrypt)) {
        	// 返回报文中没有加密节点
        	logger.info("{} 返回报文中encrypt节点内容为空" , prefix);
        	resMap.put(PARSE_CODE, "005");
			resMap.put(PARSE_MSG, "HTTP返回结果没有encrypt节点");
			return resMap;
        }
        
        String resp = RSAUtil.decryptByPublicKey(publicKeyStr, encrypt);
        resMap.put(PARSE_CODE, "000");
		resMap.put(PARSE_MSG, resp);
		return resMap;
	}
}
