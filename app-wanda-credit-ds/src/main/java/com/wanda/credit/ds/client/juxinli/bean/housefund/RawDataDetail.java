/**   
* @Description: 公积金_明细
* @author xiaobin.hou  
* @date 2016年5月29日 下午6:40:35 
* @version V1.0   
*/
package com.wanda.credit.ds.client.juxinli.bean.housefund;

/**
 * @author xiaobin.hou
 *
 */
public class RawDataDetail {
	
	private String note;// 业务描述
	private String trading_amt;// 转入余额(单位：元)
	private String trading_date;// 交易日期
	private String transfer_amount;// 转出余额(单位：元)
	private String pay_base;// 缴纳基数
	private String balance;// 账户余额
	private String company;// 所属单位
	
	
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public String getTrading_amt() {
		return trading_amt;
	}
	public void setTrading_amt(String trading_amt) {
		this.trading_amt = trading_amt;
	}
	public String getTrading_date() {
		return trading_date;
	}
	public void setTrading_date(String trading_date) {
		this.trading_date = trading_date;
	}
	public String getTransfer_amount() {
		return transfer_amount;
	}
	public void setTransfer_amount(String transfer_amount) {
		this.transfer_amount = transfer_amount;
	}
	public String getPay_base() {
		return pay_base;
	}
	public void setPay_base(String pay_base) {
		this.pay_base = pay_base;
	}
	public String getBalance() {
		return balance;
	}
	public void setBalance(String balance) {
		this.balance = balance;
	}
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	@Override
	public String toString() {
		return "RawDataDetail [note=" + note + ", trading_amt=" + trading_amt
				+ ", trading_date=" + trading_date + ", transfer_amount="
				+ transfer_amount + ", pay_base=" + pay_base + ", balance="
				+ balance + ", company=" + company + "]";
	}

}
