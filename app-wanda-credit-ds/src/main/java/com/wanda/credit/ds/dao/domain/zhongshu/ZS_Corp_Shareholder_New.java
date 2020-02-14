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
@Table(name = "T_DS_ZS_CORP_SHAREHOLDER")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer","person_ID_BOCOM","PALGORITHMID","order","id","updated","created","updatedby","createdby"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@XStreamAlias("shareholderNew")
public class ZS_Corp_Shareholder_New  extends BaseDomain {
	private static final long serialVersionUID = 1L;
	private String  Id; 
	 private String SHANAME           ; 
	 private String SUBCONAM          ; 
	 private String REGCAPCUR         ; 
	 private String CONDATE           ; 
	 private String PERSON_ID_BOCOM   ; 
	 private String FUNDEDRATIO       ; 
	 private String COUNTRY           ; 
	 private String INVAMOUNT         ; 
	 private String SUMCONAM          ; 
	 private String INVSUMFUNDEDRATIO ; 
	 private String INVTYPE           ; 
	 private String CONFORM           ; 
	 private String PALGORITHMID  ;//新个人标识
	private ZS_Order	ORDER ;
	 
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
	public String getSHANAME() {
		return SHANAME;
	}
	public void setSHANAME(String sHANAME) {
		SHANAME = sHANAME;
	}
	public String getSUBCONAM() {
		return SUBCONAM;
	}
	public void setSUBCONAM(String sUBCONAM) {
		SUBCONAM = sUBCONAM;
	}
	public String getREGCAPCUR() {
		return REGCAPCUR;
	}
	public void setREGCAPCUR(String rEGCAPCUR) {
		REGCAPCUR = rEGCAPCUR;
	}
	public String getCONDATE() {
		return CONDATE;
	}
	public void setCONDATE(String cONDATE) {
		CONDATE = cONDATE;
	}
	public String getPERSON_ID_BOCOM() {
		return PERSON_ID_BOCOM;
	}
	public void setPERSON_ID_BOCOM(String pERSON_ID_BOCOM) {
		PERSON_ID_BOCOM = pERSON_ID_BOCOM;
	}
	public String getFUNDEDRATIO() {
		return FUNDEDRATIO;
	}
	public void setFUNDEDRATIO(String fUNDEDRATIO) {
		FUNDEDRATIO = fUNDEDRATIO;
	}
	public String getCOUNTRY() {
		return COUNTRY;
	}
	public void setCOUNTRY(String cOUNTRY) {
		COUNTRY = cOUNTRY;
	}
	public String getINVAMOUNT() {
		return INVAMOUNT;
	}
	public void setINVAMOUNT(String iNVAMOUNT) {
		INVAMOUNT = iNVAMOUNT;
	}
	public String getSUMCONAM() {
		return SUMCONAM;
	}
	public void setSUMCONAM(String sUMCONAM) {
		SUMCONAM = sUMCONAM;
	}
	public String getINVSUMFUNDEDRATIO() {
		return INVSUMFUNDEDRATIO;
	}
	public void setINVSUMFUNDEDRATIO(String iNVSUMFUNDEDRATIO) {
		INVSUMFUNDEDRATIO = iNVSUMFUNDEDRATIO;
	}
	public String getINVTYPE() {
		return INVTYPE;
	}
	public void setINVTYPE(String iNVTYPE) {
		INVTYPE = iNVTYPE;
	}
	public String getCONFORM() {
		return CONFORM;
	}
	public void setCONFORM(String cONFORM) {
		CONFORM = cONFORM;
	}
	public String getPALGORITHMID() {
		return PALGORITHMID;
	}
	public void setPALGORITHMID(String pALGORITHMID) {
		PALGORITHMID = pALGORITHMID;
	}

}
