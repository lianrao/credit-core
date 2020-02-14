package com.wanda.credit.ds.client.juxinli.bean.reportNew.vo;

import java.util.List;
/**
 * 电商送货信息分析 DICT
 * @author xiaobin.hou
 *
 */
public class DeliverAddress {

	/**
	 * 开始送货时间
	 */
	private String begin_date;

	/**
	 * 总送货金额
	 */
	private String total_amount;

	/**
	 * 结束送货时间
	 */
	private String end_date;

	/**
	 * 总送货次数
	 */
	private String total_count;

	/**
	 * 收货人列表
	 */
	private List<Receiver> receiver;

	/**
	 * 收货地址
	 */
	private String address;

	/**
	 * 纬度
	 */
	private String lat;

	/**
	 * 经度
	 */
	private String lng;

	/**
	 * 地址类型
	 */
	private String predict_addr_type;

	public String getAddress() {

		return address;
	}

	public void setAddress(String address) {

		this.address = address;
	}

	public String getBegin_date() {

		return begin_date;
	}

	public void setBegin_date(String begin_date) {

		this.begin_date = begin_date;
	}

	public String getEnd_date() {

		return end_date;
	}

	public void setEnd_date(String end_date) {

		this.end_date = end_date;
	}

	public String getPredict_addr_type() {

		return predict_addr_type;
	}

	public void setPredict_addr_type(String predict_addr_type) {

		this.predict_addr_type = predict_addr_type;
	}

	public List<Receiver> getReceiver() {

		return receiver;
	}

	public void setReceiver(List<Receiver> receiver) {

		this.receiver = receiver;
	}

	public String getTotal_amount() {
		return total_amount;
	}

	public void setTotal_amount(String total_amount) {
		this.total_amount = total_amount;
	}

	public String getLat() {
		return lat;
	}

	public void setLat(String lat) {
		this.lat = lat;
	}

	public String getLng() {
		return lng;
	}

	public void setLng(String lng) {
		this.lng = lng;
	}

	public String getTotal_count() {

		return total_count;
	}

	public void setTotal_count(String total_count) {

		this.total_count = total_count;
	}

	@Override
	public String toString() {

		return "DeliverAddress [begin_date=" + begin_date + ", total_amount=" + total_amount + ", end_date=" + end_date
				+ ", total_count=" + total_count + ", receiver=" + receiver + ", address=" + address + ", lat=" + lat
				+ ", lng=" + lng + ", predict_addr_type=" + predict_addr_type + "]";
	}

}
