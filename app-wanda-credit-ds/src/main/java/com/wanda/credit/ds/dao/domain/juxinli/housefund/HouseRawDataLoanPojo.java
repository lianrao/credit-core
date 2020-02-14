/**   
* @Description: 聚信立_公积金_贷款信息
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
import javax.persistence.ManyToOne;
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
@Table(name = "CPDB_DS.T_DS_JXL_HOUSING_LOAN_INFO")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class HouseRawDataLoanPojo extends BaseDomain {


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
	private String loan_name;
	private String loan_idcard;
	private String company_name;
	private String bank_name;
	private String bank_account;
	private String loan_num;
	private String loan_amount;
	private String loan_length;
	private String loan_rate;
	private String loan_status;
	private String loan_date;
	private String house_type;
	private String guarantee_type;
	private String payment_type;
	private String payment_day;
	private String payment_amount;
	private String due_principal;
	private String overdue_times;
	private String overdue_amount;
	@ManyToOne(cascade = {CascadeType.MERGE,CascadeType.REFRESH }, optional = true) 
	@JoinColumn(name="FK_SEQID",referencedColumnName="seqId")
	private HouseRawDataBasicPojo basic;
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "FK_SEQID")
	private Set<HouseRawDataOverduePojo> overdue_details = new HashSet<HouseRawDataOverduePojo>();
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "FK_SEQID")
	private Set<HouseRawDataPaymentPojo> payment_details = new HashSet<HouseRawDataPaymentPojo>();
	
	
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
	public HouseRawDataBasicPojo getBasic() {
		return basic;
	}
	public void setBasic(HouseRawDataBasicPojo basic) {
		this.basic = basic;
	}
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
	public String getRequestId() {
		return requestId;
	}
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	public Set<HouseRawDataOverduePojo> getOverdue_details() {
		return overdue_details;
	}
	public void setOverdue_details(Set<HouseRawDataOverduePojo> overdue_details) {
		this.overdue_details = overdue_details;
	}
	public Set<HouseRawDataPaymentPojo> getPayment_details() {
		return payment_details;
	}
	public void setPayment_details(Set<HouseRawDataPaymentPojo> payment_details) {
		this.payment_details = payment_details;
	}

	
	
	
	
	
}
