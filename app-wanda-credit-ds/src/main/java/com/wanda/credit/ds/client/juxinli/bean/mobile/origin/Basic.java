package com.wanda.credit.ds.client.juxinli.bean.mobile.origin;

/**
 * 
 * @author xiaobin.hou
 *
 */
public class Basic {
	
	/**
	 * 本机号码
	 */
	private String cell_phone;
	
	/**
	 * 登记身份照号码
	 */
	private String idcard;
	
	/**
	 * 入网时间
	 */
	private String reg_time;
	
	/**
	 * 登记姓名
	 */
	private String real_name;
	
	private String update_time;
	
	public String getCell_phone(){
	
		return cell_phone;
	}
	
	public void setCell_phone(String cell_phone){
	
		this.cell_phone = cell_phone;
	}
	
	public String getIdcard(){
	
		return idcard;
	}
	
	public void setIdcard(String idcard){
	
		this.idcard = idcard;
	}
	
	public String getReg_time(){
	
		return reg_time;
	}
	
	public void setReg_time(String reg_time){
	
		this.reg_time = reg_time;
	}
	
	public String getReal_name(){
	
		return real_name;
	}
	
	public void setReal_name(String real_name){
	
		this.real_name = real_name;
	}
	
	public String getUpdate_time() {
		return update_time;
	}

	public void setUpdate_time(String update_time) {
		this.update_time = update_time;
	}

	public String toString() {
		return "Basic [cell_phone=" + cell_phone + ", idcard=" + idcard
				+ ", reg_time=" + reg_time + ", real_name=" + real_name
				+ ", update_time=" + update_time + "]";
	}
	
	
	
}
