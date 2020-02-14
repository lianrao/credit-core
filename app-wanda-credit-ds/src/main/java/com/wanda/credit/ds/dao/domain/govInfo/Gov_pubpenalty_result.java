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
 * 国家信息中心-行政处罚
 */
@Entity
@Table(name = "T_DS_GOV_PUBPENALTY",schema="CPDB_DS")
@SequenceGenerator(name = "SEQ_T_DS_GOV_PUBPENALTY", sequenceName = "SEQ_T_DS_GOV_PUBPENALTY")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer" })
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Gov_pubpenalty_result extends BaseDomain{
	private static final long serialVersionUID = 1L;
	
	private long id;
	private String trade_id ;
	private String message;
	private String status;
	private String querytype;
	private String cf_wsh;
	private String cf_cfmc;
	private String cf_cflb1;
	private String cf_cflb2;
	private String cf_sy;
	private String cf_yj;
	private String cf_xdr_mc;
	private String cf_xdr_shxym;
	private String cf_xdr_zdm;
	private String cf_xdr_gsdj;
	private String cf_xdr_swdj;
	private String cf_fr;
	private String cf_jg;
	private String cf_jdrq;
	private String cf_xzjg;
	private String cf_zt;
	private String sjc;
	private String bz;
	
	/**
	 * 获取 主键
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_T_DS_GOV_PUBPENALTY")
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

	public String getCf_wsh() {
		return cf_wsh;
	}

	public void setCf_wsh(String cf_wsh) {
		this.cf_wsh = cf_wsh;
	}

	public String getCf_cfmc() {
		return cf_cfmc;
	}

	public void setCf_cfmc(String cf_cfmc) {
		this.cf_cfmc = cf_cfmc;
	}

	public String getCf_cflb1() {
		return cf_cflb1;
	}

	public void setCf_cflb1(String cf_cflb1) {
		this.cf_cflb1 = cf_cflb1;
	}

	public String getCf_cflb2() {
		return cf_cflb2;
	}

	public void setCf_cflb2(String cf_cflb2) {
		this.cf_cflb2 = cf_cflb2;
	}

	public String getCf_sy() {
		return cf_sy;
	}

	public void setCf_sy(String cf_sy) {
		this.cf_sy = cf_sy;
	}

	public String getCf_yj() {
		return cf_yj;
	}

	public void setCf_yj(String cf_yj) {
		this.cf_yj = cf_yj;
	}

	public String getCf_xdr_mc() {
		return cf_xdr_mc;
	}

	public void setCf_xdr_mc(String cf_xdr_mc) {
		this.cf_xdr_mc = cf_xdr_mc;
	}

	public String getCf_xdr_shxym() {
		return cf_xdr_shxym;
	}

	public void setCf_xdr_shxym(String cf_xdr_shxym) {
		this.cf_xdr_shxym = cf_xdr_shxym;
	}

	public String getCf_xdr_zdm() {
		return cf_xdr_zdm;
	}

	public void setCf_xdr_zdm(String cf_xdr_zdm) {
		this.cf_xdr_zdm = cf_xdr_zdm;
	}

	public String getCf_xdr_gsdj() {
		return cf_xdr_gsdj;
	}

	public void setCf_xdr_gsdj(String cf_xdr_gsdj) {
		this.cf_xdr_gsdj = cf_xdr_gsdj;
	}

	public String getCf_xdr_swdj() {
		return cf_xdr_swdj;
	}

	public void setCf_xdr_swdj(String cf_xdr_swdj) {
		this.cf_xdr_swdj = cf_xdr_swdj;
	}

	public String getCf_fr() {
		return cf_fr;
	}

	public void setCf_fr(String cf_fr) {
		this.cf_fr = cf_fr;
	}

	public String getCf_jg() {
		return cf_jg;
	}

	public void setCf_jg(String cf_jg) {
		this.cf_jg = cf_jg;
	}

	public String getCf_jdrq() {
		return cf_jdrq;
	}

	public void setCf_jdrq(String cf_jdrq) {
		this.cf_jdrq = cf_jdrq;
	}

	public String getCf_xzjg() {
		return cf_xzjg;
	}

	public void setCf_xzjg(String cf_xzjg) {
		this.cf_xzjg = cf_xzjg;
	}

	public String getCf_zt() {
		return cf_zt;
	}

	public void setCf_zt(String cf_zt) {
		this.cf_zt = cf_zt;
	}

	public String getSjc() {
		return sjc;
	}

	public void setSjc(String sjc) {
		this.sjc = sjc;
	}

	public String getBz() {
		return bz;
	}

	public void setBz(String bz) {
		this.bz = bz;
	}

	@Override
	public String toString() {
		return "Gov_pubpenalty_result [id=" + id + ", trade_id=" + trade_id
				+ ", message=" + message + ", status=" + status
				+ ", querytype=" + querytype + ", cf_wsh=" + cf_wsh
				+ ", cf_cfmc=" + cf_cfmc + ", cf_cflb1=" + cf_cflb1
				+ ", cf_cflb2=" + cf_cflb2 + ", cf_sy=" + cf_sy + ", cf_yj="
				+ cf_yj + ", cf_xdr_mc=" + cf_xdr_mc + ", cf_xdr_shxym="
				+ cf_xdr_shxym + ", cf_xdr_zdm=" + cf_xdr_zdm
				+ ", cf_xdr_gsdj=" + cf_xdr_gsdj + ", cf_xdr_swdj="
				+ cf_xdr_swdj + ", cf_fr=" + cf_fr + ", cf_jg=" + cf_jg
				+ ", cf_jdrq=" + cf_jdrq + ", cf_xzjg=" + cf_xzjg + ", cf_zt="
				+ cf_zt + ", sjc=" + sjc + ", bz=" + bz + ", toString()="
				+ super.toString() + "]";
	}

}
