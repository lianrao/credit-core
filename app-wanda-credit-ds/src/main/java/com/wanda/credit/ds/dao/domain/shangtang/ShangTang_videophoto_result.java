package com.wanda.credit.ds.dao.domain.shangtang;

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
 * 静默活体检测以及水印照人脸比对
 * 
 * @author shenziqiang
 *
 */

@Entity
@Table(name = "T_DS_VIDEOPHOTO_RESULT")
@SequenceGenerator(name = "SEQ_T_DS_VIDEOPHOTO_RESULT", sequenceName = "SEQ_T_DS_VIDEOPHOTO_RESULT")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ShangTang_videophoto_result extends BaseDomain {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long id;
	private String trade_id;
	private String name;
	private String code;
	private String passed;
	private String liveness_score;
	private String verification_score;
	private String image_id ;
	private String  image_timestamp;
	private String cardno;
	private String base64_image;
	private String req_video;
	private String image_file;
	private String request_id;
	
	public String getImage_file() {
		return image_file;
	}
	public void setImage_file(String image_file) {
		this.image_file = image_file;
	}
	/**
	 * 获取 主键
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_T_DS_VIDEOPHOTO_RESULT")
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
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getPassed() {
		return passed;
	}
	public void setPassed(String passed) {
		this.passed = passed;
	}
	public String getLiveness_score() {
		return liveness_score;
	}
	public void setLiveness_score(String liveness_score) {
		this.liveness_score = liveness_score;
	}
	public String getVerification_score() {
		return verification_score;
	}
	public void setVerification_score(String verification_score) {
		this.verification_score = verification_score;
	}
	public String getImage_id() {
		return image_id;
	}
	public void setImage_id(String image_id) {
		this.image_id = image_id;
	}
	public String getImage_timestamp() {
		return image_timestamp;
	}
	public void setImage_timestamp(String image_timestamp) {
		this.image_timestamp = image_timestamp;
	}
	public String getCardno() {
		return cardno;
	}
	public void setCardno(String cardno) {
		this.cardno = cardno;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public String getBase64_image() {
		return base64_image;
	}
	public void setBase64_image(String base64_image) {
		this.base64_image = base64_image;
	}
	public String getReq_video() {
		return req_video;
	}
	public void setReq_video(String req_video) {
		this.req_video = req_video;
	}
	public String getRequest_id() {
		return request_id;
	}
	public void setRequest_id(String request_id) {
		this.request_id = request_id;
	}
}
