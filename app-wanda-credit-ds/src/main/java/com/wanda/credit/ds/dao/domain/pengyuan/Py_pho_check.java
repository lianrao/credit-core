package com.wanda.credit.ds.dao.domain.pengyuan;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.annotations.GenericGenerator;

import com.wanda.credit.base.domain.BaseDomain;
@Entity
@Table(name = "CPDB_DS.T_DS_PY_PHO_CHECK")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Py_pho_check extends BaseDomain{

	private static final long serialVersionUID = 1L;

	private String name;
	private String documentNo;
	private String phone;
	private String id;
	private String trade_id;
	private String nameCheckResult;
	private String documentNoCheckResult;
	private String phoneCheckResult;
	private String operator;
	private String areaInfo;
	public Py_pho_check() {
		super();
	}
	
	public Py_pho_check(String name, String documentNo, String phone,
			String nameCheckResult, String documentNoCheckResult,
			String phoneCheckResult, String operator, String areaInfo) {
		super();
		this.name = name;
		this.documentNo = documentNo;
		this.phone = phone;
		this.nameCheckResult = nameCheckResult;
		this.documentNoCheckResult = documentNoCheckResult;
		this.phoneCheckResult = phoneCheckResult;
		this.operator = operator;
		this.areaInfo = areaInfo;
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
	@Column(name="NAME_CHECK_RESULT") 
	public String getNameCheckResult() {
		return nameCheckResult;
	}
	public void setNameCheckResult(String nameCheckResult) {
		this.nameCheckResult = nameCheckResult;
	}
	@Column(name="DOCUMENTNO_CHECK_RESULT") 
	public String getDocumentNoCheckResult() {
		return documentNoCheckResult;
	}
	public void setDocumentNoCheckResult(String documentNoCheckResult) {
		this.documentNoCheckResult = documentNoCheckResult;
	}
	@Column(name="PHONE_CHECK_RESULT") 
	public String getPhoneCheckResult() {
		return phoneCheckResult;
	}
	public void setPhoneCheckResult(String phoneCheckResult) {
		this.phoneCheckResult = phoneCheckResult;
	}
	@Column(name="OPERATOR") 
	public String getOperator() {
		return operator;
	}
	
	public void setOperator(String operator) {
		this.operator = operator;
	}
	@Column(name="AREA_INFO") 
	public String getAreaInfo() {
		return areaInfo;
	}
	public void setAreaInfo(String areaInfo) {
		this.areaInfo = areaInfo;
	}
    @Column(name="INPUT_NAME") 
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	@Column(name="INPUT_DOCUMENTNO") 
	public String getDocumentNo() {
		return documentNo;
	}

	public void setDocumentNo(String documentNo) {
		this.documentNo = documentNo;
	}
	@Column(name="INPUT_PHONE") 
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	@Override
	public String toString() {
		return "Py_pho_check [name=" + name + ", documentNo=" + documentNo
				+ ", phone=" + phone + ", nameCheckResult=" + nameCheckResult
				+ ", documentNoCheckResult=" + documentNoCheckResult
				+ ", phoneCheckResult=" + phoneCheckResult + ", operator="
				+ operator + ", areaInfo=" + areaInfo + "]";
	}
}
