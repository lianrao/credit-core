package com.wanda.credit.ds.dao.domain.pengyuan;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.annotations.GenericGenerator;

import com.wanda.credit.base.domain.BaseDomain;


@Entity
@Table(name = "CPDB_DS.t_ds_py_driving_licence")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer" })
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Py_driver_license extends BaseDomain {

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
	private String name;
	private String cardNo;
	private String carModels;
	private String firstGetDocDate;
	private String archviesNo;
	private String subreportIds;
	private String subReportTypes;
	private String treatResult;
	private String basicTreatResult;
	private String nameCheckResult;
	private String cardNoCheckResult;
	private String carModelsTreatResult;
	private String carModelsCheckResult;
	private String firstGetDocDateTreatResult;
	private String firstGetDocDateCheckResult;
	private String archviesNoTreatResult;
	private String archviesNoCheckResult;
	private String statusTreatResult;
	private String driverLicenseStatusDesc;
	private String nameErrorMessage;
	private String cardnoErrorMessage;
	private String carModelsErrorMessage;
	private String firstGetDocDateErrorMessage;
	private String archviesNoErrorMessage;
	private String statusErrorMessage;

	public Py_driver_license() {
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

	public String getCarModels() {
		return carModels;
	}

	public void setCarModels(String carModels) {
		this.carModels = carModels;
	}

	public String getFirstGetDocDate() {
		return firstGetDocDate;
	}

	public void setFirstGetDocDate(String firstGetDocDate) {
		this.firstGetDocDate = firstGetDocDate;
	}

	public String getArchviesNo() {
		return archviesNo;
	}

	public void setArchviesNo(String archviesNo) {
		this.archviesNo = archviesNo;
	}

	public String getSubreportIds() {
		return subreportIds;
	}

	public void setSubreportIds(String subreportIds) {
		this.subreportIds = subreportIds;
	}

	public String getSubReportTypes() {
		return subReportTypes;
	}

	public void setSubReportTypes(String subReportTypes) {
		this.subReportTypes = subReportTypes;
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

	@Column(name = "carmodels_treatresult")
	public String getCarModelsTreatResult() {
		return carModelsTreatResult;
	}

	public void setCarModelsTreatResult(String carModelsTreatResult) {
		this.carModelsTreatResult = carModelsTreatResult;
	}

	@Column(name = "carmodels_checkresult")
	public String getCarModelsCheckResult() {
		return carModelsCheckResult;
	}

	public void setCarModelsCheckResult(String carModelsCheckResult) {
		this.carModelsCheckResult = carModelsCheckResult;
	}

	@Column(name = "firstgetdocdate_treatresult")
	public String getFirstGetDocDateTreatResult() {
		return firstGetDocDateTreatResult;
	}

	public void setFirstGetDocDateTreatResult(String firstGetDocDateTreatResult) {
		this.firstGetDocDateTreatResult = firstGetDocDateTreatResult;
	}

	@Column(name = "firstgetdocdate_checkresult")
	public String getFirstGetDocDateCheckResult() {
		return firstGetDocDateCheckResult;
	}

	public void setFirstGetDocDateCheckResult(String firstGetDocDateCheckResult) {
		this.firstGetDocDateCheckResult = firstGetDocDateCheckResult;
	}

	@Column(name = "archviesno_treatresult")
	public String getArchviesNoTreatResult() {
		return archviesNoTreatResult;
	}

	public void setArchviesNoTreatResult(String archviesNoTreatResult) {
		this.archviesNoTreatResult = archviesNoTreatResult;
	}

	@Column(name = "archviesno_checkresult")
	public String getArchviesNoCheckResult() {
		return archviesNoCheckResult;
	}

	public void setArchviesNoCheckResult(String archviesNoCheckResult) {
		this.archviesNoCheckResult = archviesNoCheckResult;
	}

	@Column(name = "status_treatresult")
	public String getStatusTreatResult() {
		return statusTreatResult;
	}

	public void setStatusTreatResult(String statusTreatResult) {
		this.statusTreatResult = statusTreatResult;
	}

	public String getDriverLicenseStatusDesc() {
		return driverLicenseStatusDesc;
	}

	public void setDriverLicenseStatusDesc(String driverLicenseStatusDesc) {
		this.driverLicenseStatusDesc = driverLicenseStatusDesc;
	}


	@Column(name = "name_errormessage")
	public String getNameErrorMessage() {
		return nameErrorMessage;
	}

	public void setNameErrorMessage(String nameErrorMessage) {
		this.nameErrorMessage = nameErrorMessage;
	}

	@Column(name = "cardno_errormessage")
	public String getCardnoErrorMessage() {
		return cardnoErrorMessage;
	}

	public void setCardnoErrorMessage(String cardnoErrorMessage) {
		this.cardnoErrorMessage = cardnoErrorMessage;
	}

	@Column(name = "carmodels_errormessage")
	public String getCarModelsErrorMessage() {
		return carModelsErrorMessage;
	}

	public void setCarModelsErrorMessage(String carModelsErrorMessage) {
		this.carModelsErrorMessage = carModelsErrorMessage;
	}

	@Column(name = "firstgetdocdate_errormessage")
	public String getFirstGetDocDateErrorMessage() {
		return firstGetDocDateErrorMessage;
	}

	public void setFirstGetDocDateErrorMessage(String firstGetDocDateErrorMessage) {
		this.firstGetDocDateErrorMessage = firstGetDocDateErrorMessage;
	}

	@Column(name = "archviesno_errormessage")
	public String getArchviesNoErrorMessage() {
		return archviesNoErrorMessage;
	}

	public void setArchviesNoErrorMessage(String archviesNoErrorMessage) {
		this.archviesNoErrorMessage = archviesNoErrorMessage;
	}

	@Column(name = "status_errormessage")
	public String getStatusErrorMessage() {
		return statusErrorMessage;
	}

	public void setStatusErrorMessage(String statusErrorMessage) {
		this.statusErrorMessage = statusErrorMessage;
	}

}