/**   
* @Description: 请求数据源汇付天下
* @author nan.liu  
* @date 2019年8月15日 下午3:22:56 
* @version V1.0   
*/
package com.wanda.credit.ds.client.huifu;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.wanda.credit.base.util.DateUtil;
import com.wanda.credit.base.util.ExceptionUtil;
import com.wanda.credit.base.util.RequestHelper;
import com.wanda.credit.ds.BaseDataSourceRequestor;


public class BaseHuifuDSRequestor extends BaseDataSourceRequestor {
	
	private Logger logger = LoggerFactory.getLogger(BaseHuifuDSRequestor.class);
	
	public  JSONObject getBankResult(String trade_id,String request_url,String callback_url,String app_token,String appkey,
			String name,String cardNo,String cardId,String mobile,int timeout,int times) {
		logger.info("{} 汇付天下银行卡鉴权包装开始...",trade_id);
		JSONObject object = new JSONObject();
        StringBuilder authorization_infos = new StringBuilder();
        String authorization_info = authorization_infos.toString();
        String order_date = DateUtil.getSimpleDate(new Date(), "yyyyMMdd");
        String order_id = trade_id;

        String full_name = name;
        String id_number = cardNo;
        String acct_no = cardId;
        String mobile_no = mobile;
        String source_channel = "0";
        String mer_bg_url = callback_url;
        StringBuilder paramString = new StringBuilder();
        paramString.append("POST").append(request_url)
                .append(acct_no)
                .append(app_token)               
                .append(authorization_info)
                .append(full_name)
                .append(id_number)
                .append(mer_bg_url)
                .append(mobile_no)
                .append(order_date)
                .append(order_id)
                .append(source_channel);
        String sign = HMACSHA256(paramString.toString().getBytes(), appkey.getBytes());
        Map<String,String> callback_params = new HashMap<String,String>();
        Map<String,String> req_params = new HashMap<String,String>();
        req_params.put("app_token", app_token);
        req_params.put("sign", sign);
        req_params.put("order_date", order_date);
        req_params.put("order_id", order_id);
        req_params.put("acct_no", acct_no);
        req_params.put("source_channel", source_channel);
        req_params.put("authorization_info", authorization_info);
        req_params.put("full_name", full_name);
        req_params.put("mer_bg_url", mer_bg_url);
        req_params.put("id_number", id_number);
        req_params.put("mobile_no", mobile_no);
        try {
        	logger.info("{} 汇付天下调用银行卡开始...",trade_id);
            String result = RequestHelper.doPost(request_url,req_params,true,timeout);
            logger.info("{} 汇付天下调用银行卡返回:{}",trade_id,result);
            JSONObject json = JSONObject.parseObject(result);
            String seq_id = json.getString("seq_id");
            for(int i=0;i<times;i++){
            	StringBuilder callback_result = new StringBuilder();
                callback_result.append("GET").append(callback_url).
                        append(app_token).append(order_date).append(order_id).append(seq_id);
                String callback_sign = HMACSHA256(callback_result.toString().getBytes(), appkey.getBytes());
                logger.info("{} 汇付天下回调银行卡开始,循环第:{} 次",trade_id,i);
                callback_params.clear();
                callback_params.put("app_token", app_token);
                callback_params.put("order_date", order_date);
                callback_params.put("order_id", order_id);
                callback_params.put("seq_id", seq_id);
                callback_params.put("sign", callback_sign);             
                Map<String,Object> rspDataMap = RequestHelper.doGetRetFull(callback_url, callback_params, 
						new HashMap<String, String>(), false, null, "UTF-8");
                logger.info("{} 汇付天下回调银行卡结束:{}",trade_id,rspDataMap.get("res_body_str"));
                object = JSONObject.parseObject(String.valueOf(rspDataMap.get("res_body_str")));
                String return_code = object.getString("return_code");
                if (!"90111".equals(return_code)) {
                	logger.info("{} 汇付天下回调银行卡结束轮询",trade_id);
                    break;
                }
                Thread.sleep(300);               
            }
            
        } catch (Exception e) {
        	logger.info("{} 汇付天下调用银行卡异常:{}",trade_id,ExceptionUtil.getTrace(e));
        }
        return object;
    }
	public static String HMACSHA256(byte[] data, byte[] key) {
        try {
            SecretKeySpec signingKey = new SecretKeySpec(key, "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(signingKey);
            return byte2hex(mac.doFinal(data));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String byte2hex(byte[] b) {
        StringBuilder hs = new StringBuilder();
        String stmp;
        for (int n = 0; b != null && n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0XFF);
            if (stmp.length() == 1) hs.append('0');
            hs.append(stmp);
        }
        return hs.toString();
    }
}
