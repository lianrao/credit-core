package com.wanda.credit.ds.dao.domain.yidao;

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
 * 银行卡信息
 * 
 * @author shenziqiang
 *
 */

@Entity
@Table(name = "T_DS_YIDAO_BANK_RESULT")
@SequenceGenerator(name = "SEQ_T_DS_YIDAO_BANK_RESULT", sequenceName = "SEQ_T_DS_YIDAO_BANK_RESULT")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Yidao_bank_result extends BaseDomain {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long id;
	private String trade_id;
	private String recotype;
	private String req_image;
	private String error;
	private String details;
	private String bankname;
	private String cardname;
	private String cardtype;
	private String cardno;
	private String expmonth;
	private String expyear;
	private String cropped_image;
	
	/**
	 * 获取 主键
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_T_DS_YIDAO_BANK_RESULT")
	@Column(name = "ID", unique = true, nullable = false)
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getTrade_id() {
		return trade_id;
	}
	public void setTrade_id(String trade_id) {
		this.trade_id = trade_id;
	}
	public String getRecotype() {
		return recotype;
	}
	public void setRecotype(String recotype) {
		this.recotype = recotype;
	}
	public String getReq_image() {
		return req_image;
	}
	public void setReq_image(String req_image) {
		this.req_image = req_image;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public String getDetails() {
		return details;
	}
	public void setDetails(String details) {
		this.details = details;
	}
	public String getBankname() {
		return bankname;
	}
	public void setBankname(String bankname) {
		this.bankname = bankname;
	}
	public String getCardname() {
		return cardname;
	}
	public void setCardname(String cardname) {
		this.cardname = cardname;
	}
	public String getCardtype() {
		return cardtype;
	}
	public void setCardtype(String cardtype) {
		this.cardtype = cardtype;
	}
	public String getCardno() {
		return cardno;
	}
	public void setCardno(String cardno) {
		this.cardno = cardno;
	}
	public String getExpmonth() {
		return expmonth;
	}
	public void setExpmonth(String expmonth) {
		this.expmonth = expmonth;
	}
	public String getExpyear() {
		return expyear;
	}
	public void setExpyear(String expyear) {
		this.expyear = expyear;
	}
	public String getCropped_image() {
		return cropped_image;
	}
	public void setCropped_image(String cropped_image) {
		this.cropped_image = cropped_image;
	}
}
