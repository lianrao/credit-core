package com.wanda.credit.ds.dao.domain.govInfo;

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
 * 国家信息中心-基本信息
 */
@Entity
@Table(name = "T_DS_GOV_BASICINFO",schema="CPDB_DS")
@SequenceGenerator(name = "SEQ_T_DS_GOV_BASICINFO", sequenceName = "SEQ_T_DS_GOV_BASICINFO")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer" })
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Gov_basicinfo_result extends BaseDomain{
	private static final long serialVersionUID = 1L;
	private long id;
	private String trade_id ;
	private String message;
	private String status;
	private String querytype;
	private String regno;
	private String credit_code;
	private String tax_code ;
	private String organization_code;
	private String entname;
	private long entstatus;
	private String area;
	private String legalperson;
	private String enttype;
	private String dom;
	private String esdate;
	private String opfrom;
	private String opto	;
	private String regorg;
	private String apprdate;
	private String candate;
	private String revdate;
	private String valid_time;
	
	/**
	 * 获取 主键
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_T_DS_GOV_BASICINFO")
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

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getQuerytype() {
		return querytype;
	}

	public void setQuerytype(String querytype) {
		this.querytype = querytype;
	}

	public String getRegno() {
		return regno;
	}

	public void setRegno(String regno) {
		this.regno = regno;
	}

	public String getCredit_code() {
		return credit_code;
	}

	public void setCredit_code(String credit_code) {
		this.credit_code = credit_code;
	}

	public String getTax_code() {
		return tax_code;
	}

	public void setTax_code(String tax_code) {
		this.tax_code = tax_code;
	}

	public String getOrganization_code() {
		return organization_code;
	}

	public void setOrganization_code(String organization_code) {
		this.organization_code = organization_code;
	}

	public String getEntname() {
		return entname;
	}

	public void setEntname(String entname) {
		this.entname = entname;
	}

	public long getEntstatus() {
		return entstatus;
	}

	public void setEntstatus(long entstatus) {
		this.entstatus = entstatus;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getLegalperson() {
		return legalperson;
	}

	public void setLegalperson(String legalperson) {
		this.legalperson = legalperson;
	}

	public String getEnttype() {
		return enttype;
	}

	public void setEnttype(String enttype) {
		this.enttype = enttype;
	}

	public String getDom() {
		return dom;
	}

	public void setDom(String dom) {
		this.dom = dom;
	}

	public String getEsdate() {
		return esdate;
	}

	public void setEsdate(String esdate) {
		this.esdate = esdate;
	}

	public String getOpfrom() {
		return opfrom;
	}

	public void setOpfrom(String opfrom) {
		this.opfrom = opfrom;
	}

	public String getOpto() {
		return opto;
	}

	public void setOpto(String opto) {
		this.opto = opto;
	}

	public String getRegorg() {
		return regorg;
	}

	public void setRegorg(String regorg) {
		this.regorg = regorg;
	}

	public String getApprdate() {
		return apprdate;
	}

	public void setApprdate(String apprdate) {
		this.apprdate = apprdate;
	}

	public String getCandate() {
		return candate;
	}

	public void setCandate(String candate) {
		this.candate = candate;
	}

	public String getRevdate() {
		return revdate;
	}

	public void setRevdate(String revdate) {
		this.revdate = revdate;
	}

	public String getValid_time() {
		return valid_time;
	}

	public void setValid_time(String valid_time) {
		this.valid_time = valid_time;
	}

	@Override
	public String toString() {
		return "Gov_basicinfo_result [id=" + id + ", trade_id=" + trade_id
				+ ", message=" + message + ", status=" + status
				+ ", querytype=" + querytype + ", regno=" + regno
				+ ", credit_code=" + credit_code + ", tax_code=" + tax_code
				+ ", organization_code=" + organization_code + ", entname="
				+ entname + ", entstatus=" + entstatus + ", area=" + area
				+ ", legalperson=" + legalperson + ", enttype=" + enttype
				+ ", dom=" + dom + ", esdate=" + esdate + ", opfrom=" + opfrom
				+ ", opto=" + opto + ", regorg=" + regorg + ", apprdate="
				+ apprdate + ", candate=" + candate + ", revdate=" + revdate
				+ ", valid_time=" + valid_time + ", toString()="
				+ super.toString() + "]";
	}
}
