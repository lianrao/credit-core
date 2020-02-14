package com.wanda.credit.ds.client.juxinli.bean.ebusi;

import java.io.Serializable;
/**
 * 数据源信息
 * @author xiaobin.hou
 *
 */
public class MobileEBusiDataSource implements Serializable {
	private static final long serialVersionUID = -8988574347113845722L;
	
	private String id;//数据源id
	private String website;//数据源英文名称
	private String name;//数据源中文名称
	private String category;//数据源分类英文名称
	private String category_name;//数据源分类中文名称
	private DataSourceTime create_time;
	private DataSourceTime update_time;
	private String offline_times;
	private String status;//服务状态 0-开发 1-上线  2-下线
	private String website_code;//数据源编号
	private String reset_pwd_method;//能否重置密码
	private String sms_required;
	private String required_captcha_user_identified;
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getWebsite() {
		return website;
	}
	public void setWebsite(String website) {
		this.website = website;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getCategory_name() {
		return category_name;
	}
	public void setCategory_name(String category_name) {
		this.category_name = category_name;
	}
	public DataSourceTime getCreate_time() {
		return create_time;
	}
	public void setCreate_time(DataSourceTime create_time) {
		this.create_time = create_time;
	}
	public DataSourceTime getUpdate_time() {
		return update_time;
	}
	public void setUpdate_time(DataSourceTime update_time) {
		this.update_time = update_time;
	}
	public String getOffline_times() {
		return offline_times;
	}
	public void setOffline_times(String offline_times) {
		this.offline_times = offline_times;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getWebsite_code() {
		return website_code;
	}
	public void setWebsite_code(String website_code) {
		this.website_code = website_code;
	}
	public String getReset_pwd_method() {
		return reset_pwd_method;
	}
	public void setReset_pwd_method(String reset_pwd_method) {
		this.reset_pwd_method = reset_pwd_method;
	}
	public String getSms_required() {
		return sms_required;
	}
	public void setSms_required(String sms_required) {
		this.sms_required = sms_required;
	}
	public String getRequired_captcha_user_identified() {
		return required_captcha_user_identified;
	}
	public void setRequired_captcha_user_identified(
			String required_captcha_user_identified) {
		this.required_captcha_user_identified = required_captcha_user_identified;
	}
	
	
	public String toString() {
		return "MobileEBusiDataSource [id=" + id + ", website=" + website
				+ ", name=" + name + ", category=" + category
				+ ", category_name=" + category_name + ", create_time="
				+ create_time + ", update_time=" + update_time
				+ ", offline_times=" + offline_times + ", status=" + status
				+ ", website_code=" + website_code + ", reset_pwd_method="
				+ reset_pwd_method + ", sms_required=" + sms_required
				+ ", required_captcha_user_identified="
				+ required_captcha_user_identified + "]";
	}
	
	
	

}
