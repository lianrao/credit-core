package com.wanda.credit.ds.client.pengyuan;

import javax.xml.rpc.ServiceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wanda.credit.base.util.EncryptionHelper.RSAHelper.PublicKeyException;
import com.wanda.credit.common.props.DynamicConfigLoader;
import com.wanda.credit.ds.BaseDataSourceRequestor;

public class BasePengYuanDataSourceRequestor extends BaseDataSourceRequestor {
	private final static Logger logger = LoggerFactory.getLogger(BasePengYuanDataSourceRequestor.class);
	protected String userId;
	protected String userPwd;
	protected String queryType;
	protected String reportIds;
	protected static WebServiceSingleQueryOfUnzipSoapBindingStub  stub;
	protected static WebServiceSingleQueryOfUnzipSoapBindingStub  oldStub;
	protected static WebServiceSingleQueryOfUnzipSoapBindingStub  ostaStub;
	
	
	/**
	 * 初始化连接资源
	 * @throws PublicKeyException
	 */
	public static void init() throws PublicKeyException{
		if(stub == null){
			try {
				stub = (WebServiceSingleQueryOfUnzipSoapBindingStub) new WebServiceSingleQueryOfUnzipServiceLocator()
						.getWebServiceSingleQueryOfUnzip();
				oldStub = (WebServiceSingleQueryOfUnzipSoapBindingStub) new WebServiceSingleQueryOfUnzipServiceOldLocator()
				.getWebServiceSingleQueryOfUnzip();
				ostaStub = (WebServiceSingleQueryOfUnzipSoapBindingStub) new WebServiceSingleQueryOfUnzipServiceOstaLocator()
						.getWebServiceSingleQueryOfUnzip();
				stub.setTimeout(Integer.parseInt(DynamicConfigLoader.get("sys.credit.client.http.timeout")));
				oldStub.setTimeout(Integer.parseInt(DynamicConfigLoader.get("sys.credit.client.http.timeout")));
				ostaStub.setTimeout(Integer.parseInt(DynamicConfigLoader.get("sys.credit.client.http.timeout")));
			} catch (ServiceException e) {
				logger.error("连接鹏元服务器失败!");
				e.printStackTrace();
			}
		}
	}
	/**
	 * 报文格式过滤
	 * @param rspBody
	 * @return
	 */
	protected String filtRspBody(String rspBody){
		rspBody = rspBody.replace("<![CDATA[", "");
		rspBody = rspBody.replace("<?xml version=\"1.0\" encoding=\"GBK\" ?>","");
		rspBody = rspBody.replace("]]>", "");
		return rspBody;
	}
	
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserPwd() {
		return userPwd;
	}

	public void setUserPwd(String userPwd) {
		this.userPwd = userPwd;
	}

	public String getQueryType() {
		return queryType;
	}

	public void setQueryType(String queryType) {
		this.queryType = queryType;
	}

	public String getReportIds() {
		return reportIds;
	}
	public void setReportIds(String reportIds) {
		this.reportIds = reportIds;
	}
}
