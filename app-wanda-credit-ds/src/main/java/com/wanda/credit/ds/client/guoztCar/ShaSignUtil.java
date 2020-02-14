package com.wanda.credit.ds.client.guoztCar;

import com.wanda.credit.base.util.CarSha1;


/**
 * 使用sha1加密算法对参数进行加密
 * @author zhangxf
 *
 */
public class ShaSignUtil {

	public static String buyerSign(String access_token, String productid, String security, String timestamp) {
		String params = "access_token=" + access_token + "&productid=" + productid + 
				"&security=" + security + "&timestamp=" + timestamp;
		return CarSha1.hex_sha1(params);
	}
	
	public static String sellerSign(String productid, String security, String nonce, String timestamp, String appsecret) {
		String params = "appsecret="+ appsecret.toUpperCase() + "&nonce=" + nonce + "&productid=" + productid + 
				"&security=" + security + "&timestamp=" + timestamp;
		return CarSha1.hex_sha1(params);
	}
}
