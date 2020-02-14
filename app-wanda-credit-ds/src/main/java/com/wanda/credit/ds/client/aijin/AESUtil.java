package com.wanda.credit.ds.client.aijin;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.SecureRandom;

public class AESUtil {
	private final static  Logger logger = LoggerFactory.getLogger(AESUtil.class);
    // AES密码器
    private static Cipher cipher;

    private static final String KEY_CHARSET = "UTF-8";

    // 算法方式
    private static final String KEY_ALGORITHM = "AES";

    // 算法/模式/填充
    private static final String CIPHER_ALGORITHM_CBC = "AES/CBC/PKCS5Padding";

    private static final Integer PRIVATE_KEY_SIZE_BIT = 128;
    private static final Integer PRIVATE_KEY_SIZE_BYTE = 16;

    /**
     * 加密
     *
     * @param key
     *            密钥：我司提供
     * @param plainText
     *            明文：要加密的内容
     * @return cipherText
     * 			     密文：加密后的内容，如有异常返回空串：""
     */
    public static String encrypt(String key, String plainText) {
        String secretKey = md5_16(key);
        if (secretKey.length() != PRIVATE_KEY_SIZE_BYTE) {
            throw new RuntimeException("AESUtil:Invalid AES secretKey length (must be 16 bytes)");
        }

        // 密文字符串
        String cipherText;
        try {
            // 加密模式初始化参数
            initParam(secretKey, Cipher.ENCRYPT_MODE);
            // 获取加密内容的字节数组
            byte[] bytePlainText = plainText.getBytes(KEY_CHARSET);
            // 执行加密
            byte[] byteCipherText = cipher.doFinal(bytePlainText);
            cipherText = byteArrayToHex(byteCipherText);
        } catch (Exception e) {
            throw new RuntimeException("AESUtil:encrypt fail!", e);
        }
        return cipherText;
    }

    /**
     * 解密
     *
     * @param key
     *            密钥：我司提供
     * @param cipherText
     *            密文：加密后的内容，即需要解密的内容
     * @return plainText
     * 		  	      明文：解密后的内容即加密前的内容，如有异常返回空串：""
     */
    public static String decrypt(String key, String cipherText) {
        String secretKey = md5_16(key);
        if (secretKey.length() != PRIVATE_KEY_SIZE_BYTE) {
            throw new RuntimeException("AESUtil:Invalid AES secretKey length (must be 16 bytes)");
        }

        // 明文字符串
        String plainText;
        try {
            initParam(secretKey, Cipher.DECRYPT_MODE);
            // 将加密并编码后的内容解码成字节数组
            byte[] byteCipherText = hexToByteArray(cipherText);
            // 解密
            byte[] bytePlainText = cipher.doFinal(byteCipherText);
            plainText = new String(bytePlainText, KEY_CHARSET);
        } catch (Exception e) {
            throw new RuntimeException("AESUtil:decrypt fail!", e);
        }
        return plainText;
    }


    /**
     * 返回 md5 值（大写）
     * @param str
     * @return 返回 md5 值（大写）
     */
    public static String md5(String str) {
        try {
            // 生成一个MD5加密计算摘要
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 计算md5函数
            md.update(str.getBytes());
            // digest()最后确定返回md5 hash值，返回值为8为字符串。因为md5 hash值是16位的hex值，实际上就是8位的字符
            // BigInteger函数则将8位的字符串转换成16位hex值，用字符串来表示；得到字符串形式的hash值
            return new BigInteger(1, md.digest()).toString(16).toUpperCase();
        } catch (Exception e) {
            throw new RuntimeException("MD5加密出现错误");
        }
    }

    public static String md5_16(String str)
    {
        return md5(str).substring(8, 24);
    }

    public static String byteArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder(a.length * 2);
        for(byte b: a)
            sb.append(String.format("%02X", b));
        return sb.toString();
    }

    private static byte[] hexToByteArray(String hexStr) {
        byte[] bits = new byte[hexStr.length()/2];
        for (int i = 0; i < hexStr.length(); i += 2) {
            String str = hexStr.substring(i, i + 2);
            bits[i/2] = (byte)Integer.parseInt(str, 16);
        }
        return bits;
    }

    /**
     * 初始化参数
     * @param secretKey
     * 			 	密钥：加密的规则 16位
     * @param mode
     * 				加密模式：加密or解密
     */
    public static void initParam(String secretKey, int mode) {
        try {
            // 防止Linux下生成随机key
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            secureRandom.setSeed(secretKey.getBytes());
            // 获取key生成器
            KeyGenerator keygen = KeyGenerator.getInstance(KEY_ALGORITHM);
            keygen.init(PRIVATE_KEY_SIZE_BIT, secureRandom);

            // 获得原始对称密钥的字节数组
            byte[] raw = secretKey.getBytes();

            // 根据字节数组生成AES内部密钥
            SecretKeySpec key = new SecretKeySpec(raw, KEY_ALGORITHM);
            // 根据指定算法"AES/CBC/PKCS5Padding"实例化密码器
            cipher = Cipher.getInstance(CIPHER_ALGORITHM_CBC);
            IvParameterSpec iv = new IvParameterSpec(md5_16(secretKey).getBytes());
            //System.out.println("iv:" + new String(iv.getIV()));
            cipher.init(mode, key, iv);
        } catch (Exception e) {
            throw new RuntimeException("AESUtil:initParam fail!", e);
        }
    }
}