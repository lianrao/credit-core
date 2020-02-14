/**   
* @Description: 聚信立_公积金_还款明细
* @author xiaobin.hou  
* @date 2016年5月29日 下午7:50:42 
* @version V1.0   
*/
package com.wanda.credit.ds.dao.domain.juxinli.housefund;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Table(name = "CPDB_DS.T_DS_JXL_HOUSING_LOAN_PAY_D")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class HouseRawDataPaymentPojo extends BaseDomain {


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
	private String payment_date;
	private String payment_num;
	private String payment_principal;
	private String payment_interest;
	private String payment_penalty;
	private String payment_sum;
	private String prin_balance;
	private String payment_describe;
	@ManyToOne(cascade = {CascadeType.MERGE,CascadeType.REFRESH }, optional = true) 
	@JoinColumn(name="FK_SEQID",referencedColumnName="seqId")
	private HouseRawDataLoanPojo loan;
	
	
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
	public HouseRawDataLoanPojo getLoan() {
		return loan;
	}
	public void setLoan(HouseRawDataLoanPojo loan) {
		this.loan = loan;
	}
	
	
	
	
	
}
