package com.wanda.credit.ds.client.yuanjian;

import com.wanda.credit.ds.dao.domain.yuanjian.YJ_FaceScore_Result;

public class YuanJBeanStoreRunnable implements Runnable {
	private String trade_id;
	private String cardNo;
	private String name;
	private YJ_FaceScore_Result yuanjian;
	public YuanJBeanStoreRunnable(String trade_id,String cardNo,String name,YJ_FaceScore_Result yuanjian) {
		this.trade_id=trade_id;
		this.cardNo=cardNo;
		this.name=name;
		this.yuanjian=yuanjian;
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
	public YJ_FaceScore_Result getYuanjian() {
		return yuanjian;
	}
	public void setYuanjian(YJ_FaceScore_Result yuanjian) {
		this.yuanjian = yuanjian;
	}
}
