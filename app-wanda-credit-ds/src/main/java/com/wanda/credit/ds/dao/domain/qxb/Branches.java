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
@Table(name = "T_DS_QXB_Branches")
@SequenceGenerator(name = "Seq_T_DS_QXB_Branches", sequenceName = "Seq_T_DS_QXB_Branches")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer","id" })
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Branches extends QxbBaseDomain {
	private static final long serialVersionUID = 1L;
	
	private long id_no;
	private String name;

	public Branches() {
		super();
	}

	/**
	 * 获取 主键
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Seq_T_DS_QXB_Branches")
	@Column(name = "ID", unique = true, nullable = false)
	public long getId_no() {
		return id_no;
	}


	public void setId_no(long id_no) {
		this.id_no = id_no;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
