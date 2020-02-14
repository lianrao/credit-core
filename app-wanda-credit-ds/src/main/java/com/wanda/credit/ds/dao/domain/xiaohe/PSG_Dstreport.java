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

//目的地分析
@Entity
@Table(name = "T_DS_JZ_DSTREPORT")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer" })
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@XStreamAlias("dstreport")
public class PSG_Dstreport extends BaseDomain {// 建表
	private static final long serialVersionUID = -1289807774918590425L;
	private String id;
	private String type;// business:商务出行、travel:旅游出行、home:探亲出行 add by wangjing
	private int idomestic;// 国内次数
	private int iinternation;// 国际次数
	private int iTotal;// 合计
	private int idcity;// 国内目的地城市统计
	private int iicity;// 国际目的地城市次数
	private int idTotal;// 国内+国际合计
	private int idstcity;// 目的地城市
	private double totaltpm;// 总里程数
	private double totalfare;// 总消费金额
	private Psg_AnalyseReport analyseReport;

	public PSG_Dstreport() {
		super();
	}

	public PSG_Dstreport(String id, String type, int idomestic, int iinternation, int iTotal, int idcity, int iicity,
			int idTotal, int idstcity, double totaltpm, double totalfare) {
		super();
		this.id = id;
		this.type = type;
		this.idomestic = idomestic;
		this.iinternation = iinternation;
		this.iTotal = iTotal;
		this.idcity = idcity;
		this.iicity = iicity;
		this.idstcity = idstcity;
		this.totalfare = totaltpm;
		this.totalfare = totalfare;
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Column(name = "IDOME_STIC")
	public int getIdomestic() {
		return idomestic;
	}

	public void setIdomestic(int idomestic) {
		this.idomestic = idomestic;
	}

	@Column(name = "IINTER_NATION")
	public int getIinternation() {
		return iinternation;
	}

	public void setIinternation(int iinternation) {
		this.iinternation = iinternation;
	}

	@Column(name = "ITOTAL")
	public int getiTotal() {
		return iTotal;
	}

	public void setiTotal(int iTotal) {
		this.iTotal = iTotal;
	}

	@Column(name = "ID_CITY")
	public int getIdcity() {
		return idcity;
	}

	public void setIdcity(int idcity) {
		this.idcity = idcity;
	}

	@Column(name = "II_CITY")
	public int getIicity() {
		return iicity;
	}

	public void setIicity(int iicity) {
		this.iicity = iicity;
	}

	@Column(name = "ID_TOTAL")
	public int getIdTotal() {
		return idTotal;
	}

	public void setIdTotal(int idTotal) {
		this.idTotal = idTotal;
	}

	@Column(name = "IDST_CITY")
	public int getIdstcity() {
		return idstcity;
	}

	public void setIdstcity(int idstcity) {
		this.idstcity = idstcity;
	}

	@Column(name = "TOTAL_TPM")
	public double getTotaltpm() {
		return totaltpm;
	}

	public void setTotaltpm(double totaltpm) {
		this.totaltpm = totaltpm;
	}

	@Column(name = "TOTAL_FARE")
	public double getTotalfare() {
		return totalfare;
	}

	public void setTotalfare(double totalfare) {
		this.totalfare = totalfare;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OWNERID")
	public Psg_AnalyseReport getAnalyseReport() {
		return analyseReport;
	}

	public void setAnalyseReport(Psg_AnalyseReport analyseReport) {
		this.analyseReport = analyseReport;
	}

	@Override
	public String toString() {
		return "PSG_Dstreport [idomestic=" + idomestic + ", iinternation=" + iinternation + ", iTotal=" + iTotal
				+ ", idcity=" + idcity + ", iicity=" + iicity + ", idTotal=" + idTotal + ", idstcity=" + idstcity
				+ ", totaltpm=" + totaltpm + ", totalfare=" + totalfare + "]";
	}

}
