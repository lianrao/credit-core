package com.wanda.credit.ds.dao.domain.juxinli.apply;

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
 * 提交申请表达和提交采集请求返回信息中的dataSource信息
 * @author xiaobin.hou
 *
 */
@Entity
@Table(name="CPDB_DS.T_DS_JXL_ORIG_RESP_RESULT")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ApplyNextDataSourcePojo extends BaseDomain {

	
	private static final long serialVersionUID = 1L;
	
	/**
	 * 获取 主键
	 */
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	@Column(name = "SEQID", unique = true, nullable = false, length = 32)
	private String seqId;
	private String requestId;
	private String datasource_id;
	private String website;
	private String name;
	private String category_name;
	private String category;
	private String deal_time;
	private String offline_times;
	private String status;
	private String website_code;
	private String reset_pwd_method;
	private String sms_required;
	private String required_captcha_user_identifi;
	private String success;
	private Date crt_time;
	private Date upd_time;
	
	
	
	public String getDatasource_id() {
		return datasource_id;
	}
	public void setDatasource_id(String datasource_id) {
		this.datasource_id = datasource_id;
	}
	public String getWebsite() {
		return website;
	}
	public void setWebsite(String website) {
		this.website = website;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCategory_name() {
		return category_name;
	}
	public void setCategory_name(String category_name) {
		this.category_name = category_name;
	}
	public String getDeal_time() {
		return deal_time;
	}
	public void setDeal_time(String deal_time) {
		this.deal_time = deal_time;
	}
	public String getOffline_times() {
		return offline_times;
	}
	public void setOffline_times(String offline_times) {
		this.offline_times = offline_times;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getWebsite_code() {
		return website_code;
	}
	public void setWebsite_code(String website_code) {
		this.website_code = website_code;
	}
	public String getReset_pwd_method() {
		return reset_pwd_method;
	}
	public void setReset_pwd_method(String reset_pwd_method) {
		this.reset_pwd_method = reset_pwd_method;
	}
	public String getSms_required() {
		return sms_required;
	}
	public void setSms_required(String sms_required) {
		this.sms_required = sms_required;
	}
	public String getRequired_captcha_user_identifi() {
		return required_captcha_user_identifi;
	}
	public void setRequired_captcha_user_identifi(
			String required_captcha_user_identifi) {
		this.required_captcha_user_identifi = required_captcha_user_identifi;
	}
	public String getSuccess() {
		return success;
	}
	public void setSuccess(String success) {
		this.success = success;
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
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
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
	
	
	
}
