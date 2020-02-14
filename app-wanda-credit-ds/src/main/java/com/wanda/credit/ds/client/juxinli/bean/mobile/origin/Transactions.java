package com.wanda.credit.ds.client.juxinli.bean.mobile.origin;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author zhashiwen 账单信息
 */
@Entity
@Table(name = "T_DS_JXL_MOBILE_TRANSACTIONS")
public class Transactions {
	
	@Id
	private int id;
	
	/**
	 * 实际花费
	 */
	private String total_amt;
	
	/**
	 * 账单月份
	 */
	private String bill_cycle;
	
	/**
	 * 计划花费
	 */
	private String plan_amt;
	
	/**
	 * 月付费
	 */
	private String pay_amt;
	
	/**
	 * 本机号码
	 */
	private String cell_phone;
	
	private String update_time;
	
	public String getTotal_amt(){
	
		return total_amt;
	}
	
	public void setTotal_amt(String total_amt){
	
		this.total_amt = total_amt;
	}
	
	public String getBill_cycle(){
	
		return bill_cycle;
	}
	
	public void setBill_cycle(String bill_cycle){
	
		this.bill_cycle = bill_cycle;
	}
	
	public String getPlan_amt(){
	
		return plan_amt;
	}
	
	public void setPlan_amt(String plan_amt){
	
		this.plan_amt = plan_amt;
	}
	
	public String getPay_amt(){
	
		return pay_amt;
	}
	
	public void setPay_amt(String pay_amt){
	
		this.pay_amt = pay_amt;
	}
	
	public String getCell_phone(){
	
		return cell_phone;
	}
	
	public void setCell_phone(String cell_phone){
	
		this.cell_phone = cell_phone;
	}
	
	@Override
	public String toString(){
	
		return "Transactions [total_amt=" + total_amt + ", bill_cycle=" + bill_cycle + ", plan_amt=" + plan_amt
		        + ", pay_amt=" + pay_amt + ", cell_phone=" + cell_phone + "]";
	}

	public String getUpdate_time() {
		return update_time;
	}

	public void setUpdate_time(String update_time) {
		this.update_time = update_time;
	}
	
}
