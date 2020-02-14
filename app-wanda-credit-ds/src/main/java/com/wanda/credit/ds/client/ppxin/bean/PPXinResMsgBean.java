package com.wanda.credit.ds.client.ppxin.bean;

public class PPXinResMsgBean {
	private String queryStatusText;
	private String queryStatus;
	private String errorCode;
	private String errorMsg;
	private PPXinResDataBean data;
	public String getQueryStatusText() {
		return queryStatusText;
	}
	public void setQueryStatusText(String queryStatusText) {
		this.queryStatusText = queryStatusText;
	}
	public String getQueryStatus() {
		return queryStatus;
	}
	public void setQueryStatus(String queryStatus) {
		this.queryStatus = queryStatus;
	}
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	public PPXinResDataBean getData() {
		return data;
	}
	public void setData(PPXinResDataBean data) {
		this.data = data;
	}
}
