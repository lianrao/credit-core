package com.wanda.credit.ds.client.dianwei;

import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wanda.credit.base.util.RequestHelper;
import com.wanda.credit.ds.BaseDataSourceRequestor;

public class BaseDWeiRequestor extends BaseDataSourceRequestor {
	private final static Logger logger = LoggerFactory
			.getLogger(BaseDWeiRequestor.class);
	public String getDWeiResp(String trade_id,Map<String, String> predata,
			String enkey,String channelId,String url) throws Exception {
        String msgData = JacksonUtils.serialObject(predata);

        byte[] data = AesUtils.encrypt(msgData.getBytes(), enkey.getBytes(), "ds");
        byte[] b64 = Base64.encodeBase64(data);
        char[] hex = Hex.encodeHex(b64);
        String signData = new String(hex);
 
        String postJson = "{\"channelId\":\"" + channelId + "\",\"signData\":\"" + signData + "\"}";
        logger.info("{} 点微请求开始...",trade_id);
        String result = RequestHelper.sendPostJson(url, null,postJson);
        logger.info("{} 点微请求结束:{}",trade_id,result);
        return result;
    }
}
