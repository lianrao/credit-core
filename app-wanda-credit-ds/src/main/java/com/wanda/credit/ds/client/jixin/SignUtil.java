package com.wanda.credit.ds.client.jixin;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;


/**
 * 签名工具类
 * 
 * @className SignUtil
 * @Description
 * @author xuguiyi
 * @contact
 * @date 2016-6-7 下午11:11:00
 */
public class SignUtil {
	

	/**
	 * 根据map key升序排序
	 * @param sortedParams
	 * @return
	 */
	public static String getSign(Map<String, String> sortedParams,String signkey) throws Exception {
		
		StringBuffer signSrc = new StringBuffer();
		List<String> keys = new ArrayList<String>(sortedParams.keySet());
		Collections.sort(keys);
		int index = 0;
		for (int i = 0; i < keys.size(); i++) {
			String key = keys.get(i);
			String value = sortedParams.get(key);
			if (key != null && !"".equals(key) && value != null && !"sign".equals(key)) {
				signSrc.append(key + "=" + value);
				index++;
			}
		}
		
		String sign = DigestUtils.md5Hex(signSrc.toString() + signkey);
		
		return sign;
		
	}
	
	
	public static void main(String[] args) throws Exception {
		//{"transcode":"001","merchno":"001","dsorderid":"2016060700","regno":"222222","compayname":"阿里巴巴","frname":"马云","version":"0100","ordersn":"201511130000003"}
	
//		String json = "{\"transcode\":\"001\",\"merchno\":\"000000000000000\",\"dsorderid\":\"2016060700\",\"regno\":\"222222\",\"compayname\":\"阿里巴巴\",\"frname\":\"马云\",\"version\":\"0100\",\"ordersn\":\"201511130000003\"}";
//	
//		Map<String,String> map = mapper.readValue(json, Map.class);
//		
//		System.out.println(getSign(map,"c26c44"));
	}
	
	
	

}
