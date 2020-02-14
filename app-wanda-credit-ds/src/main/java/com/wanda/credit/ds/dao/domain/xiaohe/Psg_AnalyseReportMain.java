package com.wanda.credit.ds.dao.domain.xiaohe;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.annotations.GenericGenerator;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.wanda.credit.base.domain.BaseDomain;

@Entity
@Table(name = "T_DS_JZ_ANALYSEREPORTMAIN")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer" })
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@XStreamAlias("psgmain")
public class Psg_AnalyseReportMain extends BaseDomain {// 建表
	private static final long serialVersionUID = -1289807774918590425L;
	private String id;
	private String cardno;
	private String name;
	private String gid;
	private String imonth;
	private String reporttype;
	private Psg_Error_Res errorRes;
	private PSG_Paydata paydata;
	private List<Psg_AnalyseReport> lstreportList;
	private String trade_id;

	public Psg_AnalyseReportMain() {
		super();
	}

	public Psg_AnalyseReportMain(String id, String cardno, String name, String gid, String imonth, String reporttype,
			Psg_Error_Res errorRes, PSG_Paydata paydata, List<Psg_AnalyseReport> lstreportList, String trade_id) {
		super();
		this.id = id;
		this.cardno = cardno;
		this.name = name;
		this.gid = gid;
		this.imonth = imonth;
		this.reporttype = reporttype;
		this.errorRes = errorRes;
		this.paydata = paydata;
		this.lstreportList = lstreportList;
		this.trade_id = trade_id;
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

	@OneToOne(fetch = FetchType.LAZY, mappedBy = "psgmain", cascade = CascadeType.ALL)
	public Psg_Error_Res getErrorRes() {
		return errorRes;
	}

	public void setErrorRes(Psg_Error_Res errorRes) {
		this.errorRes = errorRes;
	}

	@OneToOne(fetch = FetchType.LAZY, mappedBy = "psgmain", cascade = CascadeType.ALL)
	public PSG_Paydata getPaydata() {
		return paydata;
	}

	public void setPaydata(PSG_Paydata paydata) {
		this.paydata = paydata;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "psgmain", cascade = CascadeType.ALL)
	public List<Psg_AnalyseReport> getLstreportList() {
		return lstreportList;
	}

	public void setLstreportList(List<Psg_AnalyseReport> lstreportList) {
		this.lstreportList = lstreportList;
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

	public String getGid() {
		return gid;
	}

	public void setGid(String gid) {
		this.gid = gid;
	}

	public String getImonth() {
		return imonth;
	}

	public void setImonth(String imonth) {
		this.imonth = imonth;
	}

	public String getReporttype() {
		return reporttype;
	}

	public void setReporttype(String reporttype) {
		this.reporttype = reporttype;
	}

	public String getCardno() {
		return cardno;
	}

	public void setCardno(String cardno) {
		this.cardno = cardno;
	}

	@Override
	public String toString() {
		return "Psg_AnalyseReportMain [cardno=" + cardno + ", name=" + name + ", gid=" + gid + ", imonth=" + imonth
				+ ", reporttype=" + reporttype + ", errorRes=" + errorRes + ", paydata=" + paydata + ", lstreportList="
				+ lstreportList + "]";
	}
}
