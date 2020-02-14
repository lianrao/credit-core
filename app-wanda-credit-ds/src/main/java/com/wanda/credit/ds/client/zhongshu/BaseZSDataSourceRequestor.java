package com.wanda.credit.ds.client.zhongshu;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.security.Key;
import java.util.List;
import java.util.Locale;

import javax.crypto.Cipher;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bankcomm.gbicc.util.base64.BASE64Decoder;
import com.bankcomm.gbicc.util.base64.BASE64Encoder;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.wanda.credit.base.converter.WsDateConverter;
import com.wanda.credit.base.converter.WsDoubleConverter;
import com.wanda.credit.base.converter.WsIntConverter;
import com.wanda.credit.base.domain.BaseDomain;
import com.wanda.credit.common.props.DynamicConfigLoader;
import com.wanda.credit.ds.BaseDataSourceRequestor;

public class BaseZSDataSourceRequestor extends BaseDataSourceRequestor{
	private final  Logger logger = LoggerFactory.getLogger(BaseZSDataSourceRequestor.class);
//	private String address;
//	public static EntInfoQueryService entInfoQueryService = null;
//	private static final int timeout = 200;
	public static final int timeout = Integer.parseInt(DynamicConfigLoader.get("sys.credit.client.http.timeout"));
	/**
	 * 指定加密算法为RSA
	 */
	private static final String ALGORITHM = "RSA";
	/**
	 * 公钥
	 * */
	private static Key PublicKey;
	/**
	 * 私钥
	 * */
	private static Key PrivateKey;
	
	/**
	 * 初始化连接资源
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws PublicKeyException
	 */
	public static void init() throws Exception{
		if(PublicKey==null){
			ObjectInputStream ois =null;
			ois = new ObjectInputStream(new FileInputStream(
					BaseZSDataSourceRequestor.class.getResource(
							"/depends/ds/zs/PublicKey.crt").getFile()));
			PublicKey = (Key) ois.readObject();
			ois.close();
		}
//		if(httpClientPolicy == null){
//			httpClientPolicy =  new  HTTPClientPolicy();  
//			httpClientPolicy.setConnectionTimeout(timeout); 
//			httpClientPolicy.setAllowChunking( false );   
//			httpClientPolicy.setReceiveTimeout(timeout);
//		}
//		if (entInfoQueryService == null) {
//			JaxWsProxyFactoryBean j = new JaxWsProxyFactoryBean();
//			j.setAddress(address);
//			j.setServiceClass(EntInfoQueryService.class);
//			entInfoQueryService = (EntInfoQueryService) j.create();
//			
//			// 超时设置  
//			Client proxy = ClientProxy.getClient(entInfoQueryService);   
//			HTTPConduit conduit = (HTTPConduit) proxy.getConduit();  
//			conduit.setClient(httpClientPolicy);   
//
//		}
	}
	/**
	 * 私钥加密
	 * 
	 * @param source
	 *            源数据
	 * @return 加密后数据
	 */
	public static String PrivateEncrypt(String source) {
		return doEncrypt(source, PrivateKey);
	}
	/**
	 * 公钥加密
	 * 
	 * @param source
	 *            源数据
	 * @return 加密后数据
	 */
	public static String PublicEncrypt(String source) {
		return doEncrypt(source, PublicKey);
	}

	/**
	 * 公钥解密
	 * 
	 * @param cryptograph
	 *            密文
	 * @return 解密后数据
	 */
	public String PublicDecrypt(String cryptograph) {
		return doDecrypt(cryptograph, PublicKey);
	}

	/**
	 * 加密
	 * 
	 * @param source
	 *            源数据
	 * @param key
	 *            加密KEY
	 * @return 加密后数据
	 */
	private static String doEncrypt(String source, Key key) {
		try {
			// 得到Cipher对象来实现对源数据的RSA加密
			Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, key);
			// 执行加密操作
			byte[] b1 = cipher.doFinal(source.getBytes("UTF-8"));
			BASE64Encoder encoder = new BASE64Encoder();
			return encoder.encode(b1);
		} catch (Exception e) {
		}
		return null;
	}

	
	/**
	 * 解密
	 * 
	 * @param cryptograph
	 *            密文
	 * @param key
	 *            解密KEY
	 * @return 解密后数据
	 */
	private static String doDecrypt(String cryptograph, Key key) {
		try {
			Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.DECRYPT_MODE, key);
			BASE64Decoder decoder = new BASE64Decoder();
			byte[] b1 = decoder.decodeBuffer(cryptograph);
			// 执行解密操作
			byte[] b = cipher.doFinal(b1);
			return new String(b, "UTF-8");
		} catch (Exception e) {
		}
		return null;
	}
	/**
	 * 私钥解密
	 * 
	 * @param cryptograph
	 *            密文
	 * @return 解密后数据
	 */
	public static String PrivateDecrypt(String cryptograph) {
		return doDecrypt(cryptograph, PrivateKey);
	}
	
	/**
	 * 解析并转换成对象
	 * @param e
	 * @param classs
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T extends BaseDomain> T convert2Object(Element e, Class<T> classs) {
		XStream stream = new XStream(new DomDriver());
		stream.registerConverter(new WsDateConverter());
		stream.registerConverter(new WsIntConverter());
		stream.registerConverter(new WsDoubleConverter());
		stream.alias("ITEM", classs);
		return (T) stream.fromXML(e.asXML());
	}
	/**
	 * 拼接需要的xml类型（加密前）
	 * @param uid
	 * @param key
	 * @param keyType
	 * @param orders
	 * @param pwd
	 * @return
	 */
	public String toXmlStr(String uid, String key, String keyType,
			String orders, String pwd) {
		Document doc = DocumentHelper.createDocument();
		doc.setXMLEncoding("UTF-8");
		Element elementRoot = doc.addElement("DATA").addElement("ORDER");
		elementRoot.addElement("UID").setText(uid);
		elementRoot.addElement("KEY").setText(key);
		elementRoot.addElement("KEYTYPE").setText(keyType);
		elementRoot.addElement("ORDERTYPE").setText(orders);
		if (pwd != null) {
			elementRoot.addElement("PASSWORD").setText(pwd);
		}
		return doc.asXML();
	}
	/**
	 * 拼接需要的xml类型(加密后)
	 * @param key
	 * @param value
	 * @return
	 */
	public String toXmlStr(String key, String value) {
		Document doc = DocumentHelper.createDocument();
		doc.setXMLEncoding("UTF-8");
		Element elementRoot = doc.addElement("DATA");
		elementRoot.addElement("KEY").setText(key);
		elementRoot.addElement("VALUE").setText(value);
		return doc.asXML();
	}
	/**
	 * 获取节点内容 根据父节点名和子节点名
	 * @param xmlStr xml格式字符串
	 * @param nodeName 子节点名
	 * @param rootNodeName 父节点名
	 * @return 节点内容
	 */
	public String getXmlNodeText(String xmlStr, String nodeName,
			String... rootNodeName) {
		try {
			// 获取父节点
			Element elementRoot = getXmlNode(xmlStr, rootNodeName);
			if (elementRoot != null) {
				Element element = elementRoot.element(nodeName
						.toUpperCase(Locale.getDefault()));
				if (element != null) {
					return element.getText();
				} else {
					return "";
				}
			} else {
				return "";
			}
		} catch (Exception e) {
			logger.error("XML解析错误:错误原因如下:" + e.getMessage());
		}
		return null;
	}
	/**
	 * 获取节点对象 根据父节点名
	 * @param xmlStr  xml格式字符串
	 * @param rootNodeName  父节点名
	 * @return 节点对象
	 */
	public Element getXmlNode(String xmlStr, String... rootNodeName)
			throws Exception {
		// 将字符串转为XML对象
		Document doc = DocumentHelper.parseText(xmlStr);
		Element elementRoot = doc.getRootElement();
		if (rootNodeName!=null && rootNodeName.length>0) {
			for (String nodeName : rootNodeName) {
				if (elementRoot != null) {
					elementRoot = elementRoot.element(nodeName
							.toUpperCase(Locale.getDefault()));
				} else {
					break;
				}
			}
		}
		return elementRoot;
	}
	/**
	 * 获取节点对象 根据父节点名
	 * @param xmlStr  xml格式字符串
	 * @param rootNodeName  父节点名
	 * @return 节点对象
	 */
	@SuppressWarnings("unchecked")
	public List<Element> getXmlNodeList(String xmlStr, String rootNodeName)
			throws Exception {
		// 将字符串转为XML对象
		Document doc = DocumentHelper.parseText(xmlStr);
		Element elementRoot = doc.getRootElement();
		if (elementRoot != null) {
			elementRoot = elementRoot.element(rootNodeName
					.toUpperCase(Locale.getDefault()));
			if(elementRoot!=null)
				return elementRoot.elements();
		} 
		return null;
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
