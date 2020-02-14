package com.wanda.credit.ds.client.juxinli.bean.mobile;

import java.util.List;

import com.wanda.credit.ds.client.juxinli.bean.ebusi.EBusiWebsite;

/**
 * 运营商、电商提交申请获取回执信息请求Bean
 * @author xiaobin.hou
 *
 */
public class GetTokenReq {
	
	private List<EBusiWebsite> selected_website;//选择的电商数据源组
	private boolean skip_mobile;//是否跳过运营商的采集
	private UserBasicInfo basic_info;//用户基本信息
	private List<Contact> contacts;//联系人信息组
	
	
	public List<EBusiWebsite> getSelected_website() {
		return selected_website;
	}
	public void setSelected_website(List<EBusiWebsite> selected_website) {
		this.selected_website = selected_website;
	}
	public UserBasicInfo getBasic_info() {
		return basic_info;
	}
	public void setBasic_info(UserBasicInfo basic_info) {
		this.basic_info = basic_info;
	}
	public List<Contact> getContacts() {
		return contacts;
	}
	public void setContacts(List<Contact> contacts) {
		this.contacts = contacts;
	}
	public boolean isSkip_mobile() {
		return skip_mobile;
	}
	public void setSkip_mobile(boolean skip_mobile) {
		this.skip_mobile = skip_mobile;
	}
	
	

}
