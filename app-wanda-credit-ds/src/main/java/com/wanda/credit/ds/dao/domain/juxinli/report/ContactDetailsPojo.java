package com.wanda.credit.ds.dao.domain.juxinli.report;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.annotations.GenericGenerator;

import com.wanda.credit.base.domain.BaseDomain;


/**
 * 通话信息表
 * @author xiaobin.hou
 *
 */
@Entity
@Table(name = "CPDB_DS.T_DS_JXL_REP_CONTACT_DETAIL")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ContactDetailsPojo extends BaseDomain{

	private static final long serialVersionUID = 1L;
	
	private String seqId;
	private String requestId;
	private String sms_cnt;
	private String phone_num;
	private String phone_num_loc;
	private String call_cnt;
	private String call_len;
	private String call_in_cnt;
	private String call_out_cnt;
	private String trans_start;
	private String trans_end;
	private Date crt_time;
	private Date upd_time;
	private CollectionContactPojo contactPojo;
	
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
	public String getCall_in_cnt() {
		return call_in_cnt;
	}
	public void setCall_in_cnt(String call_in_cnt) {
		this.call_in_cnt = call_in_cnt;
	}
	public String getCall_out_cnt() {
		return call_out_cnt;
	}
	public void setCall_out_cnt(String call_out_cnt) {
		this.call_out_cnt = call_out_cnt;
	}
	public String getTrans_start() {
		return trans_start;
	}
	public void setTrans_start(String trans_start) {
		this.trans_start = trans_start;
	}
	public String getTrans_end() {
		return trans_end;
	}
	public void setTrans_end(String trans_end) {
		this.trans_end = trans_end;
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
	public String getSms_cnt() {
		return sms_cnt;
	}
	public void setSms_cnt(String sms_cnt) {
		this.sms_cnt = sms_cnt;
	}
	@ManyToOne(cascade = {CascadeType.MERGE,CascadeType.REFRESH }, optional = true) 
	@JoinColumn(name="FK_SEQID",referencedColumnName="seqId")
	public CollectionContactPojo getContactPojo() {
		return contactPojo;
	}
	public void setContactPojo(CollectionContactPojo contactPojo) {
		this.contactPojo = contactPojo;
	}
	

	
}
