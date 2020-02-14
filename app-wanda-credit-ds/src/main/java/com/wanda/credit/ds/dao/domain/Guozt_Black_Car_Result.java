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
@Table(name = "T_DS_GUOZT_BOOKCAR")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Guozt_Black_Car_Result extends BaseDomain{
	/**
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 主键
	 */
	 private String id          ;
	 private String trade_id    ;
	 private String cardno      ;
	 private String name        ;
	 private String token       ;
	 private String total       ;
	 private String black_type  ;
	 private String black_time  ;
	 private String black_list  ;
	 private String retstat1    ;
	 private String retstat2    ;
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
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getBlack_type() {
		return black_type;
	}
	public void setBlack_type(String black_type) {
		this.black_type = black_type;
	}
	public String getBlack_time() {
		return black_time;
	}
	public void setBlack_time(String black_time) {
		this.black_time = black_time;
	}
	public String getBlack_list() {
		return black_list;
	}
	public void setBlack_list(String black_list) {
		this.black_list = black_list;
	}
	public String getRetstat1() {
		return retstat1;
	}
	public void setRetstat1(String retstat1) {
		this.retstat1 = retstat1;
	}
	public String getRetstat2() {
		return retstat2;
	}
	public void setRetstat2(String retstat2) {
		this.retstat2 = retstat2;
	}
	public String getTotal() {
		return total;
	}
	public void setTotal(String total) {
		this.total = total;
	}
}
