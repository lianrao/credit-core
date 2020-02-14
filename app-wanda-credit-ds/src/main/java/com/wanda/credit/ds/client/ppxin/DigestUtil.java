package com.wanda.credit.ds.client.ppxin;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.codec.digest.DigestUtils;

public class DigestUtil {
	public static final String MD5 = "MD5";
	public static final String SHA = "SHA";
	public static final String UTF8 = "UTF-8";

	public static  String digestMD5(Map<String, String> dataMap,
			String appSecret) {
		return digest(dataMap, appSecret, MD5);
	}
	public static  String digestSHA(Map<String, String> dataMap,
			String appSecret) {
		return digest(dataMap, appSecret, SHA);
	}
	private static  String digest(Map<String, String> dataMap,
			String appSecret, String degestType) {
		if (dataMap == null) {
			throw new IllegalArgumentException("数据不能为空");
		}
		if (appSecret == null) {
			throw new IllegalArgumentException("安全校验码数据不能为空");
		}
		if (degestType == null) {
			throw new IllegalArgumentException("摘要算法不能为空");
		}
		Map<String, String> headMap = new TreeMap<String, String>(dataMap);
		String str = serialMapToString(appSecret, headMap);
		try {
			if (degestType.equals(MD5)) {
				return DigestUtils.md5Hex(str.getBytes(UTF8));
			} else {
				return DigestUtils.shaHex(str.getBytes(UTF8));
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("签名失败", e);
		}
	}

	private static String serialMapToString(String appSecret,
			Map<String, String> headMap) {
		StringBuffer query = new StringBuffer(appSecret);
		Set<Entry<String, String>> paramSet = headMap.entrySet();
		for (Entry<String, String> param : paramSet) {
			query.append(param.getKey()).append(param.getValue());
		}
		query.append(appSecret);
		return query.toString();
	}
}
