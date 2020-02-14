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
@Table(name = "T_DS_QXB_Abnormal_items")
@SequenceGenerator(name = "SEQ_T_DS_QXB_Abnormal_items", sequenceName = "SEQ_T_DS_QXB_Abnormal_items")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer" })
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Abnormal_items extends QxbBaseDomain {
	private static final long serialVersionUID = 1L;
	
	private long id;
	private String in_reason;
	private String in_date;
	private String out_reason;
	private String out_date;


	public Abnormal_items() {
		super();
	}

	/**
	 * 获取 主键
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_T_DS_QXB_Abnormal_items")
	@Column(name = "ID", unique = true, nullable = false)
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getIn_reason() {
		return in_reason;
	}

	public void setIn_reason(String in_reason) {
		this.in_reason = in_reason;
	}

	public String getIn_date() {
		return in_date;
	}

	public void setIn_date(String in_date) {
		this.in_date = in_date;
	}

	public String getOut_reason() {
		return out_reason;
	}

	public void setOut_reason(String out_reason) {
		this.out_reason = out_reason;
	}

	public String getOut_date() {
		return out_date;
	}

	public void setOut_date(String out_date) {
		this.out_date = out_date;
	}

}
