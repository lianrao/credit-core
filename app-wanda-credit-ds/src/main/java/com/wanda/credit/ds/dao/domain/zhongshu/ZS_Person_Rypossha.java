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
@Table(name = "T_DS_ZS_PERSON_RYPOSSHA")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer","person_ID_BOCOM","palgorithmid","order","id","updated","created","updatedby","createdby"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@XStreamAlias("rypossha")
public class ZS_Person_Rypossha  extends BaseDomain {
	private static final long serialVersionUID = 1L;
	private String  Id;
	private String	RYNAME          ;
	private String	PERSON_ID_BOCOM ;
	private String	ENTNAME         ;
	private String	REGNO           ;
	private String	ENTTYPE         ;
	private String	REGCAP          ;
	private String	REGCAPCUR       ;
	private String	SUBCONAM        ;
	private String	CURRENCY        ;
	private String	CONFORM         ;
	private String	FUNDEDRATIO     ;
	private String	ENTSTATUS       ;
	private String PALGORITHMID  ;//新个人标识
	private ZS_Person_Order	ORDER ;
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
	public String getRYNAME() {
		return RYNAME;
	}
	public void setRYNAME(String rYNAME) {
		RYNAME = rYNAME;
	}
	public String getPERSON_ID_BOCOM() {
		return PERSON_ID_BOCOM;
	}
	public void setPERSON_ID_BOCOM(String pERSON_ID_BOCOM) {
		PERSON_ID_BOCOM = pERSON_ID_BOCOM;
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
	public String getSUBCONAM() {
		return SUBCONAM;
	}
	public void setSUBCONAM(String sUBCONAM) {
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
	public String getENTSTATUS() {
		return ENTSTATUS;
	}
	public void setENTSTATUS(String eNTSTATUS) {
		ENTSTATUS = eNTSTATUS;
	}
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "REFID")
	public ZS_Person_Order getORDER() {
		return ORDER;
	}
	public void setORDER(ZS_Person_Order oRDER) {
		ORDER = oRDER;
	}
	public String getPALGORITHMID() {
		return PALGORITHMID;
	}
	public void setPALGORITHMID(String pALGORITHMID) {
		PALGORITHMID = pALGORITHMID;
	}

}
