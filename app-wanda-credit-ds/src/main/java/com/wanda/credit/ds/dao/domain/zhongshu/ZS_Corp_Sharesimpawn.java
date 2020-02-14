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
@Table(name = "T_DS_ZS_CORP_SHARESIMPAWN")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer","order","id","updated","created","updatedby","createdby"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@XStreamAlias("sharesimpawn")
public class ZS_Corp_Sharesimpawn  extends BaseDomain {
	private static final long serialVersionUID = 1L;
	private String  Id;
    private String	IMPORG     ;
	private String	IMPORGTYPE ;
	private String	IMPAM      ;
	private String	IMPONRECDATE ;
	private String	IMPEXAEEP  ;
	private String	IMPSANDATE   ;
	private String	IMPTO        ;
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
 
	public String getIMPORG() {
		return IMPORG;
	}
	public void setIMPORG(String iMPORG) {
		IMPORG = iMPORG;
	}
	public String getIMPORGTYPE() {
		return IMPORGTYPE;
	}
	public void setIMPORGTYPE(String iMPORGTYPE) {
		IMPORGTYPE = iMPORGTYPE;
	}
	public String getIMPAM() {
		return IMPAM;
	}
	public void setIMPAM(String iMPAM) {
		IMPAM = iMPAM;
	}
	public String getIMPONRECDATE() {
		return IMPONRECDATE;
	}
	public void setIMPONRECDATE(String iMPONRECDATE) {
		IMPONRECDATE = iMPONRECDATE;
	}
	public String getIMPEXAEEP() {
		return IMPEXAEEP;
	}
	public void setIMPEXAEEP(String iMPEXAEEP) {
		IMPEXAEEP = iMPEXAEEP;
	}
	public String getIMPSANDATE() {
		return IMPSANDATE;
	}
	public void setIMPSANDATE(String iMPSANDATE) {
		IMPSANDATE = iMPSANDATE;
	}
	public String getIMPTO() {
		return IMPTO;
	}
	public void setIMPTO(String iMPTO) {
		IMPTO = iMPTO;
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
