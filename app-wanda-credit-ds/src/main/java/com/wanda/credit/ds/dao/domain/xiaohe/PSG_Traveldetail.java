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

@Entity
@Table(name = "T_DS_JZ_TRAVELDEATIL")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer" })
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@XStreamAlias("traveldetail")
public class PSG_Traveldetail extends BaseDomain {// 建表
	private static final long serialVersionUID = -1289807774918590425L;
	private String id;
	private String type;// 描述（国内、国际）
	private String month;// 出行月份
	private String dstcity;// 目的地
	private String traveltype;// 类型（海岛游、观光、购物）
	private int totalfare;// 消费金额
	private String Note;// 备注
	private PSG_TravelData traveldata;

	public PSG_Traveldetail() {
		super();
	}

	public PSG_Traveldetail(String id, String type, String month, String dstcity, String traveltype, int totalfare,
			String Note) {
		super();
		this.id = id;
		this.type = type;
		this.month = month;
		this.dstcity = dstcity;
		this.traveltype = traveltype;
		this.totalfare = totalfare;
		this.Note = Note;
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

	@Column(name = "TYPE")
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Column(name = "MONTH")
	public String getMonth() {
		return this.month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	@Column(name = "DST_CITY")
	public String getDstcity() {
		return dstcity;
	}

	public void setDstcity(String dstcity) {
		this.dstcity = dstcity;
	}

	@Column(name = "TRAVEL_TYPE")
	public String getTraveltype() {
		return traveltype;
	}

	public void setTraveltype(String traveltype) {
		this.traveltype = traveltype;
	}

	@Column(name = "TOTAL_FARE")
	public int getTotalfare() {
		return totalfare;
	}

	public void setTotalfare(int totalfare) {
		this.totalfare = totalfare;
	}

	@Column(name = "NOTE")
	public String getNote() {
		return Note;
	}

	public void setNote(String note) {
		Note = note;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OWNERID_TRAVEL")
	public PSG_TravelData getTraveldata() {
		return traveldata;
	}

	public void setTraveldata(PSG_TravelData traveldata) {
		this.traveldata = traveldata;
	}

	@Override
	public String toString() {
		return "PSG_Traveldetail [type=" + type + ", Month=" + this.month + ", dstcity=" + dstcity + ", traveltype="
				+ traveltype + ", totalfare=" + totalfare + ", Note=" + Note + "]";
	}

}
