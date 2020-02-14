package com.wanda.credit.ds.dao.domain;

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

@Entity
@Table(name = "T_DS_BR_AuthenBankCard")
@SequenceGenerator(name="Seq_T_DS_BR_AuthenBankCard",sequenceName="SEQ_T_DS_XY_AUTHENBANKCARD",allocationSize=1)  
@JsonIgnoreProperties(value = { "hibernateLazyInitializer"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Bairong_AuthenBankCard extends BaseDomain{
	/**
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 主键
	 */
	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="Seq_T_DS_BR_AuthenBankCard")  
	@Column(name = "ID", unique = true, nullable = false)
	private Long id ;
	private String trade_id;
	private String typeno;
	private String name;
	private String cardNo;
	private String mobile;
	private String cardId;
	private String respCode;
	private String respDesc;
	private String seq;
	private String sysCode;
	private String sysMsg;
	private String flag_bankfourpro;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getTrade_id() {
		return trade_id;
	}
	public void setTrade_id(String trade_id) {
		this.trade_id = trade_id;
	}
	public String getTypeno() {
		return typeno;
	}
	public void setTypeno(String typeno) {
		this.typeno = typeno;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCardNo() {
		return cardNo;
	}
	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getCardId() {
		return cardId;
	}
	public void setCardId(String cardId) {
		this.cardId = cardId;
	}
	public String getRespCode() {
		return respCode;
	}
	public void setRespCode(String respCode) {
		this.respCode = respCode;
	}
    
	public String getRespDesc() {
		return respDesc;
	}
	public void setRespDesc(String respDesc) {
		this.respDesc = respDesc;
	}
	public String getSeq() {
		return seq;
	}
	public void setSeq(String seq) {
		this.seq = seq;
	}
	public String getFlag_bankfourpro() {
		return flag_bankfourpro;
	}
	public void setFlag_bankfourpro(String flag_bankfourpro) {
		this.flag_bankfourpro = flag_bankfourpro;
	}
	public String getSysCode() {
		return sysCode;
	}
	public void setSysCode(String sysCode) {
		this.sysCode = sysCode;
	}
	public String getSysMsg() {
		return sysMsg;
	}
	public void setSysMsg(String sysMsg) {
		this.sysMsg = sysMsg;
	}
	
}
