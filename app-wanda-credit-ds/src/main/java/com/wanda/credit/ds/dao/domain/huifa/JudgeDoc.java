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

//判决文书
@Entity
@Table(name = "T_DS_HF_JUDGEDOC")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class JudgeDoc extends BaseDomain {
	private static final long serialVersionUID = 1L;
	private String id;
	private String trade_id;
	@Expose
	private String title;// 标题  500
	@Expose
	private String anyou;// 案由  1000
	@Expose
	private String name;// 当事人名称  1000
	@Expose
	private String cidorcode;// 身份证号/组织机构代码证 100
	@Expose
	private String address;// 当事人住址  1000
	@Expose
	private String type;// 诉讼地位  1000
	@Expose
	private String leader;// 法定代表人/负责人  1000
	@Expose
	private String trialprocedure;// 审理程序  1000
	@Expose
	private String court;// 审理法院  1000
	@Expose
	private String casenum;// 文书字号  1000
	@Expose
	private String casetype;// 文书类型  1000
	@Expose
	private Date time;// 审结日期  date
	@Expose
	private String contenthref;// 文书内容  1000
	@Expose
	private String pdfhref;// PDF文件  1000
	@Expose
	private String ownfile;// 源文件  1000
	private String objection;// 异议内容
	@Expose
	private Date objectiontime;// 异议时间
	private String refId;
	private String queryType;
	
	public JudgeDoc() {
		super();
	}

	public JudgeDoc(String title, String anyou, String name, String cidorcode,
			String address, String type, String leader, String trialprocedure,
			String court, String casenum, String casetype, Date time,
			String contenthref, String pdfhref, String ownfile,
			String objection, Date objectiontime) {
		super();
		this.title = title;
		this.anyou = anyou;
		this.name = name;
		this.cidorcode = cidorcode;
		this.address = address;
		this.type = type;
		this.leader = leader;
		this.trialprocedure = trialprocedure;
		this.court = court;
		this.casenum = casenum;
		this.casetype = casetype;
		this.time = time;
		this.contenthref = contenthref;
		this.pdfhref = pdfhref;
		this.ownfile = ownfile;
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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAnyou() {
		return anyou;
	}

	public void setAnyou(String anyou) {
		this.anyou = anyou;
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

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getLeader() {
		return leader;
	}

	public void setLeader(String leader) {
		this.leader = leader;
	}

	public String getTrialprocedure() {
		return trialprocedure;
	}

	public void setTrialprocedure(String trialprocedure) {
		this.trialprocedure = trialprocedure;
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

	public String getCasetype() {
		return casetype;
	}

	public void setCasetype(String casetype) {
		this.casetype = casetype;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public String getContenthref() {
		return contenthref;
	}

	public void setContenthref(String contenthref) {
		this.contenthref = contenthref;
	}

	public String getPdfhref() {
		return pdfhref;
	}

	public void setPdfhref(String pdfhref) {
		this.pdfhref = pdfhref;
	}

	public String getOwnfile() {
		return ownfile;
	}

	public void setOwnfile(String ownfile) {
		this.ownfile = ownfile;
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
		return "JudgeDoc [title=" + title + ", anyou=" + anyou + ", name="
				+ name + ", cidorcode=" + cidorcode + ", address=" + address
				+ ", type=" + type + ", leader=" + leader + ", trialprocedure="
				+ trialprocedure + ", court=" + court + ", casenum=" + casenum
				+ ", casetype=" + casetype + ", time=" + time
				+ ", contenthref=" + contenthref + ", pdfhref=" + pdfhref
				+ ", ownfile=" + ownfile + ", objection=" + objection
				+ ", objectiontime=" + objectiontime + ", queryType="
				+ queryType + "]";
	}
}
