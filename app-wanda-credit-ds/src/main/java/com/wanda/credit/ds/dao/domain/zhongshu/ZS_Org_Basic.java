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
@Table(name = "T_DS_ZS_ORGBASIC")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer","order","id","updated","created"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@XStreamAlias("orgbasic")
public class ZS_Org_Basic  extends BaseDomain {
	private static final long serialVersionUID = 1L;
	private String  Id;
	 private String ZYBZ ;
	 private String JGMC ;
	 private String JGDZ ;
	 private String JGDM ;
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
	public String getZYBZ() {
		return ZYBZ;
	}
	public void setZYBZ(String zYBZ) {
		ZYBZ = zYBZ;
	}
	public String getJGMC() {
		return JGMC;
	}
	public void setJGMC(String jGMC) {
		JGMC = jGMC;
	}
	public String getJGDZ() {
		return JGDZ;
	}
	public void setJGDZ(String jGDZ) {
		JGDZ = jGDZ;
	}
	public String getJGDM() {
		return JGDM;
	}
	public void setJGDM(String jGDM) {
		JGDM = jGDM;
	}
}
