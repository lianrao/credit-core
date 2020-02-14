package com.wanda.credit.ds.client.shangtang;


import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class GenSignUtil {
	public static void main(String[] args) {
		String timestamp = "1502270244064";
		char ch = timestamp.charAt(1);
		int value=Integer.valueOf(ch);
		System.out.println("ASCII码值:"+value);
		String api_key = "06deeb41bdb94e1ba3b7ee9d019f0396";
		String api_sec = "336ce6b0f9ae41d5bd24a06bd3b3fb21";
//		System.out.println("获得结果信息:"+result);
//		System.out.println("获取加签信息:"+HMACSHA256(result.getBytes(),api_sec.getBytes()));
//		System.out.println("获取最终信息:"+genauthorization(api_key,api_sec));
	}
	/**
	 * 加签数据包装
	 * */
	public static String genauthorization(String api_key,String api_sec){
		String timestamp = System.currentTimeMillis() + "";
		String nonce = getUUID();
		String result = genjoinstr(timestamp,nonce,api_key);
		String signature = HMACSHA256(result.getBytes(),api_sec.getBytes());
	    String authorization = "key=" + api_key 
	                 +",timestamp=" + timestamp 
	                     +",nonce=" + nonce 
	                 +",signature=" + signature;
	    return authorization;
	}
	/**
	 * HMAC加签
	 * */
	public static String HMACSHA256(byte[] data, byte[] key) 
	{
	      try  {
	         SecretKeySpec signingKey = new SecretKeySpec(key, "HmacSHA256");
	         Mac mac = Mac.getInstance("HmacSHA256");
	         mac.init(signingKey);
	         return byte2hex(mac.doFinal(data));
	      } catch (NoSuchAlgorithmException e) {
	         e.printStackTrace();
	      } catch (InvalidKeyException e) {
	        e.printStackTrace();
	      }
	      return null;
	} 
	public static String byte2hex(byte[] b) 
	{
	    StringBuilder hs = new StringBuilder();
	    String stmp;
	    for (int n = 0; b!=null && n < b.length; n++) {
	        stmp = Integer.toHexString(b[n] & 0XFF);
	        if (stmp.length() == 1)
	            hs.append('0');
	        hs.append(stmp);
	    }
	    return hs.toString();
	}
	/**
	 * 获取uuid
	 * */
	public static synchronized String getUUID(){
		UUID uuid=UUID.randomUUID();
		String str = uuid.toString(); 
		String nonce=str.replace("-", "");
		return nonce;
	}
	/**
	 * 按升序排列字符串(依据字符串首位字符的ASCII码)
	 * */
	public static String genjoinstr(String timestamp,String nonce,String API_KEY){
	    ArrayList<String> beforesort = new ArrayList<String>();
	    beforesort.add(API_KEY);
	    beforesort.add(timestamp);
	    beforesort.add(nonce);

	    Collections.sort(beforesort, new Comparator<String>(){
            @Override
            public int compare(String o1, String o2) {
            	if(o1==null || o2==null){
            		return -1;
            	}
            	char ch1 = o1.charAt(0);
            	int value1=Integer.valueOf(ch1);
            	char ch2 = o2.charAt(0);
            	int value2=Integer.valueOf(ch2);
            	if(value1>=value2){
            		return 1;
            	}
                return 0;
            }
        });  
	    StringBuffer aftersort = new StringBuffer();
	    for (int i = 0; i < beforesort.size(); i++) {  
	        aftersort.append(beforesort.get(i));
	    }
	    String join_str = aftersort.toString();
	    System.out.println("拼接字符串为:"+join_str);
	    return join_str;
	}
}
