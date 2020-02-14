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
 * 通话信息汇总
 * 
 * @author xiaobin.hou
 * 
 */
@Entity
@Table(name = "CPDB_DS.T_DS_JXL_REP_CONTACT_LIST")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ContactInfoPojo extends BaseDomain {

	private static final long serialVersionUID = 1L;

	private String seqId;
	private String requestId;
	private String phone_num;
	private String phone_num_loc;
	private String contact_name;
	private String needs_type;
	private String call_cnt;
	private String call_len;
	private String call_out_len;
	private String call_out_cnt;
	private String call_in_cnt;
	private String call_in_len;
	private String p_relation;
	private String contact_1w;
	private String contact_1m;
	private String contact_3m;
	private String contact_3m_plus;
	private String contact_early_morning;
	private String contact_morning;
	private String contact_holiday;
	private String contact_noon;
	private String contact_afternoon;
	private String contact_night;
	private String contact_all_day;
	private String contact_weekday;
	private String contact_weekend;
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
	public String getPhone_num() {
		return phone_num;
	}
	public void setPhone_num(String phone_num) {
		this.phone_num = phone_num;
	}
	public String getPhone_num_loc() {
		return phone_num_loc;
	}
	public void setPhone_num_loc(String phone_num_loc) {
		this.phone_num_loc = phone_num_loc;
	}
	public String getContact_name() {
		return contact_name;
	}
	public void setContact_name(String contact_name) {
		this.contact_name = contact_name;
	}
	public String getCall_cnt() {
		return call_cnt;
	}
	public void setCall_cnt(String call_cnt) {
		this.call_cnt = call_cnt;
	}
	public String getCall_len() {
		return call_len;
	}
	public void setCall_len(String call_len) {
		this.call_len = call_len;
	}
	public String getCall_out_len() {
		return call_out_len;
	}
	public void setCall_out_len(String call_out_len) {
		this.call_out_len = call_out_len;
	}
	public String getCall_out_cnt() {
		return call_out_cnt;
	}
	public void setCall_out_cnt(String call_out_cnt) {
		this.call_out_cnt = call_out_cnt;
	}
	public String getCall_in_cnt() {
		return call_in_cnt;
	}
	public void setCall_in_cnt(String call_in_cnt) {
		this.call_in_cnt = call_in_cnt;
	}
	public String getCall_in_len() {
		return call_in_len;
	}
	public void setCall_in_len(String call_in_len) {
		this.call_in_len = call_in_len;
	}
	public String getP_relation() {
		return p_relation;
	}
	public void setP_relation(String p_relation) {
		this.p_relation = p_relation;
	}
	public String getContact_3m() {
		return contact_3m;
	}
	public void setContact_3m(String contact_3m) {
		this.contact_3m = contact_3m;
	}
	public String getContact_morning() {
		return contact_morning;
	}
	public void setContact_morning(String contact_morning) {
		this.contact_morning = contact_morning;
	}
	public String getContact_holiday() {
		return contact_holiday;
	}
	public void setContact_holiday(String contact_holiday) {
		this.contact_holiday = contact_holiday;
	}
	public String getContact_all_day() {
		return contact_all_day;
	}
	public void setContact_all_day(String contact_all_day) {
		this.contact_all_day = contact_all_day;
	}
	public String getContact_weekday() {
		return contact_weekday;
	}
	public void setContact_weekday(String contact_weekday) {
		this.contact_weekday = contact_weekday;
	}
	public String getContact_weekend() {
		return contact_weekend;
	}
	public void setContact_weekend(String contact_weekend) {
		this.contact_weekend = contact_weekend;
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
	public String getContact_1w() {
		return contact_1w;
	}
	public void setContact_1w(String contact_1w) {
		this.contact_1w = contact_1w;
	}
	public String getContact_1m() {
		return contact_1m;
	}
	public void setContact_1m(String contact_1m) {
		this.contact_1m = contact_1m;
	}
	public String getContact_3m_plus() {
		return contact_3m_plus;
	}
	public void setContact_3m_plus(String contact_3m_plus) {
		this.contact_3m_plus = contact_3m_plus;
	}
	public String getContact_early_morning() {
		return contact_early_morning;
	}
	public void setContact_early_morning(String contact_early_morning) {
		this.contact_early_morning = contact_early_morning;
	}
	public String getContact_noon() {
		return contact_noon;
	}
	public void setContact_noon(String contact_noon) {
		this.contact_noon = contact_noon;
	}
	public String getContact_afternoon() {
		return contact_afternoon;
	}
	public void setContact_afternoon(String contact_afternoon) {
		this.contact_afternoon = contact_afternoon;
	}
	public String getContact_night() {
		return contact_night;
	}
	public void setContact_night(String contact_night) {
		this.contact_night = contact_night;
	}
	public String getNeeds_type() {
		return needs_type;
	}
	public void setNeeds_type(String needs_type) {
		this.needs_type = needs_type;
	}

	
	public String toString() {
		return "ContactInfoPojo [seqId=" + seqId + ", requestId=" + requestId
				+ ", phone_num=" + phone_num + ", phone_num_loc="
				+ phone_num_loc + ", contact_name=" + contact_name
				+ ", needs_type=" + needs_type + ", call_cnt=" + call_cnt
				+ ", call_len=" + call_len + ", call_out_len=" + call_out_len
				+ ", call_out_cnt=" + call_out_cnt + ", call_in_cnt="
				+ call_in_cnt + ", call_in_len=" + call_in_len
				+ ", p_relation=" + p_relation + ", contact_1w=" + contact_1w
				+ ", contact_1m=" + contact_1m + ", contact_3m=" + contact_3m
				+ ", contact_3m_plus=" + contact_3m_plus
				+ ", contact_early_morning=" + contact_early_morning
				+ ", contact_morning=" + contact_morning + ", contact_holiday="
				+ contact_holiday + ", contact_noon=" + contact_noon
				+ ", contact_afternoon=" + contact_afternoon
				+ ", contact_night=" + contact_night + ", contact_all_day="
				+ contact_all_day + ", contact_weekday=" + contact_weekday
				+ ", contact_weekend=" + contact_weekend + ", crt_time="
				+ crt_time + ", upd_time=" + upd_time + "]";
	}
	
	

}
