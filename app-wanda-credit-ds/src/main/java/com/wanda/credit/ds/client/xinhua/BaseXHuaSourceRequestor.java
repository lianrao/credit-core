package com.wanda.credit.ds.client.xinhua;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wanda.credit.base.util.encode.DESEncryptUTF8;
import com.wanda.credit.ds.BaseDataSourceRequestor;

public class BaseXHuaSourceRequestor extends BaseDataSourceRequestor {
	private final Logger logger = LoggerFactory.getLogger(BaseXHuaSourceRequestor.class);

	public static String Service(String trade_id,String key,String api_product,String xhua_url,String username, Map<String, String> param){
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, String> entry : param.entrySet()){
            if (builder.length() > 0){
                builder.append("&");
            }
            builder.append(entry.getKey()+"=");
            builder.append(entry.getValue());
        }
        String  desParam =  DESEncryptUTF8.encode(key, builder.toString());
        StringBuffer sbf = new StringBuffer();
        URL url = null;
        HttpURLConnection conn = null;
        BufferedReader reader = null;
        try{
            url = new URL(xhua_url);

            conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            conn.setDoOutput(true);

            byte[] bytes = ("apiKey=" + api_product + "&username=" + username + "&params=" + desParam).getBytes();
            conn.getOutputStream().write(bytes);
            conn.connect();
            InputStream inStream = conn.getInputStream();

            String strRead = null;
            reader = new BufferedReader(new InputStreamReader(inStream, "UTF-8"));
            while ((strRead = reader.readLine()) != null) {
                sbf.append(strRead);
                sbf.append("\r\n");
            }
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }finally{
            if (null != reader){
                try{
                    reader.close();
                }
                catch (IOException e)
                {
                }

            }
            if (null != conn){
                conn.disconnect();
            }
        }
        return  sbf.toString();
    }
}
