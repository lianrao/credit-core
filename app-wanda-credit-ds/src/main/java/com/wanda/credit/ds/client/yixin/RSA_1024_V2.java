package com.wanda.credit.ds.client.yixin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.ObjectInputStream;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sun.misc.BASE64Decoder;


/**
 * 
 * @author Ning Qing Qing
 * 
 * RSA 1024bit 加密&解密算法，2016-01-29更新版本V2
 *
 */
public class RSA_1024_V2 {
	
	private static final Logger logger = LoggerFactory.getLogger(RSA_1024_V2.class);
	private static final String RSA = "RSA";
	private static final String UTF8 = "UTF-8";	

	/**
	 * 从模和指数构造RSAPrivateKey对象；如果失败，则返回null
	 * @param modulus
	 * @param exponent
	 * @return
	 */
	public static final RSAPrivateKey getPrivateKey(final String modulus, final String exponent) 
	{
		try 
		{
			final RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(new BigInteger(modulus), new BigInteger(exponent));
			return (RSAPrivateKey) KeyFactory.getInstance(RSA).generatePrivate(keySpec);
		} 
		catch (Throwable t) 
		{
			logger.error("", t);
			return null;
		}
	}

	/**
	 * RSA私钥加密明文，输出是经过Base64的；如果加密失败，返回值是null
	 * @param privateKey
	 * @param plainText
	 * @return
	 */
	public static final String encodeByPrivateKey(final RSAPrivateKey privateKey, final String plainText) 
	{
		try 
		{
			final Cipher cipher = Cipher.getInstance(RSA);
			cipher.init(Cipher.ENCRYPT_MODE, privateKey);
			return new String(Base64.encodeBase64(cipher.doFinal(plainText.getBytes(UTF8))));
		} 
		catch (Throwable t) 
		{
			logger.error("", t);
			return null;
		}
	}	
	
	/**
	 * RSA私钥解密，输入是经过Base64的；如果解密失败，返回值是null
	 * @param privateKey
	 * @param encodedText
	 * @return
	 */
	public static final String decodeByPrivateKey(final RSAPrivateKey privateKey, final String encodedText) 
	{
		try 
		{
			final Cipher cipher = Cipher.getInstance(RSA);
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			return new String(cipher.doFinal(Base64.decodeBase64(encodedText)), UTF8);
			
		} catch (Throwable t) 
		{
			logger.error("", t);
			return null;
		}
	}
	
	/**
	 * RSA公钥加密明文，输出是经过Base64的；如果加密失败，返回值是null
	 * @param publicKey
	 * @param plainText
	 * @return
	 */
	public static final String encodeByPublicKey(final RSAPublicKey publicKey, final String plainText) 
	{
		try 
		{
			final Cipher cipher = Cipher.getInstance(RSA);
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			return new String(Base64.encodeBase64(cipher.doFinal(plainText.getBytes(UTF8))));
		}
		catch (Throwable t) 
		{
			logger.error("", t);
			return null;
		}
	}	
	
	/**
	 * RSA公钥解密，输入是经过Base64的；如果解密失败，返回值是null
	 * @param publicKey
	 * @param encodedText
	 * @return
	 */
	public static final String decodeByPublicKey(final RSAPublicKey publicKey, final String encodedText) 
	{
		try 
		{
			final Cipher cipher = Cipher.getInstance(RSA);
			cipher.init(Cipher.DECRYPT_MODE, publicKey);
			return new String(cipher.doFinal(Base64.decodeBase64(encodedText)), UTF8);			
		} 
		catch (Throwable t) 
		{
			logger.error("", t);
			return null;
		}
	}
	
	/**
	 * 从CRT文件中，获取公钥；其实就是Java的反序列化过程
	 * @param crtFilePath
	 * @return
	 */
	public static final RSAPublicKey gainRSAPublicKeyFromCrtFile ( final String crtFilePath )
	{
		RSAPublicKey publicKey = null;
		ObjectInputStream ois = null;
		
		try
		{
			final File crtFile = new File(crtFilePath);
			ois = new ObjectInputStream(new FileInputStream(crtFile));
			publicKey = (RSAPublicKey) ois.readObject();
		}
		catch( Throwable t )
		{
			logger.error("", t);
		}finally{
			if(ois != null){
				IOUtils.closeQuietly(ois);
			}
		}
		return publicKey;
	}
	
	/**
	 * 从PEM文件中，获取公钥
	 * @param pemFilePath
	 * @return
	 */
	public static final RSAPublicKey gainRSAPublicKeyFromPemFile ( final String pemFilePath )
	{
		BufferedReader br = null;
		try
		{
			br = new BufferedReader(new FileReader(pemFilePath)); 
			String s = br.readLine(); 
			String str = ""; 
			s = br.readLine(); 
			while (s.charAt(0) != '-')
			{ 
				str += s + "\r"; 
				s = br.readLine(); 
			}
	
			//生成公匙 
			KeyFactory kf = KeyFactory.getInstance(RSA); 
			return (RSAPublicKey)(kf.generatePublic(new X509EncodedKeySpec(new BASE64Decoder().decodeBuffer(str))));
		}
		catch( Throwable t )
		{
			logger.error("", t);
			br = null;
			return null;
		}finally{
			if(br != null){
				IOUtils.closeQuietly(br);
			}
		}
	}

	/**
	 * 通过加密和解密，来校验一对私钥和公钥是否匹配
	 * @param rsaPrivateKey
	 * @param rsaPublicKey
	 * @return
	 */
	public static String verifyPrivatePublicPair(final RSAPrivateKey rsaPrivateKey, final RSAPublicKey rsaPublicKey)
	{
		boolean matched = true;
		
		final String input = "@！￥%——+12ACeg中国#$%&,.[]{}，。￥（）人民";
		//final String input = "xbakd_testusr";
		
		/**
		 * 私钥加密，公钥解密
		 */
		final String encodedByPrivateKey = encodeByPrivateKey(rsaPrivateKey, input);
		final String decodedByPublicKey = decodeByPublicKey(rsaPublicKey, encodedByPrivateKey);
		if( ! input.equals(decodedByPublicKey) )
			matched = false;
		else
		{
			/**
			 * 公钥加密，私钥解密
			 */
			final String encodedByPublicKey = encodeByPublicKey(rsaPublicKey, input);
			final String decodedByPrivateKey = decodeByPrivateKey(rsaPrivateKey, encodedByPublicKey);
			if( ! input.equals(decodedByPrivateKey) )
				matched = false;
		}
		
		if( matched )
			return "此公钥和私钥对儿，匹配！";
		else
			return "此公钥和私钥对儿，不匹配！";
	}
	
	// 内部测试
	public static void main(String [] args)
	{}
	
}
