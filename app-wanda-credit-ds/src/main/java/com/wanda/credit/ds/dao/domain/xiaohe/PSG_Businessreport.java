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

//商务出行分析
@Entity
@Table(name = "T_DS_JZ_BUSINESSREPORT")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer" })
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@XStreamAlias("bizreport")
public class PSG_Businessreport extends BaseDomain {// 建表
	private static final long serialVersionUID = -1289807774918590425L;
	private String id;
	private int ibusinessCount;// 商务频次
	private int icitycount;// 业务范围
	private int ifare;// 消费能力
	private double avgearlydays;// 提前规划
	private int iTotalscore;// 总分
	private double rankibusinessCount;// 商务频次排名
	private double rankicitycount;// 业务范围排名
	private double rankifare;// 消费能力排名
	private double rankavgearlydays;// 提前规划排名
	private double rankiTotalscore;// 总分排名
	private String maincity;// 主要城市
	private List<PSG_BusinessDetail> lstdetail;// 按季度，按年统计明细
	private Psg_AnalyseReport analyseReport;

	public PSG_Businessreport() {
		super();
	}

	public PSG_Businessreport(String id, int ibusinessCount, int icitycount, int ifare, double avgearlydays,
			int iTotalscore, double rankibusinessCount, double rankicitycount, double rankifare,
			double rankavgearlydays, double rankiTotalscore, String maincity) {
		super();
		this.id = id;
		this.ibusinessCount = ibusinessCount;
		this.icitycount = icitycount;
		this.ifare = ifare;
		this.avgearlydays = avgearlydays;
		this.iTotalscore = iTotalscore;

		this.rankibusinessCount = rankibusinessCount;
		this.rankicitycount = rankicitycount;
		this.rankifare = rankifare;
		this.rankavgearlydays = rankavgearlydays;
		this.rankiTotalscore = rankiTotalscore;
		this.maincity = maincity;
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

	@Column(name = "IBUSINESS_COUNT")
	public int getIbusinessCount() {
		return ibusinessCount;
	}

	public void setIbusinessCount(int ibusinessCount) {
		this.ibusinessCount = ibusinessCount;
	}

	@Column(name = "ICITY_COUNT")
	public int getIcitycount() {
		return icitycount;
	}

	public void setIcitycount(int icitycount) {
		this.icitycount = icitycount;
	}

	@Column(name = "IFARE")
	public int getIfare() {
		return ifare;
	}

	public void setIfare(int ifare) {
		this.ifare = ifare;
	}

	@Column(name = "AVGEARLY_DAYS")
	public double getAvgearlydays() {
		return avgearlydays;
	}

	public void setAvgearlydays(double avgearlydays) {
		this.avgearlydays = avgearlydays;
	}

	@Column(name = "ITOTAL_SCORE")
	public int getiTotalscore() {
		return iTotalscore;
	}

	public void setiTotalscore(int iTotalscore) {
		this.iTotalscore = iTotalscore;
	}

	@Column(name = "RANK_IBUSINESS_COUNT")
	public double getRankibusinessCount() {
		return rankibusinessCount;
	}

	public void setRankibusinessCount(double rankibusinessCount) {
		this.rankibusinessCount = rankibusinessCount;
	}

	@Column(name = "RANK_ICITY_COUNT")
	public double getRankicitycount() {
		return rankicitycount;
	}

	public void setRankicitycount(double rankicitycount) {
		this.rankicitycount = rankicitycount;
	}

	@Column(name = "RANK_IFARE")
	public double getRankifare() {
		return rankifare;
	}

	public void setRankifare(double rankifare) {
		this.rankifare = rankifare;
	}

	@Column(name = "RANK_AVGEARLY_DAYS")
	public double getRankavgearlydays() {
		return rankavgearlydays;
	}

	public void setRankavgearlydays(double rankavgearlydays) {
		this.rankavgearlydays = rankavgearlydays;
	}

	@Column(name = "RANK_ITOTAL_SCORE")
	public double getRankiTotalscore() {
		return rankiTotalscore;
	}

	public void setRankiTotalscore(double rankiTotalscore) {
		this.rankiTotalscore = rankiTotalscore;
	}

	@Column(name = "MAIN_CITY")
	public String getMaincity() {
		return maincity;
	}

	public void setMaincity(String maincity) {
		this.maincity = maincity;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "bizreport", cascade = CascadeType.ALL)
	public List<PSG_BusinessDetail> getLstdetail() {
		return lstdetail;
	}

	public void setLstdetail(List<PSG_BusinessDetail> lstdetail) {
		this.lstdetail = lstdetail;
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
	public String toString() {//
		return "PSG_Businessreport [ibusinessCount=" + ibusinessCount + ", icitycount=" + icitycount + ", ifare="
				+ ifare + ", avgearlydays=" + avgearlydays + ", iTotalscore=" + iTotalscore + ", rankibusinessCount="
				+ rankibusinessCount + ", rankicitycount=" + rankicitycount + ", rankifare=" + rankifare
				+ ", rankavgearlydays=" + rankavgearlydays + ", rankiTotalscore=" + rankiTotalscore + ", maincity="
				+ maincity + ", lstdetail=" + lstdetail + "]";
	}
}
