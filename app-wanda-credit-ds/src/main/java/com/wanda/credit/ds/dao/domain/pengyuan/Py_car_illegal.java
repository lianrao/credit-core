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
@Table(name = "CPDB_DS.t_ds_py_car_illegal")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Py_car_illegal extends BaseDomain {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id;
	private String tradeId;
	private String carNumber;
	private String carType;
	private String carCode;
	private String carEngine;
	private String reportID;
	private String subReportTypes;
	private String treatResult;
	private String time;
	private String areaCode;
	private String province;
	private String city;
	private String county;
	private String location;
	private String reason;
	private String penalty;
	private String status;
	private String department;
	private String penaltyPoint;
	private String code;
	private String archive;
	private String phone;
	private String excutelocation;
	private String excutedepartment;
	private String category;
	private String lateFine;
	private String punishmentAccording;
	private String illegalEntry;
	private String recordType;
	private String poundage;
	private String canProcess;
	private String uniqueCode;
	private String penaltyFee;
	private String canProcessMsg;

	// Constructors

	/** default constructor */
	public Py_car_illegal() {
		super();
	}
	



	// Property accessors
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	@Column(name = "id", unique = true, nullable = false, length = 32)
	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Column(name="trade_id")
	public String getTradeId() {
		return tradeId;
	}


	public void setTradeId(String tradeId) {
		this.tradeId = tradeId;
	}


	public String getCarNumber() {
		return carNumber;
	}


	public void setCarNumber(String carNumber) {
		this.carNumber = carNumber;
	}


	public String getCarType() {
		return carType;
	}


	public void setCarType(String carType) {
		this.carType = carType;
	}


	public String getCarCode() {
		return carCode;
	}


	public void setCarCode(String carCode) {
		this.carCode = carCode;
	}


	public String getCarEngine() {
		return carEngine;
	}


	public void setCarEngine(String carEngine) {
		this.carEngine = carEngine;
	}


	public String getReportID() {
		return reportID;
	}


	public void setReportID(String reportID) {
		this.reportID = reportID;
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


	public String getTime() {
		return time;
	}


	public void setTime(String time) {
		this.time = time;
	}


	public String getAreaCode() {
		return areaCode;
	}


	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}


	public String getProvince() {
		return province;
	}


	public void setProvince(String province) {
		this.province = province;
	}


	public String getCity() {
		return city;
	}


	public void setCity(String city) {
		this.city = city;
	}


	public String getCounty() {
		return county;
	}


	public void setCounty(String county) {
		this.county = county;
	}


	public String getLocation() {
		return location;
	}


	public void setLocation(String location) {
		this.location = location;
	}


	public String getReason() {
		return reason;
	}


	public void setReason(String reason) {
		this.reason = reason;
	}


	public String getPenalty() {
		return penalty;
	}


	public void setPenalty(String penalty) {
		this.penalty = penalty;
	}


	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}


	public String getDepartment() {
		return department;
	}


	public void setDepartment(String department) {
		this.department = department;
	}


	public String getPenaltyPoint() {
		return penaltyPoint;
	}


	public void setPenaltyPoint(String penaltyPoint) {
		this.penaltyPoint = penaltyPoint;
	}


	public String getCode() {
		return code;
	}


	public void setCode(String code) {
		this.code = code;
	}


	public String getArchive() {
		return archive;
	}


	public void setArchive(String archive) {
		this.archive = archive;
	}


	public String getPhone() {
		return phone;
	}


	public void setPhone(String phone) {
		this.phone = phone;
	}


	public String getExcutelocation() {
		return excutelocation;
	}


	public void setExcutelocation(String excutelocation) {
		this.excutelocation = excutelocation;
	}


	public String getExcutedepartment() {
		return excutedepartment;
	}


	public void setExcutedepartment(String excutedepartment) {
		this.excutedepartment = excutedepartment;
	}


	public String getCategory() {
		return category;
	}


	public void setCategory(String category) {
		this.category = category;
	}


	public String getLateFine() {
		return lateFine;
	}


	public void setLateFine(String lateFine) {
		this.lateFine = lateFine;
	}


	public String getPunishmentAccording() {
		return punishmentAccording;
	}


	public void setPunishmentAccording(String punishmentAccording) {
		this.punishmentAccording = punishmentAccording;
	}


	public String getIllegalEntry() {
		return illegalEntry;
	}


	public void setIllegalEntry(String illegalEntry) {
		this.illegalEntry = illegalEntry;
	}


	public String getRecordType() {
		return recordType;
	}


	public void setRecordType(String recordType) {
		this.recordType = recordType;
	}


	public String getPoundage() {
		return poundage;
	}


	public void setPoundage(String poundage) {
		this.poundage = poundage;
	}


	public String getCanProcess() {
		return canProcess;
	}


	public void setCanProcess(String canProcess) {
		this.canProcess = canProcess;
	}

	public String getUniqueCode() {
		return uniqueCode;
	}


	public void setUniqueCode(String uniqueCode) {
		this.uniqueCode = uniqueCode;
	}


	public String getPenaltyFee() {
		return penaltyFee;
	}


	public void setPenaltyFee(String penaltyFee) {
		this.penaltyFee = penaltyFee;
	}


	public String getCanProcessMsg() {
		return canProcessMsg;
	}


	public void setCanProcessMsg(String canProcessMsg) {
		this.canProcessMsg = canProcessMsg;
	}





	
}