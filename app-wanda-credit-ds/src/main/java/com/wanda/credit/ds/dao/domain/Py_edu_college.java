package com.wanda.credit.ds.dao.domain;

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
@Table(name = "T_DS_PY_EDU_COLLEGES",schema="CPDB_DS")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Py_edu_college extends BaseDomain{
	/**
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 主键
	 */
	private String id;
	private String trade_id;
	private String college;
	private String collegeOldName;
	private String address;
	private String createDate;
	private String createYears;
	private String colgCharacter;
	private String colgLevel;
	private String character;
	private String colgType;
	private String scienceBatch;
	private String artBatch;
	private String postDoctorNum;
	private String doctorDegreeNum;
	private String masterDegreeNum;
	private String academicianNum;
	private String is211;
	private String manageDept;
	private String keySubjectNum;
	
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
	public String getCollege() {
		return college;
	}
	public void setCollege(String college) {
		this.college = college;
	}
	public String getCollegeOldName() {
		return collegeOldName;
	}
	public void setCollegeOldName(String collegeOldName) {
		this.collegeOldName = collegeOldName;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getCreateDate() {
		return createDate;
	}
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}
	public String getCreateYears() {
		return createYears;
	}
	public void setCreateYears(String createYears) {
		this.createYears = createYears;
	}
	public String getColgCharacter() {
		return colgCharacter;
	}
	public void setColgCharacter(String colgCharacter) {
		this.colgCharacter = colgCharacter;
	}
	public String getColgLevel() {
		return colgLevel;
	}
	public void setColgLevel(String colgLevel) {
		this.colgLevel = colgLevel;
	}
	public String getCharacter() {
		return character;
	}
	public void setCharacter(String character) {
		this.character = character;
	}
	public String getColgType() {
		return colgType;
	}
	public void setColgType(String colgType) {
		this.colgType = colgType;
	}
	public String getScienceBatch() {
		return scienceBatch;
	}
	public void setScienceBatch(String scienceBatch) {
		this.scienceBatch = scienceBatch;
	}
	public String getArtBatch() {
		return artBatch;
	}
	public void setArtBatch(String artBatch) {
		this.artBatch = artBatch;
	}
	public String getPostDoctorNum() {
		return postDoctorNum;
	}
	public void setPostDoctorNum(String postDoctorNum) {
		this.postDoctorNum = postDoctorNum;
	}
	 
	public String getDoctorDegreeNum() {
		return doctorDegreeNum;
	}
	public void setDoctorDegreeNum(String doctorDegreeNum) {
		this.doctorDegreeNum = doctorDegreeNum;
	}
	 
	public String getMasterDegreeNum() {
		return masterDegreeNum;
	}
	public void setMasterDegreeNum(String masterDegreeNum) {
		this.masterDegreeNum = masterDegreeNum;
	}
	public String getAcademicianNum() {
		return academicianNum;
	}
	public void setAcademicianNum(String academicianNum) {
		this.academicianNum = academicianNum;
	}
	public String getIs211() {
		return is211;
	}
	public void setIs211(String is211) {
		this.is211 = is211;
	}
	public String getManageDept() {
		return manageDept;
	}
	public void setManageDept(String manageDept) {
		this.manageDept = manageDept;
	}
	public String getKeySubjectNum() {
		return keySubjectNum;
	}
	public void setKeySubjectNum(String keySubjectNum) {
		this.keySubjectNum = keySubjectNum;
	}
}
