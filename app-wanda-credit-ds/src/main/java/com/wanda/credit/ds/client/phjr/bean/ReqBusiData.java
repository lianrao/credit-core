package com.wanda.credit.ds.client.phjr.bean;

import java.io.Serializable;
import java.util.Map;

public class ReqBusiData implements Serializable {

	@Override
	public String toString() {
		return "ReqBusiData [serviceId=" + serviceId + ", busiNo=" + busiNo
				+ ", deviceId=" + deviceId + ", macAddress=" + macAddress
				+ ", ipAddress=" + ipAddress + ", time=" + time + ", channel="
				+ channel + ", busiObject=" + busiObject + "]";
	}

	private static final long serialVersionUID = -3196442213521288087L;

	private String serviceId;// serviceId
	private String busiNo;// 交易码
	private String deviceId;// 设备IEMI
	private String macAddress;// 设备mac地址
	private String ipAddress;// 设备IP地址
	private long time;// 接口调用时间
	private String channel;// 渠道号
	private Map<String, Object> busiObject;// 业务数据对象

	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	public String getBusiNo() {
		return busiNo;
	}

	public void setBusiNo(String busiNo) {
		this.busiNo = busiNo;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getMacAddress() {
		return macAddress;
	}

	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public Map<String, Object> getBusiObject() {
		return busiObject;
	}

	public void setBusiObject(Map<String, Object> busiObject) {
		this.busiObject = busiObject;
	}

}
