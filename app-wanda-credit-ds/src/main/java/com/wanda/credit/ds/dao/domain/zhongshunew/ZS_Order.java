package com.wanda.credit.ds.dao.domain.zhongshunew;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.annotations.GenericGenerator;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.wanda.credit.base.domain.BaseDomain;
@Entity(name="ZS_N_Order")
@Table(name = "T_DS_ZS_NEW_ORDER")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer","id","updated","created","updatedby","createdby"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@XStreamAlias("order")
public class ZS_Order  extends BaseDomain {
	private static final long serialVersionUID = 1L;
	private String  Id;
	private String  TRADE_ID;
	private String  ZS_API      ;//请求中数接口 人员探查/person/nam 或 按个人标识码查询/person/pal 或 企业详情查询企业版/entinfo
	private String  PALGORITHMID;// /pal参数 人员标识码（中数加密）
	private String  PERSON_ID_BOCOM;// /pal参数 人员标识码（征信加密）
	private String	VERSION     ;// /pal /entinfo参数 人员标识version
	private String  MASK        ;//查询掩码,个人（分别代表法人、股东、高管、行政处罚、失信被执行人、被执行人共6个模块）企业（分别代表各个企业信息模块）
	private String	ENTNAME     ;// /nam 企业名称 // /entinfo 企业名称
	private String	NAME        ;// /nam 人员姓名
	private String	ENTID       ;// /entinfo参数 中数企业ID
	private String	CREDITCODE  ;// /entinfo参数 统一信用代码
	private String	REGNO       ;// /entinfo参数 企业注册号
	private String	ORGCODE     ;// /entinfo参数 组织机构代码
	private String	ENTTYPE     ;// /entinfo参数 企业类型:1-企业 2-个体
	private String	acct_id ;

	private String	CODE    ;//响应结果编码
	private String	MSG     ;//响应结果信息

//	private String  UID        ;
//	private String	ORDERNO    ;
//	private String	KEY        ;
//	private String	KEYTYPE    ;
//	private String	STATUS     ;
//	private String	FINISHTIME ;
	//企业信息
	private List<ZS_Corp_Person> 	  PERSONS;
	private List<ZS_Corp_Frposition>  FRPOSITIONS;
	private List<ZS_Corp_Entinv> 	  ZSENTINVS;
	private List<ZS_Corp_Punished> 	  PUNISHEDS;
//	private List<ZS_Corp_Punished> 	  RELATEDPUNISHEDS;
	private List<ZS_Corp_Alter>       ALTER;
	private List<ZS_Corp_Filiation>   FILIATION;
	private List<ZS_Corp_Sharesfrost> SHARESFROSTS;
	private List<ZS_Corp_Punishbreak> PUNISHBREAKS;
//	private List<ZS_Corp_Punishbreak> RELATEDPUNISHBREAKS;
	private List<ZS_Corp_Shareholder> SHAREHOLDERS;
	private ZS_Corp_Basic        	  BASIC;
	private List<ZS_Corp_Frinv> 	  FRINVS;
	
	private List<ZS_Corp_Mortgagebasic> MORTGAGEBASICS ;
	private List<ZS_Corp_Mortgagereg>   MORTGAGEREGS ;
	private List<ZS_Corp_Mortgageper>   MORTGAGEPERS ;
	private List<ZS_Corp_Mortgagepawn>  MORTGAGEPAWNS ;
	private List<ZS_Corp_Mortgagedebt>  MORTGAGEDEBTS ;
	private List<ZS_Corp_Mortgagealt>   MORTGAGEALTS ;
	private List<ZS_Corp_Mortgagecan>   MORTGAGECANS ;
	private List<ZS_Corp_Stockpawn>     STOCKPAWNS ;
	private List<ZS_Corp_Stockpawnalt>  STOCKPAWNALTS ;
	private List<ZS_Corp_Stockpawnrev>  STOCKPAWNREVS ;
	private List<ZS_Corp_Caseinfo>      CASEINFO;
	
//	private List<ZS_Corp_Sharesimpawn> SHARESIMPAWNS;
//	private List<ZS_Corp_Mordetail> MORDETAIL;
//	private List<ZS_Corp_Morguainfo> MORGUAINFO;
//	private List<ZS_Corp_Dealin> DEALIN;
//	private List<ZS_Corp_Liquidation>  LIQUIDATION;
//	private List<ZS_Corp_Finalshareholder>  FINALSHAREHOLDER;
//	//非企业信息
//	private List<ZS_Org_Detail>  ORGDETAIL;
//	private List<ZS_Org_Basic>  ORGBASIC;
	
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
	public String getZS_API() {
		return ZS_API;
	}
	public void setZS_API(String zS_API) {
		ZS_API = zS_API;
	}
	public String getPALGORITHMID() {
		return PALGORITHMID;
	}
	public void setPALGORITHMID(String pALGORITHMID) {
		PALGORITHMID = pALGORITHMID;
	}
	public String getPERSON_ID_BOCOM() {
		return PERSON_ID_BOCOM;
	}
	public void setPERSON_ID_BOCOM(String pERSON_ID_BOCOM) {
		PERSON_ID_BOCOM = pERSON_ID_BOCOM;
	}
	public String getVERSION() {
		return VERSION;
	}
	public void setVERSION(String vERSION) {
		VERSION = vERSION;
	}
	public String getMASK() {
		return MASK;
	}
	public void setMASK(String mASK) {
		MASK = mASK;
	}
	public String getENTNAME() {
		return ENTNAME;
	}
	public void setENTNAME(String eNTNAME) {
		ENTNAME = eNTNAME;
	}
	public String getNAME() {
		return NAME;
	}
	public void setNAME(String nAME) {
		NAME = nAME;
	}
	public String getENTID() {
		return ENTID;
	}
	public void setENTID(String eNTID) {
		ENTID = eNTID;
	}
	public String getCREDITCODE() {
		return CREDITCODE;
	}
	public void setCREDITCODE(String cREDITCODE) {
		CREDITCODE = cREDITCODE;
	}
	public String getREGNO() {
		return REGNO;
	}
	public void setREGNO(String rEGNO) {
		REGNO = rEGNO;
	}
	public String getORGCODE() {
		return ORGCODE;
	}
	public void setORGCODE(String oRGCODE) {
		ORGCODE = oRGCODE;
	}
	public String getENTTYPE() {
		return ENTTYPE;
	}
	public void setENTTYPE(String eNTTYPE) {
		ENTTYPE = eNTTYPE;
	}
//	@Column(name="OID",nullable=false)
//	public String getUID() {
//		return UID;
//	}
//	public void setUID(String uID) {
//		UID = uID;
//	}
//	public String getORDERNO() {
//		return ORDERNO;
//	}
//	public void setORDERNO(String oRDERNO) {
//		ORDERNO = oRDERNO;
//	}
//	public String getKEY() {
//		return KEY;
//	}
//	public void setKEY(String kEY) {
//		KEY = kEY;
//	}
//	public String getKEYTYPE() {
//		return KEYTYPE;
//	}
//	public void setKEYTYPE(String kEYTYPE) {
//		KEYTYPE = kEYTYPE;
//	}
//	public String getSTATUS() {
//		return STATUS;
//	}
//	public void setSTATUS(String sTATUS) {
//		STATUS = sTATUS;
//	}
//	public String getFINISHTIME() {
//		return FINISHTIME;
//	}
//	public void setFINISHTIME(String fINISHTIME) {
//		FINISHTIME = fINISHTIME;
//	}
//	@OneToMany(fetch = FetchType.LAZY, mappedBy = "ORDER",cascade=CascadeType.ALL)
//	public List<ZS_Corp_Basic> getBASICS() {
//		return BASICS;
//	}
//	public void setBASICS(List<ZS_Corp_Basic> bASICS) {
//		BASICS = bASICS;
//	}
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

	@OneToOne(fetch = FetchType.LAZY, mappedBy = "ORDER",cascade=CascadeType.ALL)
	public ZS_Corp_Basic getBASIC() {
		return BASIC;
	}
	public void setBASIC(ZS_Corp_Basic bASIC) {
		BASIC = bASIC;
	}
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "ORDER",cascade=CascadeType.ALL)
	public List<ZS_Corp_Sharesfrost> getSHARESFROSTS() {
		return SHARESFROSTS;
	}
	public void setSHARESFROSTS(List<ZS_Corp_Sharesfrost> sHARESFROSTS) {
		SHARESFROSTS = sHARESFROSTS;
	}
//	@OneToMany(fetch = FetchType.LAZY, mappedBy = "ORDER",cascade=CascadeType.ALL)
//	public List<ZS_Corp_Sharesimpawn> getSHARESIMPAWNS() {
//		return SHARESIMPAWNS;
//	}
//	public void setSHARESIMPAWNS(List<ZS_Corp_Sharesimpawn> sHARESIMPAWNS) {
//		SHARESIMPAWNS = sHARESIMPAWNS;
//	}
	
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
	
//	@OneToMany(fetch = FetchType.LAZY, mappedBy = "ORDER",cascade=CascadeType.ALL)
//	public List<ZS_Corp_Mordetail> getMORDETAIL() {
//		return MORDETAIL;
//	}
//	public void setMORDETAIL(List<ZS_Corp_Mordetail> mORDETAIL) {
//		MORDETAIL = mORDETAIL;
//	}
//	
//	@OneToMany(fetch = FetchType.LAZY, mappedBy = "ORDER",cascade=CascadeType.ALL)
//	public List<ZS_Corp_Morguainfo> getMORGUAINFO() {
//		return MORGUAINFO;
//	}
//	public void setMORGUAINFO(List<ZS_Corp_Morguainfo> mORGUAINFO) {
//		MORGUAINFO = mORGUAINFO;
//	}
//	
//	@OneToMany(fetch = FetchType.LAZY, mappedBy = "ORDER",cascade=CascadeType.ALL)
//	public List<ZS_Corp_Dealin> getDEALIN() {
//		return DEALIN;
//	}
//	public void setDEALIN(List<ZS_Corp_Dealin> dEALIN) {
//		DEALIN = dEALIN;
//	}
//	
//	@OneToMany(fetch = FetchType.LAZY, mappedBy = "ORDER",cascade=CascadeType.ALL)
//	public List<ZS_Corp_Liquidation> getLIQUIDATION() {
//		return LIQUIDATION;
//	}
//	public void setLIQUIDATION(List<ZS_Corp_Liquidation> lIQUIDATION) {
//		LIQUIDATION = lIQUIDATION;
//	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "ORDER",cascade=CascadeType.ALL)
	public List<ZS_Corp_Caseinfo> getCASEINFO() {
		return CASEINFO;
	}
	public void setCASEINFO(List<ZS_Corp_Caseinfo> cASEINFO) {
		CASEINFO = cASEINFO;
	}
	
//	@OneToMany(fetch = FetchType.LAZY, mappedBy = "ORDER",cascade=CascadeType.ALL)
//	public List<ZS_Corp_Finalshareholder> getFINALSHAREHOLDER() {
//		return FINALSHAREHOLDER;
//	}
//	public void setFINALSHAREHOLDER(List<ZS_Corp_Finalshareholder> fINALSHAREHOLDER) {
//		FINALSHAREHOLDER = fINALSHAREHOLDER;
//	}
	
//	@OneToMany(fetch = FetchType.LAZY, mappedBy = "ORDER",cascade=CascadeType.ALL)
//	public List<ZS_Org_Detail> getORGDETAIL() {
//		return ORGDETAIL;
//	}
//	public void setORGDETAIL(List<ZS_Org_Detail> oRGDETAIL) {
//		ORGDETAIL = oRGDETAIL;
//	}
//	
//	@OneToMany(fetch = FetchType.LAZY, mappedBy = "ORDER",cascade=CascadeType.ALL)
//	public List<ZS_Org_Basic> getORGBASIC() {
//		return ORGBASIC;
//	}
//	public void setORGBASIC(List<ZS_Org_Basic> oRGBASIC) {
//		ORGBASIC = oRGBASIC;
//	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "ORDER",cascade=CascadeType.ALL)
	public List<ZS_Corp_Mortgagebasic> getMORTGAGEBASICS() {
		return MORTGAGEBASICS;
	}
	public void setMORTGAGEBASICS(List<ZS_Corp_Mortgagebasic> mORTGAGEBASICS) {
		MORTGAGEBASICS = mORTGAGEBASICS;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "ORDER",cascade=CascadeType.ALL)
	public List<ZS_Corp_Mortgagereg> getMORTGAGEREGS() {
		return MORTGAGEREGS;
	}
	public void setMORTGAGEREGS(List<ZS_Corp_Mortgagereg> mORTGAGEREGS) {
		MORTGAGEREGS = mORTGAGEREGS;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "ORDER",cascade=CascadeType.ALL)
	public List<ZS_Corp_Mortgageper> getMORTGAGEPERS() {
		return MORTGAGEPERS;
	}
	public void setMORTGAGEPERS(List<ZS_Corp_Mortgageper> mORTGAGEPERS) {
		MORTGAGEPERS = mORTGAGEPERS;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "ORDER",cascade=CascadeType.ALL)
	public List<ZS_Corp_Mortgagepawn> getMORTGAGEPAWNS() {
		return MORTGAGEPAWNS;
	}
	public void setMORTGAGEPAWNS(List<ZS_Corp_Mortgagepawn> mORTGAGEPAWNS) {
		MORTGAGEPAWNS = mORTGAGEPAWNS;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "ORDER",cascade=CascadeType.ALL)
	public List<ZS_Corp_Mortgagedebt> getMORTGAGEDEBTS() {
		return MORTGAGEDEBTS;
	}
	public void setMORTGAGEDEBTS(List<ZS_Corp_Mortgagedebt> mORTGAGEDEBTS) {
		MORTGAGEDEBTS = mORTGAGEDEBTS;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "ORDER",cascade=CascadeType.ALL)
	public List<ZS_Corp_Mortgagealt> getMORTGAGEALTS() {
		return MORTGAGEALTS;
	}
	public void setMORTGAGEALTS(List<ZS_Corp_Mortgagealt> mORTGAGEALTS) {
		MORTGAGEALTS = mORTGAGEALTS;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "ORDER",cascade=CascadeType.ALL)
	public List<ZS_Corp_Mortgagecan> getMORTGAGECANS() {
		return MORTGAGECANS;
	}
	public void setMORTGAGECANS(List<ZS_Corp_Mortgagecan> mORTGAGECANS) {
		MORTGAGECANS = mORTGAGECANS;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "ORDER",cascade=CascadeType.ALL)
	public List<ZS_Corp_Stockpawn> getSTOCKPAWNS() {
		return STOCKPAWNS;
	}
	public void setSTOCKPAWNS(List<ZS_Corp_Stockpawn> sTOCKPAWNS) {
		STOCKPAWNS = sTOCKPAWNS;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "ORDER",cascade=CascadeType.ALL)
	public List<ZS_Corp_Stockpawnalt> getSTOCKPAWNALTS() {
		return STOCKPAWNALTS;
	}
	public void setSTOCKPAWNALTS(List<ZS_Corp_Stockpawnalt> sTOCKPAWNALTS) {
		STOCKPAWNALTS = sTOCKPAWNALTS;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "ORDER",cascade=CascadeType.ALL)
	public List<ZS_Corp_Stockpawnrev> getSTOCKPAWNREVS() {
		return STOCKPAWNREVS;
	}
	public void setSTOCKPAWNREVS(List<ZS_Corp_Stockpawnrev> sTOCKPAWNREVS) {
		STOCKPAWNREVS = sTOCKPAWNREVS;
	}
	
	public String getAcct_id() {
		return acct_id;
	}
	public void setAcct_id(String acct_id) {
		this.acct_id = acct_id;
	}
	public String getCODE() {
		return CODE;
	}
	public void setCODE(String cODE) {
		CODE = cODE;
	}
	public String getMSG() {
		return MSG;
	}
	public void setMSG(String mSG) {
		MSG = mSG;
	}
}
