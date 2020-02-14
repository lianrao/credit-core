package com.wanda.credit.ds.dao.domain.xiaoshi;

import java.util.Date;

/**
 * 小视人脸信息传输接口
 */
public class XiaoShi_faceInfo_result {
	 private static final long serialVersionUID = 1L;
	 private long id;
	 private String trade_id;
	 private String dev_id;
	 private String face_token;
	 private String confidence ;
	 private Date timestamp;
	 private String age    ;
	 private String age_confidence ;
	 private String gender     ;
	 private String gender_confidence ;
	 private String photo_url     ;
	 private String sendflag    ;
	 private String reserve1    ;
	 private String reserve2   ;
	 private String reserve3   ;
	 private String photo_id;
	
	/**
	 * 获取 主键
	 */
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

	public String getDev_id() {
		return dev_id;
	}

	public void setDev_id(String dev_id) {
		this.dev_id = dev_id;
	}

	public String getFace_token() {
		return face_token;
	}

	public void setFace_token(String face_token) {
		this.face_token = face_token;
	}

	public String getConfidence() {
		return confidence;
	}

	public void setConfidence(String confidence) {
		this.confidence = confidence;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public String getAge_confidence() {
		return age_confidence;
	}

	public void setAge_confidence(String age_confidence) {
		this.age_confidence = age_confidence;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getGender_confidence() {
		return gender_confidence;
	}

	public void setGender_confidence(String gender_confidence) {
		this.gender_confidence = gender_confidence;
	}

	public String getPhoto_url() {
		return photo_url;
	}

	public void setPhoto_url(String photo_url) {
		this.photo_url = photo_url;
	}

	public String getSendflag() {
		return sendflag;
	}

	public void setSendflag(String sendflag) {
		this.sendflag = sendflag;
	}

	public String getReserve1() {
		return reserve1;
	}

	public void setReserve1(String reserve1) {
		this.reserve1 = reserve1;
	}

	public String getReserve2() {
		return reserve2;
	}

	public void setReserve2(String reserve2) {
		this.reserve2 = reserve2;
	}

	public String getReserve3() {
		return reserve3;
	}

	public void setReserve3(String reserve3) {
		this.reserve3 = reserve3;
	}

	public String getPhoto_id() {
		return photo_id;
	}

	public void setPhoto_id(String photo_id) {
		this.photo_id = photo_id;
	}

}
