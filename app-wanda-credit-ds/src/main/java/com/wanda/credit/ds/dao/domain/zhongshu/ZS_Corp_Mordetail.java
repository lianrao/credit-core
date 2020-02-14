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
@Table(name = "T_DS_ZS_CORP_MORDETAIL")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer","order","id","updated","created","updatedby","createdby"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@XStreamAlias("mordetail")
public class ZS_Corp_Mordetail  extends BaseDomain {
	private static final long serialVersionUID = 1L;
	private String  Id;
	private ZS_Order	ORDER ;
	private String	MORTGAGOR      ;
	private String	MORTYPE      ;
	private String	MORREG_ID    ;
	private String	PEFPERFORM   ;
	private String	PEFPERTO     ;
	private String	REGIDATE     ;
	private String	MORREGCNO    ;
	private String	PRICLASECAM  ;
	private String	APPREGREA    ;
	private String	PRICLASECKIND;
	private String	CANDATE      ;
	private String	MORE      		;
	private String	REGORG       ;
	private String	REGORGCODE   ;
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
	public String getMORTGAGOR() {
		return MORTGAGOR;
	}
	public void setMORTGAGOR(String mORTGAGOR) {
		MORTGAGOR = mORTGAGOR;
	}
	public String getMORTYPE() {
		return MORTYPE;
	}
	public void setMORTYPE(String mORTYPE) {
		MORTYPE = mORTYPE;
	}
	public String getMORREG_ID() {
		return MORREG_ID;
	}
	public void setMORREG_ID(String mORREG_ID) {
		MORREG_ID = mORREG_ID;
	}
	public String getPEFPERFORM() {
		return PEFPERFORM;
	}
	public void setPEFPERFORM(String pEFPERFORM) {
		PEFPERFORM = pEFPERFORM;
	}
	public String getPEFPERTO() {
		return PEFPERTO;
	}
	public void setPEFPERTO(String pEFPERTO) {
		PEFPERTO = pEFPERTO;
	}
	public String getREGIDATE() {
		return REGIDATE;
	}
	public void setREGIDATE(String rEGIDATE) {
		REGIDATE = rEGIDATE;
	}
	public String getMORREGCNO() {
		return MORREGCNO;
	}
	public void setMORREGCNO(String mORREGCNO) {
		MORREGCNO = mORREGCNO;
	}
	public String getPRICLASECAM() {
		return PRICLASECAM;
	}
	public void setPRICLASECAM(String pRICLASECAM) {
		PRICLASECAM = pRICLASECAM;
	}
	public String getAPPREGREA() {
		return APPREGREA;
	}
	public void setAPPREGREA(String aPPREGREA) {
		APPREGREA = aPPREGREA;
	}
	public String getPRICLASECKIND() {
		return PRICLASECKIND;
	}
	public void setPRICLASECKIND(String pRICLASECKIND) {
		PRICLASECKIND = pRICLASECKIND;
	}
	public String getCANDATE() {
		return CANDATE;
	}
	public void setCANDATE(String cANDATE) {
		CANDATE = cANDATE;
	}
	public String getMORE() {
		return MORE;
	}
	public void setMORE(String mORE) {
		MORE = mORE;
	}
	public String getREGORG() {
		return REGORG;
	}
	public void setREGORG(String rEGORG) {
		REGORG = rEGORG;
	}
	public String getREGORGCODE() {
		return REGORGCODE;
	}
	public void setREGORGCODE(String rEGORGCODE) {
		REGORGCODE = rEGORGCODE;
	}
}
