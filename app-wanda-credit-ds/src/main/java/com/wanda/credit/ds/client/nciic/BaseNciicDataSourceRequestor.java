package com.wanda.credit.ds.client.nciic;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Proxy;
import java.net.MalformedURLException;

import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.codehaus.xfire.client.Client;
import org.codehaus.xfire.client.XFireProxy;
import org.codehaus.xfire.client.XFireProxyFactory;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.transport.http.CommonsHttpMessageSender;
import org.codehaus.xfire.transport.http.EasySSLProtocolSocketFactory;
import org.codehaus.xfire.util.dom.DOMOutHandler;
import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import com.wanda.credit.ds.BaseDataSourceRequestor;
import com.wanda.credit.base.util.EncryptionHelper.RSAHelper.PublicKeyException;
import com.wanda.credit.common.props.DynamicConfigLoader;

public class BaseNciicDataSourceRequestor extends BaseDataSourceRequestor{
	public  static String license = null;
	public  static String licenseMult = null;
	private static final int timeout = Integer.parseInt(DynamicConfigLoader.get("sys.credit.client.http.timeout"));
	private static String nciic_timeout = DynamicConfigLoader.get("sys.credit.client.http.timeout");
	protected static String nciic_address = DynamicConfigLoader.get("sys.credit.client.nciic.address");	
	protected static  String NCIIC_CHECK_API = "NciicServices";
	protected  String NCIIC_CODE_FSD = "310000";
	protected  String NCIIC_CODE_YWLX = "个人贷款";
	protected  String STATUS_CHECK_EQUAL = "00";
	protected  String STATUS_CHECK_NO = "01";
	protected  String STATUS_CHECK_NULL = "02";
	protected  String CHECK_EQUAL = "一致";
	protected  String CHECK_NOEQUAL = "不一致";
	
	protected  String CODE_EQUAL = "gajx_001";
	protected  String CODE_NOEQUAL = "gajx_002";
	protected  String CODE_NOEXIST = "gajx_003";
	public  static NciicService nciicService;
	public  static NciicMultService nciicMultService;
	/**
	 * 初始化连接资源
	 * @throws PublicKeyException
	 */
	public static void init() throws PublicKeyException{
		if(license==null){
			license = loadPublicKey(BaseNciicDataSourceRequestor.class
							.getResource("/depends/ds/nciic/yushanNciic.txt")
							.getPath());
		}
		if(licenseMult==null){
			licenseMult = loadPublicKey(BaseNciicDataSourceRequestor.class
							.getResource("/depends/ds/nciic/kuaiqianNciicMult.txt")
							.getPath());
		}
		if(nciicService == null){
			try{
				HttpClientParams params = new HttpClientParams();
				params.setParameter(HttpClientParams.USE_EXPECT_CONTINUE,
				                Boolean.FALSE);
				params.setParameter(HttpClientParams.CONNECTION_MANAGER_TIMEOUT,
				        Long.parseLong(timeout+""));
				params.setParameter(HttpClientParams.SO_TIMEOUT, timeout);
				ProtocolSocketFactory easy = new EasySSLProtocolSocketFactory();
				Protocol protocol = new Protocol("https", easy, 443);
				Protocol.registerProtocol("https", protocol);
				Service serviceModel = new ObjectServiceFactory().create(NciicService.class, NCIIC_CHECK_API, null, null);
				nciicService = (NciicService) new XFireProxyFactory().create(
				serviceModel, nciic_address + NCIIC_CHECK_API);
				Client client = ((XFireProxy) Proxy.getInvocationHandler(nciicService))
				.getClient();
				client.addOutHandler(new DOMOutHandler());
				client.setProperty(CommonsHttpMessageSender.HTTP_CLIENT_PARAMS, params);
				//压缩传输
				client.setProperty(CommonsHttpMessageSender.GZIP_ENABLED, Boolean.TRUE);
				//忽略超时
				client.setProperty(CommonsHttpMessageSender.DISABLE_EXPECT_CONTINUE, "1");
				client.setProperty(CommonsHttpMessageSender.HTTP_TIMEOUT, nciic_timeout);
				client.setTimeout(timeout);				
			}catch(Exception ex){
				ex.printStackTrace();
			}			
		}
		
		if(nciicMultService == null){
			try{
				HttpClientParams params = new HttpClientParams();
				params.setParameter(HttpClientParams.USE_EXPECT_CONTINUE,
				                Boolean.FALSE);
				params.setParameter(HttpClientParams.CONNECTION_MANAGER_TIMEOUT,
				        Long.parseLong(timeout+""));
				params.setParameter(HttpClientParams.SO_TIMEOUT, timeout);
				ProtocolSocketFactory easy = new EasySSLProtocolSocketFactory();
				Protocol protocol = new Protocol("https", easy, 443);
				Protocol.registerProtocol("https", protocol);
				Service serviceModel = new ObjectServiceFactory().create(NciicMultService.class, NCIIC_CHECK_API, null, null);
				nciicMultService = (NciicMultService) new XFireProxyFactory().create(
				serviceModel, nciic_address + NCIIC_CHECK_API);
				Client client = ((XFireProxy) Proxy.getInvocationHandler(nciicMultService))
				.getClient();
				client.addOutHandler(new DOMOutHandler());
				//压缩传输
				client.setProperty(CommonsHttpMessageSender.GZIP_ENABLED, Boolean.TRUE);
				//忽略超时
				client.setProperty(CommonsHttpMessageSender.DISABLE_EXPECT_CONTINUE, "1");
				client.setProperty(CommonsHttpMessageSender.HTTP_TIMEOUT, nciic_timeout);
				client.setTimeout(timeout);
				client.setProperty(CommonsHttpMessageSender.HTTP_CLIENT_PARAMS, params);
			}catch(Exception ex){
				ex.printStackTrace();
			}
			
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
    public String executeClient( String license, String condition)
	throws MalformedURLException {
		
		String result = null;
		//调用核查方法
		result = nciicService.nciicCheck(license, condition);
		return result;
		}
    
    public String executeMultClient( String licenseMult, String condition)
    		throws MalformedURLException {
    			
    			String result = null;
    			//调用核查方法
    			result = nciicMultService.nciicAddrExactSearch(licenseMult, condition);
    			return result;
    			}
	/**
	 * 报文格式过滤
	 * @param rspBody
	 * @return
	 */
	protected String filtRspBody(String rspBody){  
		rspBody = rspBody.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>","");
		return rspBody;
	}
	/**
	 * 格式化XML
	 * @param inputXML
	 * @return
	 * @throws Exception
	 */
	public String formatXML(String inputXML) throws Exception {
	    SAXReader reader = new SAXReader();
	    Document document = reader.read(new StringReader(inputXML));
	    String requestXML = null;
	    XMLWriter writer = null;
	    if (document != null) {
	      try {
	        StringWriter stringWriter = new StringWriter();
	        OutputFormat format = new OutputFormat(" ", true);
	        writer = new XMLWriter(stringWriter, format);
	        writer.write(document);
	        writer.flush();
	        requestXML = stringWriter.getBuffer().toString();
	      } finally {
	        if (writer != null) {
	          try {
	            writer.close();
	          } catch (IOException e) {
	          }
	        }
	      }
	    }
	    return requestXML;
	  }
}
