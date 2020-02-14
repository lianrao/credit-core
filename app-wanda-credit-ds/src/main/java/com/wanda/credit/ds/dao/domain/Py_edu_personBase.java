package com.wanda.credit.ds.dao.domain;

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

@Entity
@Table(name = "T_DS_PY_EDU_PERSONBASE",schema="CPDB_DS")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Py_edu_personBase extends BaseDomain implements Cloneable{
	/**
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 主键
	 */
	private String id;
	private String trade_id;
	private String name;
	private String documentNo;
	private String degree;
	private String specialty;
	private String college;
	private String graduateTime;
	private String graduateYears;
	private String originalAddress;
	private Integer verifyResult;
	private String birthday;
	private Integer gender;
	private Integer age;
	private String riskAndAdviceInfo;
	private String reportId;
	private Date create_date;
	
	
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
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDocumentNo() {
		return documentNo;
	}
	public void setDocumentNo(String documentNo) {
		this.documentNo = documentNo;
	}
	public String getDegree() {
		return degree;
	}
	public void setDegree(String degree) {
		this.degree = degree;
	}
	public String getSpecialty() {
		return specialty;
	}
	public void setSpecialty(String specialty) {
		this.specialty = specialty;
	}
	public String getCollege() {
		return college;
	}
	public void setCollege(String college) {
		this.college = college;
	}
	public String getGraduateTime() {
		return graduateTime;
	}
	public void setGraduateTime(String graduateTime) {
		this.graduateTime = graduateTime;
	}
	public String getGraduateYears() {
		return graduateYears;
	}
	public void setGraduateYears(String graduateYears) {
		this.graduateYears = graduateYears;
	}
	public String getOriginalAddress() {
		return originalAddress;
	}
	public void setOriginalAddress(String originalAddress) {
		this.originalAddress = originalAddress;
	}
	public Integer getVerifyResult() {
		return verifyResult;
	}
	public void setVerifyResult(Integer verifyResult) {
		this.verifyResult = verifyResult;
	}
	public String getBirthday() {
		return birthday;
	}
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}
	public Integer getGender() {
		return gender;
	}
	public void setGender(Integer gender) {
		this.gender = gender;
	}
	public Integer getAge() {
		return age;
	}
	public void setAge(Integer age) {
		this.age = age;
	}
	public String getRiskAndAdviceInfo() {
		return riskAndAdviceInfo;
	}
	public void setRiskAndAdviceInfo(String riskAndAdviceInfo) {
		this.riskAndAdviceInfo = riskAndAdviceInfo;
	}
	public String getReportId() {
		return reportId;
	}
	public void setReportId(String reportId) {
		this.reportId = reportId;
	}
	public Date getCreate_date() {
		return create_date;
	}
	public void setCreate_date(Date create_date) {
		this.create_date = create_date;
	}
	public Py_edu_personBase clone() throws CloneNotSupportedException{ 
	      return (Py_edu_personBase)super.clone(); 
	   }
}
