package com.wanda.credit.ds.dao.domain.yixin;

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
@Table(name = "T_DS_YX_Loanoverdue")
@SequenceGenerator(name="Seq_T_DS_YX_Loanoverdue",sequenceName="Seq_T_DS_YX_Loanoverdue")  
@JsonIgnoreProperties(value = { "hibernateLazyInitializer" })
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class YXLoanoverdueVO extends YXCommonDomain {

	private static final long serialVersionUID = 1L;
	private long id;
	private String overdueTimes;
	private String overdueTimes90;
	private String overdueTimes180;	

	

	public YXLoanoverdueVO() {
		super();
	}

	/**
	 * 获取 主键
	 */
	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="Seq_T_DS_YX_Loanoverdue")  
	@Column(name = "ID", unique = true, nullable = false)
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getOverdueTimes() {
		return overdueTimes;
	}

	public void setOverdueTimes(String overdueTimes) {
		this.overdueTimes = overdueTimes;
	}

	public String getOverdueTimes90() {
		return overdueTimes90;
	}

	public void setOverdueTimes90(String overdueTimes90) {
		this.overdueTimes90 = overdueTimes90;
	}

	public String getOverdueTimes180() {
		return overdueTimes180;
	}

	public void setOverdueTimes180(String overdueTimes180) {
		this.overdueTimes180 = overdueTimes180;
	}

}
