package com.wanda.credit.ds.client.juxinli.bean.mobile.origin;

import javax.persistence.Id;

/**
 *  短信信息
 */
public class Smses {
	
	@Id
	private int id;
	
	/**
	 * 发送时间
	 */
	private String start_time;
	
	/**
	 * 发送类型
	 */
	private String init_type;
	
	/**
	 * 发送地点
	 */
	private String place;
	
	/**
	 * 对方号码
	 */
	private String other_cell_phone;
	
	/**
	 * 本机号码
	 */
	private String cell_phone;
	
	/**
	 * 本次短信花费
	 */
	private String subtotal;
	
	private String update_time;
	
	public void setStart_time(String start_time){
	
		this.start_time = start_time;
	}
	
	public String getInit_type(){
	
		return init_type;
	}
	
	public void setInit_type(String init_type){
	
		this.init_type = init_type;
	}
	
	public String getPlace(){
	
		return place;
	}
	
	public void setPlace(String place){
	
		this.place = place;
	}
	
	public String getOther_cell_phone(){
	
		return other_cell_phone;
	}
	
	public void setOther_cell_phone(String other_cell_phone){
	
		this.other_cell_phone = other_cell_phone;
	}
	
	public String getCell_phone(){
	
		return cell_phone;
	}
	
	public void setCell_phone(String cell_phone){
	
		this.cell_phone = cell_phone;
	}
	
	public String getSubtotal(){
	
		return subtotal;
	}
	
	public void setSubtotal(String subtotal){
	
		this.subtotal = subtotal;
	}

	public String getUpdate_time() {
		return update_time;
	}

	public void setUpdate_time(String update_time) {
		this.update_time = update_time;
	}

	public String getStart_time() {
		return start_time;
	}

	public String toString() {
		return "Smses [id=" + id + ", start_time=" + start_time
				+ ", init_type=" + init_type + ", place=" + place
				+ ", other_cell_phone=" + other_cell_phone + ", cell_phone="
				+ cell_phone + ", subtotal=" + subtotal + ", update_time="
				+ update_time + "]";
	}
	
}
