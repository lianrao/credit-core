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

//税务信息-案件查处-行政处罚决定书
@Entity
@Table(name = "T_DS_HF_ADMINPUNISHFORM")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer" })
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class AdminPunishForm extends BaseDomain {
	private static final long serialVersionUID = 1L;
	private String id;
	private String trade_id;
	@Expose
	private String name;// 纳税人名称 1000
	@Expose
	private String taxnum;// 纳税人识别号 1000
	@Expose
	private String leader;// 法定代表人（业主） 1000
	@Expose
	private String type;// 证件类别  1000
	@Expose
	private String cidorcode;// 证件号码   1000
	@Expose
	private String unit;// 主管税务机关  1000
	private String situation;// 违法事实CLOB
	@Expose
	private Date time;// 处罚时间
	@Expose
	private String punishmenttype;// 处罚类别 1000
	private String punishmentresult;// 处罚结果CLOB
	@Expose
	private String opentime;// 公告时间1000
	@Expose
	private String address;// 生产经营地址1000
	private String objection;// 异议内容CLOB
	@Expose
	private Date objectiontime;// 异议时间
	private String refId;
	private String queryType;
	
	public AdminPunishForm() {
		super();
	}

	public AdminPunishForm(String name, String taxnum, String leader,
			String type, String cidorcode, String unit, String situation,
			Date time, String punishmenttype, String punishmentresult,
			String opentime, String address, String objection,
			Date objectiontime) {
		super();
		this.name = name;
		this.taxnum = taxnum;
		this.leader = leader;
		this.type = type;
		this.cidorcode = cidorcode;
		this.unit = unit;
		this.situation = situation;
		this.time = time;
		this.punishmenttype = punishmenttype;
		this.punishmentresult = punishmentresult;
		this.opentime = opentime;
		this.address = address;
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

	public String getSituation() {
		return situation;
	}

	public void setSituation(String situation) {
		this.situation = situation;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public String getPunishmenttype() {
		return punishmenttype;
	}

	public void setPunishmenttype(String punishmenttype) {
		this.punishmenttype = punishmenttype;
	}

	public String getPunishmentresult() {
		return punishmentresult;
	}

	public void setPunishmentresult(String punishmentresult) {
		this.punishmentresult = punishmentresult;
	}

	public String getOpentime() {
		return opentime;
	}

	public void setOpentime(String opentime) {
		this.opentime = opentime;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
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
		return "AdminPunishForm [name=" + name + ", taxnum=" + taxnum
				+ ", leader=" + leader + ", type=" + type + ", cidorcode="
				+ cidorcode + ", unit=" + unit + ", situation=" + situation
				+ ", time=" + time + ", punishmenttype=" + punishmenttype
				+ ", punishmentresult=" + punishmentresult + ", opentime="
				+ opentime + ", address=" + address + ", objection="
				+ objection + ", objectiontime=" + objectiontime + ", refId="
				+ refId + "]";
	}

}
