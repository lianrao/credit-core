package com.wanda.credit.ds.dao.domain.huifa;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.annotations.GenericGenerator;

import com.google.gson.annotations.Expose;
import com.wanda.credit.base.domain.BaseDomain;

//税务信息-税务登记-注销信息
@Entity
@Table(name = "T_DS_HF_CANCELINFO")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer" })
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class CancelInfo extends BaseDomain {
	private static final long serialVersionUID = 1L;
	private String id;
	private String trade_id;
	@Expose
	private String name;// 失踪纳税人名称 1000
	@Expose
	private String taxnum;// 纳税人识别号 1000
	@Expose
	private String peopletype;// 纳税户类型 1000
	@Expose
	private String address;// 经营地址 1000
	@Expose
	private String leader;// 法定代表人（业主） 1000
	@Expose
	private String type;// 证件类别 1000
	@Expose
	private String cidorcode;// 证件号码 100
	@Expose
	private String unit;// 主管税务机关 1000
	@Expose
	private Date cancletime;// 注销日期  date
	@Expose
	private String cancletype;// 注销类型 1000
	private String canclereason;// 注销原因 CLOB
	@Expose
	private String time;// 公告时间  1000
	private String objection;// 异议内容
	@Expose
	private Date objectiontime;// 异议时间
	private String refId;
	private String queryType;
	
	public CancelInfo() {
		super();
	}

	public CancelInfo(String name, String taxnum, String peopletype,
			String address, String leader, String type, String cidorcode,
			String unit, Date cancletime, String cancletype,
			String canclereason, String time, String objection,
			Date objectiontime) {
		super();
		this.name = name;
		this.taxnum = taxnum;
		this.peopletype = peopletype;
		this.address = address;
		this.leader = leader;
		this.type = type;
		this.cidorcode = cidorcode;
		this.unit = unit;
		this.cancletime = cancletime;
		this.cancletype = cancletype;
		this.canclereason = canclereason;
		this.time = time;
		this.objection = objection;
		this.objectiontime = objectiontime;
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

	public String getRefId() {
		return refId;
	}
	
	public void setRefId(String refId) {
		this.refId = refId;
	}

	public String getQueryType() {
		return queryType;
	}

	public void setQueryType(String queryType) {
		this.queryType = queryType;
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

	public String getTaxnum() {
		return taxnum;
	}

	public void setTaxnum(String taxnum) {
		this.taxnum = taxnum;
	}

	public String getPeopletype() {
		return peopletype;
	}

	public void setPeopletype(String peopletype) {
		this.peopletype = peopletype;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getLeader() {
		return leader;
	}

	public void setLeader(String leader) {
		this.leader = leader;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCidorcode() {
		return cidorcode;
	}

	public void setCidorcode(String cidorcode) {
		this.cidorcode = cidorcode;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public Date getCancletime() {
		return cancletime;
	}

	public void setCancletime(Date cancletime) {
		this.cancletime = cancletime;
	}

	public String getCancletype() {
		return cancletype;
	}

	public void setCancletype(String cancletype) {
		this.cancletype = cancletype;
	}

	public String getCanclereason() {
		return canclereason;
	}

	public void setCanclereason(String canclereason) {
		this.canclereason = canclereason;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getObjection() {
		return objection;
	}

	public void setObjection(String objection) {
		this.objection = objection;
	}

	public Date getObjectiontime() {
		return objectiontime;
	}

	public void setObjectiontime(Date objectiontime) {
		this.objectiontime = objectiontime;
	}

	@Override
	public String toString() {
		return "CancelInfo [name=" + name + ", taxnum=" + taxnum
				+ ", peopletype=" + peopletype + ", address=" + address
				+ ", leader=" + leader + ", type=" + type + ", cidorcode="
				+ cidorcode + ", unit=" + unit + ", cancletime=" + cancletime
				+ ", cancletype=" + cancletype + ", canclereason="
				+ canclereason + ", time=" + time + ", objection=" + objection
				+ ", objectiontime=" + objectiontime + ", queryType="
				+ queryType + "]";
	}

}
