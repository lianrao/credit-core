package com.wanda.credit.ds.client.phjr.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @Title: MD5Utils.java
 * @Description: MD5帮助类
 * @date 2016-1-19 下午7:23:27
 * @author:hejun.shen
 * @email:shenhj15971@hundsun.com
 * @replace author:
 * @replace date:
 * @version V1.0
 */
public class MD5Utils {
	private static final char HEX_DIGITS[] = { '0', '1', '2', '3', '4', '5',
			'6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	private static MessageDigest messageDigest = null;

	static {
		try {
			messageDigest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 字节数组MD5签名
	 * 
	 * @param bytes
	 * @return
	 */
	public static String getMD5String(byte[] bytes) {
		messageDigest.update(bytes);
		return bytesToHex(messageDigest.digest());
	}

	/**
	 * 
	 * @param bytes
	 * @return
	 */
	private static String bytesToHex(byte bytes[]) {
		return bytesToHex(bytes, 0, bytes.length);
	}

	/**
	 * 
	 * @param bytes
	 * @param start
	 * @param end
	 * @return
	 */
	private static String bytesToHex(byte bytes[], int start, int end) {
		StringBuilder sb = new StringBuilder();

		for (int i = start; i < start + end; i++) {
			sb.append(byteToHex(bytes[i]));
		}

		return sb.toString();
	}

	/**
	 * 
	 * @param bt
	 * @return
	 */
	private static String byteToHex(byte bt) {
		return HEX_DIGITS[(bt & 0xf0) >> 4] + "" + HEX_DIGITS[bt & 0xf];
	}
	
	public static void main(String[] args) {
		String str = "qaz123WSX@#$";
		String md5String = getMD5String(str.getBytes());
		
		if ("94d9c130c6ed012d57d6227b7b3beb55".equalsIgnoreCase(md5String)) {
			System.out.println("一致");
		}else{
			System.out.println("不一致");
			System.out.println(md5String);
			
		}
	}

}
