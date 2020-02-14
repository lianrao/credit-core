package com.wanda.credit.ds.client.xiaohe;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.converter.WsDateConverter;
import com.wanda.credit.base.converter.WsDoubleConverter;
import com.wanda.credit.base.converter.WsIntConverter;
import com.wanda.credit.base.domain.BaseDomain;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.SSLClient;
import com.wanda.credit.ds.BaseDataSourceRequestor;


public class BasePSGDataSourceRequestor extends BaseDataSourceRequestor {
	
	private final Logger logger = LoggerFactory.getLogger(BasePSGDataSourceRequestor.class);
	/**
	 * 初始化连接资源
	 * 
	 * @throws Exception
	 */
	public static void init() throws Exception {
	}

	protected void exceptionReturn(Map<String, Object> rets, String prefix, Exception ex) {
		ex.printStackTrace();
		rets.clear();
		rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
		rets.put(Conts.KEY_RET_MSG, "数据源处理时异常! 详细信息:" + ex.getMessage());
		logger.error("{} 数据源处理时异常：{}", prefix, ex.getMessage());
	}
	
	public String getJsonMessage(String passName,String pid,String gid,String hashcode,String md5Str,String url,String NotifyUrl) throws Exception{
		String soapRequestData = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                "  <soap:Body>\n" +
                "    <PSAreport_sync_json xmlns=\"http://xiaoher.jzdata.com/\">\n" +
                "      <Hashcode>"+hashcode+"</Hashcode>\n" +
                "      <passName>"+passName+"</passName>\n" +
                "      <pid>"+pid+"</pid>\n" +
                "      <gid>"+gid+"</gid>\n" +
                "      <iMonth>12</iMonth>\n" +
                "      <reporttype>ALL</reporttype>\n" +
                "      <NotifyURL>"+NotifyUrl+"</NotifyURL>\n" +
                "      <sign>"+md5Str+"</sign>\n" +
                "    </PSAreport_sync_json>\n" +
                "  </soap:Body>\n" +
                "</soap:Envelope>";
        HttpPost httppost = new HttpPost(url);
        byte[] b = soapRequestData.getBytes("utf-8");
        HttpEntity re = new StringEntity(soapRequestData, Charset.forName("utf-8"));
        httppost.setHeader("Content-Type","text/xml; charset=utf-8");
        httppost.setEntity(re);

        HttpClient httpClient = new SSLClient();
        HttpResponse response = httpClient.execute(httppost);
        String jsonStr = EntityUtils.toString(response.getEntity());
        Document document = DocumentHelper.parseText(filtRspBody(jsonStr));
        Element root = document.getRootElement();
        List<Element> childList = root.element("Body").elements();
        List<Element> childList2 = childList.get(0).elements("PSAreport_sync_jsonResult");
        return childList2.get(0).elementText("jsonnsource");
	}
	
	public String getIncacheMessage(String passName,String pid,String gid,String hashcode,String md5Str,String url) throws Exception{
		String soapRequestData = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                "  <soap:Body>\n" +
                "    <PSAcacheInfo xmlns=\"http://xiaoher.jzdata.com/\">\n" +
                "      <Hashcode>"+hashcode+"</Hashcode>\n" +
                "      <passName>"+passName+"</passName>\n" +
                "      <pid>"+pid+"</pid>\n" +
                "      <gid>"+gid+"</gid>\n" +
                "      <sign>"+ md5Str+"</sign>\n" +
                "    </PSAcacheInfo>\n" +
                "  </soap:Body>\n" +
                "</soap:Envelope>";
        HttpPost httppost = new HttpPost(url);
        byte[] b = soapRequestData.getBytes("utf-8");
        HttpEntity re = new StringEntity(soapRequestData, Charset.forName("utf-8"));
        httppost.setHeader("Content-Type","text/xml; charset=utf-8");
        httppost.setEntity(re);

        HttpClient httpClient = new SSLClient();
        HttpResponse response = httpClient.execute(httppost);
        return EntityUtils.toString(response.getEntity());
	}
	/**
	 * 读取文件
	 */
	protected StringBuffer readJSONFile1(String fullFileName) {
		File file = new File(fullFileName);
		StringBuffer sBuffer = null;
		try {
			
			FileInputStream fis = new FileInputStream(file);
			InputStreamReader inputStreamReader = new InputStreamReader(fis, "UTF-8");
			BufferedReader in = new BufferedReader(inputStreamReader);
			sBuffer = new StringBuffer();
			String sbt =null;
			while((sbt = in.readLine())!=null){
				sBuffer.append(sbt);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return sBuffer;
	}
	
	/**
	 * 报文格式过滤
	 * @param rspBody
	 * @return
	 */
	protected String filtRspBody(String rspBody){  
		rspBody = rspBody.replace("<?xml version=\"1.0\" encoding=\"utf-8\"?>","");
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
	/**
	 * 解析并转换成对象
	 * @param e
	 * @param classs
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T extends BaseDomain> T convert2Object(Element e, Class<T> classs,String item) {
		XStream stream = new XStream(new DomDriver());
		stream.registerConverter(new WsDateConverter());
		stream.registerConverter(new WsIntConverter());
		stream.registerConverter(new WsDoubleConverter());
		stream.alias(item, classs);
		return (T) stream.fromXML(e.asXML());
	}
}
