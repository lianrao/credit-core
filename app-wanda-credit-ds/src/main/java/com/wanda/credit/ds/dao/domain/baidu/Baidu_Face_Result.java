package com.wanda.credit.ds.dao.domain.baidu;

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
 * 百度人脸识别
 */
@Entity
@Table(name = "T_DS_BAIDU_FACE_RESULT",schema="CPDB_DS")
@SequenceGenerator(name = "SEQ_T_DS_BAIDU_FACE_RESULT", sequenceName = "SEQ_T_DS_BAIDU_FACE_RESULT")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer" })
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Baidu_Face_Result extends BaseDomain{
	private static final long serialVersionUID = 1L;
	private long id;
	private String trade_id ;
   private String cardno;
   private String name ;
   private String photo_id;
   private String error_code;
   private String error_msg;
   private String log_id;
   private String cached ;
   private String score;
   private String face_token1;
   private String face_token2;
	
	/**
	 * 获取 主键
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_T_DS_BAIDU_FACE_RESULT")
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhoto_id() {
		return photo_id;
	}

	public void setPhoto_id(String photo_id) {
		this.photo_id = photo_id;
	}

	public String getError_code() {
		return error_code;
	}

	public void setError_code(String error_code) {
		this.error_code = error_code;
	}

	public String getError_msg() {
		return error_msg;
	}

	public void setError_msg(String error_msg) {
		this.error_msg = error_msg;
	}

	public String getLog_id() {
		return log_id;
	}

	public void setLog_id(String log_id) {
		this.log_id = log_id;
	}

	public String getCached() {
		return cached;
	}

	public void setCached(String cached) {
		this.cached = cached;
	}

	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}

	public String getFace_token1() {
		return face_token1;
	}

	public void setFace_token1(String face_token1) {
		this.face_token1 = face_token1;
	}

	public String getFace_token2() {
		return face_token2;
	}

	public void setFace_token2(String face_token2) {
		this.face_token2 = face_token2;
	}

	@Override
	public String toString() {
		return "Baidu_Face_Result [id=" + id + ", trade_id=" + trade_id
				+ ", cardno=" + cardno + ", name=" + name + ", photo_id="
				+ photo_id + ", error_code=" + error_code + ", error_msg="
				+ error_msg + ", log_id=" + log_id + ", cached=" + cached
				+ ", score=" + score + ", face_token1=" + face_token1
				+ ", face_token2=" + face_token2 + "]";
	}

}
