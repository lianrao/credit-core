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
@XStreamAlias("errorResIncache")
public class Psg_Error_Res_Incache extends BaseDomain {// 建表
	private static final long serialVersionUID = -1289807774918590425L;
	private String id;
	private String Err_code;// ERR_CODE
	private String Err_content;// ERR_CONTENT
	private Psg_IncacheMain psgincache;
	
	public Psg_Error_Res_Incache() {
		super();
	}

	public Psg_Error_Res_Incache(String id, String Err_code, String Err_content,Psg_IncacheMain psgincache) {
		super();
		this.id = id;
		this.Err_code = Err_code;
		this.Err_content = Err_content;
		this.psgincache = psgincache;
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
		return Err_code;
	}

	public void setErr_code(String Err_code) {
		this.Err_code = Err_code;
	}

	public String getErr_content() {
		return Err_content;
	}

	public void setErr_content(String Err_content) {
		this.Err_content = Err_content;
	}
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OWNERID")
	public Psg_IncacheMain getPsgincache() {
		return psgincache;
	}

	public void setPsgincache(Psg_IncacheMain psgincache) {
		this.psgincache = psgincache;
	}

	@Override
	public String toString() {
		return "Psg_Error_Res [err_code=" + this.Err_code + ", err_content=" + this.Err_content + "]";
	}
}
