package com.wanda.credit.ds.dao.domain.yitu;


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
 * 国政通学历
 */
@Entity
@Table(name = "T_DS_YITU_AUTH_OPTION")
@SequenceGenerator(name = "SEQ_T_DS_YITUAUTH_OPTIN", sequenceName = "SEQ_T_DS_YITUAUTH_OPTIN")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Yitu_auth_option extends BaseDomain {
	private static final long serialVersionUID = 1L;
	 private long id  ;
	 private String trade_id ;
	 private String cardno ;
	 private String customer_defined_content;
	 private String is_same_person ;
	 private String is_anti_screen_check_valid ;
	 private String is_anti_screen_check_passed;
	 private String is_anti_screen_check_score;
	 private String is_dark_illumination_check_valid ;
	 private String is_dark_illumination_check_passed ;
	 private String is_dark_illumination_check_score ;
	 private String is_anti_picture_check_valid  ;
	 private String is_anti_picture_check_passed   ;
	 private String is_anti_picture_check_score ;
	 private String is_anti_eye_blockage_check_valid  ;
	 private String is_anti_eye_blockage_check_passed  ;
	 private String is_anti_eye_blockage_check_score ;
	 private String is_anti_hole_check_valid;
	 private String is_anti_hole_check_passed ;
	 private String is_anti_hole_check_score;
	
	/**
	 * 获取 主键
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_T_DS_YITUAUTH_OPTIN")
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

	public String getCardno() {
		return cardno;
	}

	public void setCardno(String cardno) {
		this.cardno = cardno;
	}
	@Column(name="CUSTOM_CONTENT")
	public String getCustomer_defined_content() {
		return customer_defined_content;
	}

	public void setCustomer_defined_content(String customer_defined_content) {
		this.customer_defined_content = customer_defined_content;
	}

	public String getIs_same_person() {
		return is_same_person;
	}

	public void setIs_same_person(String is_same_person) {
		this.is_same_person = is_same_person;
	}

	public String getIs_anti_screen_check_valid() {
		return is_anti_screen_check_valid;
	}

	public void setIs_anti_screen_check_valid(String is_anti_screen_check_valid) {
		this.is_anti_screen_check_valid = is_anti_screen_check_valid;
	}
	@Column(name="IS_ANTI_SCREEN_CHECK_PASS")
	public String getIs_anti_screen_check_passed() {
		return is_anti_screen_check_passed;
	}

	public void setIs_anti_screen_check_passed(String is_anti_screen_check_passed) {
		this.is_anti_screen_check_passed = is_anti_screen_check_passed;
	}

	public String getIs_anti_screen_check_score() {
		return is_anti_screen_check_score;
	}

	public void setIs_anti_screen_check_score(String is_anti_screen_check_score) {
		this.is_anti_screen_check_score = is_anti_screen_check_score;
	}
	@Column(name="IS_DARK_ILLUM_CHECK_VALID")
	public String getIs_dark_illumination_check_valid() {
		return is_dark_illumination_check_valid;
	}

	public void setIs_dark_illumination_check_valid(
			String is_dark_illumination_check_valid) {
		this.is_dark_illumination_check_valid = is_dark_illumination_check_valid;
	}
	@Column(name="IS_DARK_ILLUM_CHECK_PASS")
	public String getIs_dark_illumination_check_passed() {
		return is_dark_illumination_check_passed;
	}

	public void setIs_dark_illumination_check_passed(
			String is_dark_illumination_check_passed) {
		this.is_dark_illumination_check_passed = is_dark_illumination_check_passed;
	}
	@Column(name="IS_DARK_ILLUM_CHECK_SCORE")
	public String getIs_dark_illumination_check_score() {
		return is_dark_illumination_check_score;
	}

	public void setIs_dark_illumination_check_score(
			String is_dark_illumination_check_score) {
		this.is_dark_illumination_check_score = is_dark_illumination_check_score;
	}

	public String getIs_anti_picture_check_valid() {
		return is_anti_picture_check_valid;
	}

	public void setIs_anti_picture_check_valid(String is_anti_picture_check_valid) {
		this.is_anti_picture_check_valid = is_anti_picture_check_valid;
	}
	@Column(name="IS_ANTI_PICTURE_CHECK_PASS")
	public String getIs_anti_picture_check_passed() {
		return is_anti_picture_check_passed;
	}

	public void setIs_anti_picture_check_passed(String is_anti_picture_check_passed) {
		this.is_anti_picture_check_passed = is_anti_picture_check_passed;
	}

	public String getIs_anti_picture_check_score() {
		return is_anti_picture_check_score;
	}

	public void setIs_anti_picture_check_score(String is_anti_picture_check_score) {
		this.is_anti_picture_check_score = is_anti_picture_check_score;
	}
	@Column(name="IS_ANTI_EYE_CHECK_VALID")
	public String getIs_anti_eye_blockage_check_valid() {
		return is_anti_eye_blockage_check_valid;
	}

	public void setIs_anti_eye_blockage_check_valid(
			String is_anti_eye_blockage_check_valid) {
		this.is_anti_eye_blockage_check_valid = is_anti_eye_blockage_check_valid;
	}
	@Column(name="IS_ANTI_EYE_CHECK_PASS")
	public String getIs_anti_eye_blockage_check_passed() {
		return is_anti_eye_blockage_check_passed;
	}

	public void setIs_anti_eye_blockage_check_passed(
			String is_anti_eye_blockage_check_passed) {
		this.is_anti_eye_blockage_check_passed = is_anti_eye_blockage_check_passed;
	}
	@Column(name="IS_ANTI_EYE_CHECK_SCORE")
	public String getIs_anti_eye_blockage_check_score() {
		return is_anti_eye_blockage_check_score;
	}

	public void setIs_anti_eye_blockage_check_score(
			String is_anti_eye_blockage_check_score) {
		this.is_anti_eye_blockage_check_score = is_anti_eye_blockage_check_score;
	}

	public String getIs_anti_hole_check_valid() {
		return is_anti_hole_check_valid;
	}

	public void setIs_anti_hole_check_valid(String is_anti_hole_check_valid) {
		this.is_anti_hole_check_valid = is_anti_hole_check_valid;
	}
	@Column(name="IS_ANTI_HOLE_CHECK_PASS")
	public String getIs_anti_hole_check_passed() {
		return is_anti_hole_check_passed;
	}

	public void setIs_anti_hole_check_passed(String is_anti_hole_check_passed) {
		this.is_anti_hole_check_passed = is_anti_hole_check_passed;
	}

	public String getIs_anti_hole_check_score() {
		return is_anti_hole_check_score;
	}

	public void setIs_anti_hole_check_score(String is_anti_hole_check_score) {
		this.is_anti_hole_check_score = is_anti_hole_check_score;
	}

}
