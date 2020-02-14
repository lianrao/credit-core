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
@Table(name = "T_DS_JZ_TRAVELDATA")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer" })
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@XStreamAlias("traveldata")
public class PSG_TravelData extends BaseDomain {// 建表
	private static final long serialVersionUID = -1289807774918590425L;
	private String id;
	private int icount;// 旅游频次
	private int iFare;// 消费能力
	private double iearlyoutday;// 提前规划
	private int iholidayCount;// 假期使用
	private double rankicount;// 旅游频次排名
	private double rankiFare;// 消费能力排名
	private double rankiearlyoutday;// 提前规划排名
	private double rankiholidayCount;// 假期使用排名
	private List<PSG_Traveldetail> lsttravel;// 假期出行明细
	private Psg_AnalyseReport analyseReport;

	public PSG_TravelData() {
		super();
	}

	public PSG_TravelData(String id, int icount, int iFare, double iearlyoutday, int iholidayCount, double rankicount,
			double rankiFare, double rankiearlyoutday, double rankiholidayCount, List<PSG_Traveldetail> lsttravel) {
		super();
		this.id = id;
		this.icount = icount;
		this.iFare = iFare;
		this.iearlyoutday = iearlyoutday;
		this.iholidayCount = iholidayCount;
		this.rankicount = rankicount;
		this.rankiFare = rankiFare;
		this.rankiearlyoutday = rankiearlyoutday;
		this.rankiholidayCount = rankiholidayCount;
		this.lsttravel = lsttravel;
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

	@Column(name = "ICOUNT")
	public int getIcount() {
		return icount;
	}

	public void setIcount(int icount) {
		this.icount = icount;
	}

	@Column(name = "IFARE")
	public int getiFare() {
		return iFare;
	}

	public void setiFare(int iFare) {
		this.iFare = iFare;
	}

	@Column(name = "IEARLY_OUT_DAY")
	public double getIearlyoutday() {
		return iearlyoutday;
	}

	public void setIearlyoutday(double iearlyoutday) {
		this.iearlyoutday = iearlyoutday;
	}

	@Column(name = "IHOLIDAY_COUNT")
	public int getIholidayCount() {
		return iholidayCount;
	}

	public void setIholidayCount(int iholidayCount) {
		this.iholidayCount = iholidayCount;
	}

	@Column(name = "RANK_ICOUNT")
	public double getRankicount() {
		return rankicount;
	}

	public void setRankicount(double rankicount) {
		this.rankicount = rankicount;
	}

	@Column(name = "RANK_IFARE")
	public double getRankiFare() {
		return rankiFare;
	}

	public void setRankiFare(double rankiFare) {
		this.rankiFare = rankiFare;
	}

	@Column(name = "RANK_IEARLY_OUT_DAY")
	public double getRankiearlyoutday() {
		return rankiearlyoutday;
	}

	public void setRankiearlyoutday(double rankiearlyoutday) {
		this.rankiearlyoutday = rankiearlyoutday;
	}

	@Column(name = "RANK_IHOLIDAY_COUNT")
	public double getRankiholidayCount() {
		return rankiholidayCount;
	}

	public void setRankiholidayCount(double rankiholidayCount) {
		this.rankiholidayCount = rankiholidayCount;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "traveldata", cascade = CascadeType.ALL)
	public List<PSG_Traveldetail> getLsttravel() {
		return lsttravel;
	}

	public void setLsttravel(List<PSG_Traveldetail> lsttravel) {
		this.lsttravel = lsttravel;
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
		return "PSG_TravelData [icount=" + icount + ", iFare=" + iFare + ", iearlyoutday=" + iearlyoutday
				+ ", iholidayCount=" + iholidayCount + ", rankicount=" + rankicount + ", rankiFare=" + rankiFare
				+ ", rankiearlyoutday=" + rankiearlyoutday + ", rankiholidayCount=" + rankiholidayCount
				+ ", lsttravel=" + lsttravel + "]";
	}

}
