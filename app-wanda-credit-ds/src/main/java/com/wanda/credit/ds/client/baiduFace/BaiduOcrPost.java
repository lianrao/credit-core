package com.wanda.credit.ds.client.baiduFace;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baidu.aip.client.BaseClient;
import com.baidu.aip.http.AipRequest;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.enums.CRSStatusEnum;
/**
 * @author liunan
 * @version 百度OCR请求类
 */
public class BaiduOcrPost extends BaseClient {
	private Logger logger = LoggerFactory.getLogger(BaiduOcrPost.class);
    protected BaiduOcrPost(String appId, String apiKey, String secretKey) {
		super(appId, apiKey, secretKey);
	}
    public JSONObject getOcrRsp(String side,
    		String url,String image,String trade_id)
    		throws Exception{
    	logger.info("{} 百度OCR请求开始...",trade_id);
    	AipRequest request = new AipRequest();
    	preOperation(request);
	    request.addBody("image", image);
	    request.addBody("id_card_side", side);
	    request.setUri(url);
	    Long start_time = System.currentTimeMillis();
	    postOperation(request);
	    Long end_time = System.currentTimeMillis();
	    logger.info("{} 百度OCR请求结束,耗时:{}",trade_id,(end_time-start_time)+" ms");
	    return requestServer(request);
    }
    public String buildRsp(String trade_id,JSONObject json,Map<String,Object> rets,String resource_tag,String side)
    		throws JSONException{
    	logger.info("{} 出参包装开始...",trade_id);
    	if(json == null){
			rets.clear();
			rets.put(Conts.KEY_RET_STATUS,
					CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, "数据源请求失败!");
			resource_tag = Conts.TAG_SYS_ERROR;
			rets.put(Conts.KEY_RET_TAG, new String[] { resource_tag });
			return resource_tag;
		}
		String error_code = json.getString("direction");
		String status = json.getString("image_status");
		if("0".equals(error_code) && "normal".equals(status)){
			HashMap<String, Object> retdata = new HashMap<String, Object>();
			HashMap<String, String> result = new HashMap<String, String>();
			com.alibaba.fastjson.JSONObject data = com.alibaba.fastjson.JSONObject.parseObject(json.getString("words_result"));
			if("front".equals(side)){
				logger.info("{} 正面解析开始...",trade_id);
				com.alibaba.fastjson.JSONObject address = com.alibaba.fastjson.JSONObject.parseObject(data.getString("住址"));
				com.alibaba.fastjson.JSONObject cardNo = com.alibaba.fastjson.JSONObject.parseObject(data.getString("公民身份号码"));
				com.alibaba.fastjson.JSONObject birday = com.alibaba.fastjson.JSONObject.parseObject(data.getString("出生"));
				com.alibaba.fastjson.JSONObject name = com.alibaba.fastjson.JSONObject.parseObject(data.getString("姓名"));
				com.alibaba.fastjson.JSONObject sex = com.alibaba.fastjson.JSONObject.parseObject(data.getString("性别"));
				com.alibaba.fastjson.JSONObject nation = com.alibaba.fastjson.JSONObject.parseObject(data.getString("民族"));
				result.put("name", name.getString("words"));
				result.put("gender", sex.getString("words"));
				result.put("nation", nation.getString("words"));
				result.put("birthdate", birday.getString("words"));
				result.put("address", address.getString("words"));
				result.put("idno", cardNo.getString("words"));
				result.put("side", side);
				result.put("head_image", "");
				result.put("cropped_image", "");
			}else{
				logger.info("{} 反面解析开始...",trade_id);
				com.alibaba.fastjson.JSONObject expire_date = com.alibaba.fastjson.JSONObject.parseObject(data.getString("失效日期"));
				com.alibaba.fastjson.JSONObject begin_date = com.alibaba.fastjson.JSONObject.parseObject(data.getString("签发日期"));
				com.alibaba.fastjson.JSONObject issue = com.alibaba.fastjson.JSONObject.parseObject(data.getString("签发机关"));

				result.put("issuedby", issue.getString("words"));
				result.put("validthru", begin_date.getString("words")+"-"+expire_date.getString("words"));
				result.put("side", "back");
				result.put("cropped_image", "");
			}			
			retdata.put("result", result);
			resource_tag = Conts.TAG_TST_SUCCESS;
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
			rets.put(Conts.KEY_RET_DATA, retdata);
			rets.put(Conts.KEY_RET_MSG, "交易成功!");
			rets.put(Conts.KEY_RET_TAG, new String[] { resource_tag });
		}else{
			resource_tag = Conts.TAG_SYS_ERROR;
			rets.clear();
			rets.put(
					Conts.KEY_RET_STATUS,
					CRSStatusEnum.STATUS_FAILED_DS_YIDAO_RECOGNITION_ERRO);
			rets.put(Conts.KEY_RET_MSG,
					"证件识别失败,返回原因:" + status);
			rets.put(Conts.KEY_RET_TAG, new String[] { resource_tag });
		}
		return resource_tag;
    }
}
