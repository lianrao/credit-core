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
@Table(name = "T_DS_YX_BlackList")
@SequenceGenerator(name="Seq_T_DS_YX_BlackList",sequenceName="Seq_T_DS_YX_BlackList")  
@JsonIgnoreProperties(value = { "hibernateLazyInitializer" })
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class YXBlackListVO extends YXCommonDomain {

	private static final long serialVersionUID = 1L;
	private long id;
	private String riskItems;
	private String riskItemType;
	private String riskItemTypeCode;
	private String riskItemValue;
	private String riskType;
	private String riskTypeCode;
	private String source;
	private String sourceCode;
	private String riskTime;


	public YXBlackListVO() {
		super();
	}

	/**
	 * 获取 主键
	 */
	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="Seq_T_DS_YX_BlackList")  
	@Column(name = "ID", unique = true, nullable = false)
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getRiskItems() {
		return riskItems;
	}

	public void setRiskItems(String riskItems) {
		this.riskItems = riskItems;
	}

	public String getRiskItemType() {
		return riskItemType;
	}

	public void setRiskItemType(String riskItemType) {
		this.riskItemType = riskItemType;
	}

	public String getRiskItemTypeCode() {
		return riskItemTypeCode;
	}

	public void setRiskItemTypeCode(String riskItemTypeCode) {
		this.riskItemTypeCode = riskItemTypeCode;
	}

	public String getRiskItemValue() {
		return riskItemValue;
	}

	public void setRiskItemValue(String riskItemValue) {
		this.riskItemValue = riskItemValue;
	}

	public String getRiskType() {
		return riskType;
	}

	public void setRiskType(String riskType) {
		this.riskType = riskType;
	}

	public String getRiskTypeCode() {
		return riskTypeCode;
	}

	public void setRiskTypeCode(String riskTypeCode) {
		this.riskTypeCode = riskTypeCode;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getSourceCode() {
		return sourceCode;
	}

	public void setSourceCode(String sourceCode) {
		this.sourceCode = sourceCode;
	}

	public String getRiskTime() {
		return riskTime;
	}

	public void setRiskTime(String riskTime) {
		this.riskTime = riskTime;
	}


}
