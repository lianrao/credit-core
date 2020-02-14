/**   
* @Description: TODO(用一句话描述该文件做什么) 
* @author xiaobin.hou  
* @date 2017年2月13日 下午8:14:59 
* @version V1.0   
*/
package com.wanda.credit.ds.client.trulioo;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wanda.credit.ds.BaseDataSourceRequestor;

/**
 * @author xiaobin.hou
 *
 */
public class BaseTruliooDSRequestor extends BaseDataSourceRequestor {
	
	private static Logger log = LoggerFactory
			.getLogger(BaseTruliooDSRequestor.class);
	
	public static final String http_resp_status = "respStatus";
	public static final String http_resp_str = "respStr";
	
	private static CloseableHttpClient client;
	private static final RequestConfig requestConfig = RequestConfig.custom()
			.setConnectionRequestTimeout(1000000).setSocketTimeout(1000000)
			.setConnectTimeout(1000000).build();

	private static synchronized CloseableHttpClient getClient() {
		if (null == client) {
			SSLConnectionSocketFactory sslCSF = null;
			try {
				SSLContext sslContext = new SSLContextBuilder()
						.loadTrustMaterial(null, new TrustSelfSignedStrategy())
						.build();

				sslCSF = new SSLConnectionSocketFactory(sslContext,
						SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
			client = HttpClients.custom().setMaxConnPerRoute(20)
					.setMaxConnTotal(1000).setSSLSocketFactory(sslCSF).build();
		}

		return client;
	}
	
	public static Map<String,String> doPost(String url, Map<String, String> params,
			Map<String, String> headers, Object bodyObj,
			ContentType contentType, RequestConfig reqConfig, boolean print)
			throws Exception {
		if (null == contentType) {
			contentType = ContentType.APPLICATION_JSON;
		}
		URI uri = generateURL(url, params);
		log.info(new StringBuilder().append("HTTP Request url: ").append(url)
				.toString());
		if (print) {
			log.info(new StringBuilder().append("HTTP Request params: ")
					.append(new ObjectMapper().writeValueAsString(params))
					.toString());

			log.info(new StringBuilder().append("HTTP Request headers: ")
					.append(new ObjectMapper().writeValueAsString(headers))
					.toString());

			log.info(new StringBuilder().append("HTTP Request entitys: ")
					.append(new ObjectMapper().writeValueAsString(bodyObj))
					.toString());
		}

		HttpPost post = new HttpPost(uri);
		if (requestConfig != null)
			post.setConfig(reqConfig);
		else {
			post.setConfig(requestConfig);
		}
		if (null != bodyObj) {
			String jsonStr = new ObjectMapper().writeValueAsString(bodyObj);
			HttpEntity entity = new StringEntity(jsonStr, contentType);
			post.setEntity(entity);
		}
		for (String key : headers.keySet())
			post.setHeader(key, (String) headers.get(key));
		Map<String,String> res = execute(post);
		if (print) {
			log.info(new StringBuilder().append("HTTP Response context: \n")
					.append(res).toString());
		}

		return res;
	}
	
	private static Map<String,String> execute(HttpUriRequest request)
			throws ParseException, IOException {
		String responseStr = null;
		Map<String,String> respMap = new HashMap<String, String>();
		CloseableHttpResponse httpResponse = null;
		httpResponse = getClient().execute(request);
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		responseStr = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
		
		respMap.put(http_resp_status, statusCode + "");
		respMap.put(http_resp_str, responseStr);

		httpResponse.close();
		return respMap;
	}
	

	private static URI generateURL(String url, Map<String, String> params) {
		URI uri = null;
		try {
			URIBuilder uriBuilder = new URIBuilder(url);
			if (null != params) {
				for (Map.Entry entry : params.entrySet()) {
					uriBuilder.addParameter((String) entry.getKey(),
							(String) entry.getValue());
				}
			}
			uri = uriBuilder.build();
		} catch (URISyntaxException e) {
			log.error(e.getMessage(), e);
		}
		return uri;
	}
	/**
	 * 判断是否为json字符串
	 * */
	public boolean isGoodJson(String json){
		boolean result = false;
		try{
			JSONObject result_json = JSON.parseObject(json);
			result = true;
		}catch(Exception e){			
		}
		return result;
	}
}
