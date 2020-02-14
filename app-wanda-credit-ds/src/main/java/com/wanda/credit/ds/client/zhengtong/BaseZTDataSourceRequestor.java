package com.wanda.credit.ds.client.zhengtong;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

import com.wanda.credit.base.util.EncryptionHelper.RSAHelper.PublicKeyException;
import com.wanda.credit.ds.BaseDataSourceRequestor;

public class BaseZTDataSourceRequestor extends BaseDataSourceRequestor{
	public  static String pk1 = null;
	public  static String pk_authen_bankcard = null;
	
	protected String zhengtong_address; 
	protected String accessId; 
	protected String ptycd;
	protected String accessKey; 
	protected String encrykey; 
	protected String sourceChannel;
	protected String placeId;
	protected String biztyp;
	protected String biztypDesc;
	protected int  times; 
	protected int  interval;
	
	protected  String ZHENGTONG_PHOTO_API = "2000101";
	protected  String ZHENGTONG_PHOTO_RESULT = "2000102";
	protected  String ZHENGTONG_IDENT_API = "2000201";
	/**wcs add*/
	protected  String ZHENGTONG_AUTHENBANKCARD_API = "2000207";
	protected  String STATUS_FAIL_NULL = "10";
	protected  String STATUS_FAIL_ERROR = "11";
	protected  String STATUS_IDENT_NO1 = "00";
	protected  String STATUS_IDENT_NO2 = "01";
	/**
	 * 初始化连接资源
	 * @throws PublicKeyException
	 */
	public static void init() throws PublicKeyException{
		if(pk1==null){
			pk1 = loadPublicKey(BaseZTDataSourceRequestor.class
							.getResource("/depends/ds/zhengtong/staging.public.pem")
							.getPath());
		}
		
		if(pk_authen_bankcard==null){
			pk_authen_bankcard = loadPublicKey(BaseZTDataSourceRequestor.class
							.getResource("/depends/ds/zhengtong/staging.public.authbankcd.pem")
							.getPath());
		}
	}
	
	/**
     * 从pem文件读取公钥
     * @param filename, 公钥文件路径
     * @return 公钥字符串
     * @throws PublicKeyException
     */
    public static String loadPublicKey(String filename) throws PublicKeyException {
        try {
        	 InputStream is = new FileInputStream(filename);
        	 StringBuffer buffer =new StringBuffer();
             String line; // 用来保存每行读取的内容
             BufferedReader reader = new BufferedReader(new InputStreamReader(is));
             line = reader.readLine(); // 读取第一行
             if(line != null) { // 如果 line 为空说明读完了
                 buffer.append(line); // 将读到的内容添加到 buffer 中
             }
          return buffer.toString(); 
        } catch (Exception e) {
            e.printStackTrace();
            throw new PublicKeyException("载入公钥错误");
        }
        
    }
    public static boolean isChineseWord(String str){
        //^[\u4E00-\u9FA5\uf900-\ufa2d·s]{2,20}$
		String pattern = "^[\u4E00-\u9FA5\uf900-\ufa2d·s]{2,20}$";
		boolean isMatch=Pattern.matches(pattern, str);
		return isMatch;
	}
	/**
     * @param publicKey 公钥
     * @param bodyString, HTTP 请求内容
     * @return 生成的加密后的Signature 
     */
	public static String generateSignature(String publicKey,String bodyString) throws Exception {	
		String result = null;	
		//AES加密
    	result = KeyHelp.getStrByPublic(publicKey,bodyString);
		return result;
	}

	public String getZhengtong_address() {
		return zhengtong_address;
	}

	public void setZhengtong_address(String zhengtong_address) {
		this.zhengtong_address = zhengtong_address;
	}

	public String getAccessId() {
		return accessId;
	}

	public void setAccessId(String accessId) {
		this.accessId = accessId;
	}

	public String getAccessKey() {
		return accessKey;
	}

	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}

	public String getEncrykey() {
		return encrykey;
	}

	public void setEncrykey(String encrykey) {
		this.encrykey = encrykey;
	}

	public String getSourceChannel() {
		return sourceChannel;
	}

	public void setSourceChannel(String sourceChannel) {
		this.sourceChannel = sourceChannel;
	}

	public String getPlaceId() {
		return placeId;
	}

	public String getPtycd() {
		return ptycd;
	}

	public void setPtycd(String ptycd) {
		this.ptycd = ptycd;
	}

	public void setPlaceId(String placeId) {
		this.placeId = placeId;
	}

	public String getBiztyp() {
		return biztyp;
	}

	public void setBiztyp(String biztyp) {
		this.biztyp = biztyp;
	}

	public String getBiztypDesc() {
		return biztypDesc;
	}

	public void setBiztypDesc(String biztypDesc) {
		this.biztypDesc = biztypDesc;
	}

	public int getTimes() {
		return times;
	}

	public void setTimes(int times) {
		this.times = times;
	}

	public int getInterval() {
		return interval;
	}

	public void setInterval(int interval) {
		this.interval = interval;
	}
}
