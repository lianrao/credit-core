package com.wanda.credit.ds.dao.domain.xiaohe;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.annotations.GenericGenerator;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.wanda.credit.base.domain.BaseDomain;

@Entity
@Table(name = "T_DS_JZ_BUSINESSDETAIL")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer" })
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@XStreamAlias("bizdetail")
public class PSG_BusinessDetail extends BaseDomain {// 建表
	private static final long serialVersionUID = -1289807774918590425L;
	private String id;
	private String type;// 描述
	private int iout;// 出差次数
	private int iflight;// 乘机次数
	private int iCity;// 飞行城市
	private int iavgdiscount;// 平均折扣
	private double FCrate;// 两舱比例
	private double avgoverrate;// 溢价折扣
	private double iRange;// 活动范围
	private PSG_Businessreport bizreport;

	public PSG_BusinessDetail() {
		super();
	}

	public PSG_BusinessDetail(String id, String type, int iout, int iflight, int iCity, int iavgdiscount,
			double FCrate, double avgoverrate, double iRange) {
		super();
		this.id = id;
		this.type = type;
		this.iout = iout;
		this.iflight = iflight;
		this.iCity = iCity;
		this.iavgdiscount = iavgdiscount;
		this.FCrate = FCrate;
		this.iavgdiscount = iavgdiscount;
		this.iavgdiscount = iavgdiscount;
		this.iavgdiscount = iavgdiscount;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
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

	@Column(name = "TYPE")
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Column(name = "IOUT")
	public int getIout() {
		return iout;
	}

	public void setIout(int iout) {
		this.iout = iout;
	}

	@Column(name = "IFLIGHT")
	public int getIflight() {
		return iflight;
	}

	public void setIflight(int iflight) {
		this.iflight = iflight;
	}

	@Column(name = "ICITY")
	public int getiCity() {
		return iCity;
	}

	public void setiCity(int iCity) {
		this.iCity = iCity;
	}

	@Column(name = "IAVGDIS_COUNT")
	public int getIavgdiscount() {
		return iavgdiscount;
	}

	public void setIavgdiscount(int iavgdiscount) {
		this.iavgdiscount = iavgdiscount;
	}

	@Column(name = "FCRATE")
	public double getFCrate() {
		return FCrate;
	}

	public void setFCrate(double fCrate) {
		FCrate = fCrate;
	}

	@Column(name = "AVGOVER_RATE")
	public double getAvgoverrate() {
		return avgoverrate;
	}

	public void setAvgoverrate(double avgoverrate) {
		this.avgoverrate = avgoverrate;
	}

	@Column(name = "IRANGE")
	public double getiRange() {
		return iRange;
	}

	public void setiRange(double iRange) {
		this.iRange = iRange;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OWNERID_BIZ")
	public PSG_Businessreport getBizreport() {
		return bizreport;
	}

	public void setBizreport(PSG_Businessreport bizreport) {
		this.bizreport = bizreport;
	}

	@Override
	public String toString() {
		return "PSG_BusinessDetail [type=" + type + ", iout=" + iout + ", iflight=" + iflight + ", iCity=" + iCity
				+ ", iavgdiscount=" + iavgdiscount + ", FCrate=" + FCrate + ", avgoverrate=" + avgoverrate
				+ ", iRange=" + iRange + "]";
	}

}
