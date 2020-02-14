package com.wanda.credit.ds.client.phjr.bean;

import java.io.Serializable;

public class ReqBodyBean implements Serializable{

	private String data;// 数据加密串
	private String key;// 加密密钥
	private String channel;// 渠道号
	private String sign;// 签名串
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}

}
