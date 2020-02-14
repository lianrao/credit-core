package com.wanda.credit.ds.dao.domain.huifa;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.annotations.GenericGenerator;

import com.google.gson.annotations.Expose;
import com.wanda.credit.base.domain.BaseDomain;

//审判流程-立案信息
@Entity
@Table(name = "T_DS_HF_REGISTERINFO")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class RegisterInfo extends BaseDomain{
	private static final long serialVersionUID = 1L;
	private String id;
	private String trade_id;
	@Expose
	private String plaintiff;// 原告（上诉人） 1000
	@Expose
	private String defendant;// 被告（被上诉人）1000
	@Expose
	private String thirdpeople;// 第三人1000
	@Expose
	private String plaintiff2;// 原审原告1000
	@Expose
	private String defendant2;// 原审被告 1000
	@Expose
	private String thirdpeople2;// 原审第三人1000
	@Expose
	private String court;// 受理法院1000
	@Expose
	private Date time;// 立案时间 date
	@Expose
    private String anyou;// 案由 1000
	@Expose
	private String casenum;// 案号1000
	private String objection;// 异议内容
	@Expose
	private Date objectiontime;// 异议时间
	private String refId;
	private String queryType;
	
	public RegisterInfo() {
		super();
	}

	public RegisterInfo(String plaintiff, String defendant, String thirdpeople,
			String plaintiff2, String defendant2, String thirdpeople2,
			String court, Date time, String anyou, String casenum,
			String objection, Date objectiontime) {
		super();
		this.plaintiff = plaintiff;
		this.defendant = defendant;
		this.thirdpeople = thirdpeople;
		this.plaintiff2 = plaintiff2;
		this.defendant2 = defendant2;
		this.thirdpeople2 = thirdpeople2;
		this.court = court;
		this.time = time;
		this.anyou = anyou;
		this.casenum = casenum;
		this.objection = objection;
		this.objectiontime = objectiontime;
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

	
	public String getRefId() {
		return refId;
	}

	public void setRefId(String refId) {
		this.refId = refId;
	}

	public String getQueryType() {
		return queryType;
	}

	public void setQueryType(String queryType) {
		this.queryType = queryType;
	}
	
	public String getTrade_id() {
		return trade_id;
	}

	public void setTrade_id(String trade_id) {
		this.trade_id = trade_id;
	}

	public String getPlaintiff() {
		return plaintiff;
	}

	public void setPlaintiff(String plaintiff) {
		this.plaintiff = plaintiff;
	}

	public String getDefendant() {
		return defendant;
	}

	public void setDefendant(String defendant) {
		this.defendant = defendant;
	}

	public String getThirdpeople() {
		return thirdpeople;
	}

	public void setThirdpeople(String thirdpeople) {
		this.thirdpeople = thirdpeople;
	}

	public String getPlaintiff2() {
		return plaintiff2;
	}

	public void setPlaintiff2(String plaintiff2) {
		this.plaintiff2 = plaintiff2;
	}

	public String getDefendant2() {
		return defendant2;
	}

	public void setDefendant2(String defendant2) {
		this.defendant2 = defendant2;
	}

	public String getThirdpeople2() {
		return thirdpeople2;
	}

	public void setThirdpeople2(String thirdpeople2) {
		this.thirdpeople2 = thirdpeople2;
	}

	public String getCourt() {
		return court;
	}

	public void setCourt(String court) {
		this.court = court;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public String getAnyou() {
		return anyou;
	}

	public void setAnyou(String anyou) {
		this.anyou = anyou;
	}

	public String getCasenum() {
		return casenum;
	}

	public void setCasenum(String casenum) {
		this.casenum = casenum;
	}

	public String getObjection() {
		return objection;
	}

	public void setObjection(String objection) {
		this.objection = objection;
	}

	public Date getObjectiontime() {
		return objectiontime;
	}

	public void setObjectiontime(Date objectiontime) {
		this.objectiontime = objectiontime;
	}

	@Override
	public String toString() {
		return "RegisterInfo [plaintiff=" + plaintiff + ", defendant="
				+ defendant + ", thirdpeople=" + thirdpeople + ", plaintiff2="
				+ plaintiff2 + ", defendant2=" + defendant2 + ", thirdpeople2="
				+ thirdpeople2 + ", court=" + court + ", time=" + time
				+ ", anyou=" + anyou + ", casenum=" + casenum + ", objection="
				+ objection + ", objectiontime=" + objectiontime
				+ ", queryType=" + queryType + "]";
	}
}
