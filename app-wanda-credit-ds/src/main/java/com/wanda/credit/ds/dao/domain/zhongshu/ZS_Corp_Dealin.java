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
@Table(name = "T_DS_ZS_CORP_DEALIN")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer","order","id","updated","created","updatedby","createdby"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@XStreamAlias("dealin")
public class ZS_Corp_Dealin  extends BaseDomain {
	private static final long serialVersionUID = 1L;
	private String  Id;
	private ZS_Order	ORDER ;
	private String CURRENCY      ;                       
	private String	ASSGRO      ;                       
	private String	PROGRO      ;                       
	private String	DEFICIT     ;                       
	private String	VENDINC     ;                       
	private String	LTERMINV    ;                       
	private String	LTERMLIAAM  ;                       
	private String	ANCHEYEAR   ;                       
	private String	NETINC      ;                       
	private String	BUSST       ;                       
	private String	LIAGRO      ;                       
	private String	SERVINC     ;                       
	private String	RATGRO      ;
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
	public String getCURRENCY() {
		return CURRENCY;
	}
	public void setCURRENCY(String cURRENCY) {
		CURRENCY = cURRENCY;
	}
	public String getASSGRO() {
		return ASSGRO;
	}
	public void setASSGRO(String aSSGRO) {
		ASSGRO = aSSGRO;
	}
	public String getPROGRO() {
		return PROGRO;
	}
	public void setPROGRO(String pROGRO) {
		PROGRO = pROGRO;
	}
	public String getDEFICIT() {
		return DEFICIT;
	}
	public void setDEFICIT(String dEFICIT) {
		DEFICIT = dEFICIT;
	}
	public String getVENDINC() {
		return VENDINC;
	}
	public void setVENDINC(String vENDINC) {
		VENDINC = vENDINC;
	}
	public String getLTERMINV() {
		return LTERMINV;
	}
	public void setLTERMINV(String lTERMINV) {
		LTERMINV = lTERMINV;
	}
	public String getLTERMLIAAM() {
		return LTERMLIAAM;
	}
	public void setLTERMLIAAM(String lTERMLIAAM) {
		LTERMLIAAM = lTERMLIAAM;
	}
	public String getANCHEYEAR() {
		return ANCHEYEAR;
	}
	public void setANCHEYEAR(String aNCHEYEAR) {
		ANCHEYEAR = aNCHEYEAR;
	}
	public String getNETINC() {
		return NETINC;
	}
	public void setNETINC(String nETINC) {
		NETINC = nETINC;
	}
	public String getBUSST() {
		return BUSST;
	}
	public void setBUSST(String bUSST) {
		BUSST = bUSST;
	}
	public String getLIAGRO() {
		return LIAGRO;
	}
	public void setLIAGRO(String lIAGRO) {
		LIAGRO = lIAGRO;
	}
	public String getSERVINC() {
		return SERVINC;
	}
	public void setSERVINC(String sERVINC) {
		SERVINC = sERVINC;
	}
	public String getRATGRO() {
		return RATGRO;
	}
	public void setRATGRO(String rATGRO) {
		RATGRO = rATGRO;
	}   
}
