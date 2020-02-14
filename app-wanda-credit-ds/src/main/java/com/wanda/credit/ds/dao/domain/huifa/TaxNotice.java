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

//税务信息-税务监管-欠税公告
@Entity
@Table(name = "T_DS_HF_TAXNOTICE")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class TaxNotice extends BaseDomain {
	private static final long serialVersionUID = 1L;
	private String id;
	private String trade_id;
	@Expose
	private String name;// 纳税人名称 1000
	@Expose
	private String taxnum;// 纳税人识别号 1000
	@Expose
	private String address;// 经营地点 1000
	@Expose
	private String leader;// 法定代表人（业主） 1000
	@Expose
	private String type;// 证件类别 1000
	@Expose
	private String cidorcode;// 证件号码 100
	@Expose
	private String taxtype;// 所欠税种 1000
	@Expose
	private String money;// 期初陈欠 1000
	@Expose
	private String money2;// 当期发生欠税余额（元） 1000
	@Expose
	private String money3;// 欠税余额 1000
	@Expose
	private String time;// 应征发生日期 1000
	@Expose
	private Date time2;// 认定日期 date
	@Expose
	private String unit;// 主管税务机关 1000
	@Expose
	private String leadership;// 分管领导1000
	@Expose
	private String taxpeople;// 主管税务人员1000
	@Expose
	private String region;// 所属市县区1000
	@Expose
	private String casenum;// 认定字号1000
	@Expose
	private String period;// 公告期次1000
	private String objection;// 异议内容
	@Expose
	private Date objectiontime;// 异议时间
	private String refId;
	private String queryType;
	
	public TaxNotice() {
		super();
	}
	public TaxNotice(String name, String taxnum, String address, String leader,
			String type, String cidorcode, String taxtype, String money,
			String money2, String money3, String time, Date time2, String unit,
			String leadership, String taxpeople, String region, String casenum,
			String period, String objection, Date objectiontime) {
		super();
		this.name = name;
		this.taxnum = taxnum;
		this.address = address;
		this.leader = leader;
		this.type = type;
		this.cidorcode = cidorcode;
		this.taxtype = taxtype;
		this.money = money;
		this.money2 = money2;
		this.money3 = money3;
		this.time = time;
		this.time2 = time2;
		this.unit = unit;
		this.leadership = leadership;
		this.taxpeople = taxpeople;
		this.region = region;
		this.casenum = casenum;
		this.period = period;
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
	public String getMoney3() {
		return money3;
	}
	public void setMoney3(String money3) {
		this.money3 = money3;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public Date getTime2() {
		return time2;
	}
	public void setTime2(Date time2) {
		this.time2 = time2;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public String getLeadership() {
		return leadership;
	}
	public void setLeadership(String leadership) {
		this.leadership = leadership;
	}
	public String getTaxpeople() {
		return taxpeople;
	}
	public void setTaxpeople(String taxpeople) {
		this.taxpeople = taxpeople;
	}
	public String getRegion() {
		return region;
	}
	public void setRegion(String region) {
		this.region = region;
	}
	public String getCasenum() {
		return casenum;
	}
	public void setCasenum(String casenum) {
		this.casenum = casenum;
	}
	public String getPeriod() {
		return period;
	}
	public void setPeriod(String period) {
		this.period = period;
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
		return "TaxNotice [name=" + name + ", taxnum=" + taxnum + ", address="
				+ address + ", leader=" + leader + ", type=" + type
				+ ", cidorcode=" + cidorcode + ", taxtype=" + taxtype
				+ ", money=" + money + ", money2=" + money2 + ", money3="
				+ money3 + ", time=" + time + ", time2=" + time2 + ", unit="
				+ unit + ", leadership=" + leadership + ", taxpeople="
				+ taxpeople + ", region=" + region + ", casenum=" + casenum
				+ ", period=" + period + ", objection=" + objection
				+ ", objectiontime=" + objectiontime + ", queryType="
				+ queryType + "]";
	}
	
}
