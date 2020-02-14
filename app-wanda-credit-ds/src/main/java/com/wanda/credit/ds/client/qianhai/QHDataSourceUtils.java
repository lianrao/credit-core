package com.wanda.credit.ds.client.qianhai;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

import net.sf.json.JSONObject;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.protocol.Protocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wanda.credit.common.props.DynamicConfigLoader;
import com.wanda.credit.common.template.PropertyEngine;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class QHDataSourceUtils
{
 	private static final Logger logger = LoggerFactory
			.getLogger(QHDataSourceUtils.class);

 	private static int timeout = Integer.parseInt(DynamicConfigLoader.get("sys.credit.client.http.timeout"));;
    public static String digest(byte[] oriByte) throws Exception
    {
        MessageDigest md = null;
        String strDes = null;
        
        try
        {
            md = MessageDigest.getInstance("SHA1");
            md.update(oriByte);
            strDes = bytes2Hex(md.digest());
        } catch (Exception e)
        {
            throw new QHDataSourceException("摘要生成失败！",e);
        }
        return strDes;
    }

    public static String signData(PrivateKey key,String data) throws Exception
    {
        try
        {
            Signature sig = Signature.getInstance("SHA1WithRSA");
            sig.initSign(key);
            sig.update(data.getBytes("utf-8"));
            byte[] sigBytes = sig.sign();
            BASE64Encoder encoder = new BASE64Encoder();
            return encoder.encodeBuffer(sigBytes);
        } catch (Exception e)
        {
            throw new QHDataSourceException("数据签名失败!",e);
        }
    }

    public static void verifyData(PublicKey key,String data, String signValue) throws Exception
    {
        try
        {
            Signature sig = Signature.getInstance("SHA1WithRSA");
            sig.initVerify(key);
            sig.update(data.getBytes("utf-8"));
            BASE64Decoder decoder = new BASE64Decoder();
            byte[] signValueByte = decoder.decodeBuffer(signValue);
            if (!sig.verify(signValueByte))
            {
                throw new QHDataSourceException("验签失败!");
            }
        } catch (Exception e)
        {
            throw new QHDataSourceException("验签失败!",e);
        }
    }

    public static PublicKey getPublicKey(String publicKeyPath) throws Exception
    {
        InputStream is = null;
        try
        {
            is = new FileInputStream(publicKeyPath);
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            X509Certificate cert = (X509Certificate) cf.generateCertificate(is);
            return cert.getPublicKey();
        } catch (FileNotFoundException e)
        {
            throw new QHDataSourceException("获取公钥失败，公钥文件不存在",e);
        } catch (CertificateException e)
        {
            throw new QHDataSourceException("获取公钥失败",e);
        }
        finally
        {
            if (is != null)
            {
                try
                {
                    is.close();
                } catch (IOException e)
                {
                	logger.error("文件关闭异常",e);
                }
            }
        }
    }

    public static PrivateKey getPrivateKey(String privateKeyPath,String passwd,String alias) throws Exception{
        char[] storePwdArr;
        int i;
        BufferedInputStream bis = null;
        try{
            KeyStore ks = KeyStore.getInstance("JKS");
            FileInputStream fis = new FileInputStream(privateKeyPath);
            
            bis = new BufferedInputStream(fis);
            String storePassword = passwd;
            String storeAlias = alias;
            storePwdArr = new char[storePassword.length()];// store password
            for (i = 0; i < storePassword.length(); i++)
            {
                storePwdArr[i] = storePassword.charAt(i);
            }
            ks.load(bis, storePwdArr);
            PrivateKey priv = (PrivateKey) ks.getKey(storeAlias, storePwdArr);
            return priv;
        }catch (Exception e){
        	e.printStackTrace();
            throw new QHDataSourceException("获取私钥失败",e);
        }
        finally
        {
            if (bis != null)
            {
                try
                {
                    bis.close();
                } catch (IOException e)
                {
                	logger.error("文件关闭异常",e);
                }
            }
        }
    }

    public static String decrypt(String sealTxt, String keyStr) throws Exception
    {
        try
        {
            Cipher cipher = null;
            byte[] byteFina = null;
            SecretKey key = getKey(keyStr);
            try
            {
                cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
                cipher.init(Cipher.DECRYPT_MODE, key);
                BASE64Decoder decoder = new BASE64Decoder();
                byte[] sealByte = decoder.decodeBuffer(sealTxt);
                byteFina = cipher.doFinal(sealByte);
                return new String(byteFina, "utf-8");
            } catch (Exception e)
            {
                throw new QHDataSourceException("数据解密失败!",e);
            }
            finally
            {
                cipher = null;
            }
        } catch (Exception e)
        {
            throw new QHDataSourceException("数据解密失败!",e);
        }
    }

    public static String encrypt(byte[] oriByte, String keyStr) throws Exception
    {
        try
        {
            byte[] sealTxt = null;
            SecretKey key = getKey(keyStr);
            Cipher cipher = null;
            try
            {
                cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
                cipher.init(Cipher.ENCRYPT_MODE, key);
                sealTxt = cipher.doFinal(oriByte);
                BASE64Encoder encoder = new BASE64Encoder();
                String ret = encoder.encode(sealTxt);
                return ret;
            } catch (Exception e)
            {
                throw new QHDataSourceException("数据加密失败!",e);
            }
            finally
            {
                cipher = null;
            }
        } catch (Exception e)
        {
            throw new QHDataSourceException("数据加密失败!",e);
        }
    }

    private static SecretKey getKey(String key) throws Exception
    {
        try
        {
            // 实例化DESede密钥
            DESedeKeySpec dks = new DESedeKeySpec(key.getBytes("utf-8"));
            // 实例化密钥工厂
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
            // 生成密钥
            SecretKey secretKey = keyFactory.generateSecret(dks);
            return secretKey;
        } catch (Exception e)
        {
            throw new QHDataSourceException("秘钥创建失败!",e);
        }
    }
    
    /**
     * 如果在jsnObj中不存在key属性 返回null 而不是抛出异常
     * */
	public static String getValFromJsnObj(JSONObject jsnObj,String key){
		try{
			return jsnObj.getString(key);
		}catch(Exception e){
			return null;
		}
	}
	
    public static String bytes2Hex(byte[] inbuf)
    {
        int i;
        String byteStr;
        StringBuffer strBuf = new StringBuffer();
        for (i = 0; i < inbuf.length; i++)
        {
            byteStr = Integer.toHexString(inbuf[i] & 0x00ff);
            if (byteStr.length() != 2)
            {
                strBuf.append('0').append(byteStr);
            }
            else
            {
                strBuf.append(byteStr);
            }
        }
        return new String(strBuf);
    }
    
    final static String PROTOCOL_NAME = "https";

    public static String sendJsonWithHttps(String surl, String json) throws Exception
    {
        HttpClient client = new HttpClient();
        client.getParams().setContentCharset("UTF-8");
        Protocol httpProtocol = new Protocol(PROTOCOL_NAME, new SSLProtocolSocketFactory(false), 443);
        Protocol.registerProtocol(PROTOCOL_NAME, httpProtocol);
        PostMethod post = new PostMethod(surl);
        post.setRequestHeader("Content-Type", "application/json");
        RequestEntity requestEntity = new ByteArrayRequestEntity(json.getBytes("utf-8"));
        post.setRequestEntity(requestEntity);
        String timeout = PropertyEngine.get("qh_req_timeout");
        client.getHttpConnectionManager().getParams().setConnectionTimeout(Integer.valueOf(timeout));    
        client.getHttpConnectionManager().getParams().setSoTimeout(Integer.valueOf(timeout));  
        client.executeMethod(post);
        InputStream in = post.getResponseBodyAsStream();
        byte[] buf = new byte[512];
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        do
        {
            int n = in.read(buf);
            if (n > 0)
            {
                baos.write(buf, 0, n);
            }
            else if (n <= 0)
            {
                break;
            }
        } while (true);
        return baos.toString();
    }
}