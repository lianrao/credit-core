package com.wanda.credit.ds.client.ppxin.bean;

public class PPXinResBean {
	private String resp_serial;
	private String resp_code;
	private String resp_msg;
	private PPXinResBodyBean resp_body;
	
	public String getResp_serial() {
		return resp_serial;
	}
	public void setResp_serial(String resp_serial) {
		this.resp_serial = resp_serial;
	}
	public String getResp_code() {
		return resp_code;
	}
	public void setResp_code(String resp_code) {
		this.resp_code = resp_code;
	}
	public String getResp_msg() {
		return resp_msg;
	}
	public void setResp_msg(String resp_msg) {
		this.resp_msg = resp_msg;
	}
	public PPXinResBodyBean getResp_body() {
		return resp_body;
	}
	public void setResp_body(PPXinResBodyBean resp_body) {
		this.resp_body = resp_body;
	}
	
}
