package com.wanda.credit.ds.client.xiaoan;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wanda.credit.base.util.ExceptionUtil;
import com.wanda.credit.ds.BaseDataSourceRequestor;
import com.wanda.credit.ds.client.xiaoan.bean.XiaoAnFace;

public class BaseXiaoAnSourceRequestor extends BaseDataSourceRequestor {
	private final static Logger logger = LoggerFactory
			.getLogger(BaseXiaoAnSourceRequestor.class);
	
	public String xiaoanHttpClient(String trade_id,String url,String xa_key,String fullpath
			,String name,String cardNo,int timeout){
		logger.info("{} 小安人脸01请求开始...",trade_id);
		String result = "";
	    CloseableHttpClient httpClient = HttpClients.custom().build();
	    RequestConfig requestConfig = RequestConfig.custom().
	    		setConnectionRequestTimeout(timeout).setSocketTimeout(timeout).setConnectTimeout(timeout).build();
	    HttpPost post = new HttpPost(url);
	    post.setConfig(requestConfig);
	    FileBody fileBody = new FileBody(new File(fullpath));
	    MultipartEntityBuilder entity = MultipartEntityBuilder.create();
	    entity.setContentType(ContentType.MULTIPART_FORM_DATA);
	    
	    XiaoAnFace req = new XiaoAnFace();
	    req.setIdCard(cardNo);
	    req.setName(name);

	    CloseableHttpResponse response = null;
		try {
			ObjectMapper mapper = new ObjectMapper();

		    entity.addTextBody("request", mapper.writeValueAsString(req), ContentType.APPLICATION_JSON);
		    entity.addPart("image", fileBody);
		    post.setEntity(entity.build());
		    post.addHeader("xa-key", xa_key);
		    logger.info("{} 小安人脸http请求开始...",trade_id);
		    response = httpClient.execute(post);
		    logger.info("{} 小安人脸http请求结束",trade_id);
		    if (response.getStatusLine().getStatusCode() == 200) {
		      HttpEntity entitys = response.getEntity();
		      BufferedReader reader = new BufferedReader(new InputStreamReader(entitys.getContent()));
		      result = reader.readLine();
		      logger.info("{} 小安人脸http请求返回信息:{}",trade_id,result);
		    } else {
		      HttpEntity r_entity = response.getEntity();
		      String err_line = EntityUtils.toString(r_entity);
		      logger.info("{} 小安人脸http请求错误返回信息:{}",trade_id,err_line);
		    }
		} catch (Exception e) {
			 logger.info("{} 小安人脸http请求异常:{}",trade_id,ExceptionUtil.getTrace(e));
		}finally{
			try {
				if(httpClient!=null)
					httpClient.close();
				if(response!=null)
					response.close();
			} catch (IOException e) {
			}		   
		}
	    return result;	    
	  }
}
