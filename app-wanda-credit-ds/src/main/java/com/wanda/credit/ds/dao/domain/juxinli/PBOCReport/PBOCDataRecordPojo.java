/**   
* @Description: 报告数据-报告查询记录信息
* @author xiaobin.hou  
* @date 2016年7月11日 下午4:43:44 
* @version V1.0   
*/
package com.wanda.credit.ds.dao.domain.juxinli.PBOCReport;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.wanda.credit.base.domain.BaseDomain;

/**
 * @author xiaobin.hou
 *
 */
@Entity
@Table(name = "CPDB_DS.T_DS_JXL_PBOC_CREDIT_RECORD")
@SequenceGenerator(name="SEQ_JXL_PBOC_CREDIT_RECORD",sequenceName="CPDB_DS.SEQ_JXL_PBOC_CREDIT_RECORD")  
@JsonIgnoreProperties(value = { "hibernateLazyInitializer"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class PBOCDataRecordPojo extends BaseDomain {
	

	private static final long serialVersionUID = 1L;
	/**
	 * 获取 主键
	 */
	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="SEQ_JXL_PBOC_CREDIT_RECORD")  
	@Column(name = "SEQID", unique = true, nullable = false)
	private long seqId;
	private String credit_record_type;
	private String is_overdue;
	private String deadline_time;
	private String grant_amount_type;
	private String grant_account_type;
	private String clear_time;
	private String grant_time;
	private String grant_amount;
	private String grant_name;
	private String grant_company;
	private String balance;
	private String expiration_time;
	private String month_of_five_year;
	private String month_of_five_year_90;
	private String overdue_amount;
	private String credit_limit;
	private String used_quotas;
	private String status;
	private Date create_date;
	private Date update_date;
	
	@ManyToOne(cascade = {CascadeType.MERGE,CascadeType.REFRESH }, optional = true) 
	@JoinColumn(name="FK_SEQID",referencedColumnName="seqId")
	private PBOCDataResPojo res;
	
	
	public long getSeqId() {
		return seqId;
	}
	public void setSeqId(long seqId) {
		this.seqId = seqId;
	}
	public String getCredit_record_type() {
		return credit_record_type;
	}
	public void setCredit_record_type(String credit_record_type) {
		this.credit_record_type = credit_record_type;
	}
	public String getIs_overdue() {
		return is_overdue;
	}
	public void setIs_overdue(String is_overdue) {
		this.is_overdue = is_overdue;
	}
	public String getDeadline_time() {
		return deadline_time;
	}
	public void setDeadline_time(String deadline_time) {
		this.deadline_time = deadline_time;
	}
	public String getGrant_amount_type() {
		return grant_amount_type;
	}
	public void setGrant_amount_type(String grant_amount_type) {
		this.grant_amount_type = grant_amount_type;
	}
	public String getClear_time() {
		return clear_time;
	}
	public void setClear_time(String clear_time) {
		this.clear_time = clear_time;
	}
	public String getGrant_time() {
		return grant_time;
	}
	public void setGrant_time(String grant_time) {
		this.grant_time = grant_time;
	}
	public String getGrant_amount() {
		return grant_amount;
	}
	public void setGrant_amount(String grant_amount) {
		this.grant_amount = grant_amount;
	}
	public String getGrant_name() {
		return grant_name;
	}
	public void setGrant_name(String grant_name) {
		this.grant_name = grant_name;
	}
	public String getGrant_company() {
		return grant_company;
	}
	public void setGrant_company(String grant_company) {
		this.grant_company = grant_company;
	}
	public String getBalance() {
		return balance;
	}
	public void setBalance(String balance) {
		this.balance = balance;
	}
	public String getExpiration_time() {
		return expiration_time;
	}
	public void setExpiration_time(String expiration_time) {
		this.expiration_time = expiration_time;
	}
	public String getMonth_of_five_year() {
		return month_of_five_year;
	}
	public void setMonth_of_five_year(String month_of_five_year) {
		this.month_of_five_year = month_of_five_year;
	}
	public String getMonth_of_five_year_90() {
		return month_of_five_year_90;
	}
	public void setMonth_of_five_year_90(String month_of_five_year_90) {
		this.month_of_five_year_90 = month_of_five_year_90;
	}
	public String getOverdue_amount() {
		return overdue_amount;
	}
	public void setOverdue_amount(String overdue_amount) {
		this.overdue_amount = overdue_amount;
	}
	public String getCredit_limit() {
		return credit_limit;
	}
	public void setCredit_limit(String credit_limit) {
		this.credit_limit = credit_limit;
	}
	public String getUsed_quotas() {
		return used_quotas;
	}
	public void setUsed_quotas(String used_quotas) {
		this.used_quotas = used_quotas;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Date getCreate_date() {
		return create_date;
	}
	public void setCreate_date(Date create_date) {
		this.create_date = create_date;
	}
	public Date getUpdate_date() {
		return update_date;
	}
	public void setUpdate_date(Date update_date) {
		this.update_date = update_date;
	}
	public PBOCDataResPojo getRes() {
		return res;
	}
	public void setRes(PBOCDataResPojo res) {
		this.res = res;
	}
	public String getGrant_account_type() {
		return grant_account_type;
	}
	public void setGrant_account_type(String grant_account_type) {
		this.grant_account_type = grant_account_type;
	}
	
	
	
	

}
