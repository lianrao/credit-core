package com.wanda.credit.ds.client.yixin;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Ning Qing Qing
 * 
 * RC4 128bit 加密&解密算法，2016-01-29更新版本V2
 *
 */
public class RC4_128_V2 {
	
	private static final Logger logger = LoggerFactory.getLogger(RC4_128_V2.class);
	private static final String RC4 = "RC4";
	private static final String UTF8 = "UTF-8";
	
	/**
	 * RC4加密明文（可能包含汉字），输出是经过Base64的；如果加密失败，返回值是null
	 * @param plainText
	 * @param rc4Key
	 * @return
	 */
	public static final String encode( final String plainText, final String rc4Key )
	{
		try
		{
			final Cipher c1 = Cipher.getInstance(RC4);
			c1.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(rc4Key.getBytes(), RC4));
			return new String( Base64.encodeBase64(c1.doFinal(plainText.getBytes(UTF8))) );
		}
		catch(Throwable t)
		{
			logger.error("", t);
			return null;
		}
	}
	
	/**
	 * RC4从密文解密为明文，输入是经过Base64的；如果解密失败，返回值是null
	 * @param encodedText
	 * @param rc4Key
	 * @return
	 */
	public static final String decode( final String encodedText, final String rc4Key )
	{
		try
		{
			final Cipher c1 = Cipher.getInstance(RC4);
			c1.init(Cipher.DECRYPT_MODE, new SecretKeySpec(rc4Key.getBytes(), RC4));
			return new String( c1.doFinal(Base64.decodeBase64(encodedText.getBytes())), UTF8 );
		}
		catch( Throwable t )
		{
			logger.error("", t);
			return null;
		}		
	}
	
	// 内部测试
	public static void main(String[] args)
	{
		final String rc4Key = "1234567890012345";
			System.out.println("RC4 KEY [" + rc4Key + "]");
		
		final String plainTextSeed = "@！￥%——+12ACeg中国#$%&,.[]{}，。￥（）人民";
		final StringBuilder plainText = new StringBuilder();
		final int loopNumber = 5;
		for( int i=0; i<loopNumber; i++ )
		{
			plainText.append(plainTextSeed);
		}
			System.out.println("明文 [" + plainText.toString() + "]");
			System.out.println("明文长度 [" + plainText.toString().getBytes().length + "]");
		
		final String encodedText = RC4_128_V2.encode(plainText.toString(), rc4Key);
			System.out.println("加密后 [" + encodedText + "]");
			System.out.println("密文长度 [" + encodedText.length() + "]");
		
		final String decodedText = RC4_128_V2.decode(encodedText, rc4Key);
			System.out.println("解密后 [" + decodedText + "]");
		
			System.out.println();
			
		if( plainText.toString().equals( decodedText ) )
			System.out.println("解密后的  =  明文");
		else
			System.out.println("警报！！！解密后的  <> 明文");
	}
	
}
