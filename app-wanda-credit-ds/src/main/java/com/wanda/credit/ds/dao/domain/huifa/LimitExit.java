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

//执行信息-执行惩戒-限制出境被执行人
@Entity
@Table(name = "T_DS_HF_LIMITEXIT")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class LimitExit extends BaseDomain {
	private static final long serialVersionUID = 1L;
	private String id;
	private String trade_id;
	@Expose
	private String name;// 被限制人 1000
	@Expose
	private String address;// 被限制人地址 1000
	private String control;// 边控措施 clob
	@Expose
	private String controltime;// 边控日期1000
	@Expose
	private String cidorcode;// 身份证号/护照号100
	@Expose
	private String court;// 承办法院1000
	@Expose
	private String casenum;// 案号1000
	@Expose
	private String basic;// 执行依据编号1000
	@Expose
	private Date time;// 立案时间 date
	@Expose
	private String money;// 执行标的1000
	private String content;// 具体内容 clob
	private String objection;// 异议内容
	@Expose
	private Date objectiontime;// 异议时间
	private String refId;
	private String queryType;
	
	public LimitExit() {
		// TODO Auto-generated constructor stub
	}

	public LimitExit(String name, String address, String control,
			String controltime, String cidorcode, String court, String casenum,
			String basic, Date time, String money, String content,
			String objection, Date objectiontime) {
		super();
		this.name = name;
		this.address = address;
		this.control = control;
		this.controltime = controltime;
		this.cidorcode = cidorcode;
		this.court = court;
		this.casenum = casenum;
		this.basic = basic;
		this.time = time;
		this.money = money;
		this.content = content;
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

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getControl() {
		return control;
	}

	public void setControl(String control) {
		this.control = control;
	}

	public String getControltime() {
		return controltime;
	}

	public void setControltime(String controltime) {
		this.controltime = controltime;
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

	public String getCasenum() {
		return casenum;
	}

	public void setCasenum(String casenum) {
		this.casenum = casenum;
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

	public String getMoney() {
		return money;
	}

	public void setMoney(String money) {
		this.money = money;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
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
		return "LimitExit [name=" + name + ", address=" + address
				+ ", control=" + control + ", controltime=" + controltime
				+ ", cidorcode=" + cidorcode + ", court=" + court
				+ ", casenum=" + casenum + ", basic=" + basic + ", time="
				+ time + ", money=" + money + ", content=" + content
				+ ", objection=" + objection + ", objectiontime="
				+ objectiontime + ", queryType=" + queryType + "]";
	}
}
