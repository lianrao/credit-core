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
 * 出行消费列表
 * @author xiaobin.hou
 *
 */
@Entity
@Table(name = "CPDB_DS.T_DS_JXL_REP_TRIP_CONSUME")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class TripConsumePojo extends BaseDomain{
	
	private static final long serialVersionUID = 1L;
	
	private String seqId;
	private String requestId;
	private String total_spend;
	private String order_date;
	private String flight_spend;
	private String train_spend;
	private String hotel_spend;
	private String count;
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
	public String getOrder_date() {
		return order_date;
	}
	public void setOrder_date(String order_date) {
		this.order_date = order_date;
	}
	public String getFlight_spend() {
		return flight_spend;
	}
	public void setFlight_spend(String flight_spend) {
		this.flight_spend = flight_spend;
	}
	public String getTrain_spend() {
		return train_spend;
	}
	public void setTrain_spend(String train_spend) {
		this.train_spend = train_spend;
	}
	public String getHotel_spend() {
		return hotel_spend;
	}
	public void setHotel_spend(String hotel_spend) {
		this.hotel_spend = hotel_spend;
	}
	public String getCount() {
		return count;
	}
	public void setCount(String count) {
		this.count = count;
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
	public String getTotal_spend() {
		return total_spend;
	}
	public void setTotal_spend(String total_spend) {
		this.total_spend = total_spend;
	}
}
