/**   
* @Description: 聚信立公积金_原始数据_还款明细 
* @author xiaobin.hou  
* @date 2016年5月29日 下午6:20:42 
* @version V1.0   
*/
package com.wanda.credit.ds.client.juxinli.bean.housefund;

/**
 * @author xiaobin.hou
 *
 */
public class RawDataPayment {
	
	private String payment_date;// 还款日期
	private String payment_num;// 还款期次
	private String payment_principal; // 还款本金
	private String payment_interest; // 还款利息
	private String payment_penalty; // 偿还罚息
	private String payment_sum; // 本息合计
	private String prin_balance; // 本金余额
	private String payment_describe; // 还款描述
	
	public String getPayment_date() {
		return payment_date;
	}
	public void setPayment_date(String payment_date) {
		this.payment_date = payment_date;
	}
	public String getPayment_num() {
		return payment_num;
	}
	public void setPayment_num(String payment_num) {
		this.payment_num = payment_num;
	}
	public String getPayment_principal() {
		return payment_principal;
	}
	public void setPayment_principal(String payment_principal) {
		this.payment_principal = payment_principal;
	}
	public String getPayment_interest() {
		return payment_interest;
	}
	public void setPayment_interest(String payment_interest) {
		this.payment_interest = payment_interest;
	}
	public String getPayment_penalty() {
		return payment_penalty;
	}
	public void setPayment_penalty(String payment_penalty) {
		this.payment_penalty = payment_penalty;
	}
	public String getPayment_sum() {
		return payment_sum;
	}
	public void setPayment_sum(String payment_sum) {
		this.payment_sum = payment_sum;
	}
	public String getPrin_balance() {
		return prin_balance;
	}
	public void setPrin_balance(String prin_balance) {
		this.prin_balance = prin_balance;
	}
	public String getPayment_describe() {
		return payment_describe;
	}
	public void setPayment_describe(String payment_describe) {
		this.payment_describe = payment_describe;
	}
	@Override
	public String toString() {
		return "RawDataPayment [payment_date=" + payment_date
				+ ", payment_num=" + payment_num + ", payment_principal="
				+ payment_principal + ", payment_interest=" + payment_interest
				+ ", payment_penalty=" + payment_penalty + ", payment_sum="
				+ payment_sum + ", prin_balance=" + prin_balance
				+ ", payment_describe=" + payment_describe + "]";
	}
	
	

}
