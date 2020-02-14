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
@Table(name = "T_DS_ZS_CORP_FILIATION")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer","order","id","updated","created","updatedby","createdby"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@XStreamAlias("filiation")
public class ZS_Corp_Filiation  extends BaseDomain {
	private static final long serialVersionUID = 1L;
	private String  Id;
	private ZS_Order	ORDER ;
	private String	BRREGNO     ;
	private String	BRNAME      ;
	private String	CBUITEM     ;
	private String	BRADDR      ;
	private String	BRPRINCIPAL ;
	
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
	
	
	public String getBRREGNO() {
		return BRREGNO;
	}
	public void setBRREGNO(String bRREGNO) {
		BRREGNO = bRREGNO;
	}
	public String getBRNAME() {
		return BRNAME;
	}
	public void setBRNAME(String bRNAME) {
		BRNAME = bRNAME;
	}
	public String getCBUITEM() {
		return CBUITEM;
	}
	public void setCBUITEM(String cBUITEM) {
		CBUITEM = cBUITEM;
	}
	public String getBRADDR() {
		return BRADDR;
	}
	public void setBRADDR(String bRADDR) {
		BRADDR = bRADDR;
	}
	public String getBRPRINCIPAL() {
		return BRPRINCIPAL;
	}
	public void setBRPRINCIPAL(String bRPRINCIPAL) {
		BRPRINCIPAL = bRPRINCIPAL;
	}
	
}
