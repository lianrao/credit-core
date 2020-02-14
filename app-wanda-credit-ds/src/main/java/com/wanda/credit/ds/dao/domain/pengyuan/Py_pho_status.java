package com.wanda.credit.ds.dao.domain.pengyuan;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.annotations.GenericGenerator;

import com.wanda.credit.base.domain.BaseDomain;
@Entity
@Table(name = "CPDB_DS.T_DS_PY_PHO_STATUS")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Py_pho_status extends BaseDomain{
	private static final long serialVersionUID = 1L;
	private String id;
	private String trade_id;
    private String areaInfo;
    private String phoneStatus;
    private String timeLength;
    private String cancelTime;
    private String operator;
	public Py_pho_status() {
		super();
	}
	public Py_pho_status(String areaInfo, String phoneStatus,
			String timeLength, String cancelTime, String operator) {
		super();
		this.areaInfo = areaInfo;
		this.phoneStatus = phoneStatus;
		this.timeLength = timeLength;
		this.cancelTime = cancelTime;
		this.operator = operator;
	}
	/**
	 * 获取 主键
	 */
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	@Column(name = "ID", unique = true, nullable = false, length = 32)
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTrade_id() {
		return trade_id;
	}
	public void setTrade_id(String trade_id) {
		this.trade_id = trade_id;
	}
	@Column(name="AREA_INFO")
	public String getAreaInfo() {
		return areaInfo;
	}
	public void setAreaInfo(String areaInfo) {
		this.areaInfo = areaInfo;
	}
	@Column(name="PHONE_STATUS")
	public String getPhoneStatus() {
		return phoneStatus;
	}
	public void setPhoneStatus(String phoneStatus) {
		this.phoneStatus = phoneStatus;
	}
	@Column(name="TIME_LENGTH") 
	public String getTimeLength() {
		return timeLength;
	}
	public void setTimeLength(String timeLength) {
		this.timeLength = timeLength;
	}
	@Column(name="CANCEL_TIME") 
	public String getCancelTime() {
		return cancelTime;
	}
	public void setCancelTime(String cancelTime) {
		this.cancelTime = cancelTime;
	}
	@Column(name="OPERATOR") 
	public String getOperator() {
		return operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}
	@Override
	public String toString() {
		return "Py_pho_status [areaInfo=" + areaInfo + ", phoneStatus="
				+ phoneStatus + ", timeLength=" + timeLength + ", cancelTime="
				+ cancelTime + ", operator=" + operator + "]";
	}
}
