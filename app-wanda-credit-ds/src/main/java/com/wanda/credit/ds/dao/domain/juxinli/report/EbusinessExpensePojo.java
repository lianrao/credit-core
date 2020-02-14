package com.wanda.credit.ds.dao.domain.juxinli.report;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.annotations.GenericGenerator;

import com.wanda.credit.base.domain.BaseDomain;

/**
 * 电商月消费分析
 * @author xiaobin.hou
 *
 */
@Entity
@Table(name = "CPDB_DS.T_DS_JXL_REP_EBUSINESS_EXPENSE")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class EbusinessExpensePojo extends BaseDomain{
	

	private static final long serialVersionUID = 1L;
	private String seqId;
	private String requestId;
	private String trans_mth;
	private String all_count;
//	total_amt;
	private String all_amount;
//	total_cnt;
	private String category;
	private Date crt_time;
	private Date upd_time;
	
	/**
	 * 获取 主键
	 */
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	@Column(name = "SEQID", unique = true, nullable = false, length = 32)
	public String getSeqId() {
		return seqId;
	}
	public void setSeqId(String seqId) {
		this.seqId = seqId;
	}
	public String getRequestId() {
		return requestId;
	}
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	public String getTrans_mth() {
		return trans_mth;
	}
	public void setTrans_mth(String trans_mth) {
		this.trans_mth = trans_mth;
	}
	@Column(name = "TOTAL_CNT")
	public String getAll_count() {
		return all_count;
	}
	public void setAll_count(String all_count) {
		this.all_count = all_count;
	}
	@Column(name = "TOTAL_AMT")
	public String getAll_amount() {
		return all_amount;
	}
	public void setAll_amount(String all_amount) {
		this.all_amount = all_amount;
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
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	
	
}
