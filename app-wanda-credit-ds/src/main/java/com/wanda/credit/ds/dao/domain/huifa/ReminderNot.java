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

//催收索赔-催欠公告
@Entity
@Table(name = "T_DS_HF_REMINDERNOT")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ReminderNot extends BaseDomain {
	private static final long serialVersionUID = 1L;
	private String id;
	private String trade_id;
	@Expose
	private String name;// 被催欠人名称/姓名 1000
	@Expose
	private String cidorcode;// 身份证/住址机构代码证 100
	@Expose
	private String leader;// 被催欠单位法定代表人 1000
	@Expose
	private String phone;// 被催欠人电话号码1000
	@Expose
	private String email;// 电子邮箱1000
	@Expose
	private String address;// 详细地址1000
	@Expose
	private String money;// 催欠金额1000
	@Expose
	private String statute;// 催欠状态1000
	@Expose
	private String letterhref;// 律师函1000
	@Expose
	private String type;// 催欠人身份1000
	@Expose
	private Date time;// 催欠时间 date
	private String objection;// 异议内容
	@Expose
	private Date objectiontime;// 异议时间
	private String refId;
	private String queryType;
	
	public ReminderNot() {
		super();
	}

	public ReminderNot(String name, String cidorcode, String leader,
			String phone, String email, String address, String money,
			String statute, String letterhref, String type, Date time,
			String objection, Date objectiontime) {
		super();
		this.name = name;
		this.cidorcode = cidorcode;
		this.leader = leader;
		this.phone = phone;
		this.email = email;
		this.address = address;
		this.money = money;
		this.statute = statute;
		this.letterhref = letterhref;
		this.type = type;
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

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
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

	public String getLetterhref() {
		return letterhref;
	}

	public void setLetterhref(String letterhref) {
		this.letterhref = letterhref;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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
		return "ReminderNot [name=" + name + ", cidorcode=" + cidorcode
				+ ", leader=" + leader + ", phone=" + phone + ", email="
				+ email + ", address=" + address + ", money=" + money
				+ ", statute=" + statute + ", letterhref=" + letterhref
				+ ", type=" + type + ", time=" + time + ", objection="
				+ objection + ", objectiontime=" + objectiontime
				+ ", queryType=" + queryType + "]";
	}
}
