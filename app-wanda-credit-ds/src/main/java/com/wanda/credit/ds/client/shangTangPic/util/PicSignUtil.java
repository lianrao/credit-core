package com.wanda.credit.ds.client.shangTangPic.util;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Formatter;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class PicSignUtil {
	public static void main(String[] args) {
		
		String API_KEY = "abcdefg"; 
		String API_SECRET = "1234567890";
		String nonce = "86cb646a267c4602913f2034bce0cea4";
		String timeStamp = "1471924244823";
		
		String genAuthorization = genAuthorization(API_KEY, API_SECRET, timeStamp, nonce);
		System.out.println(genAuthorization);
		
		if ("key=abcdefg,timestamp=1471924244823,nonce=86cb646a267c4602913f2034bce0cea4,signature=eea4300393cd859421fa8eb074781df93ca95d120e9ed0b7b4a92b4537fbccd1".equals(genAuthorization)) {
			System.out.println("一致");
		}else{
			System.out.println("不一致");
		}
	}

	public static String genAuthorization(String apiKey, String apiSecret,
			String timeStamp, String nonce) {
		String authStr = null;
		try {
			// 获取字符串
			String joinStr = genjoinstr(apiKey, timeStamp, nonce);
			// 加密并转16进制
			String encJoinStr = genEncryptStr(joinStr, apiSecret);
			// 拼接成Authorization
			StringBuffer authBf = new StringBuffer();
			authBf.append("key=").append(apiKey).append(",timestamp=")
					.append(timeStamp).append(",nonce=").append(nonce)
					.append(",signature=").append(encJoinStr);
			authStr = authBf.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return authStr;
	}

	/**
	 * 获取nonce信息
	 * 
	 * @return
	 */
	public static synchronized String getNonce() {
		UUID uuid = UUID.randomUUID();
		return uuid.toString().replace("-", "");
	}

	/**
	 * 字符串按照首字母ASCII码排序并拼接成字符串
	 * 
	 * @param paramsIn
	 * @return
	 */
	public static String genjoinstr(String... paramsIn) {
		String join_str = null;
		if (paramsIn != null && paramsIn.length > 0) {
			ArrayList<String> beforesort = new ArrayList<String>();
			for (int i = 0; i < paramsIn.length; i++) {
				beforesort.add(paramsIn[i]);
			}
			Collections.sort(beforesort);
//			Collections.sort(beforesort, new Comparator<String>() {
//				@Override
//				public int compare(String o1, String o2) {
//					if (o1 == null || o2 == null) {
//						return -1;
//					}
//					char ch1 = o1.charAt(0);
//					int value1 = Integer.valueOf(ch1);
//					char ch2 = o2.charAt(0);
//					int value2 = Integer.valueOf(ch2);
//					if (value1 >= value2) {
//						return 1;
//					}
//					return 0;
//				}
//			});
			StringBuffer aftersort = new StringBuffer();
			for (int i = 0; i < beforesort.size(); i++) {
				aftersort.append(beforesort.get(i));
			}

			join_str = aftersort.toString();
		}
		return join_str;
	}

	public static String genEncryptStr(String join_str, String API_SECRET)
			throws NoSuchAlgorithmException, InvalidKeyException {

		Key sk = new SecretKeySpec(API_SECRET.getBytes(), "HmacSHA256");
		Mac mac = Mac.getInstance(sk.getAlgorithm());
		mac.init(sk);
		final byte[] hmac = mac.doFinal(join_str.getBytes());// 完成hamc-sha256签名
		StringBuilder sb = new StringBuilder(hmac.length * 2);
		Formatter formatter = new Formatter(sb);
		for (byte b : hmac) {
			formatter.format("%02x", b);
		}
		return sb.toString();// 完成16进制编码
	}
}
