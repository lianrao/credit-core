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
@Entity(name="ZS_N_Corp_Caseinfo")
@Table(name = "t_ds_zs_new_corp_caseinfo")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer","order","id","updated","created","updatedby","createdby"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@XStreamAlias("caseinfo")
public class ZS_Corp_Caseinfo  extends BaseDomain {
	private static final long serialVersionUID = 1L;
	private String  Id;
	private ZS_Order ORDER         ;
	private String  PENDECNO       ; // 处罚决定文书
	private String  ILLEGACTTYPE   ; // 主要违法事实
	private String  PENTYPE        ; // 处罚种类
	private String  PENTYPE_CN     ; // 处罚种类中文
	private String  PENCONTENT     ; // 行政处罚内容
	private String	PENAUTH        ; // 处罚机关
	private String  PENAUTH_CN     ; // 处罚机关名称
	private String  PENDECISSDATE  ; // 处罚决定书签发日期
	private String  PUBLICDATE     ; // 公示日期
//	private String    CASEREASON   ;
//	private String    PENBASIS     ;
//	private String    PENRESULT    ;
//	private String    PENAM        ;
//	private String    PENEXEST     ;
//	private String    CASETYPE     ;
//	private String    ILLEGFACT    ;
//	private String    CASETIME     ;
//	private String    CASEVAL      ;
//	private String    CASERESULT   ;
//	private String    EXESORT      ;
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
	public String getPENAUTH() {
		return PENAUTH;
	}
	public void setPENAUTH(String pENAUTH) {
		PENAUTH = pENAUTH;
	}
//	public String getCASEREASON() {
//		return CASEREASON;
//	}
//	public void setCASEREASON(String cASEREASON) {
//		CASEREASON = cASEREASON;
//	}
	public String getPENDECISSDATE() {
		return PENDECISSDATE;
	}
	public void setPENDECISSDATE(String pENDECISSDATE) {
		PENDECISSDATE = pENDECISSDATE;
	}
//	public String getPENBASIS() {
//		return PENBASIS;
//	}
//	public void setPENBASIS(String pENBASIS) {
//		PENBASIS = pENBASIS;
//	}
//	public String getPENRESULT() {
//		return PENRESULT;
//	}
//	public void setPENRESULT(String pENRESULT) {
//		PENRESULT = pENRESULT;
//	}
//	public String getPENAM() {
//		return PENAM;
//	}
//	public void setPENAM(String pENAM) {
//		PENAM = pENAM;
//	}
//	public String getPENEXEST() {
//		return PENEXEST;
//	}
//	public void setPENEXEST(String pENEXEST) {
//		PENEXEST = pENEXEST;
//	}
//	public String getCASETYPE() {
//		return CASETYPE;
//	}
//	public void setCASETYPE(String cASETYPE) {
//		CASETYPE = cASETYPE;
//	}
//	public String getILLEGFACT() {
//		return ILLEGFACT;
//	}
//	public void setILLEGFACT(String iLLEGFACT) {
//		ILLEGFACT = iLLEGFACT;
//	}
	public String getPENTYPE() {
		return PENTYPE;
	}
	public void setPENTYPE(String pENTYPE) {
		PENTYPE = pENTYPE;
	}
//	public String getCASETIME() {
//		return CASETIME;
//	}
//	public void setCASETIME(String cASETIME) {
//		CASETIME = cASETIME;
//	}
	public String getPENDECNO() {
		return PENDECNO;
	}
	public void setPENDECNO(String pENDECNO) {
		PENDECNO = pENDECNO;
	}
public String getILLEGACTTYPE() {
		return ILLEGACTTYPE;
	}
	public void setILLEGACTTYPE(String iLLEGACTTYPE) {
		ILLEGACTTYPE = iLLEGACTTYPE;
	}
	public String getPENAUTH_CN() {
		return PENAUTH_CN;
	}
	public void setPENAUTH_CN(String pENAUTH_CN) {
		PENAUTH_CN = pENAUTH_CN;
	}
	public String getPENCONTENT() {
		return PENCONTENT;
	}
	public void setPENCONTENT(String pENCONTENT) {
		PENCONTENT = pENCONTENT;
	}
	public String getPUBLICDATE() {
		return PUBLICDATE;
	}
	public void setPUBLICDATE(String pUBLICDATE) {
		PUBLICDATE = pUBLICDATE;
	}
	public String getPENTYPE_CN() {
		return PENTYPE_CN;
	}
	public void setPENTYPE_CN(String pENTYPE_CN) {
		PENTYPE_CN = pENTYPE_CN;
	}
	//	public String getCASEVAL() {
//		return CASEVAL;
//	}
//	public void setCASEVAL(String cASEVAL) {
//		CASEVAL = cASEVAL;
//	}
//	public String getCASERESULT() {
//		return CASERESULT;
//	}
//	public void setCASERESULT(String cASERESULT) {
//		CASERESULT = cASERESULT;
//	}
//	public String getEXESORT() {
//		return EXESORT;
//	}
//	public void setEXESORT(String eXESORT) {
//		EXESORT = eXESORT;
//	}

}
