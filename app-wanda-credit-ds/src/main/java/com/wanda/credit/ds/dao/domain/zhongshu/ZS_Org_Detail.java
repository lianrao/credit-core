package com.wanda.credit.ds.dao.domain.zhongshu;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.annotations.GenericGenerator;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.wanda.credit.base.domain.BaseDomain;
@Entity
@Table(name = "T_DS_ZS_ORGDETAIL")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer","order","id","updated","created"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@XStreamAlias("orgdetail")
public class ZS_Org_Detail  extends BaseDomain {
	private static final long serialVersionUID = 1L;
	private String  Id;
	private String JGDM  ;
	private String JGMC  ;
	private String JGDZ  ;
	private String ZYBZ  ;
	private String FDDBR ;
	private String XZQH  ;
	private String JGLX  ;
	private String ZCRQ  ;
	private String BZRQ  ;
	private String ZFRQ  ;
	private String BZJG  ;
	private ZS_Order	ORDER ;
	/**
	 * 获取 主键
	 */
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	@Column(name = "ID", unique = true, nullable = false, length = 32)
	public String getId() {
		return Id;
	}
	public void setId(String id) {
		Id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "REFID")
	public ZS_Order getORDER() {
		return ORDER;
	}
	public void setORDER(ZS_Order oRDER) {
		ORDER = oRDER;
	}
	public String getJGDM() {
		return JGDM;
	}
	public void setJGDM(String jGDM) {
		JGDM = jGDM;
	}
	public String getJGMC() {
		return JGMC;
	}
	public void setJGMC(String jGMC) {
		JGMC = jGMC;
	}
	public String getJGDZ() {
		return JGDZ;
	}
	public void setJGDZ(String jGDZ) {
		JGDZ = jGDZ;
	}
	public String getZYBZ() {
		return ZYBZ;
	}
	public void setZYBZ(String zYBZ) {
		ZYBZ = zYBZ;
	}
	public String getFDDBR() {
		return FDDBR;
	}
	public void setFDDBR(String fDDBR) {
		FDDBR = fDDBR;
	}
	public String getXZQH() {
		return XZQH;
	}
	public void setXZQH(String xZQH) {
		XZQH = xZQH;
	}
	public String getJGLX() {
		return JGLX;
	}
	public void setJGLX(String jGLX) {
		JGLX = jGLX;
	}
	public String getZCRQ() {
		return ZCRQ;
	}
	public void setZCRQ(String zCRQ) {
		ZCRQ = zCRQ;
	}
	public String getBZRQ() {
		return BZRQ;
	}
	public void setBZRQ(String bZRQ) {
		BZRQ = bZRQ;
	}
	public String getZFRQ() {
		return ZFRQ;
	}
	public void setZFRQ(String zFRQ) {
		ZFRQ = zFRQ;
	}
	public String getBZJG() {
		return BZJG;
	}
	public void setBZJG(String bZJG) {
		BZJG = bZJG;
	}
	
}
