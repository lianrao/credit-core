package com.wanda.credit.ds.client.juxinli.bean.reportNew.po;
/**
 * 用户的黑名单信息	DICT
 * @author xiaobin.hou
 *
 */
public class CheckBlackInfo {

	private String phone_gray_score;

	private String contacts_class1_blacklist_cnt;

	private String contacts_class2_blacklist_cnt;

	private String contacts_class1_cnt;

	private String contacts_router_cnt;

	private String contacts_router_ratio;

	public String getPhone_gray_score() {
		return phone_gray_score;
	}

	public void setPhone_gray_score(String phone_gray_score) {
		this.phone_gray_score = phone_gray_score;
	}

	public String getContacts_class1_blacklist_cnt() {
		return contacts_class1_blacklist_cnt;
	}

	public void setContacts_class1_blacklist_cnt(
			String contacts_class1_blacklist_cnt) {
		this.contacts_class1_blacklist_cnt = contacts_class1_blacklist_cnt;
	}

	public String getContacts_class2_blacklist_cnt() {
		return contacts_class2_blacklist_cnt;
	}

	public void setContacts_class2_blacklist_cnt(
			String contacts_class2_blacklist_cnt) {
		this.contacts_class2_blacklist_cnt = contacts_class2_blacklist_cnt;
	}

	public String getContacts_class1_cnt() {
		return contacts_class1_cnt;
	}

	public void setContacts_class1_cnt(String contacts_class1_cnt) {
		this.contacts_class1_cnt = contacts_class1_cnt;
	}

	public String getContacts_router_cnt() {
		return contacts_router_cnt;
	}

	public void setContacts_router_cnt(String contacts_router_cnt) {
		this.contacts_router_cnt = contacts_router_cnt;
	}

	public String getContacts_router_ratio() {
		return contacts_router_ratio;
	}

	public void setContacts_router_ratio(String contacts_router_ratio) {
		this.contacts_router_ratio = contacts_router_ratio;
	}

	@Override
	public String toString() {
		return "CheckBlackInfo [phone_gray_score=" + phone_gray_score
				+ ", contacts_class1_blacklist_cnt="
				+ contacts_class1_blacklist_cnt
				+ ", contacts_class2_blacklist_cnt="
				+ contacts_class2_blacklist_cnt + ", contacts_class1_cnt="
				+ contacts_class1_cnt + ", contacts_router_cnt="
				+ contacts_router_cnt + ", contacts_router_ratio="
				+ contacts_router_ratio + "]";
	}

	

}
