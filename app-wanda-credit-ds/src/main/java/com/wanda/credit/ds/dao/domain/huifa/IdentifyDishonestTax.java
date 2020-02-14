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

//税务信息-认定认证-失信纳税人
@Entity
@Table(name = "T_DS_HF_IDENTIFYDISHONESTTAX")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class IdentifyDishonestTax extends BaseDomain {
	private static final long serialVersionUID = 1L;
	private String id;
	private String trade_id;
	@Expose
	private String name;// 纳税人名称 1000
	@Expose
	private String taxnum;// 纳税人识别号1000
	@Expose
	private String leader;// 法定代表人（业主）1000
	@Expose
	private String type;// 证件类别1000
	@Expose
	private String cidorcode;// 证件号码 100
	@Expose
	private String unit;// 主管税务机关1000
	@Expose
	private String isd;// 是否评定为D级1000
	@Expose
	private Date time;// 评定时间 DATE
	private String objection;// 异议内容
	@Expose
	private Date objectiontime;// 异议时间
	private String refId;
	private String queryType;
	
	public IdentifyDishonestTax() {
		super();
	}

	public IdentifyDishonestTax(String name, String taxnum, String leader,
			String type, String cidorcode, String unit, String isd, Date time,
			String objection, Date objectiontime) {
		super();
		this.name = name;
		this.taxnum = taxnum;
		this.leader = leader;
		this.type = type;
		this.cidorcode = cidorcode;
		this.unit = unit;
		this.isd = isd;
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

	public String getIsd() {
		return isd;
	}

	public void setIsd(String isd) {
		this.isd = isd;
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
		return "IdentifyDishonestTax [name=" + name + ", taxnum=" + taxnum
				+ ", leader=" + leader + ", type=" + type + ", cidorcode="
				+ cidorcode + ", unit=" + unit + ", isd=" + isd + ", time="
				+ time + ", objection=" + objection + ", objectiontime="
				+ objectiontime + ", queryType=" + queryType + "]";
	}
}
