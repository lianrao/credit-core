/**   
* @Description: TODO(用一句话描述该文件做什么) 
* @author xiaobin.hou  
* @date 2016年11月1日 上午11:57:58 
* @version V1.0   
*/
package com.wanda.credit.ds.client.ji_ao.bean;

/**
 * @author xiaobin.hou
 *
 */
public class WSJiAoReqBean {
	
	private String method;
	private MobileInfo params;
	private BigDataLogin login;
	
	
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public MobileInfo getParams() {
		return params;
	}
	public void setParams(MobileInfo params) {
		this.params = params;
	}
	public BigDataLogin getLogin() {
		return login;
	}
	public void setLogin(BigDataLogin login) {
		this.login = login;
	}
	
	

}
