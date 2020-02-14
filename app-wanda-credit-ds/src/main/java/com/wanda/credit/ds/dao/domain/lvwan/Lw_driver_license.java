package com.wanda.credit.ds.dao.domain.lvwan;

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
@Table(name = "T_DS_LW_DRIVING_LICENCE")
@SequenceGenerator(name = "Seq_T_DS_LW_DRIVING_LICENCE", sequenceName = "Seq_T_DS_LW_DRIVING_LICENCE")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer" })
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Lw_driver_license extends BaseDomain {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	private long id;
	private String trade_id;
	private String cardNo;
	private String cardNo_checkResult;
	private String name;
	private String name_checkResult;
	private String birthday;
	private String birthday_checkResult;
	private String archviesNo;
	private String archviesNo_checkResult;
	private String carModels;
	private String carModels_checkResult;
	private String firstGetDocDate;
	private String firstGetDocDate_checkResult;
	private String validday;
	private String validday_checkResult;
	private String state_code;
	private String state_name;

	public Lw_driver_license() {
		super();
	}

	/**
	 * 获取 主键
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Seq_T_DS_LW_DRIVING_LICENCE")
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

	public String getCardNo() {
		return cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	public String getCardNo_checkResult() {
		return cardNo_checkResult;
	}

	public void setCardNo_checkResult(String cardNo_checkResult) {
		this.cardNo_checkResult = cardNo_checkResult;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName_checkResult() {
		return name_checkResult;
	}

	public void setName_checkResult(String name_checkResult) {
		this.name_checkResult = name_checkResult;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getBirthday_checkResult() {
		return birthday_checkResult;
	}

	public void setBirthday_checkResult(String birthday_checkResult) {
		this.birthday_checkResult = birthday_checkResult;
	}

	public String getArchviesNo() {
		return archviesNo;
	}

	public void setArchviesNo(String archviesNo) {
		this.archviesNo = archviesNo;
	}

	public String getArchviesNo_checkResult() {
		return archviesNo_checkResult;
	}

	public void setArchviesNo_checkResult(String archviesNo_checkResult) {
		this.archviesNo_checkResult = archviesNo_checkResult;
	}

	public String getCarModels() {
		return carModels;
	}

	public void setCarModels(String carModels) {
		this.carModels = carModels;
	}

	public String getCarModels_checkResult() {
		return carModels_checkResult;
	}

	public void setCarModels_checkResult(String carModels_checkResult) {
		this.carModels_checkResult = carModels_checkResult;
	}

	public String getFirstGetDocDate() {
		return firstGetDocDate;
	}

	public void setFirstGetDocDate(String firstGetDocDate) {
		this.firstGetDocDate = firstGetDocDate;
	}

	public String getFirstGetDocDate_checkResult() {
		return firstGetDocDate_checkResult;
	}

	public void setFirstGetDocDate_checkResult(String firstGetDocDate_checkResult) {
		this.firstGetDocDate_checkResult = firstGetDocDate_checkResult;
	}

	public String getValidday() {
		return validday;
	}

	public void setValidday(String validday) {
		this.validday = validday;
	}

	public String getValidday_checkResult() {
		return validday_checkResult;
	}

	public void setValidday_checkResult(String validday_checkResult) {
		this.validday_checkResult = validday_checkResult;
	}

	public String getState_code() {
		return state_code;
	}

	public void setState_code(String state_code) {
		this.state_code = state_code;
	}

	public String getState_name() {
		return state_name;
	}

	public void setState_name(String state_name) {
		this.state_name = state_name;
	}

}