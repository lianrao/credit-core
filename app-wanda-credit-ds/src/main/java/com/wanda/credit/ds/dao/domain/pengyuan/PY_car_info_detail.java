package com.wanda.credit.ds.dao.domain.pengyuan;

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
@Table(name = "CPDB_DS.t_ds_py_car_info_detail")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer" })
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class PY_car_info_detail extends BaseDomain {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String id;
	private String tradeId;
	private String cj;
	private String pp;
	private String cx;
	private String chex;
	private String xsmc;
	private String nk;
	private String ssnf;
	private String cllb;
	private String cljb;
	private String zdjg;
	private String sczt;
	private String gb;
	private String gchzjk;
	private String pfbz;
	private String pl;
	private String gxbzhyh;
	private String bsxlx;

	public PY_car_info_detail() {
		super();
	}

	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	@Column(name = "id", unique = true, nullable = false, length = 32)
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Column(name = "trade_id")
	public String getTradeId() {
		return tradeId;
	}

	public void setTradeId(String tradeId) {
		this.tradeId = tradeId;
	}

	public String getCj() {
		return cj;
	}

	public void setCj(String cj) {
		this.cj = cj;
	}

	public String getPp() {
		return pp;
	}

	public void setPp(String pp) {
		this.pp = pp;
	}

	public String getCx() {
		return cx;
	}

	public void setCx(String cx) {
		this.cx = cx;
	}

	@Column(name = "chx")
	public String getChex() {
		return chex;
	}

	public void setChex(String chex) {
		this.chex = chex;
	}

	public String getXsmc() {
		return xsmc;
	}

	public void setXsmc(String xsmc) {
		this.xsmc = xsmc;
	}

	public String getNk() {
		return nk;
	}

	public void setNk(String nk) {
		this.nk = nk;
	}

	public String getSsnf() {
		return ssnf;
	}

	public void setSsnf(String ssnf) {
		this.ssnf = ssnf;
	}

	public String getCllb() {
		return cllb;
	}

	public void setCllb(String cllb) {
		this.cllb = cllb;
	}

	public String getCljb() {
		return cljb;
	}

	public void setCljb(String cljb) {
		this.cljb = cljb;
	}

	public String getZdjg() {
		return zdjg;
	}

	public void setZdjg(String zdjg) {
		this.zdjg = zdjg;
	}

	public String getSczt() {
		return sczt;
	}

	public void setSczt(String sczt) {
		this.sczt = sczt;
	}

	public String getGb() {
		return gb;
	}

	public void setGb(String gb) {
		this.gb = gb;
	}

	public String getGchzjk() {
		return gchzjk;
	}

	public void setGchzjk(String gchzjk) {
		this.gchzjk = gchzjk;
	}

	public String getPfbz() {
		return pfbz;
	}

	public void setPfbz(String pfbz) {
		this.pfbz = pfbz;
	}

	public String getPl() {
		return pl;
	}

	public void setPl(String pl) {
		this.pl = pl;
	}

	public String getGxbzhyh() {
		return gxbzhyh;
	}

	public void setGxbzhyh(String gxbzhyh) {
		this.gxbzhyh = gxbzhyh;
	}

	public String getBsxlx() {
		return bsxlx;
	}

	public void setBsxlx(String bsxlx) {
		this.bsxlx = bsxlx;
	}

}