/**   
* @Description: 聚信立-信用卡账单-账单信息
* @author xiaobin.hou  
* @date 2016年7月22日 下午2:59:06 
* @version V1.0   
*/
package com.wanda.credit.ds.client.juxinli.bean.creditCardBill;

import java.util.List;

/**
 * @author xiaobin.hou
 *
 */
public class CreditBillInfo {
	
	private String received;// 发件服务器信息
	private String from;// 发件人邮箱名
	private String internaldate;// 邮件发送日期
	private String card_number;// 卡号
	private String user_name;// 用户名
	private List<CreditBase> credit_limit;// 信用额度
	private List<CreditBase> cash_advance_limit;// 取现额度
	private String statement_date;// 账单日
	private String payment_due_date;// 还款日
	private String statement_cycle;// 免息期
	private List<CreditBase> current_balance;// 本期还款额
	private List<CreditBase> minimum_payment_due;// 本期最低还款额
	private List<CreditTransSum> transaction_summary;// 账务说明
	private List<CreditTransDetail> transaction_detail;// 交易明细
	private List<CreditInstallment> installment_plan_info;// 分期计划信息
	
	
	public String getReceived() {
		return received;
	}
	public void setReceived(String received) {
		this.received = received;
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getInternaldate() {
		return internaldate;
	}
	public void setInternaldate(String internaldate) {
		this.internaldate = internaldate;
	}
	public String getCard_number() {
		return card_number;
	}
	public void setCard_number(String card_number) {
		this.card_number = card_number;
	}
	public String getUser_name() {
		return user_name;
	}
	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
	public List<CreditBase> getCredit_limit() {
		return credit_limit;
	}
	public void setCredit_limit(List<CreditBase> credit_limit) {
		this.credit_limit = credit_limit;
	}
	public List<CreditBase> getCash_advance_limit() {
		return cash_advance_limit;
	}
	public void setCash_advance_limit(List<CreditBase> cash_advance_limit) {
		this.cash_advance_limit = cash_advance_limit;
	}
	public String getStatement_date() {
		return statement_date;
	}
	public void setStatement_date(String statement_date) {
		this.statement_date = statement_date;
	}
	public String getPayment_due_date() {
		return payment_due_date;
	}
	public void setPayment_due_date(String payment_due_date) {
		this.payment_due_date = payment_due_date;
	}
	public String getStatement_cycle() {
		return statement_cycle;
	}
	public void setStatement_cycle(String statement_cycle) {
		this.statement_cycle = statement_cycle;
	}
	public List<CreditBase> getCurrent_balance() {
		return current_balance;
	}
	public void setCurrent_balance(List<CreditBase> current_balance) {
		this.current_balance = current_balance;
	}
	public List<CreditBase> getMinimum_payment_due() {
		return minimum_payment_due;
	}
	public void setMinimum_payment_due(List<CreditBase> minimum_payment_due) {
		this.minimum_payment_due = minimum_payment_due;
	}
	public List<CreditTransSum> getTransaction_summary() {
		return transaction_summary;
	}
	public void setTransaction_summary(List<CreditTransSum> transaction_summary) {
		this.transaction_summary = transaction_summary;
	}
	public List<CreditTransDetail> getTransaction_detail() {
		return transaction_detail;
	}
	public void setTransaction_detail(List<CreditTransDetail> transaction_detail) {
		this.transaction_detail = transaction_detail;
	}
	public List<CreditInstallment> getInstallment_plan_info() {
		return installment_plan_info;
	}
	public void setInstallment_plan_info(
			List<CreditInstallment> installment_plan_info) {
		this.installment_plan_info = installment_plan_info;
	}
	@Override
	public String toString() {
		return "CreditBillInfo [received=" + received + ", from=" + from
				+ ", internaldate=" + internaldate + ", card_number="
				+ card_number + ", user_name=" + user_name + ", credit_limit="
				+ credit_limit + ", cash_advance_limit=" + cash_advance_limit
				+ ", statement_date=" + statement_date + ", payment_due_date="
				+ payment_due_date + ", statement_cycle=" + statement_cycle
				+ ", current_balance=" + current_balance
				+ ", minimum_payment_due=" + minimum_payment_due
				+ ", transaction_summary=" + transaction_summary
				+ ", transaction_detail=" + transaction_detail
				+ ", installment_plan_info=" + installment_plan_info + "]";
	}
	
	
	
	
}
