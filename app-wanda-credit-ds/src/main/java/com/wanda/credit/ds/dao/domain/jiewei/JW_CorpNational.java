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
@Table(name = "T_DS_JW_CORP_National")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer" })
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class JW_CorpNational extends SubReportBaseInfo {
	/**
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 主键
	 */
	private String id;
	private String trade_id;
	private String refid;
	private String corpName;
	private String registerNo;
	private String registDate;
	private String artificialName;
	private String status;
	private String registFund;
	private String manageRange;
	private String openDate;
	private String manageBeginDate;
	private String manageEndDate;
	private String corpType;
	private String registerDepartment;
	private String registerAddress;

	public JW_CorpNational() {
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

	public String getCorpName() {
		return corpName;
	}

	public void setCorpName(String corpName) {
		this.corpName = corpName;
	}

	public String getRegisterNo() {
		return registerNo;
	}

	public void setRegisterNo(String registerNo) {
		this.registerNo = registerNo;
	}

	public String getRegistDate() {
		return registDate;
	}

	public void setRegistDate(String registDate) {
		this.registDate = registDate;
	}

	public String getArtificialName() {
		return artificialName;
	}

	public void setArtificialName(String artificialName) {
		this.artificialName = artificialName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getRegistFund() {
		return registFund;
	}

	public void setRegistFund(String registFund) {
		this.registFund = registFund;
	}

	public String getManageRange() {
		return manageRange;
	}

	public void setManageRange(String manageRange) {
		this.manageRange = manageRange;
	}

	public String getOpenDate() {
		return openDate;
	}

	public void setOpenDate(String openDate) {
		this.openDate = openDate;
	}

	public String getManageBeginDate() {
		return manageBeginDate;
	}

	public void setManageBeginDate(String manageBeginDate) {
		this.manageBeginDate = manageBeginDate;
	}

	public String getManageEndDate() {
		return manageEndDate;
	}

	public void setManageEndDate(String manageEndDate) {
		this.manageEndDate = manageEndDate;
	}

	public String getCorpType() {
		return corpType;
	}

	public void setCorpType(String corpType) {
		this.corpType = corpType;
	}

	public String getRegisterDepartment() {
		return registerDepartment;
	}

	public void setRegisterDepartment(String registerDepartment) {
		this.registerDepartment = registerDepartment;
	}

	public String getRegisterAddress() {
		return registerAddress;
	}

	public void setRegisterAddress(String registerAddress) {
		this.registerAddress = registerAddress;
	}

}
