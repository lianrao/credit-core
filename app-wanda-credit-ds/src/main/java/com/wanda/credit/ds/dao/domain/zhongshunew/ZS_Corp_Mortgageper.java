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
@Entity(name="ZS_N_Corp_Mortgageper")
@Table(name = "t_ds_zs_new_corp_mortgageper")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer","order","id","updated","created","updatedby","createdby"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@XStreamAlias("alter")
public class ZS_Corp_Mortgageper  extends BaseDomain {
	private static final long serialVersionUID = 1L;
	private String  Id;
	private ZS_Order	ORDER      ;	
	private String	MAB_REGNO      ; // 登记编号 
	private String	MAB_PER_NAME   ; // 抵押权人名称
	private String	MAB_PER_CERTYPE; // 抵押权人证照/证件类型
	private String	MAB_PER_CERNO  ; // 抵押权人证照/证件号码
	private String	MAB_PER_DOM    ; // 所在地
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
	public String getMAB_REGNO() {
		return MAB_REGNO;
	}
	public void setMAB_REGNO(String mAB_REGNO) {
		MAB_REGNO = mAB_REGNO;
	}
	public String getMAB_PER_NAME() {
		return MAB_PER_NAME;
	}
	public void setMAB_PER_NAME(String mAB_PER_NAME) {
		MAB_PER_NAME = mAB_PER_NAME;
	}
	public String getMAB_PER_CERTYPE() {
		return MAB_PER_CERTYPE;
	}
	public void setMAB_PER_CERTYPE(String mAB_PER_CERTYPE) {
		MAB_PER_CERTYPE = mAB_PER_CERTYPE;
	}
	public String getMAB_PER_CERNO() {
		return MAB_PER_CERNO;
	}
	public void setMAB_PER_CERNO(String mAB_PER_CERNO) {
		MAB_PER_CERNO = mAB_PER_CERNO;
	}
	public String getMAB_PER_DOM() {
		return MAB_PER_DOM;
	}
	public void setMAB_PER_DOM(String mAB_PER_DOM) {
		MAB_PER_DOM = mAB_PER_DOM;
	}
	
}
