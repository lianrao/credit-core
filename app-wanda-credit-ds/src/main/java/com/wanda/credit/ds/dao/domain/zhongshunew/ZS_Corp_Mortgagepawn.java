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
@Entity(name="ZS_N_Corp_Mortgagepawn")
@Table(name = "t_ds_zs_new_corp_mortgagepawn")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer","order","id","updated","created","updatedby","createdby"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@XStreamAlias("alter")
public class ZS_Corp_Mortgagepawn  extends BaseDomain {
	private static final long serialVersionUID = 1L;
	private String  Id;
	private ZS_Order	ORDER       ;	
	private String	MAB_REGNO       ; // 登记编号
	private String	MAB_PAWN_NAME   ; // 名称
	private String	MAB_PAWN_OWNER  ; // 所有权或使用权归属
	private String	MAB_PAWN_DETAILS; // 数量、质量、状况、所在地等情况
	private String	MAB_PAWN_RMK    ; // 备注
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
	public String getMAB_PAWN_NAME() {
		return MAB_PAWN_NAME;
	}
	public void setMAB_PAWN_NAME(String mAB_PAWN_NAME) {
		MAB_PAWN_NAME = mAB_PAWN_NAME;
	}
	public String getMAB_PAWN_OWNER() {
		return MAB_PAWN_OWNER;
	}
	public void setMAB_PAWN_OWNER(String mAB_PAWN_OWNER) {
		MAB_PAWN_OWNER = mAB_PAWN_OWNER;
	}
	public String getMAB_PAWN_DETAILS() {
		return MAB_PAWN_DETAILS;
	}
	public void setMAB_PAWN_DETAILS(String mAB_PAWN_DETAILS) {
		MAB_PAWN_DETAILS = mAB_PAWN_DETAILS;
	}
	public String getMAB_PAWN_RMK() {
		return MAB_PAWN_RMK;
	}
	public void setMAB_PAWN_RMK(String mAB_PAWN_RMK) {
		MAB_PAWN_RMK = mAB_PAWN_RMK;
	}
	
}
