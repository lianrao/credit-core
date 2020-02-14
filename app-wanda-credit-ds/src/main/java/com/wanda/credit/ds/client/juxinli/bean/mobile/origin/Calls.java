package com.wanda.credit.ds.client.juxinli.bean.mobile.origin;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 通话信息
 * @author xiaobin.hou
 *
 */
@Entity
@Table(name = "T_DS_JXL_MOBILE_CALLS")
public class Calls {
	
	
	@Id
	private int id;
	
	/**
	 * 通话地点
	 */
	private String place;
	
	/**
	 * 对方号码
	 */
	private String other_cell_phone;
	
	/**
	 * 本次通话花费
	 */
	private String subtotal;
	
	/**
	 * 通话开始时间
	 */
	private String start_time;
	
	/**
	 * 本机号码
	 */
	private String cell_phone;
	
	/**
	 * 呼叫类型
	 */
	private String init_type;
	
	/**
	 * 通话类型
	 */
	private String call_type;
	
	/**
	 * 通话时长
	 */
	private String use_time;
	
	private String update_time;
	
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
	
	public String getSubtotal(){
	
		return subtotal;
	}
	
	public void setSubtotal(String subtotal){
	
		this.subtotal = subtotal;
	}
	
	public String getStart_time(){
	
		return start_time;
	}
	
	public void setStart_time(String start_time){
	
		this.start_time = start_time;
	}
	
	public String getCell_phone(){
	
		return cell_phone;
	}
	
	public void setCell_phone(String cell_phone){
	
		this.cell_phone = cell_phone;
	}
	
	public String getInit_type(){
	
		return init_type;
	}
	
	public void setInit_type(String init_type){
	
		this.init_type = init_type;
	}
	
	public String getCall_type(){
	
		return call_type;
	}
	
	public void setCall_type(String call_type){
	
		this.call_type = call_type;
	}

	@Override
	public String toString() {
		return "Calls [id=" + id + ", place=" + place + ", other_cell_phone="
				+ other_cell_phone + ", subtotal=" + subtotal + ", start_time="
				+ start_time + ", cell_phone=" + cell_phone + ", init_type="
				+ init_type + ", call_type=" + call_type + ", use_time="
				+ use_time + ", update_time=" + update_time + "]";
	}

	public String getUse_time() {
		return use_time;
	}

	public void setUse_time(String use_time) {
		this.use_time = use_time;
	}

	public String getUpdate_time() {
		return update_time;
	}

	public void setUpdate_time(String update_time) {
		this.update_time = update_time;
	}
	
}
