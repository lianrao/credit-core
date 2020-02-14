package com.wanda.credit.ds.client.juxinli.bean;

public class AccessToken {
	
	private String expires_in;//超时时间
	private String success;//是否成功
	private String access_token;//安全凭证码
	private String note;
	
	
	public String getExpires_in() {
		return expires_in;
	}
	public void setExpires_in(String expires_in) {
		this.expires_in = expires_in;
	}
	public String getSuccess() {
		return success;
	}
	public void setSuccess(String success) {
		this.success = success;
	}
	public String getAccess_token() {
		return access_token;
	}
	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	
	public String toString() {
		return "AccessToken [expires_in=" + expires_in + ", success=" + success
				+ ", access_token=" + access_token + ", note=" + note + "]";
	} 
	
	

}
