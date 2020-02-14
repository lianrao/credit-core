package com.wanda.credit.ds.dao.domain.yuanjin;

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

/**
 * @description  爰金风险评分(公安负面)
 * @author wuchsh 
 * @version 1.0
 * @createdate 2016年8月30日 上午11:45:30 
 *  
 */
@Entity
@Table(name = "T_DS_YJ_RISKLIST")
@SequenceGenerator(name="Seq_T_DS_YJ_RISKLIST",sequenceName="Seq_T_DS_YJ_RISKLIST")  
@JsonIgnoreProperties(value = { "hibernateLazyInitializer"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class YJ_CreditScoreVO extends BaseDomain{
	
	private static final long serialVersionUID = -1L;		

	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="Seq_T_DS_YJ_RISKLIST")  
	@Column(name = "ID", unique = true, nullable = false)
	private long id;
	private String trade_id;
	private String name;
	private String cardNo;
	private String responseCode;
	private String responseText;
	private String result;
	private String resultText;
	private String creditScores;
	private String creditScoresName;
	private Date create_date;
	private Date update_date;
	
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
	public String getCreditScores() {
		return creditScores;
	}
	public void setCreditScores(String creditScores) {
		this.creditScores = creditScores;
	}
	public String getCreditScoresName() {
		return creditScoresName;
	}
	public void setCreditScoresName(String creditScoresName) {
		this.creditScoresName = creditScoresName;
	}
	public Date getCreate_date() {
		return create_date;
	}
	public void setCreate_date(Date create_date) {
		this.create_date = create_date;
	}
	public Date getUpdate_date() {
		return update_date;
	}
	public void setUpdate_date(Date update_date) {
		this.update_date = update_date;
	}

}
