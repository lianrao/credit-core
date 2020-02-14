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
@Table(name = "T_DS_QXB_Partners")
@SequenceGenerator(name = "SEQ_T_DS_QXB_Partners", sequenceName = "SEQ_T_DS_QXB_Partners")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer" })
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Partners extends QxbBaseDomain {
	private static final long serialVersionUID = 1L;
	
	private long id;
	private String name ;
	private String  stock_type ;
	private String  identify_type ;
	private String  identify_no  ;
	
	public Partners() {
		super();
	}

	/**
	 * 获取 主键
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_T_DS_QXB_Partners")
	@Column(name = "ID", unique = true, nullable = false)
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStock_type() {
		return stock_type;
	}

	public void setStock_type(String stock_type) {
		this.stock_type = stock_type;
	}

	public String getIdentify_type() {
		return identify_type;
	}

	public void setIdentify_type(String identify_type) {
		this.identify_type = identify_type;
	}

	public String getIdentify_no() {
		return identify_no;
	}

	public void setIdentify_no(String identify_no) {
		this.identify_no = identify_no;
	}

}
