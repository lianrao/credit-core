package com.wanda.credit.ds.dao.domain.xiaohe;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.annotations.GenericGenerator;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.wanda.credit.base.domain.BaseDomain;

@Entity
@Table(name = "T_DS_JZ_INCACHEMAIN")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer" })
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@XStreamAlias("psgincache")
public class Psg_IncacheMain extends BaseDomain {// 建表
	private static final long serialVersionUID = -1289807774918590425L;
	private String id;
	private String pid;
	private String gid;
	private String Name;
	private String Birthday;
	private String BirthArea;
	private Psg_Error_Res_Incache ErrorRes;
	private Psg_Incache_Detail FlightInfo;
	private String trade_id;

	public Psg_IncacheMain() {
		super();
	}

	public Psg_IncacheMain(String id, String pid, String Name, String Birthday, String BirthArea,
			Psg_Error_Res_Incache ErrorRes,Psg_Incache_Detail FlightInfo, String trade_id) {
		super();
		this.id = id;
		this.pid = pid;
		this.Name = Name;
		this.Birthday = Birthday;
		this.BirthArea = BirthArea;
		this.ErrorRes = ErrorRes;
		this.FlightInfo = FlightInfo;
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

	@OneToOne(fetch = FetchType.LAZY, mappedBy = "psgincache", cascade = CascadeType.ALL)
	public Psg_Error_Res_Incache getErrorRes() {
		return ErrorRes;
	}

	public void setErrorRes(Psg_Error_Res_Incache ErrorRes) {
		this.ErrorRes = ErrorRes;
	}


	public String getTrade_id() {
		return trade_id;
	}

	public void setTrade_id(String trade_id) {
		this.trade_id = trade_id;
	}

	public String getName() {
		return Name;
	}

	public void setName(String Name) {
		this.Name = Name;
	}


	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getBirthday() {
		return Birthday;
	}

	public void setBirthday(String birthday) {
		Birthday = birthday;
	}

	public String getBirthArea() {
		return BirthArea;
	}

	public void setBirthArea(String birthArea) {
		BirthArea = birthArea;
	}
	public String getGid() {
		return gid;
	}

	public void setGid(String gid) {
		this.gid = gid;
	}

	@OneToOne(fetch = FetchType.LAZY, mappedBy = "psgincache", cascade = CascadeType.ALL)
	public Psg_Incache_Detail getFlightInfo() {
		return FlightInfo;
	}

	public void setFlightInfo(Psg_Incache_Detail flightInfo) {
		FlightInfo = flightInfo;
	}

	@Override
	public String toString() {
		return "Psg_AnalyseReportMain [pid=" + pid + ", name=" + Name + ", birday=" + Birthday + ", birth_area=" + BirthArea
				+ ", errorRes=" + ErrorRes  + "]";
	}
}
