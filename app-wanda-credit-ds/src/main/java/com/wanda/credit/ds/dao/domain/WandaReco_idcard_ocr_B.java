package com.wanda.credit.ds.dao.domain;

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

@Entity
@Table(name = "T_DS_WANDARECO_IDCARD_OCR_B")
@SequenceGenerator(name="ID_SEQ_T_DS_WANDARECO_IDCARD_OCR_B",sequenceName="SEQ_T_DS_WANDARECO_IDCARD_O_B",allocationSize=1)  
@JsonIgnoreProperties(value = { "hibernateLazyInitializer"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class WandaReco_idcard_ocr_B extends BaseDomain{
	/**
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 主键
	 */
	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="ID_SEQ_T_DS_WANDARECO_IDCARD_OCR_B")  
	@Column(name = "ID", unique = true, nullable = false)
	private Long id ;
	private String trade_id;
	private String requst_sn;
	private String authority;
	private String timelimit;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getTrade_id() {
		return trade_id;
	}
	public void setTrade_id(String trade_id) {
		this.trade_id = trade_id;
	}
	public String getRequst_sn() {
		return requst_sn;
	}
	public void setRequst_sn(String requst_sn) {
		this.requst_sn = requst_sn;
	}
	public String getAuthority() {
		return authority;
	}
	public void setAuthority(String authority) {
		this.authority = authority;
	}
	public String getTimelimit() {
		return timelimit;
	}
	public void setTimelimit(String timelimit) {
		this.timelimit = timelimit;
	}
	
}
