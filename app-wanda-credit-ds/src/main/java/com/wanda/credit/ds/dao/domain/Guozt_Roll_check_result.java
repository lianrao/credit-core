package com.wanda.credit.ds.dao.domain;

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
 * 国政通就业潜力查询表
 */
@Entity
@Table(name = "T_DS_GZT_JYQLZSCHECK")
@SequenceGenerator(name = "Seq_T_DS_GZT_JyqlzsCheck", sequenceName = "Seq_T_DS_GZT_JyqlzsCheck")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer" })
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Guozt_Roll_check_result extends BaseDomain {
	private static final long serialVersionUID = 1L;
	private long id;
	private String trade_id;
	 private String inputzt;
	 private String name;
	 private String cardno;
	 private String inputYxmc;
	 private String inputCc;
	 private String inputXllb;
	 private String inputRxrq;
	 private String inputZymc;
	 private String code;
	 private String message;
	 private String jyqlzs;
	 private String wybs;
	
	/**
	 * 获取 主键
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Seq_T_DS_GZT_JyqlzsCheck")
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

	public String getInputzt() {
		return inputzt;
	}

	public void setInputzt(String inputzt) {
		this.inputzt = inputzt;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCardno() {
		return cardno;
	}

	public void setCardno(String cardno) {
		this.cardno = cardno;
	}

	public String getInputYxmc() {
		return inputYxmc;
	}

	public void setInputYxmc(String inputYxmc) {
		this.inputYxmc = inputYxmc;
	}

	public String getInputCc() {
		return inputCc;
	}

	public void setInputCc(String inputCc) {
		this.inputCc = inputCc;
	}

	public String getInputXllb() {
		return inputXllb;
	}

	public void setInputXllb(String inputXllb) {
		this.inputXllb = inputXllb;
	}

	public String getInputRxrq() {
		return inputRxrq;
	}

	public void setInputRxrq(String inputRxrq) {
		this.inputRxrq = inputRxrq;
	}

	public String getInputZymc() {
		return inputZymc;
	}

	public void setInputZymc(String inputZymc) {
		this.inputZymc = inputZymc;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getJyqlzs() {
		return jyqlzs;
	}

	public void setJyqlzs(String jyqlzs) {
		this.jyqlzs = jyqlzs;
	}

	public String getWybs() {
		return wybs;
	}

	public void setWybs(String wybs) {
		this.wybs = wybs;
	}

}
