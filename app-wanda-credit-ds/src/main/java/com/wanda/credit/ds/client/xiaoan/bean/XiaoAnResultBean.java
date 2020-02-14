package com.wanda.credit.ds.client.xiaoan.bean;

public class XiaoAnResultBean {
	private String code;
	private String message;
	private XiaoAnpayBean payload;
	private String uuid;
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public XiaoAnpayBean getPayload() {
		return payload;
	}
	public void setPayload(XiaoAnpayBean payload) {
		this.payload = payload;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
}
