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
@Table(name = "T_DS_ZS_CORP_PERSON")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer","id","order","updated","created","updatedby","createdby"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@XStreamAlias("personNew")
public class ZS_Corp_Person_New  extends BaseDomain {
	private static final long serialVersionUID = 1L;
	private String  Id;
	private String  PERNAME;
	private String  PERSON_ID_BOCOM;
	private String  POSITION;
	private String  PERSONAMOUNT;
	private String PALGORITHMID  ; //新个人标识
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
	public String getPERNAME() {
		return PERNAME;
	}
	public void setPERNAME(String pERNAME) {
		PERNAME = pERNAME;
	}
	public String getPERSON_ID_BOCOM() {
		return PERSON_ID_BOCOM;
	}
	public void setPERSON_ID_BOCOM(String pERSON_ID_BOCOM) {
		PERSON_ID_BOCOM = pERSON_ID_BOCOM;
	}
	public String getPOSITION() {
		return POSITION;
	}
	public void setPOSITION(String pOSITION) {
		POSITION = pOSITION;
	}
	public String getPERSONAMOUNT() {
		return PERSONAMOUNT;
	}
	public void setPERSONAMOUNT(String pERSONAMOUNT) {
		PERSONAMOUNT = pERSONAMOUNT;
	}
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "REFID")
	public ZS_Order getORDER() {
		return ORDER;
	}
	public void setORDER(ZS_Order oRDER) {
		ORDER = oRDER;
	}
	public String getPALGORITHMID() {
		return PALGORITHMID;
	}
	public void setPALGORITHMID(String pALGORITHMID) {
		PALGORITHMID = pALGORITHMID;
	}
}
