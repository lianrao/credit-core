/**   
* @Description: 聚信立_公积金_基本信息表
* @author xiaobin.hou  
* @date 2016年5月29日 下午7:50:42 
* @version V1.0   
*/
package com.wanda.credit.ds.dao.domain.juxinli.housefund;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.annotations.GenericGenerator;

import com.wanda.credit.base.domain.BaseDomain;

/**
 * @author xiaobin.hou
 *
 */
@Entity
@Table(name = "CPDB_DS.T_DS_JXL_HOUSING_BASEINFO")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class HouseRawDataBasicPojo extends BaseDomain {


	private static final long serialVersionUID = 1L;
	
	/**
	 * 获取 主键
	 */
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	@Column(name = "SEQID", unique = true, nullable = false, length = 32)
	private String seqId;
	private Date crt_time;
	private Date upd_time;

	private String requestId;
	private String housing_fund_status;
	private String update_time;
	private String last_fund_date;
	private String data_source;
	private String id_card;
	private String real_name;
	private String fund_amt;
	private String fund_num;
	private String open_date;
	private String balance;
	private String company;
	private String company_code;
	private String employee_code;
	private String transfer_amount;
	private String open_bank_name;
	private String open_bank_account;
	private String deposit_ratio_personal;
	private String deposit_ratio_company;
	private String pay_base_amount;
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "FK_SEQID")
	private Set<HouseRawDataDetailPojo> details = new HashSet<HouseRawDataDetailPojo>();
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "FK_SEQID")
	private Set<HouseRawDataLoanPojo> loan_info = new HashSet<HouseRawDataLoanPojo>();
	
	
	public String getSeqId() {
		return seqId;
	}
	public void setSeqId(String seqId) {
		this.seqId = seqId;
	}
	public Date getCrt_time() {
		return crt_time;
	}
	public void setCrt_time(Date crt_time) {
		this.crt_time = crt_time;
	}
	public Date getUpd_time() {
		return upd_time;
	}
	public void setUpd_time(Date upd_time) {
		this.upd_time = upd_time;
	}
	public String getRequestId() {
		return requestId;
	}
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	public String getHousing_fund_status() {
		return housing_fund_status;
	}
	public void setHousing_fund_status(String housing_fund_status) {
		this.housing_fund_status = housing_fund_status;
	}
	public String getUpdate_time() {
		return update_time;
	}
	public void setUpdate_time(String update_time) {
		this.update_time = update_time;
	}
	public String getLast_fund_date() {
		return last_fund_date;
	}
	public void setLast_fund_date(String last_fund_date) {
		this.last_fund_date = last_fund_date;
	}
	public String getData_source() {
		return data_source;
	}
	public void setData_source(String data_source) {
		this.data_source = data_source;
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
	public Set<HouseRawDataDetailPojo> getDetails() {
		return details;
	}
	public void setDetails(Set<HouseRawDataDetailPojo> details) {
		this.details = details;
	}
	public Set<HouseRawDataLoanPojo> getLoan_info() {
		return loan_info;
	}
	public void setLoan_info(Set<HouseRawDataLoanPojo> loan_info) {
		this.loan_info = loan_info;
	}
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public String getEmployee_code() {
		return employee_code;
	}
	public void setEmployee_code(String employee_code) {
		this.employee_code = employee_code;
	}

	
	
}
