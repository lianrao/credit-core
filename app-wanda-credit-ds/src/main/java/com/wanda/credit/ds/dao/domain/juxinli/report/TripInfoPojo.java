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
 * 出行数据
 * @author xiaobin.hou
 *
 */
@Entity
@Table(name = "CPDB_DS.T_DS_JXL_REP_TRIP_INFO")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class TripInfoPojo extends BaseDomain{
	
	private static final long serialVersionUID = 1L;
	
	private String seqId;
	private String requestId;
	private String trip_leave;
	private String trip_dest;
	private String trip_start_time;
	private String trip_end_time;
	private String trip_type;
	private String trip_transportation;
	private String trip_person;
	private String trip_datasource;
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
	public String getTrip_leave() {
		return trip_leave;
	}
	public void setTrip_leave(String trip_leave) {
		this.trip_leave = trip_leave;
	}
	public String getTrip_dest() {
		return trip_dest;
	}
	public void setTrip_dest(String trip_dest) {
		this.trip_dest = trip_dest;
	}
	public String getTrip_start_time() {
		return trip_start_time;
	}
	public void setTrip_start_time(String trip_start_time) {
		this.trip_start_time = trip_start_time;
	}
	public String getTrip_end_time() {
		return trip_end_time;
	}
	public void setTrip_end_time(String trip_end_time) {
		this.trip_end_time = trip_end_time;
	}
	public String getTrip_type() {
		return trip_type;
	}
	public void setTrip_type(String trip_type) {
		this.trip_type = trip_type;
	}
	public String getTrip_transportation() {
		return trip_transportation;
	}
	public void setTrip_transportation(String trip_transportation) {
		this.trip_transportation = trip_transportation;
	}
	public String getTrip_person() {
		return trip_person;
	}
	public void setTrip_person(String trip_person) {
		this.trip_person = trip_person;
	}
	public String getTrip_datasource() {
		return trip_datasource;
	}
	public void setTrip_datasource(String trip_datasource) {
		this.trip_datasource = trip_datasource;
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
