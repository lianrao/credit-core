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
 * 前海 常贷客 信息
 **/
@Entity
@Table(name = "T_DS_QH_Loanee")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer" })
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class LoaneeVO extends BaseDomain {
	
	private static final long serialVersionUID = 1L;

	private String id;
	private String trade_id;
	private String idNo;
	private String idType;
	private String name;
	private String seqNo;
	private String reasonCode;
	private String industry;
	private String amount;
	private String busiDate;
	private String batchNo;
	private String ercode;
	private String ermsg;
	
	private String bnkAmount;
	private String cnssAmount;
	private String p2pAmount;
	private String queryAmt;
	private String queryAmtM3;
	private String queryAmtM6;

	public String getCnssAmount() {
		return cnssAmount;
	}

	public void setCnssAmount(String cnssAmount) {
		this.cnssAmount = cnssAmount;
	}

	public String getP2pAmount() {
		return p2pAmount;
	}

	public void setP2pAmount(String p2pAmount) {
		this.p2pAmount = p2pAmount;
	}

	public String getQueryAmt() {
		return queryAmt;
	}

	public void setQueryAmt(String queryAmt) {
		this.queryAmt = queryAmt;
	}

	public String getQueryAmtM3() {
		return queryAmtM3;
	}

	public void setQueryAmtM3(String queryAmtM3) {
		this.queryAmtM3 = queryAmtM3;
	}

	public String getQueryAmtM6() {
		return queryAmtM6;
	}

	public void setQueryAmtM6(String queryAmtM6) {
		this.queryAmtM6 = queryAmtM6;
	}

	public LoaneeVO() {
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

	public String getTrade_id() {
		return trade_id;
	}

	public void setTrade_id(String trade_id) {
		this.trade_id = trade_id;
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

	public String getReasonCode() {
		return reasonCode;
	}

	public void setReasonCode(String reasonCode) {
		this.reasonCode = reasonCode;
	}

	public String getIndustry() {
		return industry;
	}

	public void setIndustry(String industry) {
		this.industry = industry;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getBusiDate() {
		return busiDate;
	}

	public void setBusiDate(String busiDate) {
		this.busiDate = busiDate;
	}

	public String getBatchNo() {
		return batchNo;
	}

	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
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

	public String getBnkAmount() {
		return bnkAmount;
	}

	public void setBnkAmount(String bnkAmount) {
		this.bnkAmount = bnkAmount;
	}

	
}
