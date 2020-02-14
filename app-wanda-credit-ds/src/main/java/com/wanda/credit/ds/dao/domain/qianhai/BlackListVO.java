package com.wanda.credit.ds.dao.domain.qianhai;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.annotations.GenericGenerator;

import com.wanda.credit.base.domain.BaseDomain;

/**
 * 前海 黑名单 信息
 **/
@Entity
@Table(name = "T_DS_QH_BLACKLIST")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer" })
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class BlackListVO extends BaseDomain {

	private static final long serialVersionUID = 1L;

	private String id;
	private String resultId;
	private String idNo;
	private String idType;
	private String name;
	private String seqNo;
	private String sourceId;
	private String gradeQuery;
	private String moneyBound;
	private String dataBuild_time;
	private String dataStatus;
	private String reserved_filed1;
	private String reserved_filed2;
	private String reserved_filed3;
	private String reserved_filed4;
	private String reserved_filed5;
	private String state;
	private String batchNo;
	private String ercode;
	private String ermsg;

	public BlackListVO() {
		super();
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



	public String getResultId() {
		return resultId;
	}



	public void setResultId(String resultId) {
		this.resultId = resultId;
	}



	public String getIdNo() {
		return idNo;
	}



	public void setIdNo(String idNo) {
		this.idNo = idNo;
	}



	public String getIdType() {
		return idType;
	}



	public void setIdType(String idType) {
		this.idType = idType;
	}



	public String getName() {
		return name;
	}



	public void setName(String name) {
		this.name = name;
	}



	public String getSeqNo() {
		return seqNo;
	}



	public void setSeqNo(String seqNo) {
		this.seqNo = seqNo;
	}



	public String getSourceId() {
		return sourceId;
	}



	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}



	public String getGradeQuery() {
		return gradeQuery;
	}



	public void setGradeQuery(String gradeQuery) {
		this.gradeQuery = gradeQuery;
	}



	public String getMoneyBound() {
		return moneyBound;
	}



	public void setMoneyBound(String moneyBound) {
		this.moneyBound = moneyBound;
	}



	public String getDataBuild_time() {
		return dataBuild_time;
	}



	public void setDataBuild_time(String dataBuild_time) {
		this.dataBuild_time = dataBuild_time;
	}



	public String getDataStatus() {
		return dataStatus;
	}



	public void setDataStatus(String dataStatus) {
		this.dataStatus = dataStatus;
	}



	public String getReserved_filed1() {
		return reserved_filed1;
	}



	public void setReserved_filed1(String reserved_filed1) {
		this.reserved_filed1 = reserved_filed1;
	}



	public String getReserved_filed2() {
		return reserved_filed2;
	}



	public void setReserved_filed2(String reserved_filed2) {
		this.reserved_filed2 = reserved_filed2;
	}



	public String getReserved_filed3() {
		return reserved_filed3;
	}



	public void setReserved_filed3(String reserved_filed3) {
		this.reserved_filed3 = reserved_filed3;
	}



	public String getReserved_filed4() {
		return reserved_filed4;
	}



	public void setReserved_filed4(String reserved_filed4) {
		this.reserved_filed4 = reserved_filed4;
	}



	public String getReserved_filed5() {
		return reserved_filed5;
	}



	public void setReserved_filed5(String reserved_filed5) {
		this.reserved_filed5 = reserved_filed5;
	}



	public String getState() {
		return state;
	}



	public void setState(String state) {
		this.state = state;
	}



	public String getErcode() {
		return ercode;
	}



	public void setErcode(String ercode) {
		this.ercode = ercode;
	}



	public String getErmsg() {
		return ermsg;
	}



	public void setErmsg(String ermsg) {
		this.ermsg = ermsg;
	}

	public String getBatchNo() {
		return batchNo;
	}

	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}
}
