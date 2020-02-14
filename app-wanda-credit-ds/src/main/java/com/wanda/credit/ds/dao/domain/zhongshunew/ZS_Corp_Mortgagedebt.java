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
@Entity(name="ZS_N_Corp_Mortgagedebt")
@Table(name = "t_ds_zs_new_corp_mortgagedebt")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer","order","id","updated","created","updatedby","createdby"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@XStreamAlias("alter")
public class ZS_Corp_Mortgagedebt  extends BaseDomain {
	private static final long serialVersionUID = 1L;
	private String  Id;
	private ZS_Order	ORDER     ;	
	private String	MAB_REGNO     ; // 登记编号
	private String	MAB_DEBT_TYPE ; // 种类
	private String	MAB_DEBT_RANGE; // 担保范围
	private String	MAB_DEBT_RMK  ; // 备注
	private String	MAB_DEBT_AMT  ; // 数额
	private String	DEBT_SDATE    ; // 履行债务开始日期
	private String	DEBT_EDATE    ; // 履行债务结束日期
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
	public String getMAB_DEBT_TYPE() {
		return MAB_DEBT_TYPE;
	}
	public void setMAB_DEBT_TYPE(String mAB_DEBT_TYPE) {
		MAB_DEBT_TYPE = mAB_DEBT_TYPE;
	}
	public String getMAB_DEBT_RANGE() {
		return MAB_DEBT_RANGE;
	}
	public void setMAB_DEBT_RANGE(String mAB_DEBT_RANGE) {
		MAB_DEBT_RANGE = mAB_DEBT_RANGE;
	}
	public String getMAB_DEBT_RMK() {
		return MAB_DEBT_RMK;
	}
	public void setMAB_DEBT_RMK(String mAB_DEBT_RMK) {
		MAB_DEBT_RMK = mAB_DEBT_RMK;
	}
	public String getMAB_DEBT_AMT() {
		return MAB_DEBT_AMT;
	}
	public void setMAB_DEBT_AMT(String mAB_DEBT_AMT) {
		MAB_DEBT_AMT = mAB_DEBT_AMT;
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
}
