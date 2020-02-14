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
@Entity(name="ZS_N_Corp_Stockpawnalt")
@Table(name = "t_ds_zs_new_corp_stockpawnalt")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer","order","id","updated","created","updatedby","createdby"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@XStreamAlias("alter")
public class ZS_Corp_Stockpawnalt  extends BaseDomain {
	private static final long serialVersionUID = 1L;
	private String  Id;
	private ZS_Order	ORDER    ;	
	private String  STK_PAWN_BGNR; // 变更内容
	private String	STK_PAWN_BGRQ; // 变更日期
	private String	URL          ; // 关联内容
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
	public String getSTK_PAWN_BGNR() {
		return STK_PAWN_BGNR;
	}
	public void setSTK_PAWN_BGNR(String sTK_PAWN_BGNR) {
		STK_PAWN_BGNR = sTK_PAWN_BGNR;
	}
	public String getSTK_PAWN_BGRQ() {
		return STK_PAWN_BGRQ;
	}
	public void setSTK_PAWN_BGRQ(String sTK_PAWN_BGRQ) {
		STK_PAWN_BGRQ = sTK_PAWN_BGRQ;
	}
	public String getURL() {
		return URL;
	}
	public void setURL(String uRL) {
		URL = uRL;
	}
}
