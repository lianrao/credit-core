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
@Table(name = "T_DS_ZS_CORP_LIQUIDATION")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer","order","id","updated","created","updatedby","createdby"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@XStreamAlias("liquidation")
public class ZS_Corp_Liquidation  extends BaseDomain {
	private static final long serialVersionUID = 1L;
	private String  Id;
	private ZS_Order	ORDER ;
	private String  LIGST        ;
	private String	LIQMEN       ;
	private String	CLAIMTRANEE  ;
	private String	LIGENTITY    ;
	private String	LIGPRINCIPAL ;
	private String	DEBTTRANEE   ;
	private String	LIGENDDATE   ;
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
	public String getLIGST() {
		return LIGST;
	}
	public void setLIGST(String lIGST) {
		LIGST = lIGST;
	}
	public String getLIQMEN() {
		return LIQMEN;
	}
	public void setLIQMEN(String lIQMEN) {
		LIQMEN = lIQMEN;
	}
	public String getCLAIMTRANEE() {
		return CLAIMTRANEE;
	}
	public void setCLAIMTRANEE(String cLAIMTRANEE) {
		CLAIMTRANEE = cLAIMTRANEE;
	}
	public String getLIGENTITY() {
		return LIGENTITY;
	}
	public void setLIGENTITY(String lIGENTITY) {
		LIGENTITY = lIGENTITY;
	}
	public String getLIGPRINCIPAL() {
		return LIGPRINCIPAL;
	}
	public void setLIGPRINCIPAL(String lIGPRINCIPAL) {
		LIGPRINCIPAL = lIGPRINCIPAL;
	}
	public String getDEBTTRANEE() {
		return DEBTTRANEE;
	}
	public void setDEBTTRANEE(String dEBTTRANEE) {
		DEBTTRANEE = dEBTTRANEE;
	}
	public String getLIGENDDATE() {
		return LIGENDDATE;
	}
	public void setLIGENDDATE(String lIGENDDATE) {
		LIGENDDATE = lIGENDDATE;
	}
	public void setORDER(ZS_Order oRDER) {
		ORDER = oRDER;
	}
	
}
