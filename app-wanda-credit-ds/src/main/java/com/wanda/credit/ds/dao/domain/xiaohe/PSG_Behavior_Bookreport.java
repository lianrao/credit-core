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
import org.hibernate.annotations.GenericGenerator;

import com.wanda.credit.base.domain.BaseDomain;

@Entity
@Table(name = "T_DS_JZ_BEHAVIOR_BOOK")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer", "config", "updated", "created", "updatedby", "createdby" })
public class PSG_Behavior_Bookreport extends BaseDomain {// 建表
	private static final long serialVersionUID = -1289807774918590425L;
	private String id;
	private double iavgearlyoutday;// 提前订票
	private double igqrate;// 改签比例
	private double ifreeticket;// 免票次数
	private int iFclass;// 头等舱
	private int iCclass;// 商务舱
	private int iYclass;// 经济舱
	private int iMorning;// 上午
	private int iafternoon;// 下午
	private int inight;// 晚上
	private int iprivate;// 因私
	private int ipublic;// 因公
	private double rank_iavgearlyoutday;// 提前订票排名
	private double rank_igqrate;// 改签排名
	private double rank_ifreeticket;// 免票次数
	private double rank_iFCYclass;// 舱位排名
	private double rank_private;// 因私排名
	private double rank_public;// 因公排名
	private Psg_AnalyseReport analyseReport;

	public PSG_Behavior_Bookreport() {
		super();
	}

	public PSG_Behavior_Bookreport(String id, double iavgearlyoutday, double igqrate, double ifreeticket, int iFclass,
			int iCclass, int iYclass, int iMorning, int iafternoon, int inight, int iprivate, int ipublic,
			double rank_iavgearlyoutday, double rank_igqrate, double rank_ifreeticket, double rank_iFCYclass,
			double rank_private, double rank_public) {
		super();
		this.id = id;
		this.iavgearlyoutday = iavgearlyoutday;
		this.igqrate = igqrate;
		this.ifreeticket = ifreeticket;
		this.iFclass = iFclass;
		this.iCclass = iCclass;
		this.iYclass = iYclass;
		this.iMorning = iMorning;
		this.iafternoon = iafternoon;
		this.inight = inight;
		this.iprivate = iprivate;
		this.ipublic = ipublic;
		this.rank_iavgearlyoutday = rank_iavgearlyoutday;
		this.rank_igqrate = rank_igqrate;
		this.rank_ifreeticket = rank_ifreeticket;
		this.rank_iFCYclass = rank_iFCYclass;
		this.rank_private = rank_private;
		this.rank_public = rank_public;
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

	@Column(name = "IAVGEARLY_OUT_DAY")
	public double getIavgearlyoutday() {
		return iavgearlyoutday;
	}

	public void setIavgearlyoutday(double iavgearlyoutday) {
		this.iavgearlyoutday = iavgearlyoutday;
	}

	@Column(name = "IGQ_RATE")
	public double getIgqrate() {
		return igqrate;
	}

	public void setIgqrate(double igqrate) {
		this.igqrate = igqrate;
	}

	@Column(name = "IFREE_TICKET")
	public double getIfreeticket() {
		return ifreeticket;
	}

	public void setIfreeticket(double ifreeticket) {
		this.ifreeticket = ifreeticket;
	}

	@Column(name = "IF_CLASS")
	public int getiFclass() {
		return iFclass;
	}

	public void setiFclass(int iFclass) {
		this.iFclass = iFclass;
	}

	@Column(name = "IC_CLASS")
	public int getiCclass() {
		return iCclass;
	}

	public void setiCclass(int iCclass) {
		this.iCclass = iCclass;
	}

	@Column(name = "IY_CLASS")
	public int getiYclass() {
		return iYclass;
	}

	public void setiYclass(int iYclass) {
		this.iYclass = iYclass;
	}

	@Column(name = "IMORNING")
	public int getiMorning() {
		return iMorning;
	}

	public void setiMorning(int iMorning) {
		this.iMorning = iMorning;
	}

	@Column(name = "IA_FTERNOON")
	public int getIafternoon() {
		return iafternoon;
	}

	public void setIafternoon(int iafternoon) {
		this.iafternoon = iafternoon;
	}

	@Column(name = "INIGHT")
	public int getInight() {
		return inight;
	}

	public void setInight(int inight) {
		this.inight = inight;
	}

	@Column(name = "IPRIVATE")
	public int getIprivate() {
		return iprivate;
	}

	public void setIprivate(int iprivate) {
		this.iprivate = iprivate;
	}

	@Column(name = "IPUBLIC")
	public int getIpublic() {
		return ipublic;
	}

	public void setIpublic(int ipublic) {
		this.ipublic = ipublic;
	}

	@Column(name = "RANK_IAVGEARLY_OUT_DAY")
	public double getRank_iavgearlyoutday() {
		return rank_iavgearlyoutday;
	}

	public void setRank_iavgearlyoutday(double rank_iavgearlyoutday) {
		this.rank_iavgearlyoutday = rank_iavgearlyoutday;
	}

	@Column(name = "RANK_IGQRATE")
	public double getRank_igqrate() {
		return rank_igqrate;
	}

	public void setRank_igqrate(double rank_igqrate) {
		this.rank_igqrate = rank_igqrate;
	}

	@Column(name = "RANK_IFREE_TICKET")
	public double getRank_ifreeticket() {
		return rank_ifreeticket;
	}

	public void setRank_ifreeticket(double rank_ifreeticket) {
		this.rank_ifreeticket = rank_ifreeticket;
	}

	@Column(name = "RANK_IFCYCLASS")
	public double getRank_iFCYclass() {
		return rank_iFCYclass;
	}

	public void setRank_iFCYclass(double rank_iFCYclass) {
		this.rank_iFCYclass = rank_iFCYclass;
	}

	@Column(name = "RANK_PRIVATE")
	public double getRank_private() {
		return rank_private;
	}

	public void setRank_private(double rank_private) {
		this.rank_private = rank_private;
	}

	@Column(name = "RANK_PUBLIC")
	public double getRank_public() {
		return rank_public;
	}

	public void setRank_public(double rank_public) {
		this.rank_public = rank_public;
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
		return "PSG_Behavior_Bookreport [iavgearlyoutday=" + iavgearlyoutday + ", igqrate=" + igqrate + ", ifreeticket="
				+ ifreeticket + ", iFclass=" + iFclass + ", iCclass=" + iCclass + ", iYclass=" + iYclass
				+ ", iMorning=" + iMorning + ", iafternoon=" + iafternoon + ", inight=" + inight + ", iprivate="
				+ iprivate + ", ipublic=" + ipublic + ", rank_iavgearlyoutday=" + rank_iavgearlyoutday
				+ ", rank_igqrate=" + rank_igqrate + ", rank_ifreeticket=" + rank_ifreeticket + ", rank_iFCYclass="
				+ rank_iFCYclass + ", rank_private=" + rank_private + ", rank_public=" + rank_public + "]";
	}
}
