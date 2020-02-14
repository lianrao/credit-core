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

//执行信息-执行公开-最高法执行
@Entity
@Table(name = "T_DS_HF_PERFORMPUB")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class PerformPub extends BaseDomain {
	private static final long serialVersionUID = 1L;
	private String id;
	private String trade_id;
	@Expose
	private String name;// 被执行人姓名/名称 1000
	@Expose
	private String cidorcode;// 身份证号码/组织机构代码 100
	@Expose
	private String court;// 执行法院 1000
	@Expose
	private Date time;// 立案时间 date
	@Expose
	private String casenum;// 执行案号1000
	@Expose
	private String money;// 执行标的 1000
	@Expose
	private String statute;// 案件状态 1000
	private String basic;// 执行依据 clob
	@Expose
	private String basiccourt;// 做出执行依据的机构 1000
	private String objection;// 异议内容
	@Expose
	private Date objectiontime;// 异议时间
	private String refId;
	private String queryType;
	
	public PerformPub() {
		super();
	}

	public PerformPub(String name, String cidorcode, String court, Date time,
			String casenum, String money, String statute, String basic,
			String basiccourt, String objection, Date objectiontime) {
		super();
		this.name = name;
		this.cidorcode = cidorcode;
		this.court = court;
		this.time = time;
		this.casenum = casenum;
		this.money = money;
		this.statute = statute;
		this.basic = basic;
		this.basiccourt = basiccourt;
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

	public String getStatute() {
		return statute;
	}

	public void setStatute(String statute) {
		this.statute = statute;
	}

	public String getBasic() {
		return basic;
	}

	public void setBasic(String basic) {
		this.basic = basic;
	}

	public String getBasiccourt() {
		return basiccourt;
	}

	public void setBasiccourt(String basiccourt) {
		this.basiccourt = basiccourt;
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
		return "PerformPub [name=" + name + ", cidorcode=" + cidorcode
				+ ", court=" + court + ", time=" + time + ", casenum="
				+ casenum + ", money=" + money + ", statute=" + statute
				+ ", basic=" + basic + ", basiccourt=" + basiccourt
				+ ", objection=" + objection + ", objectiontime="
				+ objectiontime + ", queryType=" + queryType + "]";
	}
}
