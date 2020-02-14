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

//执行信息-执行公开-其他执行信息
@Entity
@Table(name = "T_DS_HF_EXECUTEOTHERINFO")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ExecuteOtherInfo extends BaseDomain {
	private static final long serialVersionUID = 1L;
	private String id;
	private String trade_id;
	@Expose
	private String applyname;// 执行申请人 1000
	@Expose
	private String name;// 被执行人1000
	@Expose
	private String leader;// 法定代表人/负责人1000
	@Expose
	private String cidorcode;// 身份证号/组织机构代码证号 100
	@Expose
	private String applyname2;// 异议申请人 1000
	@Expose
	private String court;// 执行法院 1000
	@Expose
	private String casenum;// 执行案号 1000
	@Expose
	private String money;// 执行标的 1000
	@Expose
	private String money2;// 履行标的（万元） 1000
	@Expose
	private String basic;// 执行依据文号 1000
	@Expose
	private Date time;// 立案时间 date
	@Expose
	private String statute;// 执行状态 1000
	@Expose
	private String unit;// 执行依据制作单位 1000
	@Expose
	private String opentime;// 公开日期 1000
	private String obligation;// 生效文书确定的义务 clob
	@Expose
	private String address;// 住所地 1000
	private String objection;// 异议内容
	@Expose
	private Date objectiontime;// 异议时间
	private String refId;
	private String queryType;
	
	public ExecuteOtherInfo() {
		super();
	}

	public ExecuteOtherInfo(String applyname, String name, String leader,
			String cidorcode, String applyname2, String court, String casenum,
			String money, String money2, String basic, Date time,
			String statute, String unit, String opentime, String obligation,
			String address, String objection, Date objectiontime) {
		super();
		this.applyname = applyname;
		this.name = name;
		this.leader = leader;
		this.cidorcode = cidorcode;
		this.applyname2 = applyname2;
		this.court = court;
		this.casenum = casenum;
		this.money = money;
		this.money2 = money2;
		this.basic = basic;
		this.time = time;
		this.statute = statute;
		this.unit = unit;
		this.opentime = opentime;
		this.obligation = obligation;
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

	public String getApplyname() {
		return applyname;
	}

	public void setApplyname(String applyname) {
		this.applyname = applyname;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLeader() {
		return leader;
	}

	public void setLeader(String leader) {
		this.leader = leader;
	}

	public String getCidorcode() {
		return cidorcode;
	}

	public void setCidorcode(String cidorcode) {
		this.cidorcode = cidorcode;
	}

	public String getApplyname2() {
		return applyname2;
	}

	public void setApplyname2(String applyname2) {
		this.applyname2 = applyname2;
	}

	public String getCourt() {
		return court;
	}

	public void setCourt(String court) {
		this.court = court;
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

	public String getMoney2() {
		return money2;
	}

	public void setMoney2(String money2) {
		this.money2 = money2;
	}

	public String getBasic() {
		return basic;
	}

	public void setBasic(String basic) {
		this.basic = basic;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public String getStatute() {
		return statute;
	}

	public void setStatute(String statute) {
		this.statute = statute;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getOpentime() {
		return opentime;
	}

	public void setOpentime(String opentime) {
		this.opentime = opentime;
	}

	public String getObligation() {
		return obligation;
	}

	public void setObligation(String obligation) {
		this.obligation = obligation;
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
		return "ExecuteOtherInfo [applyname=" + applyname + ", name=" + name
				+ ", leader=" + leader + ", cidorcode=" + cidorcode
				+ ", applyname2=" + applyname2 + ", court=" + court
				+ ", casenum=" + casenum + ", money=" + money + ", money2="
				+ money2 + ", basic=" + basic + ", time=" + time + ", statute="
				+ statute + ", unit=" + unit + ", opentime=" + opentime
				+ ", obligation=" + obligation + ", address=" + address
				+ ", objection=" + objection + ", objectiontime="
				+ objectiontime + ", queryType=" + queryType + "]";
	}
}
