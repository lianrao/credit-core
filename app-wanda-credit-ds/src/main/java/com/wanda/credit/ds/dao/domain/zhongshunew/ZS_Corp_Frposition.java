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
@Entity(name="ZS_N_Corp_Frposition")
@Table(name = "t_ds_zs_new_corp_frposition")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer","order","id","updated","created","updatedby","createdby"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@XStreamAlias("frposition")
public class ZS_Corp_Frposition  extends BaseDomain {
	private static final long serialVersionUID = 1L;
	private String  Id;
	private String	NAME      ;
	private String  PPVAMOUNT ;
	private String	ENTNAME   ;
	private String	REGNO     ;
	private String	ENTTYPE   ;
	private Double	REGCAP    ;
	private String	REGCAPCUR ;
	private String	ENTSTATUS ;
	private String	CANDATE   	;
	private String	REVDATE   	;
	private String	REGORG    ;
	private String	POSITION  ;
	private String	LEREPSIGN ;
	private String	ESDATE    	;
	private String	REGORGCODE    	;
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
	public String getPPVAMOUNT() {
		return PPVAMOUNT;
	}
	public void setPPVAMOUNT(String pPVAMOUNT) {
		PPVAMOUNT = pPVAMOUNT;
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
	public String getPOSITION() {
		return POSITION;
	}
	public void setPOSITION(String pOSITION) {
		POSITION = pOSITION;
	}
	public String getLEREPSIGN() {
		return LEREPSIGN;
	}
	public void setLEREPSIGN(String lEREPSIGN) {
		LEREPSIGN = lEREPSIGN;
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
