package com.wanda.credit.ds.client.juxinli.bean.mobile;

public class SubmitCollReq {
	
	private String token;
	private String account;
	private String password;
	private String captcha;
	private String type;
	private String website;
	private String queryPwd;//查询密码 -add 20160829
	
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getCaptcha() {
		return captcha;
	}
	public void setCaptcha(String captcha) {
		this.captcha = captcha;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getWebsite() {
		return website;
	}
	public void setWebsite(String website) {
		this.website = website;
	}
	public String getQueryPwd() {
		return queryPwd;
	}
	public void setQueryPwd(String queryPwd) {
		this.queryPwd = queryPwd;
	}
	@Override
	public String toString() {
		return "SubmitCollReq [token=" + token + ", account=" + account
				+ ", password=" + password + ", captcha=" + captcha + ", type="
				+ type + ", website=" + website + ", queryPwd=" + queryPwd
				+ "]";
	}
	
	

}
