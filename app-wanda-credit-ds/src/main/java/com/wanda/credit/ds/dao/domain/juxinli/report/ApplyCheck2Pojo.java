package com.wanda.credit.ds.dao.domain.juxinli.report;

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
 * 申请表单检查
 */

@Entity
@Table(name = "CPDB_DS.T_DS_JXL_REP_APPLY_CHECK2")
@SequenceGenerator(name="SEQ_T_DS_REP_APPLY_CHECK2",sequenceName="CPDB_DS.SEQ_T_DS_REP_APPLY_CHECK2")  
@JsonIgnoreProperties(value = { "hibernateLazyInitializer"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ApplyCheck2Pojo extends BaseDomain {
	
	private static final long serialVersionUID = 1L;
	
	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="SEQ_T_DS_REP_APPLY_CHECK2")  
	@Column(name = "SEQID", unique = true, nullable = false)
	private long seqId;
	private String requestId;
	private String app_point;
	private String key_value;
	private String age;
	private String gender;
	private String province;
	private String city;
	private String region;
	private String website;
	private String reliability;
	private String reg_time;
	private String check_name;
	private String check_idcard;
	private String check_ebusiness;
	private String check_addr;
	private String relationship;
	private String contact_name;
	private String check_xiaohao;
	private String check_mobile;
	private String court_black;
	private String financial_black;
	
	
	
	public long getSeqId() {
		return seqId;
	}
	public void setSeqId(long seqId) {
		this.seqId = seqId;
	}
	public String getRequestId() {
		return requestId;
	}
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	public String getApp_point() {
		return app_point;
	}
	public void setApp_point(String app_point) {
		this.app_point = app_point;
	}
	public String getKey_value() {
		return key_value;
	}
	public void setKey_value(String key_value) {
		this.key_value = key_value;
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
	public String getRegion() {
		return region;
	}
	public void setRegion(String region) {
		this.region = region;
	}
	public String getWebsite() {
		return website;
	}
	public void setWebsite(String website) {
		this.website = website;
	}
	public String getReliability() {
		return reliability;
	}
	public void setReliability(String reliability) {
		this.reliability = reliability;
	}
	public String getReg_time() {
		return reg_time;
	}
	public void setReg_time(String reg_time) {
		this.reg_time = reg_time;
	}
	public String getCheck_name() {
		return check_name;
	}
	public void setCheck_name(String check_name) {
		this.check_name = check_name;
	}
	public String getCheck_idcard() {
		return check_idcard;
	}
	public void setCheck_idcard(String check_idcard) {
		this.check_idcard = check_idcard;
	}
	public String getCheck_ebusiness() {
		return check_ebusiness;
	}
	public void setCheck_ebusiness(String check_ebusiness) {
		this.check_ebusiness = check_ebusiness;
	}
	public String getCheck_addr() {
		return check_addr;
	}
	public void setCheck_addr(String check_addr) {
		this.check_addr = check_addr;
	}
	public String getRelationship() {
		return relationship;
	}
	public void setRelationship(String relationship) {
		this.relationship = relationship;
	}
	public String getContact_name() {
		return contact_name;
	}
	public void setContact_name(String contact_name) {
		this.contact_name = contact_name;
	}
	public String getCheck_xiaohao() {
		return check_xiaohao;
	}
	public void setCheck_xiaohao(String check_xiaohao) {
		this.check_xiaohao = check_xiaohao;
	}
	public String getCheck_mobile() {
		return check_mobile;
	}
	public void setCheck_mobile(String check_mobile) {
		this.check_mobile = check_mobile;
	}
	public String getCourt_black() {
		return court_black;
	}
	public void setCourt_black(String court_black) {
		this.court_black = court_black;
	}
	public String getFinancial_black() {
		return financial_black;
	}
	public void setFinancial_black(String financial_black) {
		this.financial_black = financial_black;
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
	
	
	
}
