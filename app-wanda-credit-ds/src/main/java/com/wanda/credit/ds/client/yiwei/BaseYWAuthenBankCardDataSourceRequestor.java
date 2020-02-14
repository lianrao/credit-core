package com.wanda.credit.ds.client.yiwei;

import com.wanda.credit.ds.BaseDataSourceRequestor;

/**
 * @description  
 * @author wuchsh 
 * @version 1.0
 * @createdate 2016年9月29日 上午9:47:32 
 *  
 */
public class BaseYWAuthenBankCardDataSourceRequestor 
       extends BaseDataSourceRequestor{
	
	protected final static String urlSplit = "/";
	protected String userName;
	protected String password;
	protected String[] succCode = {"0000","9922","9923","9941","9925"};

	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String[] getSuccCode() {
		return succCode;
	}
	public void setSuccCode(String[] succCode) {
		this.succCode = succCode;
	}
	public static String getUrlsplit() {
		return urlSplit;
	}
	
	protected static String formatUrl(String url) {
		if(url.lastIndexOf(urlSplit) < url.length()-1){
			return url + "/";
		}
		return url;
	}
	
}
