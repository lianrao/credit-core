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
@Table(name = "T_DS_XY_AUTHENBANKCARD")
@SequenceGenerator(name="ID_SEQ_T_DS_XY_AUTHENBANKCARD",sequenceName="SEQ_T_DS_XY_AUTHENBANKCARD",allocationSize=1)  
@JsonIgnoreProperties(value = { "hibernateLazyInitializer"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class XYanAuthenBankCard extends BaseDomain{
	/**
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 主键
	 */
	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="ID_SEQ_T_DS_XY_AUTHENBANKCARD")  
	@Column(name = "ID", unique = true, nullable = false)
	private Long id ;
	private String trade_id;
	private String typeno;
	private String cardno;
	private String name;
	private String mobile;
	private String cardid;
	private String card_type;
	private String valid_date_year;
	private String valid_date_month;
	private String valid_cvv2no;
	private String ret_code;
	private String ret_desc;
	private String ret_trade_no;
	private String ret_fee;
	private String bank_id;
	private String bank_desc;
	private String org_desc;
	private String org_code;
	public String getOrg_desc() {
		return org_desc;
	}
	public void setOrg_desc(String org_desc) {
		this.org_desc = org_desc;
	}
	public String getOrg_code() {
		return org_code;
	}
	public void setOrg_code(String org_code) {
		this.org_code = org_code;
	}
	public String getBank_id() {
		return bank_id;
	}
	public void setBank_id(String bank_id) {
		this.bank_id = bank_id;
	}
	public String getBank_desc() {
		return bank_desc;
	}
	public void setBank_desc(String bank_desc) {
		this.bank_desc = bank_desc;
	}
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
	public String getCardno() {
		return cardno;
	}
	public void setCardno(String cardno) {
		this.cardno = cardno;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getCardid() {
		return cardid;
	}
	public void setCardid(String cardid) {
		this.cardid = cardid;
	}
	public String getCard_type() {
		return card_type;
	}
	public void setCard_type(String card_type) {
		this.card_type = card_type;
	}
	public String getValid_date_year() {
		return valid_date_year;
	}
	public void setValid_date_year(String valid_date_year) {
		this.valid_date_year = valid_date_year;
	}
	public String getValid_date_month() {
		return valid_date_month;
	}
	public void setValid_date_month(String valid_date_month) {
		this.valid_date_month = valid_date_month;
	}
	public String getValid_cvv2no() {
		return valid_cvv2no;
	}
	public void setValid_cvv2no(String valid_cvv2no) {
		this.valid_cvv2no = valid_cvv2no;
	}
	public String getRet_code() {
		return ret_code;
	}
	public void setRet_code(String ret_code) {
		this.ret_code = ret_code;
	}
	public String getRet_desc() {
		return ret_desc;
	}
	public void setRet_desc(String ret_desc) {
		this.ret_desc = ret_desc;
	}
	public String getRet_trade_no() {
		return ret_trade_no;
	}
	public void setRet_trade_no(String ret_trade_no) {
		this.ret_trade_no = ret_trade_no;
	}
	public String getRet_fee() {
		return ret_fee;
	}
	public void setRet_fee(String ret_fee) {
		this.ret_fee = ret_fee;
	}
	
}
