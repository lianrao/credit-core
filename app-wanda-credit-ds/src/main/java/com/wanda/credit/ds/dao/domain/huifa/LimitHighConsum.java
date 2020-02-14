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
//执行信息-执行惩戒-限制高消费被执行人
@Entity
@Table(name = "T_DS_HF_LIMITHIGHCONSUM")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class LimitHighConsum extends BaseDomain {
	private static final long serialVersionUID = 1L;
	private String id;
	private String trade_id;
	@Expose
	private String name;//被限制人 1000
	@Expose
	private String cidorcode;//身份证号/组织机构代码证 100
	@Expose
	private String leader;//法定代表人/负责人1000
	@Expose
	private String address;//住所地1000
	@Expose
	private String court;//执行法院1000
	@Expose
	private String casenum;//案号1000
	@Expose
	private String anyou;//案由1000
	@Expose
	private String money;//标的1000
	@Expose
	private Date time;//立案时间 date
	@Expose
	private String posttime;//发布时间1000
	private String content;//具体内容clob
	private String basic;//执行依据clob
	private String objection;//异议内容
	@Expose
	private Date objectiontime;//异议时间
	private String refId;
	private String queryType;
	
	public LimitHighConsum() {
		super();
	}

	public LimitHighConsum(String name, String cidorcode, String leader,
			String address, String court, String casenum, String anyou,
			String money, Date time, String posttime, String content,
			String basic, String objection, Date objectiontime) {
		super();
		this.name = name;
		this.cidorcode = cidorcode;
		this.leader = leader;
		this.address = address;
		this.court = court;
		this.casenum = casenum;
		this.anyou = anyou;
		this.money = money;
		this.time = time;
		this.posttime = posttime;
		this.content = content;
		this.basic = basic;
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

	public String getCidorcode() {
		return cidorcode;
	}

	public void setCidorcode(String cidorcode) {
		this.cidorcode = cidorcode;
	}

	public String getLeader() {
		return leader;
	}

	public void setLeader(String leader) {
		this.leader = leader;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
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

	public String getAnyou() {
		return anyou;
	}

	public void setAnyou(String anyou) {
		this.anyou = anyou;
	}

	public String getMoney() {
		return money;
	}

	public void setMoney(String money) {
		this.money = money;
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

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getBasic() {
		return basic;
	}

	public void setBasic(String basic) {
		this.basic = basic;
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
		return "LimitHighConsum [name=" + name + ", cidorcode=" + cidorcode
				+ ", leader=" + leader + ", address=" + address + ", court="
				+ court + ", casenum=" + casenum + ", anyou=" + anyou
				+ ", money=" + money + ", time=" + time + ", posttime="
				+ posttime + ", content=" + content + ", basic=" + basic
				+ ", objection=" + objection + ", objectiontime="
				+ objectiontime + ", queryType=" + queryType + "]";
	}
}
