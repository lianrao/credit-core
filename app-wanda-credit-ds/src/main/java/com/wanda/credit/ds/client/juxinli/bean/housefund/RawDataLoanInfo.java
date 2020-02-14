/**   
* @Description: TODO(用一句话描述该文件做什么) 
* @author xiaobin.hou  
* @date 2016年5月29日 下午6:35:07 
* @version V1.0   
*/
package com.wanda.credit.ds.client.juxinli.bean.housefund;

import java.util.List;

/**
 * @author xiaobin.hou
 *
 */
public class RawDataLoanInfo {
	
	private String loan_name;// 贷款人姓名
	private String loan_idcard;// 贷款人身份证号
	private String company_name;// 单位名称
	private String bank_name;// 受托银行
	private String bank_account;// 还款账户
	private String loan_num;// 贷款合同编号
	private String loan_amount;// 贷款金额
	private String loan_length;// 贷款年限
	private String loan_rate;// 贷款利率
	private String loan_status;// 还款状态
	private String loan_date;// 放款日期
	private String house_type;// 购房类型
	private String guarantee_type;// 担保方式
	private String payment_type;// 还款方式
	private String payment_day;// 月还款日期
	private String payment_amount;// 月还款金额
	private String due_principal;// 已还本金
	private String overdue_times;// 逾期次数
	private String overdue_amount;// 逾期金额
	private List<RawDataPayment> payment_details;// 还款明细
	private List<RawDataOverdue> overdue_details;// 逾期明细
	
	
	public String getLoan_name() {
		return loan_name;
	}
	public void setLoan_name(String loan_name) {
		this.loan_name = loan_name;
	}
	public String getLoan_idcard() {
		return loan_idcard;
	}
	public void setLoan_idcard(String loan_idcard) {
		this.loan_idcard = loan_idcard;
	}
	public String getCompany_name() {
		return company_name;
	}
	public void setCompany_name(String company_name) {
		this.company_name = company_name;
	}
	public String getBank_name() {
		return bank_name;
	}
	public void setBank_name(String bank_name) {
		this.bank_name = bank_name;
	}
	public String getBank_account() {
		return bank_account;
	}
	public void setBank_account(String bank_account) {
		this.bank_account = bank_account;
	}
	public String getLoan_num() {
		return loan_num;
	}
	public void setLoan_num(String loan_num) {
		this.loan_num = loan_num;
	}
	public String getLoan_amount() {
		return loan_amount;
	}
	public void setLoan_amount(String loan_amount) {
		this.loan_amount = loan_amount;
	}
	public String getLoan_length() {
		return loan_length;
	}
	public void setLoan_length(String loan_length) {
		this.loan_length = loan_length;
	}
	public String getLoan_rate() {
		return loan_rate;
	}
	public void setLoan_rate(String loan_rate) {
		this.loan_rate = loan_rate;
	}
	public String getLoan_status() {
		return loan_status;
	}
	public void setLoan_status(String loan_status) {
		this.loan_status = loan_status;
	}
	public String getLoan_date() {
		return loan_date;
	}
	public void setLoan_date(String loan_date) {
		this.loan_date = loan_date;
	}
	public String getHouse_type() {
		return house_type;
	}
	public void setHouse_type(String house_type) {
		this.house_type = house_type;
	}
	public String getGuarantee_type() {
		return guarantee_type;
	}
	public void setGuarantee_type(String guarantee_type) {
		this.guarantee_type = guarantee_type;
	}
	public String getPayment_type() {
		return payment_type;
	}
	public void setPayment_type(String payment_type) {
		this.payment_type = payment_type;
	}
	public String getPayment_day() {
		return payment_day;
	}
	public void setPayment_day(String payment_day) {
		this.payment_day = payment_day;
	}
	public String getPayment_amount() {
		return payment_amount;
	}
	public void setPayment_amount(String payment_amount) {
		this.payment_amount = payment_amount;
	}
	public String getDue_principal() {
		return due_principal;
	}
	public void setDue_principal(String due_principal) {
		this.due_principal = due_principal;
	}
	public String getOverdue_times() {
		return overdue_times;
	}
	public void setOverdue_times(String overdue_times) {
		this.overdue_times = overdue_times;
	}
	public String getOverdue_amount() {
		return overdue_amount;
	}
	public void setOverdue_amount(String overdue_amount) {
		this.overdue_amount = overdue_amount;
	}
	public List<RawDataPayment> getPayment_details() {
		return payment_details;
	}
	public void setPayment_details(List<RawDataPayment> payment_details) {
		this.payment_details = payment_details;
	}
	public List<RawDataOverdue> getOverdue_details() {
		return overdue_details;
	}
	public void setOverdue_details(List<RawDataOverdue> overdue_details) {
		this.overdue_details = overdue_details;
	}
	@Override
	public String toString() {
		return "RawDataLoanInfo [loan_name=" + loan_name + ", loan_idcard="
				+ loan_idcard + ", company_name=" + company_name
				+ ", bank_name=" + bank_name + ", bank_account=" + bank_account
				+ ", loan_num=" + loan_num + ", loan_amount=" + loan_amount
				+ ", loan_length=" + loan_length + ", loan_rate=" + loan_rate
				+ ", loan_status=" + loan_status + ", loan_date=" + loan_date
				+ ", house_type=" + house_type + ", guarantee_type="
				+ guarantee_type + ", payment_type=" + payment_type
				+ ", payment_day=" + payment_day + ", payment_amount="
				+ payment_amount + ", due_principal=" + due_principal
				+ ", overdue_times=" + overdue_times + ", overdue_amount="
				+ overdue_amount + ", payment_details=" + payment_details
				+ ", overdue_details=" + overdue_details + "]";
	}
	
	

}
