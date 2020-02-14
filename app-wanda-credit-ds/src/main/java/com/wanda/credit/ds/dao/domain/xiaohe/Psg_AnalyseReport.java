package com.wanda.credit.ds.dao.domain.xiaohe;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.annotations.GenericGenerator;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.wanda.credit.base.domain.BaseDomain;

@Entity
@Table(name = "T_DS_JZ_ANALYSEREPORT")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer" })
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@XStreamAlias("analyseReport")
public class Psg_AnalyseReport extends BaseDomain {// 建表
	private static final long serialVersionUID = -1289807774918590425L;
	private String id;
	private String err_code;
	private String err_context;

	private PSG_Query query;// 查询条件
	private PSG_Result result;// 查询结果
	private PSG_Staticdata staticdata;// 统计结果

	private List<PSG_Dstreport> dstreportlst;// 目的地分析
	private PSG_Businessreport businessreport;// 商务出行分析
	private PSG_TravelData traveldata;// 旅游出行分析

	// 乘机人行为分析
	private PSG_Behavior_Bookreport bookreport;
	private List<PSG_Behavior_AirlineDetail> airlinedetailLst;
	private PSG_Behavior_Planereport palanereport;
	private PSG_Behavior_Delayreport delayreport;

	private Psg_AnalyseReportMain psgmain;

	public Psg_AnalyseReport() {
		super();
	}

	public Psg_AnalyseReport(String id, String err_code, String err_context, PSG_Query query, PSG_Result result,
			PSG_Staticdata staticdata, List<PSG_Dstreport> dstreportlst, PSG_Businessreport businessreport,
			PSG_TravelData traveldata, PSG_Behavior_Bookreport bookreport,
			List<PSG_Behavior_AirlineDetail> airlinedetailLst, PSG_Behavior_Planereport palanereport,
			PSG_Behavior_Delayreport delayreport) {
		super();
		this.id = id;
		this.err_code = err_code;
		this.err_context = err_context;
		this.query = query;
		this.result = result;
		this.staticdata = staticdata;
		this.dstreportlst = dstreportlst;
		this.businessreport = businessreport;
		this.traveldata = traveldata;
		this.bookreport = bookreport;
		this.airlinedetailLst = airlinedetailLst;
		this.palanereport = palanereport;
		this.delayreport = delayreport;
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

	public String getErr_code() {
		return err_code;
	}

	public void setErr_code(String err_code) {
		this.err_code = err_code;
	}

	public String getErr_context() {
		return err_context;
	}

	public void setErr_context(String err_context) {
		this.err_context = err_context;
	}

	@OneToOne(fetch = FetchType.LAZY, mappedBy = "analyseReport", cascade = CascadeType.ALL)
	public PSG_Query getQuery() {
		return query;
	}

	public void setQuery(PSG_Query query) {
		this.query = query;
	}

	@OneToOne(fetch = FetchType.LAZY, mappedBy = "analyseReport", cascade = CascadeType.ALL)
	public PSG_Result getResult() {
		return result;
	}

	public void setResult(PSG_Result result) {
		this.result = result;
	}

	@OneToOne(fetch = FetchType.LAZY, mappedBy = "analyseReport", cascade = CascadeType.ALL)
	public PSG_Staticdata getStaticdata() {
		return staticdata;
	}

	public void setStaticdata(PSG_Staticdata staticdata) {
		this.staticdata = staticdata;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "analyseReport", cascade = CascadeType.ALL)
	public List<PSG_Dstreport> getDstreportlst() {
		return dstreportlst;
	}

	public void setDstreportlst(List<PSG_Dstreport> dstreportlst) {
		this.dstreportlst = dstreportlst;
	}

	@OneToOne(fetch = FetchType.LAZY, mappedBy = "analyseReport", cascade = CascadeType.ALL)
	public PSG_Businessreport getBusinessreport() {
		return businessreport;
	}

	public void setBusinessreport(PSG_Businessreport businessreport) {
		this.businessreport = businessreport;
	}

	@OneToOne(fetch = FetchType.LAZY, mappedBy = "analyseReport", cascade = CascadeType.ALL)
	public PSG_TravelData getTraveldata() {
		return traveldata;
	}

	public void setTraveldata(PSG_TravelData traveldata) {
		this.traveldata = traveldata;
	}

	@OneToOne(fetch = FetchType.LAZY, mappedBy = "analyseReport", cascade = CascadeType.ALL)
	public PSG_Behavior_Bookreport getBookreport() {
		return bookreport;
	}

	public void setBookreport(PSG_Behavior_Bookreport bookreport) {
		this.bookreport = bookreport;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "analyseReport", cascade = CascadeType.ALL)
	public List<PSG_Behavior_AirlineDetail> getAirlinedetailLst() {
		return airlinedetailLst;
	}

	public void setAirlinedetailLst(List<PSG_Behavior_AirlineDetail> airlinedetailLst) {
		this.airlinedetailLst = airlinedetailLst;
	}

	@OneToOne(fetch = FetchType.LAZY, mappedBy = "analyseReport", cascade = CascadeType.ALL)
	public PSG_Behavior_Planereport getPalanereport() {
		return palanereport;
	}

	public void setPalanereport(PSG_Behavior_Planereport palanereport) {
		this.palanereport = palanereport;
	}

	@OneToOne(fetch = FetchType.LAZY, mappedBy = "analyseReport", cascade = CascadeType.ALL)
	public PSG_Behavior_Delayreport getDelayreport() {
		return delayreport;
	}

	public void setDelayreport(PSG_Behavior_Delayreport delayreport) {
		this.delayreport = delayreport;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OWNERID")
	public Psg_AnalyseReportMain getPsgmain() {
		return psgmain;
	}

	public void setPsgmain(Psg_AnalyseReportMain psgmain) {
		this.psgmain = psgmain;
	}

	@Override
	public String toString() {
		return "Psg_AnalyseReport [err_code=" + err_code + ", err_context=" + err_context + ", query=" + query
				+ ", result=" + result + ", staticdata=" + staticdata + ", dstreportlst=" + dstreportlst
				+ ", businessreport=" + businessreport + ", traveldata=" + traveldata + ", bookreport=" + bookreport
				+ ", airlinedetailLst=" + airlinedetailLst + ", palanereport=" + palanereport + ", delayreport="
				+ delayreport + "]";
	}
}
