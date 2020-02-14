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

//税务信息-税务监管-逾期信息
@Entity
@Table(name = "T_DS_HF_OVERDUEINFO")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class OverdueInfo extends BaseDomain {
	private static final long serialVersionUID = 1L;
	private String id;
	private String trade_id;
	@Expose
	private String name;// 纳税人名称 1000
	@Expose
	private String taxnum;// 纳税人识别号 1000
	@Expose
	private String code;// 海关代码 1000
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
	private String timelimit;// 申报期限 1000
	@Expose
	private String project;// 未申报项目 1000
	@Expose
	private String taxtype;// 未申报税种 1000
	@Expose
	private String money;// 欠缴税额 1000
	@Expose
	private String money2;// 处罚金额 1000
	@Expose
	private Date time;// 处罚时间 DATE
	private String objection;// 异议内容
	@Expose
	private Date objectiontime;// 异议时间
	private String refId;
	private String queryType;
	
	public OverdueInfo() {
		super();
	}

	public OverdueInfo(String name, String taxnum, String code, String address,
			String leader, String type, String cidorcode, String unit,
			String timelimit, String project, String taxtype, String money,
			String money2, Date time, String objection, Date objectiontime) {
		super();
		this.name = name;
		this.taxnum = taxnum;
		this.code = code;
		this.address = address;
		this.leader = leader;
		this.type = type;
		this.cidorcode = cidorcode;
		this.unit = unit;
		this.timelimit = timelimit;
		this.project = project;
		this.taxtype = taxtype;
		this.money = money;
		this.money2 = money2;
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

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
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

	public String getTimelimit() {
		return timelimit;
	}

	public void setTimelimit(String timelimit) {
		this.timelimit = timelimit;
	}

	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}

	public String getTaxtype() {
		return taxtype;
	}

	public void setTaxtype(String taxtype) {
		this.taxtype = taxtype;
	}

	public String getMoney() {
		return money;
	}

	public void setMoney(String money) {
		this.money = money;
	}

	public String getMoney2() {
		return money2;
	}

	public void setMoney2(String money2) {
		this.money2 = money2;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
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
		return "OverdueInfo [name=" + name + ", taxnum=" + taxnum + ", code="
				+ code + ", address=" + address + ", leader=" + leader
				+ ", type=" + type + ", cidorcode=" + cidorcode + ", unit="
				+ unit + ", timelimit=" + timelimit + ", project=" + project
				+ ", taxtype=" + taxtype + ", money=" + money + ", money2="
				+ money2 + ", time=" + time + ", objection=" + objection
				+ ", objectiontime=" + objectiontime + ", queryType="
				+ queryType + "]";
	}
}
