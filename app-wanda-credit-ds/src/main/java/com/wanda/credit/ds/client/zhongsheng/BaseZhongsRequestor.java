package com.wanda.credit.ds.client.zhongsheng;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;

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

public class BaseZhongsRequestor extends BaseDataSourceRequestor {
	private final Logger logger = LoggerFactory
			.getLogger(BaseZhongsRequestor.class);
	private static final int timeout = Integer.parseInt(DynamicConfigLoader
			.get("sys.credit.client.http.timeout"));
	protected static String qryBatchNo(String account){
		String qryBatchNo = account.toUpperCase();
		SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss"); // 14位
		String temp = sf.format(new Date());
		int random = (int) (Math.random() * 10000);
		qryBatchNo += temp + random;
		return qryBatchNo;
	}
	/**
	 * @Description:加密-16位大写
	 */
	public static String MD5encrypt16(String encryptStr) {
		return encrypt32(encryptStr).substring(8, 24).toUpperCase();
	}
	
	
	/**
	 * @Description:加密-32位小写
	 */
	public static String encrypt32(String encryptStr) {
		MessageDigest md5;
		try {
			md5 = MessageDigest.getInstance("MD5");
			byte[] md5Bytes = md5.digest(encryptStr.getBytes("UTF-8"));
			StringBuffer hexValue = new StringBuffer();
			for (int i = 0; i < md5Bytes.length; i++) {
				int val = ((int) md5Bytes[i]) & 0xff;
				if (val < 16)
					hexValue.append("0");
				hexValue.append(Integer.toHexString(val));
			}
			encryptStr = hexValue.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return encryptStr;
	}
	/**
	 * post请求
	 * 
	 * @param url
	 * @return
	 */
	public static String post(String url, String postContent)
	{
		InputStream in = null;
		HttpURLConnection connection = null;
		OutputStream out = null;
		try
		{
			URL paostUrl = new URL(url);
			// 参数配置
			connection = (HttpURLConnection) paostUrl.openConnection();
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
			connection.setRequestMethod("POST");
			connection.setDoOutput(true); // http正文内，因此需要设为true, 默认情况下是false
			connection.setDoInput(true); // 设置是否从httpUrlConnection读入，默认情况下是true
			connection.setUseCaches(false); // Post 请求不能使用缓存
			connection.setInstanceFollowRedirects(true); // URLConnection.setInstanceFollowRedirects是成员函数，仅作用于当前函数
			connection.setConnectTimeout(5000); // 设置连接主机超时时间
			connection.setReadTimeout(5000); //设置从主机读取数据超时
			connection.setReadTimeout(5000); // 设置从主机读取数据超时

			PrintWriter printout = new PrintWriter(connection.getOutputStream());
			printout.print(postContent);
			printout.flush();
			BufferedReader br = new BufferedReader(new InputStreamReader(
					connection.getInputStream(), "UTF-8"));
			StringBuffer response = new StringBuffer();
			String line;
			while ((line = br.readLine()) != null) {
				response.append(line);
			}
			br.close();
			return response.toString();
		}
		catch (Exception e)
		{
			throw new RuntimeException("post请求发生异常" + ",url=" + url, e);
		}
		finally
		{
			try
			{
				if (out != null)
				{
					out.close();
				}
				if (in != null)
				{
					in.close();
				}
				if (connection != null)
				{
					connection.disconnect();
				}
			}
			catch (IOException e)
			{
			}
		}
	}
}
