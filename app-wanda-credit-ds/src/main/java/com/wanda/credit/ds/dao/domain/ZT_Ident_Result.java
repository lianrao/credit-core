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
@Table(name = "T_DS_ZT_IDENT_RESULT")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ZT_Ident_Result extends BaseDomain{
	/** 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 主键
	 */
	 private String id           ;
	 private String trade_id     ;
	 private String card_no      ;
	 private String name         ;
	 private String local_score  ;
	 private String mp_score     ;
	 private String sysseqnb     ;
	 private String status       ;
	 private String ident_status ;
	 private String respcd       ;
	 private String respinfo     ;
	 private String cerfront     ;
	 private String certnegative ;
	
	
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
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLocal_score() {
		return local_score;
	}
	public void setLocal_score(String local_score) {
		this.local_score = local_score;
	}
	public String getMp_score() {
		return mp_score;
	}
	public void setMp_score(String mp_score) {
		this.mp_score = mp_score;
	}
	public String getSysseqnb() {
		return sysseqnb;
	}
	public void setSysseqnb(String sysseqnb) {
		this.sysseqnb = sysseqnb;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getIdent_status() {
		return ident_status;
	}
	public void setIdent_status(String ident_status) {
		this.ident_status = ident_status;
	}
	public String getRespcd() {
		return respcd;
	}
	public void setRespcd(String respcd) {
		this.respcd = respcd;
	}
	public String getCard_no() {
		return card_no;
	}
	public void setCard_no(String card_no) {
		this.card_no = card_no;
	}
	public String getRespinfo() {
		return respinfo;
	}
	public void setRespinfo(String respinfo) {
		this.respinfo = respinfo;
	}
	public String getCerfront() {
		return cerfront;
	}
	public void setCerfront(String cerfront) {
		this.cerfront = cerfront;
	}
	public String getCertnegative() {
		return certnegative;
	}
	public void setCertnegative(String certnegative) {
		this.certnegative = certnegative;
	}

}
