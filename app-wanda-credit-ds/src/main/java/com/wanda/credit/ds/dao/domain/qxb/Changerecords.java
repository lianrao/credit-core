package com.wanda.credit.ds.dao.domain.qxb;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * 前海 黑名单 信息
 **/
@Entity
@Table(name = "T_DS_QXB_Changerecords")
@SequenceGenerator(name = "SEQ_T_DS_QXB_Changerecords", sequenceName = "SEQ_T_DS_QXB_Changerecords")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer" })
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Changerecords extends QxbBaseDomain {
	private static final long serialVersionUID = 1L;
	
	private long id;
	private String change_item;
	private String change_date;
	private String before_content;
	private String after_content;

	public Changerecords() {
		super();
	}

	/**
	 * 获取 主键
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_T_DS_QXB_Changerecords")
	@Column(name = "ID", unique = true, nullable = false)
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getChange_item() {
		return change_item;
	}

	public void setChange_item(String change_item) {
		this.change_item = change_item;
	}

	public String getChange_date() {
		return change_date;
	}

	public void setChange_date(String change_date) {
		this.change_date = change_date;
	}

	public String getBefore_content() {
		return before_content;
	}

	public void setBefore_content(String before_content) {
		this.before_content = before_content;
	}

	public String getAfter_content() {
		return after_content;
	}

	public void setAfter_content(String after_content) {
		this.after_content = after_content;
	}

}
