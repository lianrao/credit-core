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
@Table(name = "T_DS_ZS_CORP_ALTER")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer","order","id","updated","created","updatedby","createdby"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@XStreamAlias("alter")
public class ZS_Corp_Alter  extends BaseDomain {
	private static final long serialVersionUID = 1L;
	private String  Id;
	private ZS_Order	ORDER ;	
	private String  ALTBE     ;
	private String	ALTDATE   ;
	private String	ALTITEM   ;
	private String	ALTAF     ;
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
	public String getALTBE() {
		return ALTBE;
	}
	public void setALTBE(String aLTBE) {
		ALTBE = aLTBE;
	}
	public String getALTDATE() {
		return ALTDATE;
	}
	public void setALTDATE(String aLTDATE) {
		ALTDATE = aLTDATE;
	}
	public String getALTITEM() {
		return ALTITEM;
	}
	public void setALTITEM(String aLTITEM) {
		ALTITEM = aLTITEM;
	}
	public String getALTAF() {
		return ALTAF;
	}
	public void setALTAF(String aLTAF) {
		ALTAF = aLTAF;
	}
	
}
