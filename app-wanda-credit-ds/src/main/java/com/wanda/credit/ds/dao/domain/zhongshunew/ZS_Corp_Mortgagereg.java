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
@Entity(name="ZS_N_Corp_Mortgagereg")
@Table(name = "t_ds_zs_new_corp_mortgagereg")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer","order","id","updated","created","updatedby","createdby"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@XStreamAlias("alter")
public class ZS_Corp_Mortgagereg  extends BaseDomain {
	private static final long serialVersionUID = 1L;
	private String  Id;
	private ZS_Order	ORDER     ;	
	private String	MAB_REGNO     ; // 登记编号
	private String	MAB_REG_DATE  ; // 登记日期
	private String	MAB_REG_ORG   ; // 登记机关
	private String	MAB_GUAR_AMT  ; // 被担保债权数额
	private String	NODENUM       ; // 省份代码
	private String	MAB_GUAR_RANGE; // 担保范围
	private String	MAB_GUAR_TYPE ; // 被担保债券种类
	private String	DEBT_SDATE    ; // 履行债务开始日期
	private String	DEBT_EDATE    ; // 履行债务结束日期
	private String	STATUS        ; // 状态

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
	public String getNODENUM() {
		return NODENUM;
	}
	public void setNODENUM(String nODENUM) {
		NODENUM = nODENUM;
	}
	public String getMAB_GUAR_RANGE() {
		return MAB_GUAR_RANGE;
	}
	public void setMAB_GUAR_RANGE(String mAB_GUAR_RANGE) {
		MAB_GUAR_RANGE = mAB_GUAR_RANGE;
	}
	public String getMAB_GUAR_TYPE() {
		return MAB_GUAR_TYPE;
	}
	public void setMAB_GUAR_TYPE(String mAB_GUAR_TYPE) {
		MAB_GUAR_TYPE = mAB_GUAR_TYPE;
	}
	public String getDEBT_SDATE() {
		return DEBT_SDATE;
	}
	public void setDEBT_SDATE(String dEBT_SDATE) {
		DEBT_SDATE = dEBT_SDATE;
	}
	public String getDEBT_EDATE() {
		return DEBT_EDATE;
	}
	public void setDEBT_EDATE(String dEBT_EDATE) {
		DEBT_EDATE = dEBT_EDATE;
	}
	public String getSTATUS() {
		return STATUS;
	}
	public void setSTATUS(String sTATUS) {
		STATUS = sTATUS;
	}
	
}
