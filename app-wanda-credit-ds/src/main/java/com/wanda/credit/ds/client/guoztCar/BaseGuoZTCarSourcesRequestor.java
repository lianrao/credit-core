package com.wanda.credit.ds.client.guoztCar;

import java.net.URLEncoder;

import javax.xml.namespace.QName;
import javax.xml.rpc.ServiceException;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wanda.credit.base.util.ExceptionUtil;
import com.wanda.credit.common.props.DynamicConfigLoader;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.ds.BaseDataSourceRequestor;

public class BaseGuoZTCarSourcesRequestor extends BaseDataSourceRequestor{
	private final static  Logger logger = LoggerFactory.getLogger(BaseGuoZTCarSourcesRequestor.class);
	private static final int timeout = Integer.parseInt(DynamicConfigLoader.get("sys.credit.client.http.timeout"));
	protected  String productid;
	protected static  String trade_ids1="201609190001";
	public static Call callProd=null;

	@Autowired
	public IPropertyEngine propertyEngine;
	/**
	 * 初始化连接资源
	 * @throws ServiceException 
	 */
	public static void init() throws ServiceException{
		if(callProd == null){
			Service s = new  Service();
			callProd = (Call) s.createCall();
			callProd.setTimeout(timeout); //设置超时时间
			callProd.setOperation("translate");
	        callProd.setOperationName(new QName("http://impl.service.server.iss.com","translate"));
		}
	}
	
	/**
	 * 国政通租车获取token
	 * @param param
	 * @return String
	 * @throws Exception
	 */
	public synchronized  String getToken(String prefix,String guozt_url,boolean print){
		String token = null;
		Call callToken=null;
		String appid = propertyEngine.readById("ds_guoztCar_appid");
		String appsecret = propertyEngine.readById("ds_guozt_appsecret");
		try{
			Service s = new  Service();
			callToken = (Call) s.createCall();
			callToken.setTimeout(timeout);
			callToken.setOperation("token");
			callToken.setTargetEndpointAddress(guozt_url);	        
	        callToken.setOperationName(new QName("http://impl.service.server.iss.com","token"));    
	        
			logger.info("{}国政通租车token获取开始...", prefix);
			Gson carGson = new GsonBuilder().disableHtmlEscaping().create();
			String tokenJson = "{\"appid\":\""+appid+"\",\"appsecret\":\""+appsecret+"\"}";
			Object[] fn01 = { tokenJson };
			String tokenReturn  = (String)callToken.invoke(fn01);		
			GetToken getToken = carGson.fromJson(tokenReturn, GetToken.class);
			Object dat = getToken.getDat();
			String retStat1 = getToken.getRet();
			if("10000".equals(retStat1)){					
				AccessToken access = carGson.fromJson(dat.toString(), AccessToken.class);
				token = access.getAccess_token();
				logger.info("{}国政通租车token获取成功,token值为：{}",  prefix,token);
			}
		}catch(Exception ex){
			logger.error("{} 国政通租车token获取时异常：{}",prefix,ExceptionUtil.getTrace(ex));
		}	
		return token;
	}
	
	/**
	 * 国政通租车查询
	 * @param param
	 * @return String
	 * @throws Exception
	 */
	public String bookCar(String name,String cardNo,String prefix,String guozt_url,String access_token) throws Exception {
		Gson carGson = new GsonBuilder().disableHtmlEscaping().create();
		String security = "0"; // 是否加密0不加密 1加密
		long timestamp = System.currentTimeMillis();
		String orderid = propertyEngine.readById("ds_guoztCar_orderid");
		String encodeTimestamp = URLEncoder.encode(String.valueOf(timestamp), "UTF-8");
		String send = "access_token=" + access_token + "&productid=" + productid + "&security=" + security
				+ "&timestamp=" + encodeTimestamp;
		//对参数进行加密
		String buyerSign = ShaSignUtil.buyerSign(access_token, productid, security, encodeTimestamp);
		String newParams = send + "&sign=" + buyerSign;
		String urls = guozt_url+"&" + newParams;
		Identity identitfy = new Identity();
		identitfy.setIdcard(cardNo);
		identitfy.setName(name);
		OrderBody orderBody = new OrderBody();
		orderBody.setOrderid(orderid);
		orderBody.setPage("1");
		orderBody.setPagesize("10");
		String paramlist = carGson.toJson(identitfy);
		orderBody.setParamlist(paramlist);
		RequestBody requestBody = new RequestBody();
		requestBody.setCmd("readdata");
		requestBody.setDat(orderBody);
		requestBody.setSrc("1");
		requestBody.setVer("1");
		String sendPost = carGson.toJson(requestBody);	
		Object[] fn02 = { sendPost };
		logger.info("{}国政通租车产品接口调用开始...", prefix);
		callProd.setTargetEndpointAddress(urls);
		String translateReturn = (String)callProd.invoke(fn02);			
		logger.info("{}国政通租车产品接口调用成功！",  prefix);
		return translateReturn;			
	}

	public String getProductid() {
		return productid;
	}

	public void setProductid(String productid) {
		this.productid = productid;
	}
}
