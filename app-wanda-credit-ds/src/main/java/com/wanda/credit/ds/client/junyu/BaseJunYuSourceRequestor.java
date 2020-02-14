package com.wanda.credit.ds.client.junyu;

import java.lang.reflect.Proxy;

import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.codehaus.xfire.XFire;
import org.codehaus.xfire.XFireFactory;
import org.codehaus.xfire.client.Client;
import org.codehaus.xfire.client.XFireProxy;
import org.codehaus.xfire.client.XFireProxyFactory;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.transport.http.CommonsHttpMessageSender;
import org.codehaus.xfire.transport.http.EasySSLProtocolSocketFactory;

import com.wanda.credit.ds.BaseDataSourceRequestor;

public class BaseJunYuSourceRequestor extends BaseDataSourceRequestor{
public static final int TIME_OUT_Default = 5000;
	
	/**
	 * 支持Xfire HTTPS的代码实现
	 */
	public static void executeHTTPS() {
		ProtocolSocketFactory easy = new EasySSLProtocolSocketFactory();
		Protocol protocol = new Protocol("https", easy, 443);
		Protocol.registerProtocol("https", protocol);
	}
	
	public static void setHttpParams(Object interfaceService){
		setHttpParams(interfaceService,TIME_OUT_Default);
	}
	
	/**
	 * 设置读取超时事件
	 * @param interfaceService 接口名
	 * @param iReadTimeOut 超时时间,单位毫秒
	 */
	public static void setHttpParams(Object interfaceService,int iReadTimeOut){
		Client xfireClient = ((XFireProxy) Proxy.getInvocationHandler(interfaceService)).getClient();
		HttpClientParams params = new HttpClientParams();
		
		params.setBooleanParameter(HttpClientParams.USE_EXPECT_CONTINUE, Boolean.FALSE);
//		params.setLongParameter(HttpClientParams.CONNECTION_MANAGER_TIMEOUT,3000L);  
		params.setIntParameter(HttpClientParams.SO_TIMEOUT,iReadTimeOut);
		xfireClient.setProperty(CommonsHttpMessageSender.DISABLE_KEEP_ALIVE, "1");
		xfireClient.setProperty(CommonsHttpMessageSender.DISABLE_EXPECT_CONTINUE, "1");
		xfireClient.setProperty(CommonsHttpMessageSender.HTTP_CLIENT_PARAMS,params);
	}
	
	
	public String client(String seviceUrl, String strHeadIn, String strParamIn, String strEncryValue) {
		String result = null;
		try {
			// 此为支持Xfire HTTPS的代码实现
			ProtocolSocketFactory easy = new EasySSLProtocolSocketFactory();
			Protocol protocol = new Protocol("https", easy, 448);
			Protocol.registerProtocol("https", protocol);

			Service service = new ObjectServiceFactory().create(IJYWebservice.class);

			XFire xfire = XFireFactory.newInstance().getXFire();
			XFireProxyFactory factory = new XFireProxyFactory(xfire);
			IJYWebservice client = (IJYWebservice) factory.create(service, seviceUrl);

			HttpClientParams params = new HttpClientParams();
			params.setParameter(HttpClientParams.USE_EXPECT_CONTINUE, Boolean.FALSE);
			params.setParameter(HttpClientParams.CONNECTION_MANAGER_TIMEOUT, 60000L); // 链接超时60s
			params.setParameter(HttpClientParams.SO_TIMEOUT, 60000); // 读取超时60s

			Client xfireClient = ((XFireProxy) Proxy.getInvocationHandler(client)).getClient();
			xfireClient.setProperty(CommonsHttpMessageSender.HTTP_CLIENT_PARAMS, params);

			result = client.method(strHeadIn, strParamIn, strEncryValue);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
