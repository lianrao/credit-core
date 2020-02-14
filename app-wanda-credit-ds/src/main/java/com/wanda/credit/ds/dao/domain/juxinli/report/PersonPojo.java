package com.wanda.credit.ds.dao.domain.juxinli.report;

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

/**
 * 申请人信息
 * 
 * @author xiaobin.hou
 * 
 */
@Entity
@Table(name = "CPDB_DS.T_DS_JXL_REP_PERSON")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class PersonPojo extends BaseDomain {

	
	private static final long serialVersionUID = 1L;
	
	private String seqId;
	private String requestId;
	private String province;
	private String city;
	private String gender;
	private String age;
	private String sign;
	private String state;
	private String status;
	private String real_name;
	private String region;
	private String id_card_num;
	private String token;
	private String version;
	private String success;
	private Date crt_time;
	private Date upd_time;
	
	/**
	 * 获取 主键
	 */
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	@Column(name = "SEQID", unique = true, nullable = false, length = 32)
	public String getSeqId() {
		return seqId;
	}
	public void setSeqId(String seqId) {
		this.seqId = seqId;
	}
	public String getRequestId() {
		return requestId;
	}
	public void setRequestId(String requestId) {
		this.requestId = requestId;
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
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getAge() {
		return age;
	}
	public void setAge(String age) {
		this.age = age;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getReal_name() {
		return real_name;
	}
	public void setReal_name(String real_name) {
		this.real_name = real_name;
	}
	public String getRegion() {
		return region;
	}
	public void setRegion(String region) {
		this.region = region;
	}
	public String getId_card_num() {
		return id_card_num;
	}
	public void setId_card_num(String id_card_num) {
		this.id_card_num = id_card_num;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public Date getCrt_time() {
		return crt_time;
	}
	public void setCrt_time(Date crt_time) {
		this.crt_time = crt_time;
	}
	public Date getUpd_time() {
		return upd_time;
	}
	public void setUpd_time(Date upd_time) {
		this.upd_time = upd_time;
	}
	public String getSuccess() {
		return success;
	}
	public void setSuccess(String success) {
		this.success = success;
	}
	
	public String toString() {
		return "PersonPojo [seqId=" + seqId + ", requestId=" + requestId
				+ ", province=" + province + ", city=" + city + ", gender="
				+ gender + ", age=" + age + ", sign=" + sign + ", state="
				+ state + ", status=" + status + ", real_name=" + real_name
				+ ", region=" + region + ", id_card_num=" + id_card_num
				+ ", token=" + token + ", version=" + version + ", success="
				+ success + ", crt_time=" + crt_time + ", upd_time=" + upd_time
				+ "]";
	}

	
	
}
