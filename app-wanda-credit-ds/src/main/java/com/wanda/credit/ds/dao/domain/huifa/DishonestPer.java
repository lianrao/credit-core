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

//执行信息-失信老赖-失信被执行人
@Entity
@Table(name = "T_DS_HF_DISHONESTPER")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class DishonestPer extends BaseDomain {
	private static final long serialVersionUID = 1L;
	private String id;
	private String trade_id;
	@Expose
	private String name;// 被执行人名称/姓名 1000
	@Expose
	private String cidorcode;// 身份证号码/组织机构代码 100
	@Expose
	private String leader;// 法定代表人/负责人 1000
	@Expose
	private String address;// 住所地 1000
	@Expose
	private String court;// 执行法院 1000
	@Expose
	private Date time;// 立案时间 date
	@Expose
	private String casenum;// 执行案号 1000
	@Expose
	private String money;// 执行标的1000
	@Expose
	private String base;// 执行依据文号1000
	@Expose
	private String basecompany;// 做出执行依据单位1000
	@Expose
	private String obligation;// 生效法律文书确定的义务 CLOB
	@Expose
	private String lasttime;// 生效法律文书确定的最后履行义务截止时间1000
	@Expose
	private String performance;// 被执行人的履行情况  CLOB
	@Expose
	private String concretesituation;// 失信被执行人行为具体情形  CLOB
	@Expose
	private String breaktime;// 认定失信时间 1000
	@Expose
	private String posttime;// 发布时间1000
	@Expose
	private String objection;// 异议内容
	@Expose
	private Date objectiontime;// 异议时间
	private String refId;
	private String queryType;

	public DishonestPer() {
		super();
	}

	public DishonestPer(String name, String cidorcode, String leader,
			String address, String court, Date time, String casenum,
			String money, String base, String basecompany, String obligation,
			String lasttime, String performance, String concretesituation,
			String breaktime, String posttime, String objection,
			Date objectiontime) {
		super();
		this.name = name;
		this.cidorcode = cidorcode;
		this.leader = leader;
		this.address = address;
		this.court = court;
		this.time = time;
		this.casenum = casenum;
		this.money = money;
		this.base = base;
		this.basecompany = basecompany;
		this.obligation = obligation;
		this.lasttime = lasttime;
		this.performance = performance;
		this.concretesituation = concretesituation;
		this.breaktime = breaktime;
		this.posttime = posttime;
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

	public String getCidorcode() {
		return cidorcode;
	}

	public void setCidorcode(String cidorcode) {
		this.cidorcode = cidorcode;
	}

	public String getLeader() {
		return leader;
	}

	public void setLeader(String leader) {
		this.leader = leader;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCourt() {
		return court;
	}

	public void setCourt(String court) {
		this.court = court;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public String getCasenum() {
		return casenum;
	}

	public void setCasenum(String casenum) {
		this.casenum = casenum;
	}

	public String getMoney() {
		return money;
	}

	public void setMoney(String money) {
		this.money = money;
	}

	public String getBase() {
		return base;
	}

	public void setBase(String base) {
		this.base = base;
	}

	public String getBasecompany() {
		return basecompany;
	}

	public void setBasecompany(String basecompany) {
		this.basecompany = basecompany;
	}

	public String getObligation() {
		return obligation;
	}

	public void setObligation(String obligation) {
		this.obligation = obligation;
	}

	public String getLasttime() {
		return lasttime;
	}

	public void setLasttime(String lasttime) {
		this.lasttime = lasttime;
	}

	public String getPerformance() {
		return performance;
	}

	public void setPerformance(String performance) {
		this.performance = performance;
	}

	public String getConcretesituation() {
		return concretesituation;
	}

	public void setConcretesituation(String concretesituation) {
		this.concretesituation = concretesituation;
	}

	public String getBreaktime() {
		return breaktime;
	}

	public void setBreaktime(String breaktime) {
		this.breaktime = breaktime;
	}

	public String getPosttime() {
		return posttime;
	}

	public void setPosttime(String posttime) {
		this.posttime = posttime;
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
		return "DishonestPer [name=" + name + ", cidorcode=" + cidorcode
				+ ", leader=" + leader + ", address=" + address + ", court="
				+ court + ", time=" + time + ", casenum=" + casenum
				+ ", money=" + money + ", base=" + base + ", basecompany="
				+ basecompany + ", obligation=" + obligation + ", lasttime="
				+ lasttime + ", performance=" + performance
				+ ", concretesituation=" + concretesituation + ", breaktime="
				+ breaktime + ", posttime=" + posttime + ", objection="
				+ objection + ", objectiontime=" + objectiontime
				+ ", queryType=" + queryType + "]";
	}

}
