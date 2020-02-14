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
public class Py_car_check extends BaseDomain{

	private static final long serialVersionUID = 1L;

	 private String id                      ;
	 private String trade_id                ;
	 private String name                    ;
	 private String documentNo              ;
	 private String licenseNo               ;
	 private String carType 	            ;
	 private String nameCheckResult         ;
	 private String documentNoCheckResult   ;
	 private String licenseNoCheckResult    ;
	 private String carTypeCheckResult      ;
	public Py_car_check() {
		super();
	}

	public Py_car_check(String name, String documentNo, String licenseNo,
			String carType,String nameCheckResult, String documentNoCheckResult,
			String licenseNoCheckResult,  String carTypeCheckResult) {
		super();
		this.name = name;
		this.documentNo = documentNo;
		this.licenseNo = licenseNo;
		this.carType = carType;
		this.nameCheckResult = nameCheckResult;
		this.documentNoCheckResult = documentNoCheckResult;
		this.licenseNoCheckResult = licenseNoCheckResult;		
		this.carTypeCheckResult = carTypeCheckResult;
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
	@Column(name="INPUT_LICENSE_NO")
	public String getLicenseNo() {
		return licenseNo;
	}

	public void setLicenseNo(String licenseNo) {
		this.licenseNo = licenseNo;
	}
	@Column(name="INPUT_CAR_TYPE")
	public String getCarType() {
		return carType;
	}

	public void setCarType(String carType) {
		this.carType = carType;
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
	@Column(name="LICENSE_CHECK_RESULT")
	public String getLicenseNoCheckResult() {
		return licenseNoCheckResult;
	}

	public void setLicenseNoCheckResult(String licenseNoCheckResult) {
		this.licenseNoCheckResult = licenseNoCheckResult;
	}
	@Column(name="CARTYPE_CHECK_RESULT")
	public String getCarTypeCheckResult() {
		return carTypeCheckResult;
	}

	public void setCarTypeCheckResult(String carTypeCheckResult) {
		this.carTypeCheckResult = carTypeCheckResult;
	}

}
