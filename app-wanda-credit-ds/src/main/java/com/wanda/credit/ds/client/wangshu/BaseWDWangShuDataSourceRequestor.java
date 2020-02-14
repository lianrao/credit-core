package com.wanda.credit.ds.client.wangshu;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URLDecoder;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.wanda.credit.base.Conts;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.common.template.PropertyEngine;
import com.wanda.credit.ds.BaseDataSourceRequestor;
import com.wanda.credit.ds.dao.iface.IJuheAuthCardService;

public class BaseWDWangShuDataSourceRequestor extends BaseDataSourceRequestor
 {
	private final  Logger logger = LoggerFactory.getLogger(BaseWDWangShuDataSourceRequestor.class);
	@Autowired
	protected PropertyEngine propertyEngine;
	@Autowired
	public IJuheAuthCardService juheAuthCardService;
	private  RequestConfig requestConfig = null;
	
	int cacheTime = 1;

	protected Map<String,Object> doGetForHttpAndHttps(String url,String prefix,Map<String,Object> header, Map<String,Object> body)throws Exception{	
		CloseableHttpClient  httpClient = null;
		Map<String,Object> rspMap = new HashMap<String,Object>();
		StringBuffer result = new StringBuffer();
		if(httpClient==null){
			if(url.indexOf("https") > -1){
				SSLContext ctx = SSLContext.getInstance("TLS");
				X509TrustManager tm = new X509TrustManager() {
					public X509Certificate[] getAcceptedIssuers() {
						return null;
					}
					public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {}
					public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {}
				};
				ctx.init(null, new TrustManager[] { tm }, null);
				//指定信任密钥存储对象和连接套接字工厂  
				 LayeredConnectionSocketFactory sslSF = new SSLConnectionSocketFactory(ctx, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);  
				RegistryBuilder<ConnectionSocketFactory> registryBuilder = RegistryBuilder.<ConnectionSocketFactory>create();  
				registryBuilder.register("https", sslSF);  
				Registry<ConnectionSocketFactory> registry = registryBuilder.build();  
				//设置连接管理器  
				PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(registry);  
				//构建客户端  
				httpClient= HttpClientBuilder.create().setConnectionManager(connManager).build();  
			}else{
				httpClient = HttpClientBuilder.create().build();
			}
		}
		/**build body as params*/
		URIBuilder uriBuilder = new URIBuilder(url);
		if (null != body  && body.size()>0) {
			for (Entry<String, Object> entry : body.entrySet()) {
				Object v = entry.getValue();
				String value = (v == null) ? null : v.toString();
				uriBuilder.addParameter(entry.getKey(), value);
			}
		}
		URI uri = uriBuilder.build();
		HttpGet httpGet  = new HttpGet(uri);
		
		if(requestConfig == null){
			 requestConfig = RequestConfig.custom()
			.setSocketTimeout(Integer.parseInt(propertyEngine.readById("req_read_timeout"))).
			 setConnectTimeout(Integer.parseInt(propertyEngine.readById("req_conn_timeout"))).
			 setConnectionRequestTimeout(Integer.parseInt(propertyEngine.readById("req_getconn_timeout"))).build();
		}
		
//		requestConfig = RequestConfig.custom().setSocketTimeout(100).setConnectTimeout(100).setConnectionRequestTimeout(100)
//				 .build();
		httpGet.setConfig(requestConfig);
		/**build header*/
		if(header!=null && header.size() > 0){
			for (Entry<String, Object>  entry : header.entrySet()) {
				Object v = entry.getValue();
				String value = (v == null) ? null : v.toString();
				httpGet.setHeader(entry.getKey(),value);
			}
		}
		CloseableHttpResponse  response = httpClient.execute(httpGet);
		/**check rsp status*/		
		rspMap.put("httpstatus", response.getStatusLine() !=null ?
				response.getStatusLine().getStatusCode() : 200);		
		url = URLDecoder.decode(url, "UTF-8");
		HttpEntity entity = response.getEntity();
		InputStream in = entity.getContent();
		BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(in,"UTF-8"));
		String line="";
		while((line=bufferedReader.readLine()) != null){
			result.append(line);
		}
		in.close();
		response.close();
		rspMap.put("rspstr", result.toString());
		return rspMap;			
}
	/**
	 * 从交易返回消息中提取参数值
	 * @param key
	 * @param params_out
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Object extractValueFromResult(String key,
			Map<String, Object> params_out) {
		Object retdataObj = params_out.get(Conts.KEY_RET_DATA);
		if(retdataObj!=null){
			if(retdataObj instanceof Map){
				return ((Map<String, Object>) params_out.get(Conts.KEY_RET_DATA))
						.get(key);
			}
		}
		return null;
	}
	
	public Map<String,Object> getCache(String prefix,String name,String cardNo,String cardId,String mobile){
		
		try{
			cacheTime =  Integer.valueOf(propertyEngine.readById("bankcard_cacheTime"));			
		}catch(Exception e){
			logger.info("{} 配置缓存时间异常 {}", prefix , e.getMessage() );
			cacheTime = 1;
		}
		Map<String, Object> queryCache = juheAuthCardService.queryCache(prefix,name, cardNo, cardId, mobile, cacheTime);
		
		logger.info("{} 缓存数据为 {}" , prefix, queryCache);
		Map<String,Object> cacheData = null;
		if (queryCache != null && queryCache.containsKey("RESPCODE")) {
			Object respCodeObj = queryCache.get("RESPCODE");
			Object respDescObj = queryCache.get("RESPDESC");
			if (!StringUtil.isEmpty(respCodeObj)) {
				cacheData = new HashMap<String, Object>();
				cacheData.put("respCode", respCodeObj.toString());
				cacheData.put("respDesc", respDescObj == null ? "":respDescObj.toString());
			}
		}
		
		return cacheData;
	}
	
	
	public static void main(String[] args) {
		System.out.println(Integer.parseInt(null));
	}
 }
