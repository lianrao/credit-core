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

//审判流程-送达信息-通知公告
@Entity
@Table(name = "T_DS_HF_NOTICE")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Notice extends BaseDomain {
	private static final long serialVersionUID = 1L;
	private String id;
	private String trade_id;
	@Expose
	private String title;// 标题 1000
	@Expose
	private String type;// 送达类型 1000
	@Expose
	private String name;// 当事人名称 1000
	@Expose
	private String cidorcode;// 身份证号、组织机构代码 100
	private String content;// 送达内容 clob
	@Expose
	private String court;// 送达法院1000
	@Expose
	private String media;// 刊登媒体1000
	@Expose
	private Date time;// 刊登日期 date
	@Expose
	private String banmian;// 刊登版面 1000
	private String objection;// 异议内容
	@Expose
	private Date objectiontime;// 异议时间
	private String refId;
	private String queryType;
	
	public Notice() {
		super();
	}

	public Notice(String title, String type, String name, String cidorcode,
			String content, String court, String media, Date time,
			String banmian, String objection, Date objectiontime) {
		super();
		this.title = title;
		this.type = type;
		this.name = name;
		this.cidorcode = cidorcode;
		this.content = content;
		this.court = court;
		this.media = media;
		this.time = time;
		this.banmian = banmian;
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getCourt() {
		return court;
	}

	public void setCourt(String court) {
		this.court = court;
	}

	public String getMedia() {
		return media;
	}

	public void setMedia(String media) {
		this.media = media;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public String getBanmian() {
		return banmian;
	}

	public void setBanmian(String banmian) {
		this.banmian = banmian;
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
		return "Notice [title=" + title + ", type=" + type + ", name=" + name
				+ ", cidorcode=" + cidorcode + ", content=" + content
				+ ", court=" + court + ", media=" + media + ", time=" + time
				+ ", banmian=" + banmian + ", objection=" + objection
				+ ", objectiontime=" + objectiontime + ", queryType="
				+ queryType + "]";
	}
}
