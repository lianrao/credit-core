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
@Table(name = "T_DS_JZ_PAYDATA")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer" })
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@XStreamAlias("paydata")
public class PSG_Paydata extends BaseDomain {// 建表
	private static final long serialVersionUID = -1289807774918590425L;
	private String id;
	private String payNote;
	private double payprice;
	private Psg_AnalyseReportMain psgmain;

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public PSG_Paydata() {
		super();
	}

	public PSG_Paydata(String id, String payNote, double payprice) {
		super();
		this.id = id;
		this.payNote = payNote;
		this.payprice = payprice;
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

	@Column(name = "PAY_NOTE")
	public String getPayNote() {
		return payNote;
	}

	public void setPayNote(String payNote) {
		this.payNote = payNote;
	}

	@Column(name = "PAY_PRICE")
	public double getPayprice() {
		return payprice;
	}

	public void setPayprice(double payprice) {
		this.payprice = payprice;
	}

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OWNERID")
	public Psg_AnalyseReportMain getPsgmain() {
		return psgmain;
	}

	public void setPsgmain(Psg_AnalyseReportMain psgmain) {
		this.psgmain = psgmain;
	}

	@Override
	public String toString() {
		return "PSG_Paydata [payNote=" + payNote + ", payprice=" + payprice + "]";
	}
}
