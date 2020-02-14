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

//税务信息-案件查处-违法案件
@Entity
@Table(name = "T_DS_HF_ILLEGALCASE")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class IllegalCase extends BaseDomain {
	private static final long serialVersionUID = 1L;
	private String id;
	private String trade_id;
	@Expose
	private String name;// 纳税人名称 1000
	@Expose
	private String taxnum;// 纳税人识别号 1000
	@Expose
	private String leader;// 法定代表人（业主）1000
	@Expose
	private String type;// 证件类别 1000
	@Expose
	private String cidorcode;// 证件号码 100
	@Expose
	private String unit;// 主管税务机关 1000
	@Expose
	private String year;// 检查/稽查年度 1000
	@Expose
	private String num;// 稽查文书编号 1000
	private String fact;// 违法违章事实 clob
	private String means;// 违法违章手段 clob
	@Expose
	private String punishtime;// 处理处罚决定日期 1000
	@Expose
	private String decisiontime;// 处理处罚限定履行日期1000
	@Expose
	private String money;// 罚款金额1000
	@Expose
	private String performtime;// 处罚处理实际履行时间1000
	@Expose
	private String money2;// 实缴税款/入库金额（税、罚、滞合计）1000
	@Expose
	private String money3;// 未缴税款/未入库金额(税、罚、滞合计)1000
	@Expose
	private String statute;// 限改状态1000
	@Expose
	private String statute2;// 纳税人当前状态1000
	@Expose
	private Date time;// 公告时间 date
	private String objection;// 异议内容
	@Expose
	private Date objectiontime;// 异议时间
	private String refId;
	private String queryType;
	
	public IllegalCase() {
		super();
	}

	public IllegalCase(String name, String taxnum, String leader, String type,
			String cidorcode, String unit, String year, String num,
			String fact, String means, String punishtime, String decisiontime,
			String money, String performtime, String money2, String money3,
			String statute, String statute2, Date time, String objection,
			Date objectiontime) {
		super();
		this.name = name;
		this.taxnum = taxnum;
		this.leader = leader;
		this.type = type;
		this.cidorcode = cidorcode;
		this.unit = unit;
		this.year = year;
		this.num = num;
		this.fact = fact;
		this.means = means;
		this.punishtime = punishtime;
		this.decisiontime = decisiontime;
		this.money = money;
		this.performtime = performtime;
		this.money2 = money2;
		this.money3 = money3;
		this.statute = statute;
		this.statute2 = statute2;
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

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}

	public String getFact() {
		return fact;
	}

	public void setFact(String fact) {
		this.fact = fact;
	}

	public String getMeans() {
		return means;
	}

	public void setMeans(String means) {
		this.means = means;
	}

	public String getPunishtime() {
		return punishtime;
	}

	public void setPunishtime(String punishtime) {
		this.punishtime = punishtime;
	}

	public String getDecisiontime() {
		return decisiontime;
	}

	public void setDecisiontime(String decisiontime) {
		this.decisiontime = decisiontime;
	}

	public String getMoney() {
		return money;
	}

	public void setMoney(String money) {
		this.money = money;
	}

	public String getPerformtime() {
		return performtime;
	}

	public void setPerformtime(String performtime) {
		this.performtime = performtime;
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

	public String getStatute() {
		return statute;
	}

	public void setStatute(String statute) {
		this.statute = statute;
	}

	public String getStatute2() {
		return statute2;
	}

	public void setStatute2(String statute2) {
		this.statute2 = statute2;
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
		return "IllegalCase [name=" + name + ", taxnum=" + taxnum + ", leader="
				+ leader + ", type=" + type + ", cidorcode=" + cidorcode
				+ ", unit=" + unit + ", year=" + year + ", num=" + num
				+ ", fact=" + fact + ", means=" + means + ", punishtime="
				+ punishtime + ", decisiontime=" + decisiontime + ", money="
				+ money + ", performtime=" + performtime + ", money2=" + money2
				+ ", money3=" + money3 + ", statute=" + statute + ", statute2="
				+ statute2 + ", time=" + time + ", objection=" + objection
				+ ", objectiontime=" + objectiontime + ", queryType="
				+ queryType + "]";
	}
}
