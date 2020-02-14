package com.wanda.credit.ds.client.yituNew;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.security.PublicKey;
import java.security.SecureRandom;

import com.wanda.credit.base.Conts;
import com.wanda.credit.base.executor.IExecutor;
import com.wanda.credit.base.executor.PoolInitManager;
import com.wanda.credit.base.util.EncryptionHelper;
import com.wanda.credit.base.util.EncryptionHelper.MD5Helper.Md5EncodingException;
import com.wanda.credit.base.util.EncryptionHelper.RSAHelper.PublicKeyException;
import com.wanda.credit.ds.BaseDataSourceRequestor;

public class BaseYiTuNewSourceRequestor extends BaseDataSourceRequestor{
	public static final IExecutor threadYiTuPool = PoolInitManager.getDsPool(Conts.KEY_DS_AGENT_POOL_ID);
	private static final char[] toDigit = ("0123456789ABCDEF").toCharArray();
	public  final String userDefinedContent ="99bill";
	public  static PublicKey pkNew = null;
	protected final String STATUS_YITU_NO1 = "11";
	protected final String STATUS_YITU_NO2 = "12";
	protected final String STATUS_YITU_NO3 = "13";
	/**
	 * 初始化连接资源
	 * @throws PublicKeyException
	 */
	public static void init() throws PublicKeyException{
		if(pkNew==null){
			pkNew = EncryptionHelper.RSAHelper
					.loadPublicKey(BaseYiTuNewSourceRequestor.class
							.getClassLoader()
							.getResource("/depends/ds/yitu/staging.public.pem")
							.getPath());
		}
//		pk = EncryptionHelper.RSAHelper.loadPublicKey("E:\\credit_worksace\\app-ifs-credit-ds\\src\\main\\resources\\depends\\ds\\yitu\\staging.public.pem");
	}
	
	/**
     * 生成Signature
     * @param accessKey, access Key
     * @param bodyString, HTTP 请求内容
     * @param userDefinedContent, 客户自定义域，要求小于41字节
     * @return 生成的加密后的Signature 
     * @throws Md5EncodingException
     * @throws PublicKeyException
     */
	public static String generateSignature(PublicKey publicKey,String accessKey,String bodyString,String userDefinedContent) throws Exception {
	
		String result = null;
		
		//生成unix时间戳
		int unixTime = (int) (System.currentTimeMillis() / 1000L);
		byte[] unixTimeArray = ByteBuffer.allocate(4).putInt(unixTime).array();
		
		//生成随机数
		SecureRandom sr = new SecureRandom();
		byte[] rndBytes = new byte[8];
		sr.nextBytes(rndBytes);
		
		//拼接Signature
		byte[] signatureStr = mergeArray(accessKey.getBytes(Charset.forName("UTF-8")),EncryptionHelper.MD5Helper.md5(bodyString).getBytes(Charset.forName("UTF-8")));
		signatureStr = mergeArray(signatureStr,unixTimeArray);
		signatureStr = mergeArray(signatureStr,rndBytes);
		signatureStr = mergeArray(signatureStr,userDefinedContent.getBytes(Charset.forName("UTF-8")));
		
		//RSA加密
    	result = hexEncode(EncryptionHelper.RSAHelper.encrypt(signatureStr,publicKey));

		return result;
	}
	
	/**
	 * 16进制编码
	 * @param bytes, 输入字符数组
	 * @return 经过16进制编码后的数组
	 */
    public static String hexEncode(byte[] bytes) {
        char[] chars = new char[2*bytes.length];
        int j = 0;

        for (int i = 0; i < bytes.length; ++i) {
            byte bits = bytes[i];

            chars[j++] = toDigit[((bits >>> 4) & 0xF)];
            chars[j++] = toDigit[(bits & 0xF)];
        }

        return new String(chars);
    }
    
    /**
	 * 合并两个byte数组
	 * @param a
	 * @param b
	 * @return a + b
	 */
	public static byte[] mergeArray(byte[] a, byte[] b) {
		byte[] c = new byte[a.length + b.length];
		System.arraycopy(a, 0, c, 0, a.length);
		System.arraycopy(b, 0, c, a.length, b.length);
		return c;
	}
	
	/**
	 * 返回byte的数据大小对应的字符串
	 * @param size
	 * @return
	 */
	public static boolean formatStrSize(String str){
		if(str==null || str.length()==0)
			return true;
		long size = str.length();
		if(size>1024*1024){
			return true;
		}
		return false;
	}
}
