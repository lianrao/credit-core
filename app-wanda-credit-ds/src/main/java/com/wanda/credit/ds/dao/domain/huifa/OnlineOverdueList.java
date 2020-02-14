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

//催收索赔-网贷逾期名单
@Entity
@Table(name = "T_DS_HF_ONLINEOVERDUELIST")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class OnlineOverdueList extends BaseDomain {
	private static final long serialVersionUID = 1L;
	private String id;
	private String trade_id;
	@Expose
	private String lender;// 出借方 1000
	@Expose
	private String name;// 借款人名称1000
	@Expose
	private String leader;// 法定代表人（负责人）1000
	@Expose
	private String cidorcode;// 身份证/组织机构代码证100
	@Expose
	private String sex;// 性别1000
	@Expose
	private String address;// 居住地址1000
	@Expose
	private String phone;// 电话号码1000
	@Expose
	private String networkname;// 网络昵称1000
	@Expose
	private String qq;// QQ号码1000
	@Expose
	private String email;// 电子邮箱1000
	@Expose
	private String taobao;// 淘宝账户1000
	@Expose
	private Date time;// 借款时间 date
	@Expose
	private String money;// 借入本金1000
	@Expose
	private String time2;// 借款到期时间1000
	@Expose
	private String totalmoney;// 欠款本息总额1000
	@Expose
	private String time3;// 首次逾期日期1000
	@Expose
	private String days;// 最长逾期天数1000
	@Expose
	private String time4;// 信息更新时间1000
	private String description;// 描述 clob
	@Expose
	private String workunit;// 工作单位 1000
	@Expose
	private String idaddress;// 身份证地址1000
	@Expose
	private String workunitaddress;// 单位地址 1000
	@Expose
	private String workunitphone;// 单位电话1000
	@Expose
	private String other;// 共同借款人1000
	@Expose
	private String othercidorcode;// 共同借款人身份证号1000
	@Expose
	private String otherphone;// 共同借款人电话1000
	private String objection;// 异议内容
	@Expose
	private Date objectiontime;// 异议时间
	private String refId;
	private String queryType;
	
	public OnlineOverdueList() {
		super();
	}

	public OnlineOverdueList(String lender, String name, String leader,
			String cidorcode, String sex, String address, String phone,
			String networkname, String qq, String email, String taobao,
			Date time, String money, String time2, String totalmoney,
			String time3, String days, String time4, String description,
			String workunit, String idaddress, String workunitaddress,
			String workunitphone, String other, String othercidorcode,
			String otherphone, String objection, Date objectiontime) {
		super();
		this.lender = lender;
		this.name = name;
		this.leader = leader;
		this.cidorcode = cidorcode;
		this.sex = sex;
		this.address = address;
		this.phone = phone;
		this.networkname = networkname;
		this.qq = qq;
		this.email = email;
		this.taobao = taobao;
		this.time = time;
		this.money = money;
		this.time2 = time2;
		this.totalmoney = totalmoney;
		this.time3 = time3;
		this.days = days;
		this.time4 = time4;
		this.description = description;
		this.workunit = workunit;
		this.idaddress = idaddress;
		this.workunitaddress = workunitaddress;
		this.workunitphone = workunitphone;
		this.other = other;
		this.othercidorcode = othercidorcode;
		this.otherphone = otherphone;
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

	public String getLender() {
		return lender;
	}

	public void setLender(String lender) {
		this.lender = lender;
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

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getNetworkname() {
		return networkname;
	}

	public void setNetworkname(String networkname) {
		this.networkname = networkname;
	}

	public String getQq() {
		return qq;
	}

	public void setQq(String qq) {
		this.qq = qq;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTaobao() {
		return taobao;
	}

	public void setTaobao(String taobao) {
		this.taobao = taobao;
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

	public String getTime2() {
		return time2;
	}

	public void setTime2(String time2) {
		this.time2 = time2;
	}

	public String getTotalmoney() {
		return totalmoney;
	}

	public void setTotalmoney(String totalmoney) {
		this.totalmoney = totalmoney;
	}

	public String getTime3() {
		return time3;
	}

	public void setTime3(String time3) {
		this.time3 = time3;
	}

	public String getDays() {
		return days;
	}

	public void setDays(String days) {
		this.days = days;
	}

	public String getTime4() {
		return time4;
	}

	public void setTime4(String time4) {
		this.time4 = time4;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getWorkunit() {
		return workunit;
	}

	public void setWorkunit(String workunit) {
		this.workunit = workunit;
	}

	public String getIdaddress() {
		return idaddress;
	}

	public void setIdaddress(String idaddress) {
		this.idaddress = idaddress;
	}

	public String getWorkunitaddress() {
		return workunitaddress;
	}

	public void setWorkunitaddress(String workunitaddress) {
		this.workunitaddress = workunitaddress;
	}

	public String getWorkunitphone() {
		return workunitphone;
	}

	public void setWorkunitphone(String workunitphone) {
		this.workunitphone = workunitphone;
	}

	public String getOther() {
		return other;
	}

	public void setOther(String other) {
		this.other = other;
	}

	public String getOthercidorcode() {
		return othercidorcode;
	}

	public void setOthercidorcode(String othercidorcode) {
		this.othercidorcode = othercidorcode;
	}

	public String getOtherphone() {
		return otherphone;
	}

	public void setOtherphone(String otherphone) {
		this.otherphone = otherphone;
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
		return "OnlineOverdueList [lender=" + lender + ", name=" + name
				+ ", leader=" + leader + ", cidorcode=" + cidorcode + ", sex="
				+ sex + ", address=" + address + ", phone=" + phone
				+ ", networkname=" + networkname + ", qq=" + qq + ", email="
				+ email + ", taobao=" + taobao + ", time=" + time + ", money="
				+ money + ", time2=" + time2 + ", totalmoney=" + totalmoney
				+ ", time3=" + time3 + ", days=" + days + ", time4=" + time4
				+ ", description=" + description + ", workunit=" + workunit
				+ ", idaddress=" + idaddress + ", workunitaddress="
				+ workunitaddress + ", workunitphone=" + workunitphone
				+ ", other=" + other + ", othercidorcode=" + othercidorcode
				+ ", otherphone=" + otherphone + ", objection=" + objection
				+ ", objectiontime=" + objectiontime + ", queryType="
				+ queryType + "]";
	}

}
