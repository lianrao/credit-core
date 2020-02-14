package com.wanda.credit.ds.dao.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.annotations.GenericGenerator;

import com.wanda.credit.base.domain.BaseDomain;

@Entity
@Table(name = "T_DS_NCIIC_MULT_RESULT",schema="CPDB_DS")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Nciic_Mult_Check_Result extends BaseDomain{
	/**
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 主键
	 */
	 private String id          ;
	 private String trade_id    ;
	 private String cardno      ;
	 private String name        ;
	 private String card_check  ;
	 private String name_check  ;
	 private String sex         ;
	 private String birth_day   ;
	 private String address     ;
	 private String city        ;
	 private String education   ;
	 private String wsdn        ;
	 private String wsdes       ;
	 private String error_mesg  ;
	 private String status      ;
	 private String image_file  ;

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
	public String getCard_check() {
		return card_check;
	}
	public void setCard_check(String card_check) {
		this.card_check = card_check;
	}
	public String getName_check() {
		return name_check;
	}
	public void setName_check(String name_check) {
		this.name_check = name_check;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getBirth_day() {
		return birth_day;
	}
	public void setBirth_day(String birth_day) {
		this.birth_day = birth_day;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getEducation() {
		return education;
	}
	public void setEducation(String education) {
		this.education = education;
	}
	public String getWsdn() {
		return wsdn;
	}
	public void setWsdn(String wsdn) {
		this.wsdn = wsdn;
	}
	public String getWsdes() {
		return wsdes;
	}
	public void setWsdes(String wsdes) {
		this.wsdes = wsdes;
	}
	public String getError_mesg() {
		return error_mesg;
	}
	public void setError_mesg(String error_mesg) {
		this.error_mesg = error_mesg;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getImage_file() {
		return image_file;
	}
	public void setImage_file(String image_file) {
		this.image_file = image_file;
	}
}
