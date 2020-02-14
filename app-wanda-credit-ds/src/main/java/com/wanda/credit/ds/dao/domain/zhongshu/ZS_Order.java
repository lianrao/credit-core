package com.wanda.credit.ds.dao.domain.zhongshu;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.annotations.GenericGenerator;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.wanda.credit.base.domain.BaseDomain;
@Entity
@Table(name = "T_DS_ZS_ORDER")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer","id","updated","created","updatedby","createdby"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@XStreamAlias("order")
public class ZS_Order  extends BaseDomain {
	private static final long serialVersionUID = 1L;
	private String  Id;
	private String  TRADE_ID;
	private String  UID        ;
	private String	ORDERNO    ;
	private String	KEY        ;
	private String	KEYTYPE    ;
	private String	STATUS     ;
	private String	FINISHTIME ;
	private String	acct_id ;
	//企业信息
	private List<ZS_Corp_Basic> 	BASICS;
	private List<ZS_Corp_Entinv> 	ZSENTINVS;
	private List<ZS_Corp_Frinv> 	FRINVS;
	private List<ZS_Corp_Frposition> 	FRPOSITIONS;
	private List<ZS_Corp_Person> 	PERSONS;
	private List<ZS_Corp_Punishbreak> PUNISHBREAKS;
	private List<ZS_Corp_Punished> 	PUNISHEDS;
	private List<ZS_Corp_Shareholder> SHAREHOLDERS;
	private List<ZS_Corp_Sharesfrost> SHARESFROSTS;
	private List<ZS_Corp_Sharesimpawn> SHARESIMPAWNS;
	
	private List<ZS_Corp_Alter> ALTER;
	private List<ZS_Corp_Filiation> FILIATION;
	private List<ZS_Corp_Mordetail> MORDETAIL;
	private List<ZS_Corp_Morguainfo> MORGUAINFO;
	private List<ZS_Corp_Dealin> DEALIN;
	private List<ZS_Corp_Liquidation>  LIQUIDATION;
	private List<ZS_Corp_Caseinfo>  CASEINFO;
	private List<ZS_Corp_Finalshareholder>  FINALSHAREHOLDER;
	
	//非企业信息
	private List<ZS_Org_Detail>  ORGDETAIL;
	private List<ZS_Org_Basic>  ORGBASIC;
	
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
	public String getTRADE_ID() {
		return TRADE_ID;
	}
	public void setTRADE_ID(String tRADE_ID) {
		TRADE_ID = tRADE_ID;
	}
	@Column(name="OID",nullable=false)
	public String getUID() {
		return UID;
	}
	public void setUID(String uID) {
		UID = uID;
	}
	public String getORDERNO() {
		return ORDERNO;
	}
	public void setORDERNO(String oRDERNO) {
		ORDERNO = oRDERNO;
	}
	public String getKEY() {
		return KEY;
	}
	public void setKEY(String kEY) {
		KEY = kEY;
	}
	public String getKEYTYPE() {
		return KEYTYPE;
	}
	public void setKEYTYPE(String kEYTYPE) {
		KEYTYPE = kEYTYPE;
	}
	public String getSTATUS() {
		return STATUS;
	}
	public void setSTATUS(String sTATUS) {
		STATUS = sTATUS;
	}
	public String getFINISHTIME() {
		return FINISHTIME;
	}
	public void setFINISHTIME(String fINISHTIME) {
		FINISHTIME = fINISHTIME;
	}
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "ORDER",cascade=CascadeType.ALL)
	public List<ZS_Corp_Basic> getBASICS() {
		return BASICS;
	}
	public void setBASICS(List<ZS_Corp_Basic> bASICS) {
		BASICS = bASICS;
	}
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "ORDER",cascade=CascadeType.ALL)
	public List<ZS_Corp_Entinv> getZSENTINVS() {
		return ZSENTINVS;
	}
	public void setZSENTINVS(List<ZS_Corp_Entinv> zSENTINVS) {
		ZSENTINVS = zSENTINVS;
	}
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "ORDER",cascade=CascadeType.ALL)
	public List<ZS_Corp_Frinv> getFRINVS() {
		return FRINVS;
	}
	public void setFRINVS(List<ZS_Corp_Frinv> fRINVS) {
		FRINVS = fRINVS;
	}
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "ORDER",cascade=CascadeType.ALL)
	public List<ZS_Corp_Frposition> getFRPOSITIONS() {
		return FRPOSITIONS;
	}
	public void setFRPOSITIONS(List<ZS_Corp_Frposition> fRPOSITIONS) {
		FRPOSITIONS = fRPOSITIONS;
	}
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "ORDER",cascade=CascadeType.ALL)
	public List<ZS_Corp_Person> getPERSONS() {
		return PERSONS;
	}
	public void setPERSONS(List<ZS_Corp_Person> pERSONS) {
		PERSONS = pERSONS;
	}
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "ORDER",cascade=CascadeType.ALL)
	public List<ZS_Corp_Punishbreak> getPUNISHBREAKS() {
		return PUNISHBREAKS;
	}
	public void setPUNISHBREAKS(List<ZS_Corp_Punishbreak> pUNISHBREAKS) {
		PUNISHBREAKS = pUNISHBREAKS;
	}
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "ORDER",cascade=CascadeType.ALL)
	public List<ZS_Corp_Punished> getPUNISHEDS() {
		return PUNISHEDS;
	}
	public void setPUNISHEDS(List<ZS_Corp_Punished> pUNISHEDS) {
		PUNISHEDS = pUNISHEDS;
	}
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "ORDER",cascade=CascadeType.ALL)
	public List<ZS_Corp_Shareholder> getSHAREHOLDERS() {
		return SHAREHOLDERS;
	}
	public void setSHAREHOLDERS(List<ZS_Corp_Shareholder> sHAREHOLDERS) {
		SHAREHOLDERS = sHAREHOLDERS;
	}
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "ORDER",cascade=CascadeType.ALL)
	public List<ZS_Corp_Sharesfrost> getSHARESFROSTS() {
		return SHARESFROSTS;
	}
	public void setSHARESFROSTS(List<ZS_Corp_Sharesfrost> sHARESFROSTS) {
		SHARESFROSTS = sHARESFROSTS;
	}
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "ORDER",cascade=CascadeType.ALL)
	public List<ZS_Corp_Sharesimpawn> getSHARESIMPAWNS() {
		return SHARESIMPAWNS;
	}
	public void setSHARESIMPAWNS(List<ZS_Corp_Sharesimpawn> sHARESIMPAWNS) {
		SHARESIMPAWNS = sHARESIMPAWNS;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "ORDER",cascade=CascadeType.ALL)
	public List<ZS_Corp_Alter> getALTER() {
		return ALTER;
	}
	public void setALTER(List<ZS_Corp_Alter> aLTER) {
		ALTER = aLTER;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "ORDER",cascade=CascadeType.ALL)
	public List<ZS_Corp_Filiation> getFILIATION() {
		return FILIATION;
	}
	public void setFILIATION(List<ZS_Corp_Filiation> fILIATION) {
		FILIATION = fILIATION;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "ORDER",cascade=CascadeType.ALL)
	public List<ZS_Corp_Mordetail> getMORDETAIL() {
		return MORDETAIL;
	}
	public void setMORDETAIL(List<ZS_Corp_Mordetail> mORDETAIL) {
		MORDETAIL = mORDETAIL;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "ORDER",cascade=CascadeType.ALL)
	public List<ZS_Corp_Morguainfo> getMORGUAINFO() {
		return MORGUAINFO;
	}
	public void setMORGUAINFO(List<ZS_Corp_Morguainfo> mORGUAINFO) {
		MORGUAINFO = mORGUAINFO;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "ORDER",cascade=CascadeType.ALL)
	public List<ZS_Corp_Dealin> getDEALIN() {
		return DEALIN;
	}
	public void setDEALIN(List<ZS_Corp_Dealin> dEALIN) {
		DEALIN = dEALIN;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "ORDER",cascade=CascadeType.ALL)
	public List<ZS_Corp_Liquidation> getLIQUIDATION() {
		return LIQUIDATION;
	}
	public void setLIQUIDATION(List<ZS_Corp_Liquidation> lIQUIDATION) {
		LIQUIDATION = lIQUIDATION;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "ORDER",cascade=CascadeType.ALL)
	public List<ZS_Corp_Caseinfo> getCASEINFO() {
		return CASEINFO;
	}
	public void setCASEINFO(List<ZS_Corp_Caseinfo> cASEINFO) {
		CASEINFO = cASEINFO;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "ORDER",cascade=CascadeType.ALL)
	public List<ZS_Corp_Finalshareholder> getFINALSHAREHOLDER() {
		return FINALSHAREHOLDER;
	}
	public void setFINALSHAREHOLDER(List<ZS_Corp_Finalshareholder> fINALSHAREHOLDER) {
		FINALSHAREHOLDER = fINALSHAREHOLDER;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "ORDER",cascade=CascadeType.ALL)
	public List<ZS_Org_Detail> getORGDETAIL() {
		return ORGDETAIL;
	}
	public void setORGDETAIL(List<ZS_Org_Detail> oRGDETAIL) {
		ORGDETAIL = oRGDETAIL;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "ORDER",cascade=CascadeType.ALL)
	public List<ZS_Org_Basic> getORGBASIC() {
		return ORGBASIC;
	}
	public void setORGBASIC(List<ZS_Org_Basic> oRGBASIC) {
		ORGBASIC = oRGBASIC;
	}
	public String getAcct_id() {
		return acct_id;
	}
	public void setAcct_id(String acct_id) {
		this.acct_id = acct_id;
	}
}
