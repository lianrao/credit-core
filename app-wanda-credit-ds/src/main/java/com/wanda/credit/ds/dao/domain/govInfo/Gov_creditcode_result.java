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
 * 国家信息中心-统一社会信用代码
 */
@Entity
@Table(name = "T_DS_GOV_CREDITCODE",schema="CPDB_DS")
@SequenceGenerator(name = "SEQ_T_DS_GOV_CREDITCODE", sequenceName = "SEQ_T_DS_GOV_CREDITCODE")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer" })
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Gov_creditcode_result extends BaseDomain{
	private static final long serialVersionUID = 1L;
	
	private long id;
	private String trade_id ;
	private String message;
	private String rsp_status;
	private String querytype;
	private String entname;
	private String cred_code;
	private String reg_code;
	private String org_code;
	private String tax_code;
	private long status;
	
	/**
	 * 获取 主键
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_T_DS_GOV_CREDITCODE")
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

	public String getRsp_status() {
		return rsp_status;
	}

	public void setRsp_status(String rsp_status) {
		this.rsp_status = rsp_status;
	}

	public String getQuerytype() {
		return querytype;
	}

	public void setQuerytype(String querytype) {
		this.querytype = querytype;
	}

	public String getEntname() {
		return entname;
	}

	public void setEntname(String entname) {
		this.entname = entname;
	}

	public String getCred_code() {
		return cred_code;
	}

	public void setCred_code(String cred_code) {
		this.cred_code = cred_code;
	}

	public String getReg_code() {
		return reg_code;
	}

	public void setReg_code(String reg_code) {
		this.reg_code = reg_code;
	}

	public String getOrg_code() {
		return org_code;
	}

	public void setOrg_code(String org_code) {
		this.org_code = org_code;
	}

	public String getTax_code() {
		return tax_code;
	}

	public void setTax_code(String tax_code) {
		this.tax_code = tax_code;
	}

	public long getStatus() {
		return status;
	}

	public void setStatus(long status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "Gov_creditcode_result [id=" + id + ", trade_id=" + trade_id
				+ ", message=" + message + ", rsp_status=" + rsp_status
				+ ", querytype=" + querytype + ", entname=" + entname
				+ ", cred_code=" + cred_code + ", reg_code=" + reg_code
				+ ", org_code=" + org_code + ", tax_code=" + tax_code
				+ ", status=" + status + ", toString()=" + super.toString()
				+ "]";
	}

}
