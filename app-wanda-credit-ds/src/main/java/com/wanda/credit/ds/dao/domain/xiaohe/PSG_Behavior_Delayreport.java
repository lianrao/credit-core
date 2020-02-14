package com.wanda.credit.ds.dao.domain.xiaohe;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.annotations.GenericGenerator;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.wanda.credit.base.domain.BaseDomain;

@Entity
@Table(name = "T_DS_JZ_BEHAVIOR_DELAY")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer" })
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@XStreamAlias("delayreport")
public class PSG_Behavior_Delayreport extends BaseDomain {// 建表
	private static final long serialVersionUID = -1289807774918590425L;
	private String id;
	private int iTotaldealy;// 总延误次数
	private double avgTotaldelay;// 平均延误时间
	private int idelay1hour;// 1小时延误次数
	private int idelay2hour;// 2小时延误次数
	private int idelay4hour;// 4小时延误次数

	private List<PSG_Behavior_Delaydetail> delayDetaillst;// 延误航线明细
	private Psg_AnalyseReport analyseReport;

	public PSG_Behavior_Delayreport() {
		super();
	}

	public PSG_Behavior_Delayreport(String id, int iTotaldealy, double avgTotaldelay, int idelay1hour, int idelay2hour,
			int idelay4hour) {
		super();
		this.id = id;
		this.iTotaldealy = iTotaldealy;
		this.avgTotaldelay = avgTotaldelay;
		this.idelay1hour = idelay1hour;
		this.idelay2hour = idelay2hour;
		this.idelay4hour = idelay4hour;
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

	@Column(name = "ITOTAL_DEALY")
	public int getiTotaldealy() {
		return iTotaldealy;
	}

	public void setiTotaldealy(int iTotaldealy) {
		this.iTotaldealy = iTotaldealy;
	}

	@Column(name = "AVGTOTAL_DELAY")
	public double getAvgTotaldelay() {
		return avgTotaldelay;
	}

	public void setAvgTotaldelay(double avgTotaldelay) {
		this.avgTotaldelay = avgTotaldelay;
	}

	@Column(name = "IDELAY_1HOUR")
	public int getIdelay1hour() {
		return idelay1hour;
	}

	public void setIdelay1hour(int idelay1hour) {
		this.idelay1hour = idelay1hour;
	}

	@Column(name = "IDELAY_2HOUR")
	public int getIdelay2hour() {
		return idelay2hour;
	}

	public void setIdelay2hour(int idelay2hour) {
		this.idelay2hour = idelay2hour;
	}

	@Column(name = "IDELAY_4HOUR")
	public int getIdelay4hour() {
		return idelay4hour;
	}

	public void setIdelay4hour(int idelay4hour) {
		this.idelay4hour = idelay4hour;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "delayreport", cascade = CascadeType.ALL)
	public List<PSG_Behavior_Delaydetail> getDelayDetaillst() {
		return delayDetaillst;
	}

	public void setDelayDetaillst(List<PSG_Behavior_Delaydetail> delayDetaillst) {
		this.delayDetaillst = delayDetaillst;
	}

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OWNERID")
	public Psg_AnalyseReport getAnalyseReport() {
		return analyseReport;
	}

	public void setAnalyseReport(Psg_AnalyseReport analyseReport) {
		this.analyseReport = analyseReport;
	}

	@Override
	public String toString() {
		return "PSG_Behavior_Delayreport [delayDetaillst=" + delayDetaillst + ", iTotaldealy=" + iTotaldealy
				+ ", avgTotaldelay=" + avgTotaldelay + ", idelay1hour=" + idelay1hour + ", idelay2hour=" + idelay2hour
				+ ", idelay4hour=" + idelay4hour + "]";
	}

}
