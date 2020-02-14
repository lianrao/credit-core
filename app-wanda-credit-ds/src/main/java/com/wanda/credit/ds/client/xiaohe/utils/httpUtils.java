package com.wanda.credit.ds.client.xiaohe.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wanda.credit.ds.client.xiaohe.XiaoHePoliceRequestor;

public class httpUtils {
	private static Logger logger = LoggerFactory.getLogger(httpUtils.class);
private static String ENCODE = "UTF-8";
	
	private static class TrustAnyTrustManager implements X509TrustManager {

		public void checkClientTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {
		}

		public void checkServerTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {
		}

		public X509Certificate[] getAcceptedIssuers() {
			return new X509Certificate[] {};
		}
	}

	private static class TrustAnyHostnameVerifier implements HostnameVerifier {
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	}
	
	/**
	 * 封装HTTPS GET方法
	 * @param url
	 * @param params Map<String, String> 
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 * @throws IOException
	 * 
	 */
	public static String httpsGet(String trade_id,String protocol,String url,String port, String api, Map<String, String> params) throws NoSuchAlgorithmException, KeyManagementException, IOException {
		String result = "";
		BufferedReader in = null;
		String urlStr = protocol +"://"+ url +":"+ port + api + "?" + getParamStr(params);
		logger.info("{} get请求的URL为：{}",trade_id,urlStr);
		SSLContext sc = SSLContext.getInstance("SSL");
		sc.init(null, new TrustManager[] { new TrustAnyTrustManager() },
				new java.security.SecureRandom());
		URL realUrl = new URL(urlStr);
		// 打开和URL之间的连接
		HttpsURLConnection connection = (HttpsURLConnection) realUrl
				.openConnection();
		// 设置https相关属性
		connection.setSSLSocketFactory(sc.getSocketFactory());
		connection.setHostnameVerifier(new TrustAnyHostnameVerifier());
		connection.setDoOutput(true);
		connection.setRequestMethod("GET");
		// 设置通用的请求属性
		connection.setRequestProperty("accept", "*/*");
		connection.setRequestProperty("connection", "Keep-Alive");
		connection.setRequestProperty("user-agent",
				"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
		connection.setConnectTimeout(7000);
		connection.setReadTimeout(7000);
		// 建立实际的连接
		connection.connect();

		// 定义 BufferedReader输入流来读取URL的响应
		in = new BufferedReader(new InputStreamReader(
				connection.getInputStream(), ENCODE));
		String line;
		while ((line = in.readLine()) != null) {
			result += line;
		}
		logger.info("{} 请求结束：{}",trade_id,result);
		if (in != null) {
			in.close();
		}
		return result;

	}
	
	/**
	 * 
	 * @param keyValueParams
	 * @return
	 */
	private static String getParamStr(Map<String, String> keyValueParams) {
		String paramStr = "";
		// 获取所有响应头字段
		Map<String, String> params = keyValueParams;
		// 获取参数列表组成参数字符串
		for (String key : params.keySet()) {
			paramStr += key + "=" + params.get(key) + "&";
		}
		// 去除最后一个"&"
		paramStr = paramStr.substring(0, paramStr.length() - 1);
		return paramStr;
	}
	
	
	
    /**
     * 
     * @param protocol 请求协议
     * @param url 请求路径
     * @param port 请求端口号
     * @param api 请求接口
     * @param paramMap 请求参数
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static String get(String protocol,String url,String port, String api, Map<String, String> paramMap) throws ClientProtocolException, IOException {
        @SuppressWarnings({ "resource", "deprecation" })
		HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet();
        List<NameValuePair> formparams = setHttpParams(paramMap);
        String param = URLEncodedUtils.format(formparams, "UTF-8");
        httpGet.setURI(URI.create(protocol +"://"+ url +":"+ port + api + "?" + param));
        HttpResponse response = httpClient.execute(httpGet);
        String httpEntityContent = getHttpEntityContent(response);
        httpGet.abort();
        
        return httpEntityContent;
    }
    
    /**
     * 设置请求参数
     * @param
     * @return
     */
    private static List<NameValuePair> setHttpParams(Map<String, String> paramMap) {
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        Set<Map.Entry<String, String>> set = paramMap.entrySet();
        for (Map.Entry<String, String> entry : set) {
            formparams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        return formparams;
    }
    /**
     * 获得响应HTTP实体内容
     * @param response
     * @return
     * @throws java.io.IOException
     * @throws java.io.UnsupportedEncodingException
     */
    private static String getHttpEntityContent(HttpResponse response) throws IOException, UnsupportedEncodingException {
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            InputStream is = entity.getContent();
            BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String line = br.readLine();
            StringBuilder sb = new StringBuilder();
            while (line != null) {
                sb.append(line + "\n");
                line = br.readLine();
            }
            return sb.toString();
        }
        return "";
    }
    
}
