/**   
* @Description: TODO(用一句话描述该文件做什么) 
* @author xiaobin.hou  
* @date 2016年5月29日 下午6:16:54 
* @version V1.0   
*/
package com.wanda.credit.ds.client.juxinli.bean.housefund;

import java.util.List;

/**
 * @author xiaobin.hou
 *
 */
public class HouseFundRawData {
	
//	private String token;// 系统用户id
//	private String version;// 接口版本
	private String data_source;// 数据源
	private String update_time;// 数据获取时间
	private String housing_fund_status;// 公积金账户状态
	private String company;// 所属单位
	private String last_fund_date;// 末次缴存年
	private String id_card;// 身份证号
	private String real_name;// 登记姓名
	private String fund_amt;// 月缴存额
	private String fund_num;// 公积金账号
	private String open_date;// 开户日期
	private String balance;// 账户余额
	private String company_code;// 公司编码
	private String employee_code;// 员工编码
	private String transfer_amount;// 转出金额
	private String open_bank_name;// 开户银行名
	private String open_bank_account;// 开户银行账号
	private String deposit_ratio_personal;// 个人缴纳比例
	private String deposit_ratio_company;// 单位缴纳比例
	private String pay_base_amount;// 缴纳基数
	private List<RawDataDetail> loan_info;// 贷款信息
	private List<RawDataLoanInfo> details;// 交易记录
	
//	public String getToken() {
//		return token;
//	}
//	public void setToken(String token) {
//		this.token = token;
//	}
//	public String getVersion() {
//		return version;
//	}
//	public void setVersion(String version) {
//		this.version = version;
//	}
	public String getData_source() {
		return data_source;
	}
	public void setData_source(String data_source) {
		this.data_source = data_source;
	}
	public String getUpdate_time() {
		return update_time;
	}
	public void setUpdate_time(String update_time) {
		this.update_time = update_time;
	}
	public String getHousing_fund_status() {
		return housing_fund_status;
	}
	public void setHousing_fund_status(String housing_fund_status) {
		this.housing_fund_status = housing_fund_status;
	}
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public String getLast_fund_date() {
		return last_fund_date;
	}
	public void setLast_fund_date(String last_fund_date) {
		this.last_fund_date = last_fund_date;
	}
	public String getId_card() {
		return id_card;
	}
	public void setId_card(String id_card) {
		this.id_card = id_card;
	}
	public String getReal_name() {
		return real_name;
	}
	public void setReal_name(String real_name) {
		this.real_name = real_name;
	}
	public String getFund_amt() {
		return fund_amt;
	}
	public void setFund_amt(String fund_amt) {
		this.fund_amt = fund_amt;
	}
	public String getFund_num() {
		return fund_num;
	}
	public void setFund_num(String fund_num) {
		this.fund_num = fund_num;
	}
	public String getOpen_date() {
		return open_date;
	}
	public void setOpen_date(String open_date) {
		this.open_date = open_date;
	}
	public String getBalance() {
		return balance;
	}
	public void setBalance(String balance) {
		this.balance = balance;
	}
	public String getCompany_code() {
		return company_code;
	}
	public void setCompany_code(String company_code) {
		this.company_code = company_code;
	}
	public String getEmployee_code() {
		return employee_code;
	}
	public void setEmployee_code(String employee_code) {
		this.employee_code = employee_code;
	}
	public String getTransfer_amount() {
		return transfer_amount;
	}
	public void setTransfer_amount(String transfer_amount) {
		this.transfer_amount = transfer_amount;
	}
	public String getOpen_bank_name() {
		return open_bank_name;
	}
	public void setOpen_bank_name(String open_bank_name) {
		this.open_bank_name = open_bank_name;
	}
	public String getOpen_bank_account() {
		return open_bank_account;
	}
	public void setOpen_bank_account(String open_bank_account) {
		this.open_bank_account = open_bank_account;
	}
	public String getDeposit_ratio_personal() {
		return deposit_ratio_personal;
	}
	public void setDeposit_ratio_personal(String deposit_ratio_personal) {
		this.deposit_ratio_personal = deposit_ratio_personal;
	}
	public String getDeposit_ratio_company() {
		return deposit_ratio_company;
	}
	public void setDeposit_ratio_company(String deposit_ratio_company) {
		this.deposit_ratio_company = deposit_ratio_company;
	}
	public String getPay_base_amount() {
		return pay_base_amount;
	}
	public void setPay_base_amount(String pay_base_amount) {
		this.pay_base_amount = pay_base_amount;
	}
	public List<RawDataDetail> getLoan_info() {
		return loan_info;
	}
	public void setLoan_info(List<RawDataDetail> loan_info) {
		this.loan_info = loan_info;
	}
	public List<RawDataLoanInfo> getDetails() {
		return details;
	}
	public void setDetails(List<RawDataLoanInfo> details) {
		this.details = details;
	}
	@Override
	public String toString() {
		return "HouseFundRawData [data_source=" + data_source
				+ ", update_time=" + update_time + ", housing_fund_status="
				+ housing_fund_status + ", company=" + company
				+ ", last_fund_date=" + last_fund_date + ", id_card=" + id_card
				+ ", real_name=" + real_name + ", fund_amt=" + fund_amt
				+ ", fund_num=" + fund_num + ", open_date=" + open_date
				+ ", balance=" + balance + ", company_code=" + company_code
				+ ", employee_code=" + employee_code + ", transfer_amount="
				+ transfer_amount + ", open_bank_name=" + open_bank_name
				+ ", open_bank_account=" + open_bank_account
				+ ", deposit_ratio_personal=" + deposit_ratio_personal
				+ ", deposit_ratio_company=" + deposit_ratio_company
				+ ", pay_base_amount=" + pay_base_amount + ", loan_info="
				+ loan_info + ", details=" + details + "]";
	}

	
	
	

}
