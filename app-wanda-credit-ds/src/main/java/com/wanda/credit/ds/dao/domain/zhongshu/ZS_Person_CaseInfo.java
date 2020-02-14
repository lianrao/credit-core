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
@Table(name = "T_DS_ZS_PERSON_PERSONCASEINFO")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer","order","id","updated","created","updatedby","createdby"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@XStreamAlias("caseinfo")
public class ZS_Person_CaseInfo  extends BaseDomain {
	private static final long serialVersionUID = 1L;
	private String  Id;	
	private ZS_Person_Order	ORDER ;
    private String CASETIME      ;
    private String CASEREASON    ;
    private String CASEVAL       ;
    private String CASETYPE      ;
    private String EXESORT       ;
    private String CASERESULT    ;
    private String PENDECNO      ;
    private String PENDECISSDATE ;
    private String PENAUTH       ;
    private String ILLEGFACT     ;
    private String PENBASIS      ;
    private String PENTYPE       ;
    private String PENRESULT     ;
    private String PENAM         ;
    private String PENEXEST      ;
    private String NAME          ;
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
	public ZS_Person_Order getORDER() {
		return ORDER;
	}
	public void setORDER(ZS_Person_Order oRDER) {
		ORDER = oRDER;
	}
	public String getCASETIME() {
		return CASETIME;
	}
	public void setCASETIME(String cASETIME) {
		CASETIME = cASETIME;
	}
	public String getCASEREASON() {
		return CASEREASON;
	}
	public void setCASEREASON(String cASEREASON) {
		CASEREASON = cASEREASON;
	}
	public String getCASEVAL() {
		return CASEVAL;
	}
	public void setCASEVAL(String cASEVAL) {
		CASEVAL = cASEVAL;
	}
	public String getCASETYPE() {
		return CASETYPE;
	}
	public void setCASETYPE(String cASETYPE) {
		CASETYPE = cASETYPE;
	}
	public String getEXESORT() {
		return EXESORT;
	}
	public void setEXESORT(String eXESORT) {
		EXESORT = eXESORT;
	}
	public String getCASERESULT() {
		return CASERESULT;
	}
	public void setCASERESULT(String cASERESULT) {
		CASERESULT = cASERESULT;
	}
	public String getPENDECNO() {
		return PENDECNO;
	}
	public void setPENDECNO(String pENDECNO) {
		PENDECNO = pENDECNO;
	}
	public String getPENDECISSDATE() {
		return PENDECISSDATE;
	}
	public void setPENDECISSDATE(String pENDECISSDATE) {
		PENDECISSDATE = pENDECISSDATE;
	}
	public String getPENAUTH() {
		return PENAUTH;
	}
	public void setPENAUTH(String pENAUTH) {
		PENAUTH = pENAUTH;
	}
	public String getILLEGFACT() {
		return ILLEGFACT;
	}
	public void setILLEGFACT(String iLLEGFACT) {
		ILLEGFACT = iLLEGFACT;
	}
	public String getPENBASIS() {
		return PENBASIS;
	}
	public void setPENBASIS(String pENBASIS) {
		PENBASIS = pENBASIS;
	}
	public String getPENTYPE() {
		return PENTYPE;
	}
	public void setPENTYPE(String pENTYPE) {
		PENTYPE = pENTYPE;
	}
	public String getPENRESULT() {
		return PENRESULT;
	}
	public void setPENRESULT(String pENRESULT) {
		PENRESULT = pENRESULT;
	}
	public String getPENAM() {
		return PENAM;
	}
	public void setPENAM(String pENAM) {
		PENAM = pENAM;
	}
	public String getPENEXEST() {
		return PENEXEST;
	}
	public void setPENEXEST(String pENEXEST) {
		PENEXEST = pENEXEST;
	}
	public String getNAME() {
		return NAME;
	}
	public void setNAME(String nAME) {
		NAME = nAME;
	}

}
