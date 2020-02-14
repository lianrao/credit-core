package com.wanda.credit.ds.client.zhongjin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.unionpay.udsp.sdk.http.BaseHttpSSLSocketFactory.MyX509TrustManager;
import com.wanda.credit.common.props.DynamicConfigLoader;
import com.wanda.credit.ds.BaseDataSourceRequestor;

public class BaseZJinSourceRequestor extends BaseDataSourceRequestor {
	private final Logger logger = LoggerFactory
			.getLogger(BaseZJinSourceRequestor.class);
	private static final int timeout = Integer.parseInt(DynamicConfigLoader
			.get("sys.credit.client.http.timeout"));
	protected String aijin_address;
	protected String account;
	protected String privateKey;

	protected String CODE_EQUAL = "gajx_001";
	protected String CODE_NOEQUAL = "gajx_002";
	protected String CODE_NOEXIST = "gajx_003";

	public String simpleCheck(String cardNo, String name, String sign,String yuanjin_url) {
		String url = null;
		try {
			url = yuanjin_url + "idNumber="
					+ URLEncoder.encode(cardNo, "UTF-8") + "&name="
					+ URLEncoder.encode(name, "UTF-8") + "&account=" + account
					+ "&pwd=" + privateKey + "&sign=" + sign;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String json = getHtml(url);
		return json;
	}

	static String md5(String text) {
		byte[] bts;
		try {
			bts = text.getBytes("UTF-8");
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] bts_hash = md.digest(bts);
			StringBuffer buf = new StringBuffer();
			for (byte b : bts_hash) {
				buf.append(String.format("%02X", b & 0xff));
			}
			return buf.toString();
		} catch (java.io.UnsupportedEncodingException e) {
			e.printStackTrace();
			return "";
		} catch (java.security.NoSuchAlgorithmException e) {
			e.printStackTrace();
			return "";
		}
	}

	static String getHtml(String url) {
		BufferedReader br = null;
		try {
			trustAllHttpsCertificates();
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("GET");
			con.setReadTimeout(timeout);
			br = new BufferedReader(new InputStreamReader(con.getInputStream(),
					"UTF-8"));
			StringBuffer response = new StringBuffer();
			String line;
			while ((line = br.readLine()) != null) {
				response.append(line);
			}
			// br.close();
			return response.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * wcs add 20160830
	 * 
	 * @throws Exception
	 */
	static String postHtml(String url, String postData) throws Exception {
		trustAllHttpsCertificates();
		URL obj = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
		conn.setRequestMethod("POST");
		conn.setReadTimeout(timeout);
		conn.setConnectTimeout(timeout);
		conn.setDoOutput(true);
		conn.setDoInput(true);
		PrintWriter out = new PrintWriter(conn.getOutputStream());
		out.print(postData);
		out.flush();
		BufferedReader br = new BufferedReader(new InputStreamReader(
				conn.getInputStream(), "UTF-8"));
		StringBuffer response = new StringBuffer();
		String line;
		while ((line = br.readLine()) != null) {
			response.append(line);
		}
		br.close();
		return response.toString();

	}

	public String getAijin_address() {
		return aijin_address;
	}

	public void setAijin_address(String aijin_address) {
		this.aijin_address = aijin_address;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getPrivateKey() {
		return privateKey;
	}

	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}

	public static void trustAllHttpsCertificates() throws Exception {
		javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[1];
		javax.net.ssl.TrustManager tm = new miTrustManager();
		trustAllCerts[0] = tm;
		javax.net.ssl.SSLContext sc = javax.net.ssl.SSLContext
				.getInstance("SSL");
		sc.init(null, trustAllCerts, null);
		javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(sc
				.getSocketFactory());
	}

	static class miTrustManager implements javax.net.ssl.TrustManager,
			javax.net.ssl.X509TrustManager {
		public java.security.cert.X509Certificate[] getAcceptedIssuers() {
			return null;
		}

		public boolean isServerTrusted(
				java.security.cert.X509Certificate[] certs) {
			return true;
		}

		public boolean isClientTrusted(
				java.security.cert.X509Certificate[] certs) {
			return true;
		}

		public void checkServerTrusted(
				java.security.cert.X509Certificate[] certs, String authType)
				throws java.security.cert.CertificateException {
			return;
		}

		public void checkClientTrusted(
				java.security.cert.X509Certificate[] certs, String authType)
				throws java.security.cert.CertificateException {
			return;
		}
	}

	protected String doHttpsPost(String trade_id, String url, String postData)
			throws Exception {/*
							 * StringBuffer result = new StringBuffer();
							 * org.apache.http.client.HttpClient httpClient
							 * =null; InputStream in = null; try{
							 */
		/**
		 * httpclient并发送post数据
		 */
		/*
		 * SSLContext ctx = SSLContext.getInstance("TLS"); X509TrustManager tm =
		 * new X509TrustManager() { public X509Certificate[]
		 * getAcceptedIssuers() { return null; } public void
		 * checkClientTrusted(X509Certificate[] arg0, String arg1) throws
		 * CertificateException {} public void
		 * checkServerTrusted(X509Certificate[] arg0, String arg1) throws
		 * CertificateException {} }; ctx.init(null, new TrustManager[] { tm },
		 * null); SSLSocketFactory ssf = new SSLSocketFactory(ctx,
		 * SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER); SchemeRegistry
		 * registry = new SchemeRegistry(); registry.register(new
		 * Scheme("https", 443, ssf)); ThreadSafeClientConnManager mgr = new
		 * ThreadSafeClientConnManager(registry); httpClient = new
		 * org.apache.http.impl.client.DefaultHttpClient(mgr);
		 * 
		 * 
		 * //HttpClient httpClient = new DefaultHttpClient(); HttpParams
		 * httpParams = httpClient.getParams();
		 * 
		 * HttpConnectionParams.setConnectionTimeout(httpParams, timeout);
		 * //设定10秒超时，届时会弹出Exception
		 * HttpConnectionParams.setSoTimeout(httpParams, timeout);
		 * org.apache.http.client.methods.HttpPost httpPost = new
		 * org.apache.http.client.methods.HttpPost(aijin_address);
		 * httpPost.setEntity(new StringEntity(reqData,"utf-8")); HttpResponse
		 * response = httpClient.execute(httpPost);
		 *//**
		 * 获取服务器响应
		 */
		/*
		 * HttpEntity entity = response.getEntity(); in = entity.getContent();
		 * BufferedReader bufferedReader=new BufferedReader(new
		 * InputStreamReader(in,"UTF-8")); String line="";
		 * while((line=bufferedReader.readLine()) != null){ result.append(line);
		 * } //in.close(); } catch (Exception e) {
		 * logger.error(trade_id+" doHttpsPost method error",e); }finally{ try {
		 * if(in != null){ in.close(); } } catch (IOException e) { // TODO
		 * Auto-generated catch block
		 * logger.error(trade_id+" doHttpsPost method error",e); } } return
		 * result.toString();
		 */
		// 创建SSLContext对象，并使用我们指定的信任管理器初始化
		TrustManager[] tm = { new MyX509TrustManager() {
			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			public void checkClientTrusted(X509Certificate[] arg0, String arg1) {
			}

			public void checkServerTrusted(X509Certificate[] arg0, String arg1) {
			}
		} };
		SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
		sslContext.init(null, tm, new java.security.SecureRandom());
		// 从上述SSLContext对象中得到SSLSocketFactory对象
		javax.net.ssl.SSLSocketFactory ssf = sslContext.getSocketFactory();

		URL obj = new URL(url);
		HttpsURLConnection conn = (HttpsURLConnection) obj.openConnection();
		conn.setSSLSocketFactory(ssf);
		conn.setDefaultHostnameVerifier(new HostnameVerifier() {			
			@Override
			public boolean verify(String hostname, SSLSession session) {
				return true;
			} 
		});
		conn.setRequestMethod("POST");
		conn.setReadTimeout(timeout);
		conn.setDoOutput(true);
		conn.setDoInput(true);
		PrintWriter out = new PrintWriter(conn.getOutputStream());
		out.print(postData);
		out.flush();
		BufferedReader br = new BufferedReader(new InputStreamReader(
				conn.getInputStream(), "UTF-8"));
		StringBuffer response = new StringBuffer();
		String line;
		while ((line = br.readLine()) != null) {
			response.append(line);
		}
		br.close();
		return response.toString();

	}
}
