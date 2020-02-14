/**   
* @Description: 用户手机号基本信息 
* @author xiaobin.hou  
* @date 2016年11月1日 上午11:59:14 
* @version V1.0   
*/
package com.wanda.credit.ds.client.ji_ao.bean;

/**
 * @author xiaobin.hou
 *
 */
public class MobileInfo {
	
	private String mobile;
	private String name;
	private String idno;
	private String innerIfType;
	
	
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getIdno() {
		return idno;
	}
	public void setIdno(String idno) {
		this.idno = idno;
	}
	public String getInnerIfType() {
		return innerIfType;
	}
	public void setInnerIfType(String innerIfType) {
		this.innerIfType = innerIfType;
	}
	
	
	@Override
	public String toString() {
		return "MobileInfo [mobile=" + mobile + ", name=" + name + ", idno="
				+ idno + ", innerIfType=" + innerIfType + "]";
	}
	
	
	
	

}
