package com.wanda.credit.ds.client.juxinli.bean.mobile;
/**
 * 联系人信息
 * @author xiaobin.hou
 *
 */
public class Contact {
	
	private String contact_tel;//联系人电话
	private String contact_name;//联系人姓名
	//（"0":配偶，"1":父母，"2":兄弟姐妹,"3":子女,"4":同事,"5": 同学,"6": 朋友）
	private String contact_type;//联系人类型
	
	
	public String getContact_tel() {
		return contact_tel;
	}
	public void setContact_tel(String contact_tel) {
		this.contact_tel = contact_tel;
	}
	public String getContact_name() {
		return contact_name;
	}
	public void setContact_name(String contact_name) {
		this.contact_name = contact_name;
	}
	public String getContact_type() {
		return contact_type;
	}
	public void setContact_type(String contact_type) {
		this.contact_type = contact_type;
	}
	
	

}
