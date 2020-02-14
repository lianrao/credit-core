/**   
* @Description: 请求数据源 集奥 BASE Requestor 
* @author xiaobin.hou  
* @date 2016年11月1日 下午3:22:56 
* @version V1.0   
*/
package com.wanda.credit.ds.client.anxin;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.wanda.credit.base.util.RequestHelper;
import com.wanda.credit.ds.BaseDataSourceRequestor;
import com.wanda.credit.ds.client.anxin.bean.CommonResult;

/**
 * @author xiaobin.hou
 *
 */
public class BaseAXinDSRequestor extends BaseDataSourceRequestor {
	
	private Logger logger = LoggerFactory.getLogger(BaseAXinDSRequestor.class);
	public CommonResult RealTimeSimpleCheck(String trade_id,String url,String name,String cardNo,String app_id,String app_key){
	    CommonResult commonResult = new CommonResult();
	    commonResult.setResult("04");
	    try {
	    	  logger.info("{} 安信公安简项查询开始...",trade_id);
		      JSONObject reqJson = new JSONObject();
		      reqJson.put("app_id", app_id);
		      reqJson.put("app_key", app_key);
		      reqJson.put("attach", "attach");
		      reqJson.put("biz_sequence_id", trade_id);
		      JSONObject userIdInfoJson = new JSONObject();
		      userIdInfoJson.put("name", name);
		      userIdInfoJson.put("idnum", cardNo);
		      reqJson.put("user_id_info", userIdInfoJson.toString());
		      Map<String, String> headers = new HashMap<String,String>();
		      String res = RequestHelper.doPost(url,null,headers,reqJson,null,false, 10000);
		      logger.info("{} 安信公安简项查询成功:{}",trade_id,res);
		      JSONObject resultJson = JSONObject.parseObject(res);
		      commonResult.setResult(resultJson.getString("result"));
		      commonResult.setResult_detail(resultJson.getString("result_detail"));
	    } catch (Exception e) {
	      e.printStackTrace();
	    }
	    return commonResult;
	  }
	public CommonResult PhotoCheck(String trade_id,String url,String name,String cardNo,String image,String app_id,String app_key){
	    CommonResult commonResult = new CommonResult();
	    commonResult.setResult("04");
	    try {
	    	  logger.info("{} 安信人脸识别查询开始...",trade_id);
		      JSONObject reqJson = new JSONObject();
		      reqJson.put("app_id", app_id);
		      reqJson.put("app_key", app_key);
		      reqJson.put("attach", "attach");
		      reqJson.put("biz_sequence_id", trade_id);
		      JSONObject userIdInfoJson = new JSONObject();
		      userIdInfoJson.put("name", name);
		      userIdInfoJson.put("idnum", cardNo);
		      userIdInfoJson.put("image", image);
		      reqJson.put("user_id_info", userIdInfoJson.toString());
		      Map<String, String> headers = new HashMap<String,String>();
		      String res = RequestHelper.doPost(url,null,headers,reqJson,null,false, 10000);
		      logger.info("{} 安信人脸识别查询成功:{}",trade_id,res);
		      commonResult = JSONObject.parseObject(res, CommonResult.class);
	    } catch (Exception e) {
	      e.printStackTrace();
	    }
	    return commonResult;
	  }
}
