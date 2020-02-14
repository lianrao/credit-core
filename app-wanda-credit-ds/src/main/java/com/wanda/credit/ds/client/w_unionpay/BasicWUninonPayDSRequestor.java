/**   
* @Description: W项目-
* @author xiaobin.hou  
* @date 2016年8月5日 上午10:39:21 
* @version V1.0   
*/
package com.wanda.credit.ds.client.w_unionpay;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.dubbo.common.utils.LogUtil;
import com.alibaba.fastjson.JSONObject;
import com.unionpay.udsp.sdk.CertUtil;
import com.unionpay.udsp.sdk.SDKConstants;
import com.unionpay.udsp.sdk.SDKUtil;
import com.unionpay.udsp.sdk.SecureUtil;
import com.wanda.credit.ds.BaseDataSourceRequestor;

/**
 * @author xiaobin.hou
 *
 */
public class BasicWUninonPayDSRequestor extends BaseDataSourceRequestor {
	
	private final static Logger logger = LoggerFactory
			.getLogger(BasicWUninonPayDSRequestor.class);
	
	private String appId;
	private String appKey;


	/**
	 * 初始化公共测试数据
	 */
	public Map<String, String> initCommonRequestData(String orderId,String reqSeq,String reqTime) {
		
		Map<String, String> data = new HashMap<String, String>();	

		// 请求参数
		data.put(SDKConstants.key_orderId, orderId); // 订单号
		data.put(SDKConstants.key_reqSeq, reqSeq); // 流水号，一个订单可能会发多次请求
		data.put(SDKConstants.key_reqTime, reqTime);
		data.put(SDKConstants.key_encoding, SDKConstants.encoding_UTF_8);
		data.put(SDKConstants.key_certId, CertUtil.getSignCertId());
		data.put(SDKConstants.key_version, SDKConstants.version_1);
		data.put(SDKConstants.key_signMethod, SDKConstants.signMethod_RSA_SHA);
		data.put(SDKConstants.key_appId, appId);
		data.put(SDKConstants.key_appKey, appKey);
		

		return data;
	}
	
	/**
	 * 
	 * @param reqMap 请求报文公共数据
	 * @param reqDataMap	请求报文子域
	 * @return
	 */
    public String getReqData(Map<String, String> reqMap, Map<String, String> reqDataMap) {
        
        JSONObject reqDataJson = (JSONObject) JSONObject.toJSON(reqDataMap);
        //请求数据reqData域加密
        String encryptJson = SecureUtil.encryptData(reqDataJson.toString(), SDKConstants.encoding_UTF_8, CertUtil.getEncryptCertPublicKey()); //使用UDSP提供的公钥加密
        reqMap.put(SDKConstants.key_reqData, encryptJson);
        //报文签名
        SDKUtil.sign(reqMap, SDKConstants.encoding_UTF_8);

        //转为JSON格式
        JSONObject json = (JSONObject) JSONObject.toJSON(reqMap);
        String jsonData = json.toJSONString();
        
        return jsonData;
    }
    
	/**
	 * 
	 * @param reqMap 请求报文公共数据
	 * @param reqDataMap	请求报文子域
	 * @return
	 */
    public Map<String, String> getReqDataMap(Map<String, String> reqMap, Map<String, String> reqDataMap) {
        
        JSONObject reqDataJson = (JSONObject) JSONObject.toJSON(reqDataMap);
        String requestJson = JSONObject.toJSONString(reqDataMap);
        //请求数据reqData域加密
        String encryptJson = SecureUtil.encryptData(requestJson, SDKConstants.encoding_UTF_8, CertUtil.getEncryptCertPublicKey()); //使用UDSP提供的公钥加密
        reqMap.put(SDKConstants.key_reqData, encryptJson);
        //报文签名
//        SDKUtil.sign(reqMap, SDKConstants.encoding_UTF_8);
        sign(reqMap, SDKConstants.encoding_UTF_8);

        return reqMap;
    }
    
    
    public  boolean sign(Map<String, String> data, String encoding) {
    	logger.info("签名处理开始.");
		if (isEmpty(encoding)) {
			encoding = "UTF-8";
		}
		data.put("certId", CertUtil.getSignCertId());

		String stringData = coverMap2String(data);

		logger.info("报文签名之前的字符串(不含signature域)=[" + stringData + "]");

		byte[] byteSign = null;
		String stringSign = null;
		try {
			byte[] signDigest = SecureUtil.sha1X16(stringData, encoding);
			logger.info("SHA1->16进制转换后的摘要=[" + new String(signDigest)
					+ "]");

			byteSign = SecureUtil.base64Encode(SecureUtil.signBySoft(
					CertUtil.getSignCertPrivateKey(), signDigest));

			stringSign = new String(byteSign);
			logger.info("报文签名之后的字符串=[" + stringSign + "]");

			data.put("signature", stringSign);
			logger.info("签名处理结束.");
			return true;
		} catch (Exception e) {
			logger.info("签名异常", e);
		}
		return false;
	}
    
    
    public  String coverMap2String(Map<String, String> data) {
		TreeMap tree = new TreeMap();
		Iterator it = data.entrySet().iterator();
		while (true) {
			Map.Entry en = null;
			if (!(it.hasNext()))
				break;
			en = (Map.Entry) it.next();
			if (!("signature".equals(((String) en.getKey()).trim())))
				;
			tree.put(en.getKey(), en.getValue());
		}

		it = tree.entrySet().iterator();
		StringBuffer sf = new StringBuffer();
		while (it.hasNext()) {
			Map.Entry en = (Map.Entry) it.next();
			sf.append(((String) en.getKey()) + "=" + ((String) en.getValue())
					+ "&");
		}

		return sf.substring(0, sf.length() - 1);
	}
    
    public  boolean isEmpty(String s) {
		return ((s == null) || ("".equals(s.trim())));
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}

}
