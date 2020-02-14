package com.wanda.credit.ds.client.jiAoDS.baseUtil;

import java.security.MessageDigest;

/**
 * 统一加密解密类
 * @author wushujia
 *
 */
public class Secret {
	public static void main(String[] args) {
		String enpry = "EF1BDCFFFB780E5CABA13292734DCFC6620CFBB267836F3E4561ED818B236F4E7840F5AF9528710EB2CDB1063EF229F86290FA873663F010124E9CD48FDAFAC9DFE48ED8B8AD145AAAB270D8169FF40BB2B57A1DD5FD6A02A28931F9C086B02B643C2BAF21700CC7C6FD6B25BA026C896A57235C47E37BB501C2D4169073722C3CDFABB032E1B98C602D51481E1AB5B38873132BA32E81F3759FC242C3B5E5E95E87B4184CF8BF3C9523ACA048E85B6BFC1640370F0984600AE9F35E29C33A6E57B72BFF128CC1B5C7EE75BE09118EE3726AC2240484DA538EF9980765812D05DE15912B8E0B8D78805550E7C68323FD";
		System.out.println("解密信息:"+decrypt("AES",enpry,"@WSX3edc3edc@WSX"));
	}
	/**
	 * 加密
	 * @param encryptionType 加密类型
	 * @param content 需要加密的内容
	 * @param encryptionKey 加密密码
	 * @return
	 */
	public static String encrypt(String encryptionType,String content, String encryptionKey) {
				
		if(encryptionType.startsWith("AES")){
    		content = AES.encrypt(content, encryptionKey) ;
    	}
		return content ;
	}
    
	/**
	 * 解密
	 * @param encryptionType 解密类型
	 * @param content 解密内容
	 * @param encryptionKey 解密密钥
	 * @return
	 */
	public static String decrypt(String encryptionType,String content, String encryptionKey) {
		if(encryptionType.startsWith("AES")){
    		content = AES.decrypt(content, encryptionKey) ;
    	}
		return content ;
	}
	/**
	 * md5加密
	 * @param source
	 * @return
	 */
	public static String md5(String source) {
	    StringBuffer sb = new StringBuffer(32); 
	    try {
	        MessageDigest md    = MessageDigest.getInstance("MD5");  
	        byte[] array        = md.digest(source.getBytes("utf-8")); 
	        for (int i = 0; i < array.length; i++) {
	            sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
	        }
	    } catch (Exception e) {
	        e.printStackTrace() ;
	        return null;  
	    }
	    return sb.toString();  
	}
}
