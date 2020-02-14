/**
 * 
 */
package com.wanda.credit.ds.client.bill99lBankCard.util;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @project mgwCore
 * @description:数据验签
 * @author cen
 * @create_time:Jun 21, 2009
 * @modify_time:Jun 21, 2009
 */
public class SignUtil {
	private static final  Logger logger = LoggerFactory.getLogger(SignUtil.class);
	/**
	 * @param data 被签名的原数据字节数组，xml去掉signData节点。
	 * @param signData 签名字节数组。
	 * @param certFile X.509标准的证书文件。
	 * @return 如果验签通过，就返回true
	 * @throws RuntimeException
	 */
	public static boolean veriSign(byte[] data, byte[] signData, String certFile) 
	                                                    throws RuntimeException{
		
		InputStream is = null;
		try {
			//加载公钥
			is = new FileInputStream(certFile);
			
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			Certificate cert = cf.generateCertificate(is);

			PublicKey publicKey = cert.getPublicKey();
			
			Signature sig = Signature.getInstance("SHA1WithRSA");
			byte[] signed = Base64Binrary.decodeBase64Binrary(new String(signData));
			sig.initVerify(publicKey);
			sig.update(data);
			return sig.verify(signed);
			
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		} finally {
			if (is != null) {
				try{
					is.close();
				}catch(Exception e){
					throw new RuntimeException(e.getMessage(), e);
				}
				
			}
		}
	}
	
	/**
	 * @param tr3Xml tr3的xml。
	 * @param certFile X.509标准的证书文件。
	 * @return 如果验签通过就返回true
	 * @throws RuntimeException
	 */
	
	public static boolean veriSignForXml(String tr3Xml)
{
		String certFile="";
		try {
			certFile = SignUtil.class
					.getClassLoader()
					.getResource("/depends/ds/mas/mgw.cer")
					.getPath();
		} catch (Exception e1) {
			logger.error("独立鉴权:读取mgw公钥文件异常! {}",e1.getMessage());
			e1.printStackTrace();
		}
		String dataBeforeSign =  tr3Xml.replaceAll("<signature>.*</signature>", "");
		
		int beginIndex = tr3Xml.indexOf("<signature>");
		int endIndex = tr3Xml.indexOf("</signature>");
		String signData =  tr3Xml.substring(beginIndex + 11, endIndex);
		
		try {
			return veriSign(dataBeforeSign.getBytes("UTF-8"), 
			        signData.getBytes("UTF-8"), certFile);
		} catch (Exception e) {
			logger.error("独立鉴权:验签异常! {}",e.getMessage());
			throw new RuntimeException(e.getMessage(), e);
		}

	}
}
	
	


