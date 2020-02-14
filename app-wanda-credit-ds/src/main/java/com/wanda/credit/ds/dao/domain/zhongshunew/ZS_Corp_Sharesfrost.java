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
@Entity(name="ZS_N_Corp_Sharesfrost")
@Table(name = "t_ds_zs_new_corp_sharesfrost")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer","order","id","updated","created","updatedby","createdby"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@XStreamAlias("sharesfrost")
public class ZS_Corp_Sharesfrost  extends BaseDomain {
	private static final long serialVersionUID = 1L;
	private String  Id;
	private String	FRODOCNO    ;
	private String	FROAUTH     ;
	private String 	FROFROM     ;
	private String 	FROTO       ;
	private String	FROAM       ;
	private String	THAWAUTH    ;
	private String	THAWDOCNO   ;
	private String	THAWDATE    ;
	private String	THAWCOMMENT ;
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
	 
	public String getFRODOCNO() {
		return FRODOCNO;
	}
	public void setFRODOCNO(String fRODOCNO) {
		FRODOCNO = fRODOCNO;
	}
	public String getFROAUTH() {
		return FROAUTH;
	}
	public void setFROAUTH(String fROAUTH) {
		FROAUTH = fROAUTH;
	}
	public String getFROFROM() {
		return FROFROM;
	}
	public void setFROFROM(String fROFROM) {
		FROFROM = fROFROM;
	}
	public String getFROTO() {
		return FROTO;
	}
	public void setFROTO(String fROTO) {
		FROTO = fROTO;
	}
	public String getFROAM() {
		return FROAM;
	}
	public void setFROAM(String fROAM) {
		FROAM = fROAM;
	}
	public String getTHAWAUTH() {
		return THAWAUTH;
	}
	public void setTHAWAUTH(String tHAWAUTH) {
		THAWAUTH = tHAWAUTH;
	}
	public String getTHAWDOCNO() {
		return THAWDOCNO;
	}
	public void setTHAWDOCNO(String tHAWDOCNO) {
		THAWDOCNO = tHAWDOCNO;
	}
	public String getTHAWDATE() {
		return THAWDATE;
	}
	public void setTHAWDATE(String tHAWDATE) {
		THAWDATE = tHAWDATE;
	}
	public String getTHAWCOMMENT() {
		return THAWCOMMENT;
	}
	public void setTHAWCOMMENT(String tHAWCOMMENT) {
		THAWCOMMENT = tHAWCOMMENT;
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
