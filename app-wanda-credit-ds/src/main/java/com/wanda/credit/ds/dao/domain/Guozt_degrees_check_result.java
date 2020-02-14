package com.wanda.credit.ds.dao.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.wanda.credit.base.domain.BaseDomain;

/**
 * 国政通学历
 */
@Entity
@Table(name = "T_DS_GZT_DEGREES",schema="CPDB_DS")
@SequenceGenerator(name = "Seq_T_DS_GZT_DEGREES", sequenceName = "Seq_T_DS_GZT_DEGREES")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer" })
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Guozt_degrees_check_result extends BaseDomain {
	private static final long serialVersionUID = 1L;
	private long id;
	private String trade_id;
	private String status1;
	private String userName;
	private String cardNo;
	private String graduate;
	private String educationDegree;
	private String enrolDate;
	private String specialityName;
	private String graduateTime;
	private String studyResult;
	private String studyStyle;
	private String schoolType;
	private String photo;
	private String image_file;
	private String schoolCity;
	private String schoolTrade;
	private String organization;
	private String schoolNature;
	private String schoolCategory;
	private String level;
	private String educationApproach;
	private String is985;
	private String is211;
	private String createDate;
	private String createYear;
	private String academicianNum;
	private String bshldzNum;
	private String bsdNum;
	private String ssdNum;
	private String zdxkNum;
	private String dstudyStyle;
	private Date create_date;
	private String sourceid;
//	国政通学历核查新的字段用于核查（P_C_B166 mafei-add）
	private String checkResult;
	private String graduate_chk_rst;
	private String educationdegree_chk_rst;
	private String educationapproach_chk_rst;
	private String graduatetime_chk_rst;
	private String birthday;
	/**
	 * 获取 主键
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Seq_T_DS_GZT_DEGREES")
	@Column(name = "ID", unique = true, nullable = false)
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTrade_id() {
		return trade_id;
	}

	public void setTrade_id(String trade_id) {
		this.trade_id = trade_id;
	}

	
	
	@Column(name="NAME")
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getCardNo() {
		return cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	public String getGraduate() {
		return graduate;
	}

	public void setGraduate(String graduate) {
		this.graduate = graduate;
	}

	public String getEducationDegree() {
		return educationDegree;
	}

	public void setEducationDegree(String educationDegree) {
		this.educationDegree = educationDegree;
	}

	public String getEnrolDate() {
		return enrolDate;
	}

	public void setEnrolDate(String enrolDate) {
		this.enrolDate = enrolDate;
	}

	public String getSpecialityName() {
		return specialityName;
	}

	public void setSpecialityName(String specialityName) {
		this.specialityName = specialityName;
	}

	public String getGraduateTime() {
		return graduateTime;
	}

	public void setGraduateTime(String graduateTime) {
		this.graduateTime = graduateTime;
	}

	public String getStudyResult() {
		return studyResult;
	}

	public void setStudyResult(String studyResult) {
		this.studyResult = studyResult;
	}

	public String getStudyStyle() {
		return studyStyle;
	}

	public void setStudyStyle(String studyStyle) {
		this.studyStyle = studyStyle;
	}

	public String getSchoolType() {
		return schoolType;
	}

	public void setSchoolType(String schoolType) {
		this.schoolType = schoolType;
	}

	@Transient
	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public String getImage_file() {
		return image_file;
	}

	public void setImage_file(String image_file) {
		this.image_file = image_file;
	}

	public String getSchoolCity() {
		return schoolCity;
	}

	public void setSchoolCity(String schoolCity) {
		this.schoolCity = schoolCity;
	}

	public String getSchoolTrade() {
		return schoolTrade;
	}

	public void setSchoolTrade(String schoolTrade) {
		this.schoolTrade = schoolTrade;
	}

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	public String getSchoolNature() {
		return schoolNature;
	}

	public void setSchoolNature(String schoolNature) {
		this.schoolNature = schoolNature;
	}

	public String getSchoolCategory() {
		return schoolCategory;
	}

	public void setSchoolCategory(String schoolCategory) {
		this.schoolCategory = schoolCategory;
	}

	@Column(name="glevel")
	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getEducationApproach() {
		return educationApproach;
	}

	public void setEducationApproach(String educationApproach) {
		this.educationApproach = educationApproach;
	}

	public String getIs985() {
		return is985;
	}

	public void setIs985(String is985) {
		this.is985 = is985;
	}

	public String getIs211() {
		return is211;
	}

	public void setIs211(String is211) {
		this.is211 = is211;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public String getCreateYear() {
		return createYear;
	}

	public void setCreateYear(String createYear) {
		this.createYear = createYear;
	}

	public String getAcademicianNum() {
		return academicianNum;
	}

	public void setAcademicianNum(String academicianNum) {
		this.academicianNum = academicianNum;
	}

	public String getBshldzNum() {
		return bshldzNum;
	}

	public void setBshldzNum(String bshldzNum) {
		this.bshldzNum = bshldzNum;
	}

	public String getBsdNum() {
		return bsdNum;
	}

	public void setBsdNum(String bsdNum) {
		this.bsdNum = bsdNum;
	}

	public String getSsdNum() {
		return ssdNum;
	}

	public void setSsdNum(String ssdNum) {
		this.ssdNum = ssdNum;
	}

	public String getZdxkNum() {
		return zdxkNum;
	}

	public void setZdxkNum(String zdxkNum) {
		this.zdxkNum = zdxkNum;
	}

	public String getDstudyStyle() {
		return dstudyStyle;
	}

	public void setDstudyStyle(String dstudyStyle) {
		this.dstudyStyle = dstudyStyle;
	}

	public String getStatus1() {
		return status1;
	}

	public void setStatus1(String status1) {
		this.status1 = status1;
	}

	public Date getCreate_date() {
		return create_date;
	}

	public void setCreate_date(Date create_date) {
		this.create_date = create_date;
	}

	public String getSourceid() {
		return sourceid;
	}

	public void setSourceid(String sourceid) {
		this.sourceid = sourceid;
	}
	public String getCheckResult() {
		return checkResult;
	}

	public void setCheckResult(String checkResult) {
		this.checkResult = checkResult;
	}

	public String getGraduate_chk_rst() {
		return graduate_chk_rst;
	}

	public void setGraduate_chk_rst(String graduate_chk_rst) {
		this.graduate_chk_rst = graduate_chk_rst;
	}

	public String getEducationdegree_chk_rst() {
		return educationdegree_chk_rst;
	}

	public void setEducationdegree_chk_rst(String educationdegree_chk_rst) {
		this.educationdegree_chk_rst = educationdegree_chk_rst;
	}

	public String getEducationapproach_chk_rst() {
		return educationapproach_chk_rst;
	}

	public void setEducationapproach_chk_rst(String educationapproach_chk_rst) {
		this.educationapproach_chk_rst = educationapproach_chk_rst;
	}

	public String getGraduatetime_chk_rst() {
		return graduatetime_chk_rst;
	}

	public void setGraduatetime_chk_rst(String graduatetime_chk_rst) {
		this.graduatetime_chk_rst = graduatetime_chk_rst;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	
}
