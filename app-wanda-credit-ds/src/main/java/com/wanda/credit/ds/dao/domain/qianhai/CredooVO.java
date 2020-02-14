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
 * 前海 好信度信息
 * */
@Entity
@Table(name = "T_DS_QH_CREDOO")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer" })
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class CredooVO extends BaseDomain {
	/**
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 主键
	 */
	private String id;
	private String resultId;
	private String idNo;
	private String idType;
	private String name;
	private String mobileNo;
	private String cardId;
	private String seqNo;
	private String sourceId;
	private String credoo_score;
	private String bseinfo_score;
	private String finRequire_score;
	private String payAbility_score;
	private String perform_score;
	private String action_score;
	private String virAsset_score;
	private String trend_score;
	private String dataBuild_time;
	private String batchNo;
	private String erCode;
	private String erMsg;

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

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
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

	public String getCredoo_score() {
		return credoo_score;
	}

	public void setCredoo_score(String credoo_score) {
		this.credoo_score = credoo_score;
	}

	public String getBseinfo_score() {
		return bseinfo_score;
	}

	public void setBseinfo_score(String bseinfo_score) {
		this.bseinfo_score = bseinfo_score;
	}

	public String getFinRequire_score() {
		return finRequire_score;
	}

	public void setFinRequire_score(String finRequire_score) {
		this.finRequire_score = finRequire_score;
	}

	public String getPayAbility_score() {
		return payAbility_score;
	}

	public void setPayAbility_score(String payAbility_score) {
		this.payAbility_score = payAbility_score;
	}

	public String getPerform_score() {
		return perform_score;
	}

	public void setPerform_score(String perform_score) {
		this.perform_score = perform_score;
	}

	public String getAction_score() {
		return action_score;
	}

	public void setAction_score(String action_score) {
		this.action_score = action_score;
	}

	public String getVirAsset_score() {
		return virAsset_score;
	}

	public void setVirAsset_score(String virAsset_score) {
		this.virAsset_score = virAsset_score;
	}

	public String getTrend_score() {
		return trend_score;
	}

	public void setTrend_score(String trend_score) {
		this.trend_score = trend_score;
	}

	public String getDataBuild_time() {
		return dataBuild_time;
	}

	public void setDataBuild_time(String dataBuild_time) {
		this.dataBuild_time = dataBuild_time;
	}

	public String getErCode() {
		return erCode;
	}

	public void setErCode(String erCode) {
		this.erCode = erCode;
	}

	public String getErMsg() {
		return erMsg;
	}

	public void setErMsg(String erMsg) {
		this.erMsg = erMsg;
	}

	public String getBatchNo() {
		return batchNo;
	}

	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}

	public String getCardId() {
		return cardId;
	}

	public void setCardId(String cardId) {
		this.cardId = cardId;
	}
}
