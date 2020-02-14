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
@Table(name = "T_DS_JW_CORP_Shareholder")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer" })
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class JW_CorpShareholder extends SubReportBaseInfo {
	/**
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 主键
	 */

	private String id;
	private String trade_id;
	private String refid;
	private String name;
	private String type;
	private String certType;
	private String certID;
	private String contributiveType;
	private String contributiveFund;

	public JW_CorpShareholder() {
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCertType() {
		return certType;
	}

	public void setCertType(String certType) {
		this.certType = certType;
	}

	public String getCertID() {
		return certID;
	}

	public void setCertID(String certID) {
		this.certID = certID;
	}

	public String getContributiveType() {
		return contributiveType;
	}

	public void setContributiveType(String contributiveType) {
		this.contributiveType = contributiveType;
	}

	public String getContributiveFund() {
		return contributiveFund;
	}

	public void setContributiveFund(String contributiveFund) {
		this.contributiveFund = contributiveFund;
	}

}
