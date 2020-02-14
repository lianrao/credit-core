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

//执行信息-失信老赖-老赖信息
@Entity
@Table(name = "T_DS_HF_OLDLAIINFO")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class OldLaiInfo extends BaseDomain {
	private static final long serialVersionUID = 1L;
	private String id;
	private String trade_id;
	@Expose
	private String name;// 被执行人  1000
	@Expose
	private String leader;// 法定代表人 1000
	@Expose
	private String imgurl;// 头像/照片 1000
	@Expose
	private String cidorcode;// 组织机构代码证/身份证 100
	@Expose
	private String address;// 住址 1000
	@Expose
	private String money;// 执行标的/未履行标的 1000
	@Expose
	private String court;// 执行法院 1000
	@Expose
	private String casenum;// 执行案号 1000
	@Expose
	private Date time;// 立案时间 date
	@Expose
	private String posttime;// 公布时间 1000
	@Expose
	private String basic;// 执行依据文号 1000
	private String situation;// 失信情形 clob
	private String obligation;// 生效文书确定的义务 clob
	private String objection;// 异议内容
	@Expose
	private Date objectiontime;// 异议时间
	private String refId;
	private String queryType;
	
	public OldLaiInfo() {
		super();
	}

	public OldLaiInfo(String name, String leader, String imgurl,
			String cidorcode, String address, String money, String court,
			String casenum, Date time, String posttime, String basic,
			String situation, String obligation, String objection,
			Date objectiontime) {
		super();
		this.name = name;
		this.leader = leader;
		this.imgurl = imgurl;
		this.cidorcode = cidorcode;
		this.address = address;
		this.money = money;
		this.court = court;
		this.casenum = casenum;
		this.time = time;
		this.posttime = posttime;
		this.basic = basic;
		this.situation = situation;
		this.obligation = obligation;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLeader() {
		return leader;
	}

	public void setLeader(String leader) {
		this.leader = leader;
	}

	public String getImgurl() {
		return imgurl;
	}

	public void setImgurl(String imgurl) {
		this.imgurl = imgurl;
	}

	public String getCidorcode() {
		return cidorcode;
	}

	public void setCidorcode(String cidorcode) {
		this.cidorcode = cidorcode;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getMoney() {
		return money;
	}

	public void setMoney(String money) {
		this.money = money;
	}

	public String getCourt() {
		return court;
	}

	public void setCourt(String court) {
		this.court = court;
	}

	public String getCasenum() {
		return casenum;
	}

	public void setCasenum(String casenum) {
		this.casenum = casenum;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public String getPosttime() {
		return posttime;
	}

	public void setPosttime(String posttime) {
		this.posttime = posttime;
	}

	public String getBasic() {
		return basic;
	}

	public void setBasic(String basic) {
		this.basic = basic;
	}

	public String getSituation() {
		return situation;
	}

	public void setSituation(String situation) {
		this.situation = situation;
	}

	public String getObligation() {
		return obligation;
	}

	public void setObligation(String obligation) {
		this.obligation = obligation;
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
		return "OldLaiInfo [name=" + name + ", leader=" + leader + ", imgurl="
				+ imgurl + ", cidorcode=" + cidorcode + ", address=" + address
				+ ", money=" + money + ", court=" + court + ", casenum="
				+ casenum + ", time=" + time + ", posttime=" + posttime
				+ ", basic=" + basic + ", situation=" + situation
				+ ", obligation=" + obligation + ", objection=" + objection
				+ ", objectiontime=" + objectiontime + ", queryType="
				+ queryType + "]";
	}
}
