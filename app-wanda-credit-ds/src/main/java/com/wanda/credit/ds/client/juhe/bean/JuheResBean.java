package com.wanda.credit.ds.client.juhe.bean;

public class JuheResBean {
	private String reason;
	private String resultcode;
	private JuheResData result;
	private int error_code;
	
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public JuheResData getResult() {
		return result;
	}
	public void setResult(JuheResData result) {
		this.result = result;
	}
	public int getError_code() {
		return error_code;
	}
	public void setError_code(int error_code) {
		this.error_code = error_code;
	}
	public String getResultcode() {
		return resultcode;
	}
	public void setResultcode(String resultcode) {
		this.resultcode = resultcode;
	}
}
