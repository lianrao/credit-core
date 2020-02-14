package com.wanda.credit.ds.client.yidaoOcr;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.baidu.aip.client.BaseClient;
import com.baidu.aip.http.AipHttpClient;
import com.baidu.aip.http.AipRequest;
import com.baidu.aip.http.AipResponse;

public class BaiduOcrRequestor extends BaseClient{
	protected BaiduOcrRequestor(String appId, String apiKey, String secretKey) {
		super(appId, apiKey, secretKey);
	}
	private final Logger logger = LoggerFactory
			.getLogger(BaiduOcrRequestor.class);

	public JSONObject getOcrRsp(String trade_id,String url,String basecontet,
			String templateSign, HashMap<String, String> options) throws Exception{
    	AipRequest request = new AipRequest();
    	JSONObject res = null;
	    preOperation(request);
	    request.addBody("image", basecontet);
	    request.addBody("templateSign", templateSign);
	    if (options != null) {
	      request.addBody(options);
	    }
	    request.setUri(url);
	    logger.info("{} 百度OCR请求开始...",trade_id);
	    postOperation(request);
		AipResponse response = AipHttpClient.post(request);		
	    String resData = response.getBodyStr();
	    logger.info("{} 百度OCR请求结束,返回结果:{}",trade_id,resData);
	    Integer status = Integer.valueOf(response.getStatus());
	    if ((status.equals(Integer.valueOf(200))) && (!resData.equals(""))) {
	    	res = JSONObject.parseObject(resData);
	    }
	    return res;
    }
}
