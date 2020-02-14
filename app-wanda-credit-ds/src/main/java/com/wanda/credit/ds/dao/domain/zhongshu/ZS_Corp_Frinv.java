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
@Table(name = "T_DS_ZS_CORP_FRINV")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer","order","id","updated","created","updatedby","createdby"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@XStreamAlias("frinv")
public class ZS_Corp_Frinv  extends BaseDomain {
	private static final long serialVersionUID = 1L;
	private String  Id;
	private String	NAME        ;
	private String	PINVAMOUNT     ;
	private String	ENTNAME     ;
	private String	REGNO       ;
	private String	ENTTYPE     ;
	private Double	REGCAP      ;
	private String	REGCAPCUR   ;
	private String	ENTSTATUS   ;
	private String	CANDATE     	;
	private String	REVDATE     	;
	private String	REGORG      ;
	private Double	SUBCONAM    ;
	private String	CURRENCY    ;
	private String	CONFORM     ;
	private String	FUNDEDRATIO ;
	private String	ESDATE      	;
	private String	REGORGCODE      	;
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
	public String getNAME() {
		return NAME;
	}
	public void setNAME(String nAME) {
		NAME = nAME;
	}
	
	public String getPINVAMOUNT() {
		return PINVAMOUNT;
	}
	public void setPINVAMOUNT(String pINVAMOUNT) {
		PINVAMOUNT = pINVAMOUNT;
	}
	public String getENTNAME() {
		return ENTNAME;
	}
	public void setENTNAME(String eNTNAME) {
		ENTNAME = eNTNAME;
	}
	public String getREGNO() {
		return REGNO;
	}
	public void setREGNO(String rEGNO) {
		REGNO = rEGNO;
	}
	public String getENTTYPE() {
		return ENTTYPE;
	}
	public void setENTTYPE(String eNTTYPE) {
		ENTTYPE = eNTTYPE;
	}
	public Double getREGCAP() {
		return REGCAP;
	}
	public void setREGCAP(Double rEGCAP) {
		REGCAP = rEGCAP;
	}
	public String getREGCAPCUR() {
		return REGCAPCUR;
	}
	public void setREGCAPCUR(String rEGCAPCUR) {
		REGCAPCUR = rEGCAPCUR;
	}
	public String getENTSTATUS() {
		return ENTSTATUS;
	}
	public void setENTSTATUS(String eNTSTATUS) {
		ENTSTATUS = eNTSTATUS;
	}
	public String getCANDATE() {
		return CANDATE;
	}
	public void setCANDATE(String cANDATE) {
		CANDATE = cANDATE;
	}
	public String getREVDATE() {
		return REVDATE;
	}
	public void setREVDATE(String rEVDATE) {
		REVDATE = rEVDATE;
	}
	public String getREGORG() {
		return REGORG;
	}
	public void setREGORG(String rEGORG) {
		REGORG = rEGORG;
	}
	public Double getSUBCONAM() {
		return SUBCONAM;
	}
	public void setSUBCONAM(Double sUBCONAM) {
		SUBCONAM = sUBCONAM;
	}
	public String getCURRENCY() {
		return CURRENCY;
	}
	public void setCURRENCY(String cURRENCY) {
		CURRENCY = cURRENCY;
	}
	public String getCONFORM() {
		return CONFORM;
	}
	public void setCONFORM(String cONFORM) {
		CONFORM = cONFORM;
	}
	public String getFUNDEDRATIO() {
		return FUNDEDRATIO;
	}
	public void setFUNDEDRATIO(String fUNDEDRATIO) {
		FUNDEDRATIO = fUNDEDRATIO;
	}
	public String getESDATE() {
		return ESDATE;
	}
	public void setESDATE(String eSDATE) {
		ESDATE = eSDATE;
	}
	public String getREGORGCODE() {
		return REGORGCODE;
	}
	public void setREGORGCODE(String rEGORGCODE) {
		REGORGCODE = rEGORGCODE;
	}
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "REFID")
	public ZS_Order getORDER() {
		return ORDER;
	}
	public void setORDER(ZS_Order oRDER) {
		ORDER = oRDER;
	}
}
