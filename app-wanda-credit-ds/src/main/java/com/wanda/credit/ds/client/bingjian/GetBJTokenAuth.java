package com.wanda.credit.ds.client.bingjian;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.wanda.credit.base.util.RequestHelper;

/**
 * @author by YingLong on 2019/1/26
 */
public class GetBJTokenAuth {
	private static Logger logger = LoggerFactory.getLogger(GetBJTokenAuth.class);
    /**
     * 获取请求接口的 token_id
     *
     * @return token_id
     * @throws Exception 登录异常
     */
    public static String getTokenId(String trade_id,String auth_url,String username,String passward,int time_out) throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("merchant_name", username);
        params.put("merchant_pwd", DigestUtils.md5Hex(passward));
        String response = "";
        if(!auth_url.startsWith("https:")){
        	response = RequestHelper.doPost(auth_url,params,false,time_out);
        }else{
        	response = RequestHelper.doPost(auth_url,params,true,time_out);
        }
        logger.info("{} 获取token返回结果:{}",trade_id,response);
        JSONObject responseJson = JSONObject.parseObject(response);
        String responseCode = responseJson.getString("response_code");
        if (!"00".equals(responseCode)) {
            throw new Exception(String.format("Ick authentication failed：%s", responseJson.getString("response_msg")));
        }
        String tokenId = responseJson.getString("token_id");
        return tokenId;
    }

}
