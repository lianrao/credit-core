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
 * 驾驶证信息
 * 
 * @author shenziqiang
 *
 */

@Entity
@Table(name = "T_DS_YIDAO_DRIVER_RESULT")
@SequenceGenerator(name = "SEQ_T_DS_YIDAO_DRIVER_RESULT", sequenceName = "SEQ_T_DS_YIDAO_DRIVER_RESULT")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Yidao_driver_result extends BaseDomain {
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
	private String name;
	private String nation;
	private String gender;
	private String cardno;
	private String address;
	private String birthdate;
	private String issuedate;
	private String driverclass;
	private String validdate;

	private String cropped_image;

	/**
	 * 获取 主键
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_T_DS_YIDAO_DRIVER_RESULT")
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public String getCardno() {
		return cardno;
	}

	public void setCardno(String cardno) {
		this.cardno = cardno;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getBirthdate() {
		return birthdate;
	}

	public void setBirthdate(String birthdate) {
		this.birthdate = birthdate;
	}

	public String getIssuedate() {
		return issuedate;
	}

	public void setIssuedate(String issuedate) {
		this.issuedate = issuedate;
	}

	public String getDriverclass() {
		return driverclass;
	}

	public void setDriverclass(String driverclass) {
		this.driverclass = driverclass;
	}

	public String getValiddate() {
		return validdate;
	}

	public void setValiddate(String validdate) {
		this.validdate = validdate;
	}

	public String getCropped_image() {
		return cropped_image;
	}

	public void setCropped_image(String cropped_image) {
		this.cropped_image = cropped_image;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
