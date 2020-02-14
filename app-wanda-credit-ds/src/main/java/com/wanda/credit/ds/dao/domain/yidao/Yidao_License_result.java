package com.wanda.credit.ds.dao.domain.yidao;

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
 * 营业执照信息
 * 
 * @author shenziqiang
 *
 */

@Entity
@Table(name = "T_DS_YIDAO_LICENSE_RESULT")
@SequenceGenerator(name = "SEQ_T_DS_YIDAO_LICENSE_RESULT", sequenceName = "SEQ_T_DS_YIDAO_LICENSE_RESULT")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Yidao_License_result extends BaseDomain {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long id;
	private String trade_id;
	private String recotype;
	private String req_image;
	private String error;
	private String details;
	private String credit_no;
	private String  name;
	private String econ_kind;
	private String  oper_name;
	private String regist_capi;
	private String start_date;
	private String address;
	private String  term_start;
	private String term_end;
	private String scope;
	private String regist_org ;
	private String check_date;
	private String oper_state;
	private String regist_code;
	public String getRegist_code() {
		return regist_code;
	}
	public void setRegist_code(String regist_code) {
		this.regist_code = regist_code;
	}
	private String cropped_image;
	
	public String getCropped_image() {
		return cropped_image;
	}
	public void setCropped_image(String cropped_image) {
		this.cropped_image = cropped_image;
	}
	/**
	 * 获取 主键
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_T_DS_YIDAO_LICENSE_RESULT")
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
	public String getRecotype() {
		return recotype;
	}
	public void setRecotype(String recotype) {
		this.recotype = recotype;
	}
	public String getReq_image() {
		return req_image;
	}
	public void setReq_image(String req_image) {
		this.req_image = req_image;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public String getDetails() {
		return details;
	}
	public void setDetails(String details) {
		this.details = details;
	}
	public String getCredit_no() {
		return credit_no;
	}
	public void setCredit_no(String credit_no) {
		this.credit_no = credit_no;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEcon_kind() {
		return econ_kind;
	}
	public void setEcon_kind(String econ_kind) {
		this.econ_kind = econ_kind;
	}
	public String getOper_name() {
		return oper_name;
	}
	public void setOper_name(String oper_name) {
		this.oper_name = oper_name;
	}
	public String getRegist_capi() {
		return regist_capi;
	}
	public void setRegist_capi(String regist_capi) {
		this.regist_capi = regist_capi;
	}
	public String getStart_date() {
		return start_date;
	}
	public void setStart_date(String start_date) {
		this.start_date = start_date;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getTerm_start() {
		return term_start;
	}
	public void setTerm_start(String term_start) {
		this.term_start = term_start;
	}
	public String getTerm_end() {
		return term_end;
	}
	public void setTerm_end(String term_end) {
		this.term_end = term_end;
	}
	public String getScope() {
		return scope;
	}
	public void setScope(String scope) {
		this.scope = scope;
	}
	public String getRegist_org() {
		return regist_org;
	}
	public void setRegist_org(String regist_org) {
		this.regist_org = regist_org;
	}
	public String getCheck_date() {
		return check_date;
	}
	public void setCheck_date(String check_date) {
		this.check_date = check_date;
	}
	public String getOper_state() {
		return oper_state;
	}
	public void setOper_state(String oper_state) {
		this.oper_state = oper_state;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
}
