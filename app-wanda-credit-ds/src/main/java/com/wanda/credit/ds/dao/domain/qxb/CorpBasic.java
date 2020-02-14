package com.wanda.credit.ds.dao.domain.qxb;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * 前海 黑名单 信息
 **/
@Entity
@Table(name = "T_DS_QXB_CORP_BASIC")
@SequenceGenerator(name = "Seq_T_DS_QXB_Corp_basic", sequenceName = "Seq_T_DS_QXB_Corp_basic")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer" })
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class CorpBasic extends QxbBaseDomain {
	private static final long serialVersionUID = 1L;
	
	private long id;
	private String name ;
	private String keyword;
	private String econ_kind;
	private String regist_capi;
	private String address ;
	private String reg_no ;
	private String scope;
	private String term_start ;
	private String term_end ;
	private String belong_org ;
	private String oper_name ;
	private String check_date ;
	private String start_date ;
	private String end_date ;
	private String status ;
	private String org_no ;
	private String credit_no ;
	private String province ;
	private String city ;
	
	
    private String domain1;
	
	public CorpBasic() {
		super();
	}

	/**
	 * 获取 主键
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Seq_T_DS_QXB_Corp_basic")
	@Column(name = "ID", unique = true, nullable = false)
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
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

	public String getRegist_capi() {
		return regist_capi;
	}

	public void setRegist_capi(String regist_capi) {
		this.regist_capi = regist_capi;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getReg_no() {
		return reg_no;
	}

	public void setReg_no(String reg_no) {
		this.reg_no = reg_no;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
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

	public String getBelong_org() {
		return belong_org;
	}

	public void setBelong_org(String belong_org) {
		this.belong_org = belong_org;
	}

	public String getOper_name() {
		return oper_name;
	}

	public void setOper_name(String oper_name) {
		this.oper_name = oper_name;
	}

	public String getCheck_date() {
		return check_date;
	}

	public void setCheck_date(String check_date) {
		this.check_date = check_date;
	}

	public String getStart_date() {
		return start_date;
	}

	public void setStart_date(String start_date) {
		this.start_date = start_date;
	}

	public String getEnd_date() {
		return end_date;
	}

	public void setEnd_date(String end_date) {
		this.end_date = end_date;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getOrg_no() {
		return org_no;
	}

	public void setOrg_no(String org_no) {
		this.org_no = org_no;
	}

	public String getCredit_no() {
		return credit_no;
	}

	public void setCredit_no(String credit_no) {
		this.credit_no = credit_no;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	@Column(name = "domains")
	public String getDomain1() {
		return domain1;
	}

	public void setDomain1(String domain1) {
		this.domain1 = domain1;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	
	
}
