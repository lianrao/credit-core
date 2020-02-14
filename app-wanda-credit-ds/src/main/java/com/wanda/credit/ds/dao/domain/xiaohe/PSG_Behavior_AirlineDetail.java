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
@Table(name = "T_DS_JZ_BEHAVIOR_AIRLINEDTL")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer" })
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@XStreamAlias("airlineDetail")
public class PSG_Behavior_AirlineDetail extends BaseDomain {// 建表
	private static final long serialVersionUID = -1289807774918590425L;
	private String id;
	private String airline;// 航空公司比例
	private String isMember;// 是否会员
	private int iCount;// 乘机次数
	private int iRate;// 占比
	private int iFree;// 免票次数
	private double airlinerate;// 可选比例
	private double pricerate;// 同价位比例
	private double timerRate;// 同时段比例
	private double planeRate;// 同机型比例
	private Psg_AnalyseReport analyseReport;

	public PSG_Behavior_AirlineDetail() {
		super();
	}

	public PSG_Behavior_AirlineDetail(String id, String airline, String isMember, int iCount, int iRate, int iFree,
			double airlinerate, double pricerate, double timerRate, double planeRate) {
		super();
		this.id = id;
		this.airline = airline;
		this.isMember = isMember;
		this.iCount = iCount;
		this.iRate = iRate;
		this.iFree = iFree;
		this.airlinerate = airlinerate;
		this.pricerate = pricerate;
		this.timerRate = timerRate;
		this.planeRate = planeRate;
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

	@Column(name = "AIR_LINE")
	public String getAirline() {
		return airline;
	}

	public void setAirline(String airline) {
		this.airline = airline;
	}

	@Column(name = "IS_MEMBER")
	public String getIsMember() {
		return isMember;
	}

	public void setIsMember(String isMember) {
		this.isMember = isMember;
	}

	@Column(name = "ICOUNT")
	public int getiCount() {
		return iCount;
	}

	public void setiCount(int iCount) {
		this.iCount = iCount;
	}

	@Column(name = "IRATE")
	public int getiRate() {
		return iRate;
	}

	public void setiRate(int iRate) {
		this.iRate = iRate;
	}

	@Column(name = "IFREE")
	public int getiFree() {
		return iFree;
	}

	public void setiFree(int iFree) {
		this.iFree = iFree;
	}

	@Column(name = "AIRLINE_RATE")
	public double getAirlinerate() {
		return airlinerate;
	}

	public void setAirlinerate(double airlinerate) {
		this.airlinerate = airlinerate;
	}

	@Column(name = "PRICE_RATE")
	public double getPricerate() {
		return pricerate;
	}

	public void setPricerate(double pricerate) {
		this.pricerate = pricerate;
	}

	@Column(name = "TIMER_RATE")
	public double getTimerRate() {
		return timerRate;
	}

	public void setTimerRate(double timerRate) {
		this.timerRate = timerRate;
	}

	@Column(name = "PLANE_RATE")
	public double getPlaneRate() {
		return planeRate;
	}

	public void setPlaneRate(double planeRate) {
		this.planeRate = planeRate;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OWNERID")
	public Psg_AnalyseReport getAnalyseReport() {
		return analyseReport;
	}

	public void setAnalyseReport(Psg_AnalyseReport analyseReport) {
		this.analyseReport = analyseReport;
	}

	@Override
	public String toString() {
		return "PSG_Behavior_AirlineDetail [airline=" + airline + ", isMember=" + isMember + ", iCount=" + iCount + ", iRate="
				+ iRate + ", iFree=" + iFree + ", airlinerate=" + airlinerate + ", pricerate=" + pricerate
				+ ", timerRate=" + timerRate + ", planeRate=" + planeRate + "]";
	}
}
