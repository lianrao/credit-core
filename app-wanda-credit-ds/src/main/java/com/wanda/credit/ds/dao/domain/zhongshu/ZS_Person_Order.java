package com.wanda.credit.ds.dao.domain.zhongshu;

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
@Entity
@Table(name = "T_DS_ZS_ORDER")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer","id","updated","created","updatedby","createdby"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@XStreamAlias("orderPerson")
public class ZS_Person_Order  extends BaseDomain {
	private static final long serialVersionUID = 1L;
	private String  Id;
	private String  TRADE_ID;
	private String  UID        ;
	private String	ORDERNO    ;
	private String	KEY        ;
	private String	KEYTYPE    ;
	private String	STATUS     ;
	private String	FINISHTIME ;
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
	@Column(name="OID",nullable=false)
	public String getUID() {
		return UID;
	}
	public void setUID(String uID) {
		UID = uID;
	}
	public String getORDERNO() {
		return ORDERNO;
	}
	public void setORDERNO(String oRDERNO) {
		ORDERNO = oRDERNO;
	}
	public String getKEY() {
		return KEY;
	}
	public void setKEY(String kEY) {
		KEY = kEY;
	}
	public String getKEYTYPE() {
		return KEYTYPE;
	}
	public void setKEYTYPE(String kEYTYPE) {
		KEYTYPE = kEYTYPE;
	}
	public String getSTATUS() {
		return STATUS;
	}
	public void setSTATUS(String sTATUS) {
		STATUS = sTATUS;
	}
	public String getFINISHTIME() {
		return FINISHTIME;
	}
	public void setFINISHTIME(String fINISHTIME) {
		FINISHTIME = fINISHTIME;
	}

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
