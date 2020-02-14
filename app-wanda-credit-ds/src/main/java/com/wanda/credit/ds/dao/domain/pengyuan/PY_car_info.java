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
@Table(name = "CPDB_DS.t_ds_py_car_info")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer" })
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class PY_car_info extends BaseDomain {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	private String id;
	private String tradeId;
	private String reportId;
	private String name;
	private String cardNo;
	private String licenseNo;
	private String carType;
	private String vin;
	private String registTime;
	private String subreportIDs;
	private String treatResult;
	private String basicTreatResult;
	private String basicErrorMessage;
	private String nameCheckResult;
	private String cardNoCheckResult;
	private String licenseNoCheckResult;
	private String carTypeCheckResult;
	private String carStatusTreatResult;
	private String carStatusErrorMessage;
	private String carStatusDesc;
	private String carInfoTreatResult;
	private String carInfoErrorMessage;
	private String carCodeTreatResult;
	private String carCodeErrorMessage;
	private String carCodeCheckResult;
	private String registTimeTreatResult;
	private String registTimeErrorMessage;
	private String registTimeCheckResult;

	public PY_car_info() {
		super();
	}

	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	@Column(name = "id", unique = true, nullable = false, length = 32)
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Column(name = "trade_id")
	public String getTradeId() {
		return tradeId;
	}

	public void setTradeId(String tradeId) {
		this.tradeId = tradeId;
	}

	public String getReportId() {
		return reportId;
	}

	public void setReportId(String reportId) {
		this.reportId = reportId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCardNo() {
		return cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	public String getLicenseNo() {
		return licenseNo;
	}

	public void setLicenseNo(String licenseNo) {
		this.licenseNo = licenseNo;
	}

	public String getCarType() {
		return carType;
	}

	public void setCarType(String carType) {
		this.carType = carType;
	}

	public String getVin() {
		return vin;
	}

	public void setVin(String vin) {
		this.vin = vin;
	}

	public String getRegistTime() {
		return registTime;
	}

	public void setRegistTime(String registTime) {
		this.registTime = registTime;
	}

	public String getSubreportIDs() {
		return subreportIDs;
	}

	public void setSubreportIDs(String subreportIDs) {
		this.subreportIDs = subreportIDs;
	}

	public String getTreatResult() {
		return treatResult;
	}

	public void setTreatResult(String treatResult) {
		this.treatResult = treatResult;
	}

	@Column(name = "basic_treatresult")
	public String getBasicTreatResult() {
		return basicTreatResult;
	}

	public void setBasicTreatResult(String basicTreatResult) {
		this.basicTreatResult = basicTreatResult;
	}

	@Column(name = "basic_errormessage")
	public String getBasicErrorMessage() {
		return basicErrorMessage;
	}

	public void setBasicErrorMessage(String basicErrorMessage) {
		this.basicErrorMessage = basicErrorMessage;
	}

	@Column(name = "name_checkresult")
	public String getNameCheckResult() {
		return nameCheckResult;
	}

	public void setNameCheckResult(String nameCheckResult) {
		this.nameCheckResult = nameCheckResult;
	}

	@Column(name = "cardno_checkresult")
	public String getCardNoCheckResult() {
		return cardNoCheckResult;
	}

	public void setCardNoCheckResult(String cardNoCheckResult) {
		this.cardNoCheckResult = cardNoCheckResult;
	}

	@Column(name = "licenseno_checkresult")
	public String getLicenseNoCheckResult() {
		return licenseNoCheckResult;
	}

	public void setLicenseNoCheckResult(String licenseNoCheckResult) {
		this.licenseNoCheckResult = licenseNoCheckResult;
	}

	@Column(name = "cartype_checkresult")
	public String getCarTypeCheckResult() {
		return carTypeCheckResult;
	}

	public void setCarTypeCheckResult(String carTypeCheckResult) {
		this.carTypeCheckResult = carTypeCheckResult;
	}

	@Column(name = "carstatus_treatresult")
	public String getCarStatusTreatResult() {
		return carStatusTreatResult;
	}

	public void setCarStatusTreatResult(String carStatusTreatResult) {
		this.carStatusTreatResult = carStatusTreatResult;
	}

	@Column(name = "carstatus_errormessage")
	public String getCarStatusErrorMessage() {
		return carStatusErrorMessage;
	}

	public void setCarStatusErrorMessage(String carStatusErrorMessage) {
		this.carStatusErrorMessage = carStatusErrorMessage;
	}

	@Column(name = "carstatus_desc")
	public String getCarStatusDesc() {
		return carStatusDesc;
	}

	public void setCarStatusDesc(String carStatusDesc) {
		this.carStatusDesc = carStatusDesc;
	}

	@Column(name = "carinfo_treatresult")
	public String getCarInfoTreatResult() {
		return carInfoTreatResult;
	}

	public void setCarInfoTreatResult(String carInfoTreatResult) {
		this.carInfoTreatResult = carInfoTreatResult;
	}

	@Column(name = "carinfo_errormessage")
	public String getCarInfoErrorMessage() {
		return carInfoErrorMessage;
	}

	public void setCarInfoErrorMessage(String carInfoErrorMessage) {
		this.carInfoErrorMessage = carInfoErrorMessage;
	}

	@Column(name = "carcode_treatresult")
	public String getCarCodeTreatResult() {
		return carCodeTreatResult;
	}

	public void setCarCodeTreatResult(String carCodeTreatResult) {
		this.carCodeTreatResult = carCodeTreatResult;
	}

	@Column(name = "carcode_errormessage")
	public String getCarCodeErrorMessage() {
		return carCodeErrorMessage;
	}

	public void setCarCodeErrorMessage(String carCodeErrorMessage) {
		this.carCodeErrorMessage = carCodeErrorMessage;
	}

	@Column(name = "carCode_CheckResult")
	public String getCarCodeCheckResult() {
		return carCodeCheckResult;
	}

	public void setCarCodeCheckResult(String carCodeCheckResult) {
		this.carCodeCheckResult = carCodeCheckResult;
	}

	@Column(name = "registtime_treatresult")
	public String getRegistTimeTreatResult() {
		return registTimeTreatResult;
	}

	public void setRegistTimeTreatResult(String registTimeTreatResult) {
		this.registTimeTreatResult = registTimeTreatResult;
	}

	@Column(name = "registtime_errormessage")
	public String getRegistTimeErrorMessage() {
		return registTimeErrorMessage;
	}

	public void setRegistTimeErrorMessage(String registTimeErrorMessage) {
		this.registTimeErrorMessage = registTimeErrorMessage;
	}

	@Column(name = "registtime_checkresult")
	public String getRegistTimeCheckResult() {
		return registTimeCheckResult;
	}

	public void setRegistTimeCheckResult(String registTimeCheckResult) {
		this.registTimeCheckResult = registTimeCheckResult;
	}

}