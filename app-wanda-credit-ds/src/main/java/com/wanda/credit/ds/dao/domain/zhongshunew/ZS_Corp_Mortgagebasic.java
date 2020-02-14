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
@Entity(name="ZS_N_Corp_Mortgagebasic")
@Table(name = "t_ds_zs_new_corp_mortgagebasic")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer","order","id","updated","created","updatedby","createdby"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@XStreamAlias("alter")
public class ZS_Corp_Mortgagebasic  extends BaseDomain {
	private static final long serialVersionUID = 1L;
	private String  Id;
	private ZS_Order	ORDER    ;	
	private String	MAB_REGNO    ; //登记编号
	private String	MAB_REG_DATE ; //登记日期
	private String	MAB_REG_ORG  ; //登记机关
	private String	MAB_GUAR_AMT ; //被担保债权数额
	private String	MAB_STATUS   ; //状态
	private String	MAB_GS_DATE  ; //公示日期

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
	public String getMAB_REG_DATE() {
		return MAB_REG_DATE;
	}
	public void setMAB_REG_DATE(String mAB_REG_DATE) {
		MAB_REG_DATE = mAB_REG_DATE;
	}
	public String getMAB_REG_ORG() {
		return MAB_REG_ORG;
	}
	public void setMAB_REG_ORG(String mAB_REG_ORG) {
		MAB_REG_ORG = mAB_REG_ORG;
	}
	public String getMAB_GUAR_AMT() {
		return MAB_GUAR_AMT;
	}
	public void setMAB_GUAR_AMT(String mAB_GUAR_AMT) {
		MAB_GUAR_AMT = mAB_GUAR_AMT;
	}
	public String getMAB_STATUS() {
		return MAB_STATUS;
	}
	public void setMAB_STATUS(String mAB_STATUS) {
		MAB_STATUS = mAB_STATUS;
	}
	public String getMAB_GS_DATE() {
		return MAB_GS_DATE;
	}
	public void setMAB_GS_DATE(String mAB_GS_DATE) {
		MAB_GS_DATE = mAB_GS_DATE;
	}
	
}
