package com.wanda.credit.ds.client.chuanglan.bean;

import java.util.List;

public class CLMobileResultBean {
	private String code;
	private String chargeStatus;
	private String chargeCount;
	private String message;
	private List<CLMobileDataBean> data;
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getChargeStatus() {
		return chargeStatus;
	}
	public void setChargeStatus(String chargeStatus) {
		this.chargeStatus = chargeStatus;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getChargeCount() {
		return chargeCount;
	}
	public void setChargeCount(String chargeCount) {
		this.chargeCount = chargeCount;
	}
	public List<CLMobileDataBean> getData() {
		return data;
	}
	public void setData(List<CLMobileDataBean> data) {
		this.data = data;
	}
	
}
