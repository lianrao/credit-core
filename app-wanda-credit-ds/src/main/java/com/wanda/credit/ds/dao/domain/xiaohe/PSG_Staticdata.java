package com.wanda.credit.ds.dao.domain.xiaohe;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.annotations.GenericGenerator;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.wanda.credit.base.domain.BaseDomain;

@Entity
@Table(name = "T_DS_JZ_STATICDATA")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer" })
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@XStreamAlias("staticdata")
public class PSG_Staticdata extends BaseDomain {// 建表
	private static final long serialVersionUID = -1289807774918590425L;
	private String id;
	private int totalflight;// 总乘机数
	private int avgdiscount;// 平均折扣
	private int citycount;// 城市数
	private int FCcount;// 两舱比例
	private int totalfare;// 总价
	private int totalTPM;// 总里程
	private double totalscore;// 总分
	private double ranktotalflight;// 总乘机数排行
	private double rankavgdiscount;// 平均折扣排行
	private double rankcitycount;// 城市数排行
	private double rankFCcount;// 两舱比例排行
	private double ranktotalfare;// 总价排行
	private double ranktotalTPM;// 总里程排行
	private double ranktotalscore;// 总分排行
	private Psg_AnalyseReport analyseReport;

	public PSG_Staticdata() {
		super();
	}

	public PSG_Staticdata(String id, int totalflight, int avgdiscount, int citycount, int FCcount, int totalfare,
			int totalTPM, double totalscore, double ranktotalflight, double rankavgdiscount, double rankcitycount,
			double rankFCcount, double ranktotalfare, double ranktotalTPM, double ranktotalscore) {
		super();
		this.id = id;
		this.totalflight = totalflight;
		this.avgdiscount = avgdiscount;
		this.citycount = citycount;
		this.FCcount = FCcount;
		this.totalfare = totalfare;
		this.totalTPM = totalTPM;
		this.totalscore = totalscore;
		this.ranktotalflight = ranktotalflight;
		this.rankavgdiscount = rankavgdiscount;
		this.rankcitycount = rankcitycount;
		this.rankFCcount = rankFCcount;
		this.ranktotalfare = ranktotalfare;
		this.ranktotalTPM = ranktotalTPM;
		this.ranktotalscore = ranktotalscore;
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

	@Column(name = "TOTAL_FLIGHT")
	public int getTotalflight() {
		return totalflight;
	}

	public void setTotalflight(int totalflight) {
		this.totalflight = totalflight;
	}

	@Column(name = "AVG_DISCOUNT")
	public int getAvgdiscount() {
		return avgdiscount;
	}

	public void setAvgdiscount(int avgdiscount) {
		this.avgdiscount = avgdiscount;
	}

	@Column(name = "CITY_COUNT")
	public int getCitycount() {
		return citycount;
	}

	public void setCitycount(int citycount) {
		this.citycount = citycount;
	}

	@Column(name = "FCCOUNT")
	public int getFCcount() {
		return FCcount;
	}

	public void setFCcount(int fCcount) {
		FCcount = fCcount;
	}

	@Column(name = "TOTAL_FARE")
	public int getTotalfare() {
		return totalfare;
	}

	public void setTotalfare(int totalfare) {
		this.totalfare = totalfare;
	}

	@Column(name = "TOTALT_TPM")
	public int getTotalTPM() {
		return totalTPM;
	}

	public void setTotalTPM(int totalTPM) {
		this.totalTPM = totalTPM;
	}

	@Column(name = "TOTAL_SCORE")
	public double getTotalscore() {
		return totalscore;
	}

	public void setTotalscore(double totalscore) {
		this.totalscore = totalscore;
	}

	@Column(name = "RANK_TOTAL_FLIGHT")
	public double getRanktotalflight() {
		return ranktotalflight;
	}

	public void setRanktotalflight(double ranktotalflight) {
		this.ranktotalflight = ranktotalflight;
	}

	@Column(name = "RANK_AVG_DISCOUNT")
	public double getRankavgdiscount() {
		return rankavgdiscount;
	}

	public void setRankavgdiscount(double rankavgdiscount) {
		this.rankavgdiscount = rankavgdiscount;
	}

	@Column(name = "RANK_CITY_COUNT")
	public double getRankcitycount() {
		return rankcitycount;
	}

	public void setRankcitycount(double rankcitycount) {
		this.rankcitycount = rankcitycount;
	}

	@Column(name = "RANK_FCCOUNT")
	public double getRankFCcount() {
		return rankFCcount;
	}

	public void setRankFCcount(double rankFCcount) {
		this.rankFCcount = rankFCcount;
	}

	@Column(name = "RANK_TOTAL_FARE")
	public double getRanktotalfare() {
		return ranktotalfare;
	}

	public void setRanktotalfare(double ranktotalfare) {
		this.ranktotalfare = ranktotalfare;
	}

	@Column(name = "RANK_TOTAL_TPM")
	public double getRanktotalTPM() {
		return ranktotalTPM;
	}

	public void setRanktotalTPM(double ranktotalTPM) {
		this.ranktotalTPM = ranktotalTPM;
	}

	@Column(name = "RANK_TOTAL_SCORE")
	public double getRanktotalscore() {
		return ranktotalscore;
	}

	public void setRanktotalscore(double ranktotalscore) {
		this.ranktotalscore = ranktotalscore;
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
		return "PSG_Staticdata [totalflight=" + totalflight + ", avgdiscount=" + avgdiscount + ", citycount="
				+ citycount + ", FCcount=" + FCcount + ", totalfare=" + totalfare + ", totalTPM=" + totalTPM
				+ ", totalscore=" + totalscore + ", ranktotalflight=" + ranktotalflight + ", rankavgdiscount="
				+ rankavgdiscount + ", rankcitycount=" + rankcitycount + ", rankFCcount=" + rankFCcount
				+ ", ranktotalfare=" + ranktotalfare + ", ranktotalTPM=" + ranktotalTPM + ", ranktotalscore="
				+ ranktotalscore + "]";
	}
}
