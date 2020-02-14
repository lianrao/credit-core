package com.wanda.credit.ds.client.anhuizx.service;



import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by caoyanfei079 on 4/23/15.
 */
public final class AhzxAES {
    static {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }
    
    public static Integer AES_SIZE_128 = 128;
    public static String ALGORITHM_AES = "AES";
    public static String CIPHER_ALGORITHM_CBC = "AES/CBC/PKCS5Padding";
    public static String BC_PROVIDER = "BC";
    public static String DEFAULT_ROOT_IV = "0000000000000000";
    public static String DEFAULT_CHARSET = "UTF-8";
    
    private final byte[] passwordBytes;
    private final Integer keySize;

    public AhzxAES(byte[] passwordBytes) {
        this(passwordBytes, AES_SIZE_128);
    }
    public AhzxAES(byte[] passwordBytes, Integer keySize) {
        this.passwordBytes = passwordBytes;
        this.keySize = keySize;
    }
    protected Cipher getEncryptCipher() throws AhzxAESException {
        return getCipher(Cipher.ENCRYPT_MODE);
    }

    protected Cipher getDecryptCipher() throws AhzxAESException {
        return getCipher(Cipher.DECRYPT_MODE);
    }

    // cipher 不是线程安全的，如果�?要�?�能上的考虑�?
    // 使用cache的方案来实现，暂时应该不�?�?
    protected Cipher getCipher(Integer mode) throws AhzxAESException {
        try {
            SecretKeySpec key = new SecretKeySpec(passwordBytes, ALGORITHM_AES);
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM_CBC,BC_PROVIDER);// 创建密码�?
            cipher.init(mode, key, new IvParameterSpec(DEFAULT_ROOT_IV.getBytes()));// 初始�?
            return cipher;
        }catch (Exception e) {
            throw new AhzxAESException("Failed to get the cipher with passwordBytes [" + passwordBytes + "] key size + [" + keySize + "] mode [" + mode + "]", e);
        }
    }

    /**
     * 加密
     *
     * @param content �?要加密的内容
     * @return
     */
    public byte[] encrypt(String content) throws AhzxAESException {
        try {
            byte[] byteContent = content.getBytes(DEFAULT_CHARSET);
            return encrypt(byteContent);
        } catch (Exception e) {
            throw new AhzxAESException("failed to encryptInputStream the content [" + content + "]",e);
        }
    }
    public byte[] decrypt(String content) throws AhzxAESException {
        try {
            byte[] byteContent = content.getBytes(DEFAULT_CHARSET);
            return decrypt(byteContent);
        } catch (Exception e) {
            throw new AhzxAESException("failed to decryptOutputStream the content [" + content + "]",e);
        }
    }

    public byte[] encrypt(byte[] content) throws AhzxAESException {
        try {
            return getEncryptCipher().doFinal(content);
        } catch (Exception e) {
            throw new AhzxAESException("failed to encryptInputStream the content [" + content + "]",e);
        }
    }
    public byte[] decrypt(byte[] content) throws AhzxAESException {
        try {
            return getDecryptCipher().doFinal(content);
        } catch (Exception e) {
            throw new AhzxAESException("failed to Decrypt the content [" + content + "]",e);
        }
    }

//    public InputStream encryptInputStream(InputStream is) throws AhzxAESException {
//        return new CipherInputStream(is, getEncryptCipher());
//    }
//    public InputStream decryptInputStream(InputStream is) throws AhzxAESException {
//        return new CipherInputStream(is, getDecryptCipher());
//    }
//
//    public OutputStream encryptOutputStream(OutputStream os) throws AhzxAESException {
//        return new CipherOutputStream(os, getEncryptCipher());
//    }
//
//    public OutputStream decryptOutputStream(OutputStream os) throws AhzxAESException {
//        return new CipherOutputStream(os, getDecryptCipher());
//    }
}
