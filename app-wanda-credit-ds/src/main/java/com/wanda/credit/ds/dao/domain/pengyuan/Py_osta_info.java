/**   
* @Description: 鹏元-职业资格信息-持久化类 
* @author xiaobin.hou  
* @date 2016年8月16日 上午11:07:21 
* @version V1.0   
*/
package com.wanda.credit.ds.dao.domain.pengyuan;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.wanda.credit.base.domain.BaseDomain;

/**
 * @author xiaobin.hou
 *
 */
@Entity
@Table(name = "CPDB_DS.T_DS_PY_Vocational")
@SequenceGenerator(name="Seq_T_DS_PY_Vocational",sequenceName="CPDB_DS.Seq_T_DS_PY_Vocational")  
@JsonIgnoreProperties(value = { "hibernateLazyInitializer"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Py_osta_info extends BaseDomain {
	private static final long serialVersionUID = -7072003523639806275L;
	
	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="Seq_T_DS_PY_Vocational")  
	@Column(name = "ID", unique = true, nullable = false)
	private long id;
	private String trade_id;
	private String name;
	private String cardNo;
	private String birthday;
	private String age;
	private String gender;
	private String originalAddress;
	private String verifyResult;
	private String treatResult;
	private String certificateID;
	private String occupation;
	@Column(name="level1")
	private String level;
	private String banZhengRiQi;
	private String submitOrgName;
	private String cityName;
	private String avgNationalSalary;
	private String avgProvinceSalary;
	private String avgSalary;
	private String infoDate;
	private Date create_date;
	private Date update_date;
	
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
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAge() {
		return age;
	}
	public void setAge(String age) {
		this.age = age;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getOriginalAddress() {
		return originalAddress;
	}
	public void setOriginalAddress(String originalAddress) {
		this.originalAddress = originalAddress;
	}
	public String getVerifyResult() {
		return verifyResult;
	}
	public void setVerifyResult(String verifyResult) {
		this.verifyResult = verifyResult;
	}
	public String getCertificateID() {
		return certificateID;
	}
	public void setCertificateID(String certificateID) {
		this.certificateID = certificateID;
	}
	public String getOccupation() {
		return occupation;
	}
	public void setOccupation(String occupation) {
		this.occupation = occupation;
	}
	
	public String getBanZhengRiQi() {
		return banZhengRiQi;
	}
	public void setBanZhengRiQi(String banZhengRiQi) {
		this.banZhengRiQi = banZhengRiQi;
	}
	public String getSubmitOrgName() {
		return submitOrgName;
	}
	public void setSubmitOrgName(String submitOrgName) {
		this.submitOrgName = submitOrgName;
	}
	public String getCityName() {
		return cityName;
	}
	public void setCityName(String cityName) {
		this.cityName = cityName;
	}
	public String getAvgNationalSalary() {
		return avgNationalSalary;
	}
	public void setAvgNationalSalary(String avgNationalSalary) {
		this.avgNationalSalary = avgNationalSalary;
	}
	public String getAvgProvinceSalary() {
		return avgProvinceSalary;
	}
	public void setAvgProvinceSalary(String avgProvinceSalary) {
		this.avgProvinceSalary = avgProvinceSalary;
	}
	public String getAvgSalary() {
		return avgSalary;
	}
	public void setAvgSalary(String avgSalary) {
		this.avgSalary = avgSalary;
	}
	public String getInfoDate() {
		return infoDate;
	}
	public void setInfoDate(String infoDate) {
		this.infoDate = infoDate;
	}
	public String getCardNo() {
		return cardNo;
	}
	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}
	public void setCreate_date(Date create_date) {
		this.create_date = create_date;
	}
	public void setUpdate_date(Date update_date) {
		this.update_date = update_date;
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	public Date getCreate_date() {
		return create_date;
	}
	public Date getUpdate_date() {
		return update_date;
	}
	public String getTreatResult() {
		return treatResult;
	}
	public void setTreatResult(String treatResult) {
		this.treatResult = treatResult;
	}
	public String getBirthday() {
		return birthday;
	}
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}
	
	

}
