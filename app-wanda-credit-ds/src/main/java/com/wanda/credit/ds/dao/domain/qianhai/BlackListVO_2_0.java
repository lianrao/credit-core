package com.wanda.credit.ds.dao.domain.qianhai;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.wanda.credit.base.domain.BaseDomain;

/**
 * 前海 黑名单 信息
 **/
@Entity
@Table(name = "T_DS_QH_RISKLIST")
@SequenceGenerator(name = "SEQ_T_DS_QH_RISKLIST", sequenceName = "SEQ_T_DS_QH_RISKLIST")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer" })
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class BlackListVO_2_0 extends BaseDomain {
	private static final long serialVersionUID = 1L;
	
	private long id;
	private String trade_id;
	private String cardNo;//身份证号
	private String cardIds;//输入的卡号集
	private String moblieNos;//输入的手机号码
	private String ips;//输入的ip集
	private String idType;
	private String name;
	private String sourceId;
	private String rskScore;
	private String rskMark;
	private String dataBuildTime;
	private String dataStatus;

	public BlackListVO_2_0() {
		super();
	}

	/**
	 * 获取 主键
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_T_DS_QH_RISKLIST")
	@Column(name = "ID", unique = true, nullable = false)
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
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

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public String getRskScore() {
		return rskScore;
	}

	public void setRskScore(String rskScore) {
		this.rskScore = rskScore;
	}

	public String getRskMark() {
		return rskMark;
	}

	public void setRskMark(String rskMark) {
		this.rskMark = rskMark;
	}

	public String getDataBuildTime() {
		return dataBuildTime;
	}

	public void setDataBuildTime(String dataBuildTime) {
		this.dataBuildTime = dataBuildTime;
	}

	public String getDataStatus() {
		return dataStatus;
	}

	public void setDataStatus(String dataStatus) {
		this.dataStatus = dataStatus;
	}

	public String getTrade_id() {
		return trade_id;
	}

	public void setTrade_id(String trade_id) {
		this.trade_id = trade_id;
	}

	public String getCardNo() {
		return cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	public String getCardIds() {
		return cardIds;
	}

	public void setCardIds(String cardIds) {
		this.cardIds = cardIds;
	}

	public String getMoblieNos() {
		return moblieNos;
	}

	public void setMoblieNos(String moblieNos) {
		this.moblieNos = moblieNos;
	}

	public String getIps() {
		return ips;
	}

	public void setIps(String ips) {
		this.ips = ips;
	}

}
