package com.wanda.credit.ds.client.ppxin.bean;

public class JuheMobileBean {
	private String resultcode;
	private String reason;
	private int error_code;
	private JuheResultBean result;
	
	public String getResultcode() {
		return resultcode;
	}
	public void setResultcode(String resultcode) {
		this.resultcode = resultcode;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public int getError_code() {
		return error_code;
	}
	public void setError_code(int error_code) {
		this.error_code = error_code;
	}
	public JuheResultBean getResult() {
		return result;
	}
	public void setResult(JuheResultBean result) {
		this.result = result;
	}
	
}
