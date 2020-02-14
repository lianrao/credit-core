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
 * 国家信息中心-行政许可
 */
@Entity
@Table(name = "T_DS_GOV_PUBPERMISSION",schema="CPDB_DS")
@SequenceGenerator(name = "SEQ_T_DS_GOV_PUBPERMISSION", sequenceName = "SEQ_T_DS_GOV_PUBPERMISSION")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer" })
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Gov_pubpermission_result extends BaseDomain{
	private static final long serialVersionUID = 1L;
	
	private long id;
	private String trade_id ;
	private String message;
	private String status;
	private String querytype;
	private String xk_wsh;
	private String xk_xmmc;
	private String xk_splb;
	private String xk_nr;
	private String xk_xdr;
	private String xk_xdr_shxym;
	private String xk_xdr_zdm;
	private String xk_xdr_gsdj;
	private String xk_xdr_swdj;
	private String xk_fr;
	private String xk_jdrq;
	private String xk_jzq;
	private String xk_xzjg;
	private String xk_zt;
	private String sjc;
	private String bz;
	private String dfbm;
	
	/**
	 * 获取 主键
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_T_DS_GOV_PUBPERMISSION")
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

	public String getXk_wsh() {
		return xk_wsh;
	}

	public void setXk_wsh(String xk_wsh) {
		this.xk_wsh = xk_wsh;
	}

	public String getXk_xmmc() {
		return xk_xmmc;
	}

	public void setXk_xmmc(String xk_xmmc) {
		this.xk_xmmc = xk_xmmc;
	}

	public String getXk_splb() {
		return xk_splb;
	}

	public void setXk_splb(String xk_splb) {
		this.xk_splb = xk_splb;
	}

	public String getXk_nr() {
		return xk_nr;
	}

	public void setXk_nr(String xk_nr) {
		this.xk_nr = xk_nr;
	}

	public String getXk_xdr() {
		return xk_xdr;
	}

	public void setXk_xdr(String xk_xdr) {
		this.xk_xdr = xk_xdr;
	}

	public String getXk_xdr_shxym() {
		return xk_xdr_shxym;
	}

	public void setXk_xdr_shxym(String xk_xdr_shxym) {
		this.xk_xdr_shxym = xk_xdr_shxym;
	}

	public String getXk_xdr_zdm() {
		return xk_xdr_zdm;
	}

	public void setXk_xdr_zdm(String xk_xdr_zdm) {
		this.xk_xdr_zdm = xk_xdr_zdm;
	}

	public String getXk_xdr_gsdj() {
		return xk_xdr_gsdj;
	}

	public void setXk_xdr_gsdj(String xk_xdr_gsdj) {
		this.xk_xdr_gsdj = xk_xdr_gsdj;
	}

	public String getXk_xdr_swdj() {
		return xk_xdr_swdj;
	}

	public void setXk_xdr_swdj(String xk_xdr_swdj) {
		this.xk_xdr_swdj = xk_xdr_swdj;
	}

	public String getXk_fr() {
		return xk_fr;
	}

	public void setXk_fr(String xk_fr) {
		this.xk_fr = xk_fr;
	}

	public String getXk_jdrq() {
		return xk_jdrq;
	}

	public void setXk_jdrq(String xk_jdrq) {
		this.xk_jdrq = xk_jdrq;
	}

	public String getXk_jzq() {
		return xk_jzq;
	}

	public void setXk_jzq(String xk_jzq) {
		this.xk_jzq = xk_jzq;
	}

	public String getXk_xzjg() {
		return xk_xzjg;
	}

	public void setXk_xzjg(String xk_xzjg) {
		this.xk_xzjg = xk_xzjg;
	}

	public String getXk_zt() {
		return xk_zt;
	}

	public void setXk_zt(String xk_zt) {
		this.xk_zt = xk_zt;
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

	public String getDfbm() {
		return dfbm;
	}

	public void setDfbm(String dfbm) {
		this.dfbm = dfbm;
	}

	@Override
	public String toString() {
		return "Gov_pubpermission_result [id=" + id + ", trade_id=" + trade_id
				+ ", message=" + message + ", status=" + status
				+ ", querytype=" + querytype + ", xk_wsh=" + xk_wsh
				+ ", xk_xmmc=" + xk_xmmc + ", xk_splb=" + xk_splb + ", xk_nr="
				+ xk_nr + ", xk_xdr=" + xk_xdr + ", xk_xdr_shxym="
				+ xk_xdr_shxym + ", xk_xdr_zdm=" + xk_xdr_zdm
				+ ", xk_xdr_gsdj=" + xk_xdr_gsdj + ", xk_xdr_swdj="
				+ xk_xdr_swdj + ", xk_fr=" + xk_fr + ", xk_jdrq=" + xk_jdrq
				+ ", xk_jzq=" + xk_jzq + ", xk_xzjg=" + xk_xzjg + ", xk_zt="
				+ xk_zt + ", sjc=" + sjc + ", bz=" + bz + ", dfbm=" + dfbm
				+ ", toString()=" + super.toString() + "]";
	}

}
