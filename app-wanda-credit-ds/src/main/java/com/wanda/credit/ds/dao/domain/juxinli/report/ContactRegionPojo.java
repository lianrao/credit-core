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
 * 联系人区域汇总
 */
@Entity
@Table(name = "CPDB_DS.T_DS_JXL_REP_CONTACT_REGION")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ContactRegionPojo extends BaseDomain {

	private static final long serialVersionUID = 1L;

	private String seqId;
	private String requestId;
	private String region_loc;
	private String region_uniq_num_cnt;
	private String region_call_in_cnt;
	private String region_call_in_time;
	private String region_call_out_cnt;
	private String region_call_out_time;
	private String region_avg_call_in_time;
	private String region_avg_call_out_time;
	private String region_call_in_cnt_pct;
	private String region_call_in_time_pct;
	private String region_call_out_time_pct;
	private String region_call_out_cnt_pct;
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
	public String getRegion_loc() {
		return region_loc;
	}
	public void setRegion_loc(String region_loc) {
		this.region_loc = region_loc;
	}
	public String getRegion_uniq_num_cnt() {
		return region_uniq_num_cnt;
	}
	public void setRegion_uniq_num_cnt(String region_uniq_num_cnt) {
		this.region_uniq_num_cnt = region_uniq_num_cnt;
	}
	public String getRegion_call_in_cnt() {
		return region_call_in_cnt;
	}
	public void setRegion_call_in_cnt(String region_call_in_cnt) {
		this.region_call_in_cnt = region_call_in_cnt;
	}
	public String getRegion_call_in_time() {
		return region_call_in_time;
	}
	public void setRegion_call_in_time(String region_call_in_time) {
		this.region_call_in_time = region_call_in_time;
	}
	public String getRegion_call_out_cnt() {
		return region_call_out_cnt;
	}
	public void setRegion_call_out_cnt(String region_call_out_cnt) {
		this.region_call_out_cnt = region_call_out_cnt;
	}
	public String getRegion_call_out_time() {
		return region_call_out_time;
	}
	public void setRegion_call_out_time(String region_call_out_time) {
		this.region_call_out_time = region_call_out_time;
	}
	public String getRegion_avg_call_in_time() {
		return region_avg_call_in_time;
	}
	public void setRegion_avg_call_in_time(String region_avg_call_in_time) {
		this.region_avg_call_in_time = region_avg_call_in_time;
	}
	public String getRegion_avg_call_out_time() {
		return region_avg_call_out_time;
	}
	public void setRegion_avg_call_out_time(String region_avg_call_out_time) {
		this.region_avg_call_out_time = region_avg_call_out_time;
	}
	public String getRegion_call_in_cnt_pct() {
		return region_call_in_cnt_pct;
	}
	public void setRegion_call_in_cnt_pct(String region_call_in_cnt_pct) {
		this.region_call_in_cnt_pct = region_call_in_cnt_pct;
	}
	public String getRegion_call_in_time_pct() {
		return region_call_in_time_pct;
	}
	public void setRegion_call_in_time_pct(String region_call_in_time_pct) {
		this.region_call_in_time_pct = region_call_in_time_pct;
	}
	public String getRegion_call_out_time_pct() {
		return region_call_out_time_pct;
	}
	public void setRegion_call_out_time_pct(String region_call_out_time_pct) {
		this.region_call_out_time_pct = region_call_out_time_pct;
	}
	public String getRegion_call_out_cnt_pct() {
		return region_call_out_cnt_pct;
	}
	public void setRegion_call_out_cnt_pct(String region_call_out_cnt_pct) {
		this.region_call_out_cnt_pct = region_call_out_cnt_pct;
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
	
	
}
