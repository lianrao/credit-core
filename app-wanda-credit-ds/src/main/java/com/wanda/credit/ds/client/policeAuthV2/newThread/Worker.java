package com.wanda.credit.ds.client.policeAuthV2.newThread;



public class Worker<T> implements Runnable {
	private CallBack<T> masterCallback;
	private String trade_id;
	private String request_url;
	private String auth_url;
	private String queryImagePhoto;
	private String name;
	private String cardNo;
	public Worker(String trade_id,String request_url,String auth_url,String queryImagePhoto,
			String name,String cardNo,CallBack<T> masterCallback) {
		this.trade_id=trade_id;
		this.request_url=request_url;
		this.auth_url=auth_url;
		this.queryImagePhoto=queryImagePhoto;
		this.name=name;
		this.cardNo=cardNo;
		this.masterCallback=masterCallback;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub		
	}
	public CallBack<T> getMasterCallback() {
		return masterCallback;
	}
	public void setMasterCallback(CallBack<T> masterCallback) {
		this.masterCallback = masterCallback;
	}
	public String getTrade_id() {
		return trade_id;
	}
	public void setTrade_id(String trade_id) {
		this.trade_id = trade_id;
	}
	public String getRequest_url() {
		return request_url;
	}
	public void setRequest_url(String request_url) {
		this.request_url = request_url;
	}
	public String getAuth_url() {
		return auth_url;
	}
	public void setAuth_url(String auth_url) {
		this.auth_url = auth_url;
	}
	public String getQueryImagePhoto() {
		return queryImagePhoto;
	}
	public void setQueryImagePhoto(String queryImagePhoto) {
		this.queryImagePhoto = queryImagePhoto;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCardNo() {
		return cardNo;
	}
	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}
}
