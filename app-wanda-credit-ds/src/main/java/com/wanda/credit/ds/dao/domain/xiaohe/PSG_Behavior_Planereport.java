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
@Table(name = "T_DS_JZ_BEHAVIOR_PLANE")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer" })
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@XStreamAlias("palanereport")
public class PSG_Behavior_Planereport extends BaseDomain {// 建表
	private static final long serialVersionUID = -1289807774918590425L;
	private String id;
	private int iBigPlane;// 大型机
	private int iMiddleplane;// 中型机
	private int smallplane;// 小型机
	private List<PSG_Behavior_Planedetail> airlinedetaillst;// 机型明细

	private Psg_AnalyseReport analyseReport;

	public PSG_Behavior_Planereport() {
		super();
	}

	public PSG_Behavior_Planereport(String id, int iBigPlane, int iMiddleplane, int smallplane,
			List<PSG_Behavior_Planedetail> airlinedetaillst) {
		super();
		this.id = id;
		this.iBigPlane = iBigPlane;
		this.iMiddleplane = iMiddleplane;
		this.smallplane = smallplane;
		this.airlinedetaillst = airlinedetaillst;
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

	@Column(name = "IBIG_PLANE")
	public int getiBigPlane() {
		return iBigPlane;
	}

	public void setiBigPlane(int iBigPlane) {
		this.iBigPlane = iBigPlane;
	}

	@Column(name = "IMIDDLE_PLANE")
	public int getiMiddleplane() {
		return iMiddleplane;
	}

	public void setiMiddleplane(int iMiddleplane) {
		this.iMiddleplane = iMiddleplane;
	}

	@Column(name = "SMALL_PLANE")
	public int getSmallplane() {
		return smallplane;
	}

	public void setSmallplane(int smallplane) {
		this.smallplane = smallplane;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "palanereport", cascade = CascadeType.ALL)
	public List<PSG_Behavior_Planedetail> getAirlinedetaillst() {
		return airlinedetaillst;
	}

	public void setAirlinedetaillst(List<PSG_Behavior_Planedetail> airlinedetaillst) {
		this.airlinedetaillst = airlinedetaillst;
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
		return "PSG_Behavior_Planereport [iBigPlane=" + iBigPlane + ", iMiddleplane=" + iMiddleplane + ", smallplane="
				+ smallplane + ", airlinedetaillst=" + airlinedetaillst + "]";
	}

}
