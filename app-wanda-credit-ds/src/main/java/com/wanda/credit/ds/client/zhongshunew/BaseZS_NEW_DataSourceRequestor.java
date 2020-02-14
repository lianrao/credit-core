package com.wanda.credit.ds.client.zhongshunew;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.ds.BaseDataSourceRequestor;

/**add by wj 20180705*/
public class BaseZS_NEW_DataSourceRequestor extends BaseDataSourceRequestor {
	@Autowired
	public IPropertyEngine propertyEngine;
	
	private final Logger logger = LoggerFactory.getLogger(BaseZS_NEW_DataSourceRequestor.class);
	 private static final TLSSocketConnectionFactory sslsf;


	static {
		try {
			sslsf = new TLSSocketConnectionFactory();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to create SSL Connection");
		}
	}

	public String callApi(String apiURL,Map<String,String> headers, String trade_id) throws IOException{
		String[] flags = propertyEngine.readByIds("ds_zs_new_isPrint", "ds_zs_new_timeout");
        URL url = new URL(apiURL);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setSSLSocketFactory(sslsf);
        connection.setDoOutput(true);
        connection.setRequestMethod("GET");
        connection.setReadTimeout(Integer.valueOf(flags[1]));
        connection.setConnectTimeout(Integer.valueOf(flags[1]));
        for(String key : headers.keySet()) {
            connection.addRequestProperty(key,headers.get(key));
        }
        
        if("1".equals(flags[0]))
        	logger.info("{} ZS请求URL及业务参数: {}", trade_id, url);
        connection.connect();
        InputStream inputStream;
        if (connection.getResponseCode() >= 400) {
        	inputStream = connection.getErrorStream();
        } else {
        	inputStream = connection.getInputStream();
        }
//        InputStream inputStream = connection.getInputStream();
        String result = inputStreamToString(inputStream,"utf-8");
        if(inputStream!=null)
        	inputStream.close();
        connection.disconnect();
        if("1".equals(flags[0]))
        	logger.info("{} ZS响应: {}", trade_id, result);
        return result;
    }

	
	public Map<String, String> prepareHeaders(String UID, String SECURITY_KEY, String trade_id) {
		String timestamp = System.currentTimeMillis() + "";
		String signature = encrypt(trade_id + ";" + SECURITY_KEY + ";" + timestamp + ";" + UID + ";");
		Map<String, String> headerMap = new HashMap<String, String>();
		headerMap.put("content-type", "application/json;charset=utf-8");
		headerMap.put("X-Uid", UID);
		headerMap.put("X-Timestamp", timestamp);
		headerMap.put("X-Nonce", trade_id);
		headerMap.put("X-Signature", signature);
		return headerMap;
	}
	public String inputStreamToString(InputStream in, String charset)
			throws IOException {
		int bufferSize = 1024;
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] data = new byte[bufferSize];
		int count = -1;
		while ((count = in.read(data, 0, bufferSize)) != -1)
			outStream.write(data, 0, count);

		data = null;
		return new String(outStream.toByteArray(), charset);
	}
	
    public String encrypt(String str) {
        String result = "";
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.update(str.getBytes());
            byte messageDigest[] = digest.digest();
            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            // 字节数组转换为 十六进制 数
            for (int i = 0; i < messageDigest.length; i++) {
                String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
                if (shaHex.length() < 2) {
                    hexString.append(0);
                }
                hexString.append(shaHex);
            }
            result = hexString.toString();
            return result;

        } catch (NoSuchAlgorithmException e) {
        }
        return result;
    }


}
