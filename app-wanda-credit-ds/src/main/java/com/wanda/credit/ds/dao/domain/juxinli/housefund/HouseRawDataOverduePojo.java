/**   
* @Description: 聚信立_公积金_逾期信息表
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
@Table(name = "CPDB_DS.T_DS_JXL_HOUSING_LOAN_OVERDUE")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class HouseRawDataOverduePojo extends BaseDomain {


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
	private String overdue_date;
	private String overdue_principal;
	private String overdue_interest;
	private String overdue_penalty;
	private String overdue_summary;
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
	public String getOverdue_date() {
		return overdue_date;
	}
	public void setOverdue_date(String overdue_date) {
		this.overdue_date = overdue_date;
	}
	public String getOverdue_principal() {
		return overdue_principal;
	}
	public void setOverdue_principal(String overdue_principal) {
		this.overdue_principal = overdue_principal;
	}
	public String getOverdue_interest() {
		return overdue_interest;
	}
	public void setOverdue_interest(String overdue_interest) {
		this.overdue_interest = overdue_interest;
	}
	public String getOverdue_penalty() {
		return overdue_penalty;
	}
	public void setOverdue_penalty(String overdue_penalty) {
		this.overdue_penalty = overdue_penalty;
	}
	public String getOverdue_summary() {
		return overdue_summary;
	}
	public void setOverdue_summary(String overdue_summary) {
		this.overdue_summary = overdue_summary;
	}
	public HouseRawDataLoanPojo getLoan() {
		return loan;
	}
	public void setLoan(HouseRawDataLoanPojo loan) {
		this.loan = loan;
	}
	
	
	
	
	
}
