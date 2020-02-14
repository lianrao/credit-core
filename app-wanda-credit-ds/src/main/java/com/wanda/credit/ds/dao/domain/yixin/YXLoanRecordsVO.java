package com.wanda.credit.ds.dao.domain.yixin;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * 前海 黑名单 信息
 **/
@Entity
@Table(name = "T_DS_YX_LoanRecords")
@SequenceGenerator(name="Seq_T_DS_YX_LoanRecords",sequenceName="Seq_T_DS_YX_LoanRecords")  
@JsonIgnoreProperties(value = { "hibernateLazyInitializer" })
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class YXLoanRecordsVO extends YXCommonDomain {

	private static final long serialVersionUID = 1L;
	private long id;
	private String loanTime;
	private String amount;
	private String approveStatus;
	private String approveStatusCode;
	private String currentStatus;
	private String currentStatusCode;
/*	private String overdue;
	private String overdueTimes;
	private String overdueTimes90;
	private String overdueTimes180;	*/

	

	public YXLoanRecordsVO() {
		super();
	}

	/**
	 * 获取 主键
	 */
	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="Seq_T_DS_YX_LoanRecords")  
	@Column(name = "ID", unique = true, nullable = false)
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getLoanTime() {
		return loanTime;
	}

	public void setLoanTime(String loanTime) {
		this.loanTime = loanTime;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getApproveStatus() {
		return approveStatus;
	}

	public void setApproveStatus(String approveStatus) {
		this.approveStatus = approveStatus;
	}

	public String getApproveStatusCode() {
		return approveStatusCode;
	}

	public void setApproveStatusCode(String approveStatusCode) {
		this.approveStatusCode = approveStatusCode;
	}

	public String getCurrentStatus() {
		return currentStatus;
	}

	public void setCurrentStatus(String currentStatus) {
		this.currentStatus = currentStatus;
	}

	public String getCurrentStatusCode() {
		return currentStatusCode;
	}

	public void setCurrentStatusCode(String currentStatusCode) {
		this.currentStatusCode = currentStatusCode;
	}


}
