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
@Entity(name="ZS_N_Person_Punishbreak")
@Table(name = "T_DS_ZS_NEW_PERSON_PUNISHBREAK")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer","order","id","updated","created","updatedby","createdby"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@XStreamAlias("punishbreak")
public class ZS_Person_Punishbreak  extends BaseDomain {
	private static final long serialVersionUID = 1L;
	private String  Id;	
	private ZS_Person_Order	ORDER ;
	private String CASECODE       ;
	private String   INAMECLEAN   ;
	private String   TYPE         ;
	private String   BUSINESSENTITY;
	private String  CARDNUM       ;
	private String   REGDATECLEAN     ;
	private String   PUBLISHDATECLEAN ;
	private String   COURTNAME        ;
	private String   AREANAMECLEAN    ;
	private String   GISTID          ;
	private String   GISTUNIT         ;
	private String   DUTY             ;
	private String   DISRUPTTYPENAME  ;
	private String   PERFORMANCE      ;
	private String   PERFORMEDPART    ;
	private String   UNPERFORMPART    ;
	private String   SEXYCLEAN        ;
	private String   AGECLEAN         ;
	private String   FOCUSNUMBER      ;
	private String   YSFZD            ;
	private String   EXITDATE         ;
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
	public String getCASECODE() {
		return CASECODE;
	}
	public void setCASECODE(String cASECODE) {
		CASECODE = cASECODE;
	}
	public String getINAMECLEAN() {
		return INAMECLEAN;
	}
	public void setINAMECLEAN(String iNAMECLEAN) {
		INAMECLEAN = iNAMECLEAN;
	}
	public String getTYPE() {
		return TYPE;
	}
	public void setTYPE(String tYPE) {
		TYPE = tYPE;
	}
	public String getBUSINESSENTITY() {
		return BUSINESSENTITY;
	}
	public void setBUSINESSENTITY(String bUSINESSENTITY) {
		BUSINESSENTITY = bUSINESSENTITY;
	}
	public String getCARDNUM() {
		return CARDNUM;
	}
	public void setCARDNUM(String cARDNUM) {
		CARDNUM = cARDNUM;
	}
	public String getREGDATECLEAN() {
		return REGDATECLEAN;
	}
	public void setREGDATECLEAN(String rEGDATECLEAN) {
		REGDATECLEAN = rEGDATECLEAN;
	}
	public String getPUBLISHDATECLEAN() {
		return PUBLISHDATECLEAN;
	}
	public void setPUBLISHDATECLEAN(String pUBLISHDATECLEAN) {
		PUBLISHDATECLEAN = pUBLISHDATECLEAN;
	}
	public String getCOURTNAME() {
		return COURTNAME;
	}
	public void setCOURTNAME(String cOURTNAME) {
		COURTNAME = cOURTNAME;
	}
	public String getAREANAMECLEAN() {
		return AREANAMECLEAN;
	}
	public void setAREANAMECLEAN(String aREANAMECLEAN) {
		AREANAMECLEAN = aREANAMECLEAN;
	}
	public String getGISTID() {
		return GISTID;
	}
	public void setGISTID(String gISTID) {
		GISTID = gISTID;
	}
	public String getGISTUNIT() {
		return GISTUNIT;
	}
	public void setGISTUNIT(String gISTUNIT) {
		GISTUNIT = gISTUNIT;
	}
	public String getDUTY() {
		return DUTY;
	}
	public void setDUTY(String dUTY) {
		DUTY = dUTY;
	}
	public String getDISRUPTTYPENAME() {
		return DISRUPTTYPENAME;
	}
	public void setDISRUPTTYPENAME(String dISRUPTTYPENAME) {
		DISRUPTTYPENAME = dISRUPTTYPENAME;
	}
	public String getPERFORMANCE() {
		return PERFORMANCE;
	}
	public void setPERFORMANCE(String pERFORMANCE) {
		PERFORMANCE = pERFORMANCE;
	}
	public String getPERFORMEDPART() {
		return PERFORMEDPART;
	}
	public void setPERFORMEDPART(String pERFORMEDPART) {
		PERFORMEDPART = pERFORMEDPART;
	}
	public String getUNPERFORMPART() {
		return UNPERFORMPART;
	}
	public void setUNPERFORMPART(String uNPERFORMPART) {
		UNPERFORMPART = uNPERFORMPART;
	}
	public String getSEXYCLEAN() {
		return SEXYCLEAN;
	}
	public void setSEXYCLEAN(String sEXYCLEAN) {
		SEXYCLEAN = sEXYCLEAN;
	}
	public String getAGECLEAN() {
		return AGECLEAN;
	}
	public void setAGECLEAN(String aGECLEAN) {
		AGECLEAN = aGECLEAN;
	}
	public String getFOCUSNUMBER() {
		return FOCUSNUMBER;
	}
	public void setFOCUSNUMBER(String fOCUSNUMBER) {
		FOCUSNUMBER = fOCUSNUMBER;
	}
	public String getYSFZD() {
		return YSFZD;
	}
	public void setYSFZD(String ySFZD) {
		YSFZD = ySFZD;
	}
	public String getEXITDATE() {
		return EXITDATE;
	}
	public void setEXITDATE(String eXITDATE) {
		EXITDATE = eXITDATE;
	}
}
