/**   
* @Description: 聚信立-信用卡账单-交易明细
* @author xiaobin.hou  
* @date 2016年7月22日 下午2:59:06 
* @version V1.0   
*/
package com.wanda.credit.ds.client.juxinli.bean.creditCardBill;

/**
 * @author xiaobin.hou
 *
 */
public class CreditTransDetail {
	
	private String trans_type;// 交易种类
	private String trans_date;// 交易日期
	private String posted_date;// 记账日期
	private String trans_description;// 交易说明
	private String trans_amount;// 交易金额
	private String trans_currency;// 交易币种
	private String payment_amount;// 清算金额
	private String payment_currency;// 清算币种
	
	public String getTrans_type() {
		return trans_type;
	}
	public void setTrans_type(String trans_type) {
		this.trans_type = trans_type;
	}
	public String getTrans_date() {
		return trans_date;
	}
	public void setTrans_date(String trans_date) {
		this.trans_date = trans_date;
	}
	public String getPosted_date() {
		return posted_date;
	}
	public void setPosted_date(String posted_date) {
		this.posted_date = posted_date;
	}
	public String getTrans_description() {
		return trans_description;
	}
	public void setTrans_description(String trans_description) {
		this.trans_description = trans_description;
	}
	public String getTrans_amount() {
		return trans_amount;
	}
	public void setTrans_amount(String trans_amount) {
		this.trans_amount = trans_amount;
	}
	public String getTrans_currency() {
		return trans_currency;
	}
	public void setTrans_currency(String trans_currency) {
		this.trans_currency = trans_currency;
	}
	public String getPayment_amount() {
		return payment_amount;
	}
	public void setPayment_amount(String payment_amount) {
		this.payment_amount = payment_amount;
	}
	public String getPayment_currency() {
		return payment_currency;
	}
	public void setPayment_currency(String payment_currency) {
		this.payment_currency = payment_currency;
	}
	
	
	@Override
	public String toString() {
		return "CreditTransDetail [trans_type=" + trans_type + ", trans_date="
				+ trans_date + ", posted_date=" + posted_date
				+ ", trans_description=" + trans_description
				+ ", trans_amount=" + trans_amount + ", trans_currency="
				+ trans_currency + ", payment_amount=" + payment_amount
				+ ", payment_currency=" + payment_currency + "]";
	}
	
	
	

}
