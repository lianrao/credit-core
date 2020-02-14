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

//行政执法-责令限期改正
@Entity
@Table(name = "T_DS_HF_ADMINLAW")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer" })
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class AdminLaw extends BaseDomain {
	private static final long serialVersionUID = 1L;
	private String id;
	private String trade_id;
	@Expose
	private String name;// 纳税人名称
	@Expose
	private String taxnum;// 纳税人识别号
	@Expose
	private String address;// 生产经营地址
	@Expose
	private String leader;// 法定代表人（业主）
	@Expose
	private String type;// 证件类别
	@Expose
	private String cidorcode;// 证件号码
	@Expose
	private String unit;// 主管税务机关
	@Expose
	private String num;// 责令限改通知书号
	@Expose
	private String statute;// 责令限改状态
	@Expose
	private Date time;// 公布日期
	private String objection;// 异议内容
	@Expose
	private Date objectiontime;// 异议时间
	private String refId;
	private String queryType;

	public AdminLaw() {
		super();
	}

	public AdminLaw(String name, String taxnum, String address, String leader,
			String type, String cidorcode, String unit, String num,
			String statute, Date time, String objection, Date objectiontime) {
		super();
		this.name = name;
		this.taxnum = taxnum;
		this.address = address;
		this.leader = leader;
		this.type = type;
		this.cidorcode = cidorcode;
		this.unit = unit;
		this.num = num;
		this.statute = statute;
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

	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}

	public String getStatute() {
		return statute;
	}

	public void setStatute(String statute) {
		this.statute = statute;
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
		return "AdminLaw [name=" + name + ", taxnum=" + taxnum + ", address="
				+ address + ", leader=" + leader + ", type=" + type
				+ ", cidorcode=" + cidorcode + ", unit=" + unit + ", num="
				+ num + ", statute=" + statute + ", time=" + time
				+ ", objection=" + objection + ", objectiontime="
				+ objectiontime + ", queryType=" + queryType + "]";
	}
}
