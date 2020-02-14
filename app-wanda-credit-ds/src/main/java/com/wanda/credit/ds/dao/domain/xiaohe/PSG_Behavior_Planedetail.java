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
@Table(name = "T_DS_JZ_BEHAVIOR_PLANEDETAIL")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer" })
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@XStreamAlias("palanedetail")
public class PSG_Behavior_Planedetail extends BaseDomain {// 建表
	private static final long serialVersionUID = -1289807774918590425L;
	private String id;
	private String type;// 航线
	private String planetype;// 机型
	private int iCount;// 统计次数
	private double pricerate;// 同价位比例
	private double timerRate;// 同时段比例
	private PSG_Behavior_Planereport palanereport;

	public PSG_Behavior_Planedetail() {
		super();
	}

	public PSG_Behavior_Planedetail(String id, String type, String planetype, int iCount, double pricerate,
			double timerRate) {
		super();
		this.id = id;
		this.type = type;
		this.planetype = planetype;
		this.iCount = iCount;
		this.pricerate = pricerate;
		this.timerRate = timerRate;
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

	@Column(name = "PLANE_TYPE")
	public String getPlanetype() {
		return planetype;
	}

	public void setPlanetype(String planetype) {
		this.planetype = planetype;
	}

	@Column(name = "ICOUNT")
	public int getiCount() {
		return iCount;
	}

	public void setiCount(int iCount) {
		this.iCount = iCount;
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OWNERID_PLANE")
	public PSG_Behavior_Planereport getPalanereport() {
		return palanereport;
	}

	public void setPalanereport(PSG_Behavior_Planereport palanereport) {
		this.palanereport = palanereport;
	}

	@Override
	public String toString() {
		return "PSG_Behavior_Planedetail [type=" + type + ", planetype=" + planetype + ", iCount=" + iCount + ", pricerate="
				+ pricerate + ", timerRate=" + timerRate + "]";
	}

}
