package com.wanda.credit.ds.client.bill99lBankCard;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.security.KeyStore;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.ds.BaseDataSourceRequestor;
import com.wanda.credit.ds.client.bill99lBankCard.util.Base64Binrary;
import com.wanda.credit.ds.client.bill99lBankCard.util.MyX509TrustManager;
import com.wanda.credit.ds.client.bill99lBankCard.util.ParseUtil;
import com.wanda.credit.ds.dao.iface.IAllAuthCardService;

public class BaseMasDataSourcesRequestor extends BaseDataSourceRequestor {
	
	private final  Logger logger = LoggerFactory.getLogger(BaseMasDataSourcesRequestor.class);
	protected static KeyManagerFactory kmf;
	@Autowired
	protected IPropertyEngine propertyEngine;
	@Autowired
	protected IAllAuthCardService allAuthCardService;
	/**
	 * 证书库初始化
	 * @throws Exception
	 */
	public void init() throws Exception{
		if (kmf == null) {
			String jksFile = BaseMasDataSourcesRequestor.class
					.getResource("/depends/ds/mas/"+propertyEngine.readById("MAS_MERCHANTID")+".jks")
					.getPath(); 
			//获取证书路径
			File certFile = new File(jksFile);  //测试证书，生产环境需要替换
		    //访问Java密钥库，JKS是keytool创建的Java密钥库，保存密钥。
			KeyStore ks = KeyStore.getInstance("JKS");
			ks.load(new FileInputStream(certFile), "vpos123".toCharArray());
			//创建用于管理JKS密钥库的密钥管理器
			kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			//初始化证书
			kmf.init(ks, "vpos123".toCharArray());
		}
	}
	
	/**
	*  发送银行卡鉴权请求
	* @param url 发送请求的URL
	* @param param 请求参数
    * @return URL所代表远程资源的响应
	 * @throws Exception 
    */
	public HashMap sendPost(String url,String reqXML,String[] nodeNames) throws Exception
	{
//		init();
		KeyManagerFactory kmf = null;
		String jksFile = BaseMasDataSourcesRequestor.class
				.getResource("/depends/ds/mas/"+propertyEngine.readById("MAS_MERCHANTID")+".jks")
				.getPath(); 
		logger.info("jks文件为 {}" ,jksFile);
		//获取证书路径
		File certFile = new File(jksFile);  //测试证书，生产环境需要替换
	    //访问Java密钥库，JKS是keytool创建的Java密钥库，保存密钥。
		KeyStore ks = KeyStore.getInstance("JKS");
		ks.load(new FileInputStream(certFile), "vpos123".toCharArray());
		//创建用于管理JKS密钥库的密钥管理器
		kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		//初始化证书
		kmf.init(ks, "vpos123".toCharArray());
		
		
		System.setProperty("jsse.enableSNIExtension", "false");
		ParseUtil parseUtil =new ParseUtil();
		OutputStream out = null;
		HashMap respXml = null;

		//同位体验证信任决策源//同位体验证可信任的证书来源
		TrustManager[] tm = { new MyX509TrustManager() }; 
		
		//初始化安全套接字
		SSLContext sslContext = SSLContext.getInstance("SSL");
		//初始化SSL环境。第二个参数是告诉JSSE使用的可信任证书的来源，设置为null是从javax.net.ssl.trustStore中获得证书。
		//第三个参数是JSSE生成的随机数，这个参数将影响系统的安全性，设置为null是个好选择，可以保证JSSE的安全性。
		sslContext.init(kmf.getKeyManagers(),tm, null);
		
		//根据上面配置的SSL上下文来产生SSLSocketFactory,与通常的产生方法不同
		SSLSocketFactory factory = sslContext.getSocketFactory();
	    URL realUrl = new URL(url);
	    //打开和URL之间的连接
	    HttpsURLConnection conn = (HttpsURLConnection)realUrl.openConnection();
	    //创建安全的连接套接字
		conn.setSSLSocketFactory(factory);
	    //发送POST请求必须设置如下两行,使用 URL 连接进行输出、入
	    conn.setDoOutput(true);
	    conn.setDoInput(true);
	    //设置URL连接的超时时限
	    conn.setReadTimeout(10000);
	    //设置通用的请求属性
	    String authString = propertyEngine.readById("MAS_MERCHANTID") + ":" + "vpos123";//测试帐号，生产环境需要替换生产商户编号
		String auth = "Basic " + Base64Binrary.encodeBase64Binrary(authString.getBytes());
	    conn.setRequestProperty("Authorization", auth);
	    // 获取URLConnection对象对应的输出流
	    out = conn.getOutputStream();
	    //发送请求参数
		out.write(reqXML.getBytes("utf-8"));
	    //flush 输出流的缓冲
	    out.flush();
	    //得到服务端返回
	 	 InputStream is = conn.getInputStream();
	 	String reqData = "";
	    if(is!=null && !"".equals(is))
	    {
	    	ByteArrayOutputStream bos = new ByteArrayOutputStream();
			byte[] receiveBuffer = new byte[2048];//缓冲区长度
			int readBytesSize = is.read(receiveBuffer);//读取数据长度，InputStream要读取的数据长度一定要小于等于缓冲区中的字节数
				
			while(readBytesSize != -1){//判断流是否位于文件末尾而没有可用的字节
				bos.write(receiveBuffer, 0, readBytesSize);//从receiveBuffer内存处的0偏移开始写，写与readBytesSize长度相等的字节
				readBytesSize = is.read(receiveBuffer);
			}
			reqData = new String(bos.toByteArray(), "UTF-8");//编码后的tr2报文
	    }
	    logger.info("返回报文数据为 {}" , reqData);
		respXml= parseUtil.parseXML(reqData,nodeNames);//给解析XML的函数传递快钱返回的TR2的XML数据流
		logger.info("返回报文数据解析后为 {}" , respXml);
		if (out != null){out.close();}
	    return respXml;
	}
	
}
