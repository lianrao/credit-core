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
@Table(name = "T_DS_ZS_CORP_FINALSHAREHOLDER")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer","order","id","updated","created","updatedby","createdby"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@XStreamAlias("finalshareholder")
public class ZS_Corp_Finalshareholder  extends BaseDomain {
	private static final long serialVersionUID = 1L;
	private String  Id;
	private String  FINALENTNAME  ;
	private String	FINALCOUNTRY  ;
	private String	FINALINVTYPE  ;
	private String	FINALSUBCONAM ;
	private String	FINALCURRENCY ;
	private String	FINALACCONAM 	;
	private String	FINALCONAM		;
	private String	FINALCONFORM  ;
	private String	FINALCONDATE  ;
	private String	FINALRATIO 		;
	private String	CAPITALCHAIN  ;
	private String	CAPITALCHAINEX;
	private String	FINALSTATUS   ;
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
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "REFID")
	public ZS_Order getORDER() {
		return ORDER;
	}
	public void setORDER(ZS_Order oRDER) {
		ORDER = oRDER;
	}
	public String getFINALENTNAME() {
		return FINALENTNAME;
	}
	public void setFINALENTNAME(String fINALENTNAME) {
		FINALENTNAME = fINALENTNAME;
	}
	public String getFINALCOUNTRY() {
		return FINALCOUNTRY;
	}
	public void setFINALCOUNTRY(String fINALCOUNTRY) {
		FINALCOUNTRY = fINALCOUNTRY;
	}
	public String getFINALINVTYPE() {
		return FINALINVTYPE;
	}
	public void setFINALINVTYPE(String fINALINVTYPE) {
		FINALINVTYPE = fINALINVTYPE;
	}
	public String getFINALSUBCONAM() {
		return FINALSUBCONAM;
	}
	public void setFINALSUBCONAM(String fINALSUBCONAM) {
		FINALSUBCONAM = fINALSUBCONAM;
	}
	public String getFINALCURRENCY() {
		return FINALCURRENCY;
	}
	public void setFINALCURRENCY(String fINALCURRENCY) {
		FINALCURRENCY = fINALCURRENCY;
	}
	public String getFINALACCONAM() {
		return FINALACCONAM;
	}
	public void setFINALACCONAM(String fINALACCONAM) {
		FINALACCONAM = fINALACCONAM;
	}
	public String getFINALCONAM() {
		return FINALCONAM;
	}
	public void setFINALCONAM(String fINALCONAM) {
		FINALCONAM = fINALCONAM;
	}
	public String getFINALCONFORM() {
		return FINALCONFORM;
	}
	public void setFINALCONFORM(String fINALCONFORM) {
		FINALCONFORM = fINALCONFORM;
	}
	public String getFINALCONDATE() {
		return FINALCONDATE;
	}
	public void setFINALCONDATE(String fINALCONDATE) {
		FINALCONDATE = fINALCONDATE;
	}
	public String getFINALRATIO() {
		return FINALRATIO;
	}
	public void setFINALRATIO(String fINALRATIO) {
		FINALRATIO = fINALRATIO;
	}
	public String getCAPITALCHAIN() {
		return CAPITALCHAIN;
	}
	public void setCAPITALCHAIN(String cAPITALCHAIN) {
		CAPITALCHAIN = cAPITALCHAIN;
	}
	public String getCAPITALCHAINEX() {
		return CAPITALCHAINEX;
	}
	public void setCAPITALCHAINEX(String cAPITALCHAINEX) {
		CAPITALCHAINEX = cAPITALCHAINEX;
	}
	public String getFINALSTATUS() {
		return FINALSTATUS;
	}
	public void setFINALSTATUS(String fINALSTATUS) {
		FINALSTATUS = fINALSTATUS;
	}

}
