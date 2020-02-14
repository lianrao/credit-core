package com.wanda.credit.ds.client.yituNew;

import java.util.List;

public class PhotoStoreRunnable implements Runnable {
	private String trade_id;
	private String cardNo;
	private String name;
	private List<String> photoList;
	public PhotoStoreRunnable(String trade_id,String cardNo,String name,List<String> photoList) {
		this.trade_id=trade_id;
		this.cardNo=cardNo;
		this.name=name;
		this.photoList=photoList;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
	}
	public String getTrade_id() {
		return trade_id;
	}
	public void setTrade_id(String trade_id) {
		this.trade_id = trade_id;
	}
	public String getCardNo() {
		return cardNo;
	}
	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<String> getPhotoList() {
		return photoList;
	}
	public void setPhotoList(List<String> photoList) {
		this.photoList = photoList;
	}
}
