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

/* 错误信息表 */
@Entity
@Table(name = "T_DS_JZ_ERROR_RES")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer" })
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@XStreamAlias("errorRes")
public class Psg_Error_Res extends BaseDomain {// 建表
	private static final long serialVersionUID = -1289807774918590425L;
	private String id;
	private String err_code;// ERR_CODE
	private String err_content;// ERR_CONTENT
	private Psg_AnalyseReportMain psgmain;

	public Psg_Error_Res() {
		super();
	}

	public Psg_Error_Res(String id, String err_code, String err_content) {
		super();
		this.id = id;
		this.err_code = err_code;
		this.err_content = err_content;
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

	public String getErr_code() {
		return err_code;
	}

	public void setErr_code(String err_code) {
		this.err_code = err_code;
	}

	public String getErr_content() {
		return err_content;
	}

	public void setErr_content(String err_content) {
		this.err_content = err_content;
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
		return "Psg_Error_Res [err_code=" + this.err_code + ", err_content=" + this.err_content + "]";
	}
}
