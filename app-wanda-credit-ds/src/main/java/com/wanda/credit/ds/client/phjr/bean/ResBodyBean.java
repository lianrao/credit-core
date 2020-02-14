package com.wanda.credit.ds.client.phjr.bean;

import java.io.Serializable;

public class ResBodyBean implements Serializable{

	private static final long serialVersionUID = 905269119066583821L;
	
	private boolean success	;//交易标识
	private String errCode	;//错误代码
	private String errMsg	;//错误信息
	private String data	;//响应内容
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String getErrCode() {
		return errCode;
	}
	public void setErrCode(String errCode) {
		this.errCode = errCode;
	}
	public String getErrMsg() {
		return errMsg;
	}
	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	@Override
	public String toString() {
		return "ResBodyBean [success=" + success + ", errCode=" + errCode
				+ ", errMsg=" + errMsg + ", data=" + data + "]";
	}

}
