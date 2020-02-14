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
 * 身份证信息
 */
@Entity
@Table(name = "T_DS_YIDAO_IDNO_RESULT")
@SequenceGenerator(name = "SEQ_T_DS_YIDAO_IDNO_RESULT", sequenceName = "SEQ_T_DS_YIDAO_IDNO_RESULT")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Yidao_Idcard_result extends BaseDomain {
	private static final long serialVersionUID = 1L;
	 private long id  ;
	 private String trade_id ;
	 private String idno ;
	 private String name ;
	 private String recotype ;
	 private String req_image ;
	 private String error ;
	 private String details;
	 private String nation ;
	 private String gender ;
	 private String birthdate;
	 private String cropped_image;
	 private String address;
	 private String issuedby;
	 private String validthru;
	 private String head_portrait;
	
	/**
	 * 获取 主键
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_T_DS_YIDAO_IDNO_RESULT")
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

	public String getIdno() {
		return idno;
	}

	public void setIdno(String idno) {
		this.idno = idno;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public String getNation() {
		return nation;
	}

	public void setNation(String nation) {
		this.nation = nation;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getBirthdate() {
		return birthdate;
	}

	public void setBirthdate(String birthdate) {
		this.birthdate = birthdate;
	}

	public String getCropped_image() {
		return cropped_image;
	}

	public void setCropped_image(String cropped_image) {
		this.cropped_image = cropped_image;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getIssuedby() {
		return issuedby;
	}

	public void setIssuedby(String issuedby) {
		this.issuedby = issuedby;
	}

	public String getValidthru() {
		return validthru;
	}

	public void setValidthru(String validthru) {
		this.validthru = validthru;
	}

	public String getHead_portrait() {
		return head_portrait;
	}

	public void setHead_portrait(String head_portrait) {
		this.head_portrait = head_portrait;
	}

}
