package com.wanda.credit.ds.dao.domain;

import java.util.Date;

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
@Table(name = "T_DS_WANDARECO_IDCARD_OCR_F")
@SequenceGenerator(name="ID_SEQ_T_DS_WANDARECO_IDCARD_OCR_F",sequenceName="SEQ_T_DS_WANDARECO_IDCARD_O_B",allocationSize=1)  
@JsonIgnoreProperties(value = { "hibernateLazyInitializer"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class WandaReco_idcard_ocr_F extends BaseDomain{
	/**
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 主键
	 */
	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="ID_SEQ_T_DS_WANDARECO_IDCARD_OCR_F")  
	@Column(name = "ID", unique = true, nullable = false)
	private Long id ;
	private String trade_id;
	private String requst_sn;
	private String cardNo;
	private String name;
	private String gender;
	private String nation;
	private String year;
	private String month;
	private String day;
	private String address;
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
	public String getRequst_sn() {
		return requst_sn;
	}
	public void setRequst_sn(String requst_sn) {
		this.requst_sn = requst_sn;
	}
	public String getCardNo() {
		return cardNo;
	}
	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getNation() {
		return nation;
	}
	public void setNation(String nation) {
		this.nation = nation;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public String getDay() {
		return day;
	}
	public void setDay(String day) {
		this.day = day;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	
}
