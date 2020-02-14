package com.wanda.credit.ds.client.baiduFace.bean;

public class FaceBean {
	private String error_code;
	private String error_msg;
	private String log_id;
	private String timestamp;
	private String cached;
	private ResultBean result;
	
	public String getError_code() {
		return error_code;
	}
	public void setError_code(String error_code) {
		this.error_code = error_code;
	}
	public String getError_msg() {
		return error_msg;
	}
	public void setError_msg(String error_msg) {
		this.error_msg = error_msg;
	}
	public String getLog_id() {
		return log_id;
	}
	public void setLog_id(String log_id) {
		this.log_id = log_id;
	}
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	public String getCached() {
		return cached;
	}
	public void setCached(String cached) {
		this.cached = cached;
	}
	public ResultBean getResult() {
		return result;
	}
	public void setResult(ResultBean result) {
		this.result = result;
	}
}
