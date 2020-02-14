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
@Table(name = "T_DS_JZ_BEHAVIOR_DELAYDETAIL")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer" })
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@XStreamAlias("delaydetail")
public class PSG_Behavior_Delaydetail extends BaseDomain {// 建表
	private static final long serialVersionUID = -1289807774918590425L;
	private String id;
	private String citypair;// 延误航线
	private int idelay;// 延误次数
	private double idelayRate;// 航线延误率
	private double avgdelay;// 平均延误时间

	private PSG_Behavior_Delayreport delayreport;

	public PSG_Behavior_Delaydetail() {
		super();
	}

	public PSG_Behavior_Delaydetail(String id, String citypair, int idelay, double idelayRate, double avgdelay) {
		super();
		this.id = id;
		this.citypair = citypair;
		this.idelay = idelay;
		this.idelayRate = idelayRate;
		this.avgdelay = avgdelay;
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

	@Column(name = "CITY_PAIR")
	public String getCitypair() {
		return citypair;
	}

	public void setCitypair(String citypair) {
		this.citypair = citypair;
	}

	@Column(name = "IDELAY")
	public int getIdelay() {
		return idelay;
	}

	public void setIdelay(int idelay) {
		this.idelay = idelay;
	}

	@Column(name = "IDELAY_RATE")
	public double getIdelayRate() {
		return idelayRate;
	}

	public void setIdelayRate(double idelayRate) {
		this.idelayRate = idelayRate;
	}

	@Column(name = "AVG_DELAY")
	public double getAvgdelay() {
		return avgdelay;
	}

	public void setAvgdelay(double avgdelay) {
		this.avgdelay = avgdelay;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OWNERID_DELAY")
	public PSG_Behavior_Delayreport getDelayreport() {
		return delayreport;
	}

	public void setDelayreport(PSG_Behavior_Delayreport delayreport) {
		this.delayreport = delayreport;
	}

	@Override
	public String toString() {
		return "PSG_Behavior_Delaydetail [citypair=" + citypair + ", idelay=" + idelay + ", idelayRate=" + idelayRate
				+ ", avgdelay=" + avgdelay + "]";
	}

}
