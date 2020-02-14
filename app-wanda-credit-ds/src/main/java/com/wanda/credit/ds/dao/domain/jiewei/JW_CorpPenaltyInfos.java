package com.wanda.credit.ds.dao.domain.jiewei;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "T_DS_JW_CORP_penaltyInfos")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer" })
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class JW_CorpPenaltyInfos extends SubReportBaseInfo {
	/**
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 主键
	 */

	private String id;
	private String trade_id;
	private String refid;
	private String recordNo;
	private String affair;
	private String panalty;
	private String execDepartment;
	private String recordDate;

	public JW_CorpPenaltyInfos() {
		super();
	}

	/**
	 * 获取 主键
	 */
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	@Column(name = "ID", unique = true, nullable = false, length = 32)
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTrade_id() {
		return trade_id;
	}

	public void setTrade_id(String trade_id) {
		this.trade_id = trade_id;
	}

	public String getRefid() {
		return refid;
	}

	public void setRefid(String refid) {
		this.refid = refid;
	}

	public String getRecordNo() {
		return recordNo;
	}

	public void setRecordNo(String recordNo) {
		this.recordNo = recordNo;
	}

	public String getAffair() {
		return affair;
	}

	public void setAffair(String affair) {
		this.affair = affair;
	}

	public String getPanalty() {
		return panalty;
	}

	public void setPanalty(String panalty) {
		this.panalty = panalty;
	}

	public String getExecDepartment() {
		return execDepartment;
	}

	public void setExecDepartment(String execDepartment) {
		this.execDepartment = execDepartment;
	}

	public String getRecordDate() {
		return recordDate;
	}

	public void setRecordDate(String recordDate) {
		this.recordDate = recordDate;
	}

}
