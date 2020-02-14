package com.wanda.credit.ds.dao.domain.zhongshunew;

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
@Entity(name="ZS_N_Corp_Basic")
@Table(name = "t_ds_zs_new_corp_basic")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer","order","INDUSTRYCO","s_ext_nodenum","id","updated","created","updatedby","createdby"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@XStreamAlias("basic")
public class ZS_Corp_Basic  extends BaseDomain {
	 private static final long serialVersionUID = 1L;
	 private String  Id;
	 private String	acct_id         ;
	 private String ENTNAME         ; 
	 private String FRNAME          ; 
	 private String REGNO           ; 
	 private String REGCAP          ; 
	 private String REGCAPCUR       ; 
	 private String ESDATE          ; 
	 private String OPFROM          ; 
//	 private String OPSCOPE         ; 
	 private String OPTO            ; 
	 private String ENTTYPE         ; 
	 private String ENTSTATUS       ; 
	 private String DOM             ; 
	 private String ABUITEM         ; 
//	 private String CBUITEM         ; 
//	 private String OPSCOANDFORM    ; 
	 private String REGORG          ; 
//	 private String ANCHEYEAR       ; 
//	 private String PERSON_ID_BOCOM ; 
//	 private String ANCHEDATE       ; 
	 private String CANDATE         ; 
	 private String REVDATE         ; 
//	 private String RECCAP          ; 
//	 private String EMPNUM          ; 
//	 private String ENTNAMEENG      ; 
	 private String ORIREGNO        ; 
//	 private String ORGCODES        ; 
	 private String CREDITCODE      ; 
//	 private String CHANGEDATE      ; 
//	 private String REGORGPROVINCE  ; 
//	 private String REGORGCODE      ; 
//	 private String OPLOC           ; 
//	 private String S_EXT_NODENUM   ; 
//	 private String DOMDISTRICT     ; 
	 private String ZSOPSCOPE       ; 
//	 private String INDUSTRYPHY     ; 
//	 private String INDUSTRYCO      ; 
//	 private String INDUSTRYPHYCODE ;
//	 private String INDUSTRYCOCODE  ; 
//	 private String INDUSTRYCONAME  ; 
//	 private String PALGORITHMID  ; //新个人标识
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

	public String getAcct_id() {
		return acct_id;
	}
	public void setAcct_id(String acct_id) {
		this.acct_id = acct_id;
	}
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "REFID")
	public ZS_Order getORDER() {
		return ORDER;
	}
	public void setORDER(ZS_Order oRDER) {
		ORDER = oRDER;
	}
	public String getENTNAME() {
		return ENTNAME;
	}
	public void setENTNAME(String eNTNAME) {
		ENTNAME = eNTNAME;
	}
	public String getFRNAME() {
		return FRNAME;
	}
	public void setFRNAME(String fRNAME) {
		FRNAME = fRNAME;
	}
	public String getREGNO() {
		return REGNO;
	}
	public void setREGNO(String rEGNO) {
		REGNO = rEGNO;
	}
	public String getREGCAP() {
		return REGCAP;
	}
	public void setREGCAP(String rEGCAP) {
		REGCAP = rEGCAP;
	}
	public String getREGCAPCUR() {
		return REGCAPCUR;
	}
	public void setREGCAPCUR(String rEGCAPCUR) {
		REGCAPCUR = rEGCAPCUR;
	}
	public String getESDATE() {
		return ESDATE;
	}
	public void setESDATE(String eSDATE) {
		ESDATE = eSDATE;
	}
	public String getOPFROM() {
		return OPFROM;
	}
	public void setOPFROM(String oPFROM) {
		OPFROM = oPFROM;
	}
//	public String getOPSCOPE() {
//		return OPSCOPE;
//	}
//	public void setOPSCOPE(String oPSCOPE) {
//		OPSCOPE = oPSCOPE;
//	}
	public String getOPTO() {
		return OPTO;
	}
	public void setOPTO(String oPTO) {
		OPTO = oPTO;
	}
	public String getENTTYPE() {
		return ENTTYPE;
	}
	public void setENTTYPE(String eNTTYPE) {
		ENTTYPE = eNTTYPE;
	}
	public String getENTSTATUS() {
		return ENTSTATUS;
	}
	public void setENTSTATUS(String eNTSTATUS) {
		ENTSTATUS = eNTSTATUS;
	}
	public String getDOM() {
		return DOM;
	}
	public void setDOM(String dOM) {
		DOM = dOM;
	}
	public String getABUITEM() {
		return ABUITEM;
	}
	public void setABUITEM(String aBUITEM) {
		ABUITEM = aBUITEM;
	}
//	public String getCBUITEM() {
//		return CBUITEM;
//	}
//	public void setCBUITEM(String cBUITEM) {
//		CBUITEM = cBUITEM;
//	}
//	public String getOPSCOANDFORM() {
//		return OPSCOANDFORM;
//	}
//	public void setOPSCOANDFORM(String oPSCOANDFORM) {
//		OPSCOANDFORM = oPSCOANDFORM;
//	}
	public String getREGORG() {
		return REGORG;
	}
	public void setREGORG(String rEGORG) {
		REGORG = rEGORG;
	}
//	public String getANCHEYEAR() {
//		return ANCHEYEAR;
//	}
//	public void setANCHEYEAR(String aNCHEYEAR) {
//		ANCHEYEAR = aNCHEYEAR;
//	}
//	public String getPERSON_ID_BOCOM() {
//		return PERSON_ID_BOCOM;
//	}
//	public void setPERSON_ID_BOCOM(String pERSON_ID_BOCOM) {
//		PERSON_ID_BOCOM = pERSON_ID_BOCOM;
//	}
//	public String getANCHEDATE() {
//		return ANCHEDATE;
//	}
//	public void setANCHEDATE(String aNCHEDATE) {
//		ANCHEDATE = aNCHEDATE;
//	}
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
//	public String getRECCAP() {
//		return RECCAP;
//	}
//	public void setRECCAP(String rECCAP) {
//		RECCAP = rECCAP;
//	}
//	public String getEMPNUM() {
//		return EMPNUM;
//	}
//	public void setEMPNUM(String eMPNUM) {
//		EMPNUM = eMPNUM;
//	}
//	public String getENTNAMEENG() {
//		return ENTNAMEENG;
//	}
//	public void setENTNAMEENG(String eNTNAMEENG) {
//		ENTNAMEENG = eNTNAMEENG;
//	}
	public String getORIREGNO() {
		return ORIREGNO;
	}
	public void setORIREGNO(String oRIREGNO) {
		ORIREGNO = oRIREGNO;
	}
//	public String getORGCODES() {
//		return ORGCODES;
//	}
//	public void setORGCODES(String oRGCODES) {
//		ORGCODES = oRGCODES;
//	}
	public String getCREDITCODE() {
		return CREDITCODE;
	}
	public void setCREDITCODE(String cREDITCODE) {
		CREDITCODE = cREDITCODE;
	}
//	public String getCHANGEDATE() {
//		return CHANGEDATE;
//	}
//	public void setCHANGEDATE(String cHANGEDATE) {
//		CHANGEDATE = cHANGEDATE;
//	}
//	public String getREGORGPROVINCE() {
//		return REGORGPROVINCE;
//	}
//	public void setREGORGPROVINCE(String rEGORGPROVINCE) {
//		REGORGPROVINCE = rEGORGPROVINCE;
//	}
//	public String getREGORGCODE() {
//		return REGORGCODE;
//	}
//	public void setREGORGCODE(String rEGORGCODE) {
//		REGORGCODE = rEGORGCODE;
//	}
//	public String getOPLOC() {
//		return OPLOC;
//	}
//	public void setOPLOC(String oPLOC) {
//		OPLOC = oPLOC;
//	}
//	public String getS_EXT_NODENUM() {
//		return S_EXT_NODENUM;
//	}
//	public void setS_EXT_NODENUM(String s_EXT_NODENUM) {
//		S_EXT_NODENUM = s_EXT_NODENUM;
//	}
//	public String getDOMDISTRICT() {
//		return DOMDISTRICT;
//	}
//	public void setDOMDISTRICT(String dOMDISTRICT) {
//		DOMDISTRICT = dOMDISTRICT;
//	}
	public String getZSOPSCOPE() {
		return ZSOPSCOPE;
	}
	public void setZSOPSCOPE(String zSOPSCOPE) {
		ZSOPSCOPE = zSOPSCOPE;
	}
//	public String getINDUSTRYPHY() {
//		return INDUSTRYPHY;
//	}
//	public void setINDUSTRYPHY(String iNDUSTRYPHY) {
//		INDUSTRYPHY = iNDUSTRYPHY;
//	}
//	public String getINDUSTRYCOCODE() {
//		return INDUSTRYCOCODE;
//	}
//	public void setINDUSTRYCOCODE(String iNDUSTRYCOCODE) {
//		INDUSTRYCOCODE = iNDUSTRYCOCODE;
//	}
//	public String getINDUSTRYCONAME() {
//		return INDUSTRYCONAME;
//	}
//	public void setINDUSTRYCONAME(String iNDUSTRYCONAME) {
//		INDUSTRYCONAME = iNDUSTRYCONAME;
//	}
//	public String getINDUSTRYPHYCODE() {
//		return INDUSTRYPHYCODE;
//	}
//	public void setINDUSTRYPHYCODE(String iNDUSTRYPHYCODE) {
//		INDUSTRYPHYCODE = iNDUSTRYPHYCODE;
//	}
//	public String getPALGORITHMID() {
//		return PALGORITHMID;
//	}
//	public void setPALGORITHMID(String pALGORITHMID) {
//		PALGORITHMID = pALGORITHMID;
//	}
	
}
