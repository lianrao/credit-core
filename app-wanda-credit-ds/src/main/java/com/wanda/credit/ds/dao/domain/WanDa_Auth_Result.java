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
@Table(name = "T_DS_WANDA_AUTH_RESULT")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class WanDa_Auth_Result extends BaseDomain{
	/** 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 主键
	 */
	 private String id              ;
	 private String trade_id        ;
	 private String cardno          ;
	 private String name            ;
	 private String photo_id        ;
	 private String message         ;
	 private String rtn             ;
	 private String pair_result     ;
	 private String pair_similarity ;
	 private String status          ;
	 private String resId      ;
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
	public String getPhoto_id() {
		return photo_id;
	}
	public void setPhoto_id(String photo_id) {
		this.photo_id = photo_id;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getRtn() {
		return rtn;
	}
	public void setRtn(String rtn) {
		this.rtn = rtn;
	}
	public String getPair_result() {
		return pair_result;
	}
	public void setPair_result(String pair_result) {
		this.pair_result = pair_result;
	}
	public String getPair_similarity() {
		return pair_similarity;
	}
	public void setPair_similarity(String pair_similarity) {
		this.pair_similarity = pair_similarity;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getResId() {
		return resId;
	}
	public void setResId(String resId) {
		this.resId = resId;
	}

}
