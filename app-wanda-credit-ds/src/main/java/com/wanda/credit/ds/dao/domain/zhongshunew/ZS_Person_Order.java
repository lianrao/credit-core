package com.wanda.credit.ds.dao.domain.zhongshunew;

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
@Entity(name="ZS_N_Person_Order")
@Table(name = "T_DS_ZS_NEW_ORDER")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer","id","updated","created","updatedby","createdby"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@XStreamAlias("orderPerson")
public class ZS_Person_Order  extends BaseDomain {
	private static final long serialVersionUID = 1L;
	private String  Id          ;
	private String  TRADE_ID    ;
	private String  ZS_API      ;//请求中数接口 人员探查/person/nam 或 按个人标识码查询/person/pal 或 企业详情查询企业版/entinfo
	
	private String  PALGORITHMID;// /pal参数 人员标识码（中数加密）
	private String  PERSON_ID_BOCOM;// /pal参数 人员标识码（征信加密）
	
	private String	VERSION     ;// /pal参数 人员标识version
	private String  MASK        ;//查询掩码,个人（分别代表法人、股东、高管、行政处罚、失信被执行人、被执行人共6个模块）企业（分别代表各个企业信息模块）
	
	private String	ENTNAME     ;// /nam 企业名称 // /entinfo 企业名称
	private String	NAME        ;// /nam 人员姓名

	private String	ENTID       ;// /entinfo参数 中数企业ID
	private String	CREDITCODE  ;// /entinfo参数 统一信用代码
	private String	REGNO       ;// /entinfo参数 企业注册号
	private String	ORGCODE     ;// /entinfo参数 组织机构代码
	private String	ENTTYPE     ;// /entinfo参数 企业类型:1-企业 2-个体
	
//	private String  UID        ;
//	private String	ORDERNO    ;
//	private String	KEY        ;
//	private String	KEYTYPE    ;
//	private String	STATUS     ;
//	private String	FINISHTIME ;
	private String	acct_id ;
	//个人信息
	private List<ZS_Person_CaseInfo> CASEINFOS;
	private List<ZS_Person_Ryposfr> RYPOSFRS;
	private List<ZS_Person_Ryposper> RYPOSPERS;
	private List<ZS_Person_Rypossha> RYPOSSHAS;
	private List<ZS_Person_Punishbreak> PUNISHBREAK;
	private List<ZS_Person_Punished> PUNISHED;
	
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
//
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "ORDER",cascade=CascadeType.ALL)
	public List<ZS_Person_CaseInfo> getCASEINFOS() {
		return CASEINFOS;
	}
	public void setCASEINFOS(List<ZS_Person_CaseInfo> cASEINFOS) {
		CASEINFOS = cASEINFOS;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "ORDER",cascade=CascadeType.ALL)
	public List<ZS_Person_Ryposfr> getRYPOSFRS() {
		return RYPOSFRS;
	}
	public void setRYPOSFRS(List<ZS_Person_Ryposfr> rYPOSFRS) {
		RYPOSFRS = rYPOSFRS;
	}
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "ORDER",cascade=CascadeType.ALL)
	public List<ZS_Person_Ryposper> getRYPOSPERS() {
		return RYPOSPERS;
	}
	public void setRYPOSPERS(List<ZS_Person_Ryposper> rYPOSPERS) {
		RYPOSPERS = rYPOSPERS;
	}
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "ORDER",cascade=CascadeType.ALL)
	public List<ZS_Person_Rypossha> getRYPOSSHAS() {
		return RYPOSSHAS;
	}
	public void setRYPOSSHAS(List<ZS_Person_Rypossha> rYPOSSHAS) {
		RYPOSSHAS = rYPOSSHAS;
	}
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "ORDER",cascade=CascadeType.ALL)
	public List<ZS_Person_Punishbreak> getPUNISHBREAK() {
		return PUNISHBREAK;
	}
	public void setPUNISHBREAK(List<ZS_Person_Punishbreak> pUNISHBREAK) {
		PUNISHBREAK = pUNISHBREAK;
	}
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "ORDER",cascade=CascadeType.ALL)
	public List<ZS_Person_Punished> getPUNISHED() {
		return PUNISHED;
	}
	public void setPUNISHED(List<ZS_Person_Punished> pUNISHED) {
		PUNISHED = pUNISHED;
	}
	public String getAcct_id() {
		return acct_id;
	}
	public void setAcct_id(String acct_id) {
		this.acct_id = acct_id;
	}
}
