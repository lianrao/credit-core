package com.wanda.credit.ds.client.yuanjian;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.ClientProtocolException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wanda.credit.base.util.EncryptionHelper.RSAHelper.PublicKeyException;
import com.wanda.credit.base.util.RequestHelper;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.ds.BaseDataSourceRequestor;
import com.wanda.credit.ds.client.yuanjian.rsa.RsaCodingUtil;
import com.wanda.credit.ds.client.yuanjian.rsa.RsaReadUtil;

public class BaseYuanJSourceRequestor extends BaseDataSourceRequestor {
	private final static Logger logger = LoggerFactory
			.getLogger(BaseYuanJSourceRequestor.class);
	public static PrivateKey yj_privatekey = null;
	/**
	 * 初始化连接资源
	 * @throws PublicKeyException
	 */
	public static void init() throws PublicKeyException{
		String cerPath = "/depends/ds/yuanjian/ktfosafer_20181130_pri.pfx";
		String pfxpwd ="yjkj999";// 私钥密码
		if(yj_privatekey==null){
			logger.info("远鉴加载秘钥路径:"+cerPath);
			yj_privatekey = RsaReadUtil.getPrivateKeyFromFile(cerPath,pfxpwd);
			logger.info("远鉴加载秘钥完成:"+yj_privatekey);
		}
	}
	public  String doYuanJian(String url,String member_id,String terminal_id,String trade_id,
			String id_card,String name,String photo,int timeout) 
			throws KeyManagementException, NoSuchAlgorithmException, ClientProtocolException, IOException {
		String trade_date = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());// 订单日期	
		
		/** 组装参数  **/
		Map<Object, Object> ArrayData = new HashMap<Object, Object>();
		ArrayData.put("member_id", member_id);
		ArrayData.put("terminal_id", terminal_id);
		ArrayData.put("trans_id", trade_id);
		ArrayData.put("trade_date", trade_date);
		ArrayData.put("id_card", id_card);
		ArrayData.put("id_holder", name);
		ArrayData.put("is_photo", "noPhoto");
		ArrayData.put("alivedet", "false");
		String XmlOrJson = "";
		JSONObject jsonObjectFromMap = JSONObject.fromObject(ArrayData);
		XmlOrJson = jsonObjectFromMap.toString();
		logger.info("{} 请求明文:{}" ,trade_id, XmlOrJson);
		
		/** base64 编码 **/
		String base64str = new String(new Base64().encode(XmlOrJson.getBytes("UTF-8")));
		String data_content = RsaCodingUtil.encryptByPriPfxFile(base64str);//加密数据
		logger.info("{} 加密串:{}",trade_id,data_content);
		
		/**============== http 请求==================== **/
		Map<String, String> params =new HashMap<String,String>();
		params.put("member_id", member_id);
		params.put("terminal_id", terminal_id);
		params.put("data_type", "json");
		params.put("data_content", data_content);
		params.put("photo", StringUtil.replaceBlank(photo));
        logger.info("{} 远鉴远程请求开始...",trade_id);
        String PostString = "";
        if(!url.startsWith("https:")){
        	PostString = RequestHelper.doPost(url,params,false,timeout);
        }else{
        	PostString = RequestHelper.doPost(url,params,true,timeout);
        }
	    
		logger.info("{} 远鉴远程请求结束",trade_id);
		logger.info("{} 远鉴远程请求返回：{}",trade_id,PostString);
		if(PostString.isEmpty()){//判断参数是否为空
			logger.info("{} 返回数据为空",trade_id);
		}		
		return PostString;
	}
	public  String doYuanJianPolice(String url,String member_id,String terminal_id,String trade_id,
			String id_card,String name,int time_out) 
			throws KeyManagementException, NoSuchAlgorithmException, ClientProtocolException, IOException {
		String trans_id=""+System.currentTimeMillis();// 商户订单号
		String trade_date = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());// 订单日期	
		
		/** 组装参数  **/
		Map<Object, Object> ArrayData = new HashMap<Object, Object>();
		ArrayData.put("member_id", member_id);
		ArrayData.put("terminal_id", terminal_id);
		ArrayData.put("trans_id", trade_id);
		ArrayData.put("trade_date", trade_date);
		ArrayData.put("id_card", id_card);
		ArrayData.put("id_holder", name);
		ArrayData.put("is_photo", "noPhoto");
		ArrayData.put("industry_type", "A7");
		String XmlOrJson = "";
		JSONObject jsonObjectFromMap = JSONObject.fromObject(ArrayData);
		XmlOrJson = jsonObjectFromMap.toString();
		logger.info("{} 请求明文:{}" ,trade_id, XmlOrJson);
		
		/** base64 编码 **/
		String base64str = new String(new Base64().encode(XmlOrJson.getBytes("UTF-8")));
		String data_content = RsaCodingUtil.encryptByPriPfxFile(base64str);//加密数据
		logger.info("{} 加密串:{}",trade_id,data_content);
		
		/**============== http 请求==================== **/
		Map<String, String> params =new HashMap<String,String>();
		params.put("member_id", member_id);
		params.put("terminal_id", terminal_id);
		params.put("data_type", "json");
		params.put("data_content", data_content);
        logger.info("{} 远鉴远程请求开始...",trade_id);
	    String PostString = RequestHelper.doPost(url,params,true,time_out);
		logger.info("{} 远鉴远程请求结束",trade_id);
		logger.info("{} 远鉴远程请求返回：{}",trade_id,PostString);
		if(PostString.isEmpty()){//判断参数是否为空
			logger.info("{} 返回数据为空",trade_id);
		}		
		return PostString;
	}
}
