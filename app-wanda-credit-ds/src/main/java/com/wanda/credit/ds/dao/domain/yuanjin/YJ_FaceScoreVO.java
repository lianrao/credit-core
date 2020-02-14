package com.wanda.credit.ds.dao.domain.yuanjin;

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
 * @description  爰金人脸比对
 * @author wuchsh 
 * @version 1.0
 * @createdate 2016年8月30日 上午11:45:30 
 *  
 */
@Entity
@Table(name = "T_DS_YUANJIN_FACE_RESULT")
@SequenceGenerator(name="SEQ_T_DS_YUANJIN_FACE_RESULT",sequenceName="SEQ_T_DS_YUANJIN_FACE_RESULT")  
@JsonIgnoreProperties(value = { "hibernateLazyInitializer"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class YJ_FaceScoreVO extends BaseDomain{
	
	private static final long serialVersionUID = -1L;		

	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="SEQ_T_DS_YUANJIN_FACE_RESULT")  
	@Column(name = "ID", unique = true, nullable = false)
	private long id;
	private String trade_id;
	private String name;
	private String cardNo;
	private String responseCode;
	private String responseText;
	private String result;
	private String resultText;
	private String score;
	private String faceResult;
	private String faceResultText;
	private String citizenResult;
	private String citizenResultText;
	private String file_image;
	
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
	public String getCardNo() {
		return cardNo;
	}
	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}
	public String getResponseCode() {
		return responseCode;
	}
	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}
	public String getResponseText() {
		return responseText;
	}
	public void setResponseText(String responseText) {
		this.responseText = responseText;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getResultText() {
		return resultText;
	}
	public void setResultText(String resultText) {
		this.resultText = resultText;
	}
	public String getScore() {
		return score;
	}
	public void setScore(String score) {
		this.score = score;
	}
	public String getFaceResult() {
		return faceResult;
	}
	public void setFaceResult(String faceResult) {
		this.faceResult = faceResult;
	}
	public String getFaceResultText() {
		return faceResultText;
	}
	public void setFaceResultText(String faceResultText) {
		this.faceResultText = faceResultText;
	}
	public String getCitizenResult() {
		return citizenResult;
	}
	public void setCitizenResult(String citizenResult) {
		this.citizenResult = citizenResult;
	}
	public String getCitizenResultText() {
		return citizenResultText;
	}
	public void setCitizenResultText(String citizenResultText) {
		this.citizenResultText = citizenResultText;
	}
	public String getFile_image() {
		return file_image;
	}
	public void setFile_image(String file_image) {
		this.file_image = file_image;
	}
	
}
