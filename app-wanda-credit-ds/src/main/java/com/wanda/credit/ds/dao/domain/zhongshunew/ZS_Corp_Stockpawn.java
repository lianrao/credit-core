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
@Entity(name="ZS_N_Corp_Stockpawn")
@Table(name = "t_ds_zs_new_corp_stockpawn")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer","order","id","updated","created","updatedby","createdby"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@XStreamAlias("alter")
public class ZS_Corp_Stockpawn  extends BaseDomain {
	private static final long serialVersionUID = 1L;
	private String  Id;
	private ZS_Order	ORDER       ;	
	private String	STK_PAWN_REGNO  ; // 登记编号
	private String	STK_PAWN_CZPER  ; // 出质人
	private String	STK_PAWN_CZCERNO; // 出质人证件/证件号
	private String	STK_PAWN_CZAMT  ; // 出质股权数额
	private String	STK_PAWN_ZQPER  ; // 质权人姓名
	private String	STK_PAWN_ZQCERNO; // 质权人证件/证件号
	private String	STK_PAWN_REGDATE; // 质权出质设立登记日期
	private String	STK_PAWN_STATUS ; // 状态
	private String	STK_PAWN_DATE   ; // 公示日期
	private String	URL             ; // 关联内容
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
	public String getSTK_PAWN_REGNO() {
		return STK_PAWN_REGNO;
	}
	public void setSTK_PAWN_REGNO(String sTK_PAWN_REGNO) {
		STK_PAWN_REGNO = sTK_PAWN_REGNO;
	}
	public String getSTK_PAWN_CZPER() {
		return STK_PAWN_CZPER;
	}
	public void setSTK_PAWN_CZPER(String sTK_PAWN_CZPER) {
		STK_PAWN_CZPER = sTK_PAWN_CZPER;
	}
	public String getSTK_PAWN_CZCERNO() {
		return STK_PAWN_CZCERNO;
	}
	public void setSTK_PAWN_CZCERNO(String sTK_PAWN_CZCERNO) {
		STK_PAWN_CZCERNO = sTK_PAWN_CZCERNO;
	}
	public String getSTK_PAWN_CZAMT() {
		return STK_PAWN_CZAMT;
	}
	public void setSTK_PAWN_CZAMT(String sTK_PAWN_CZAMT) {
		STK_PAWN_CZAMT = sTK_PAWN_CZAMT;
	}
	public String getSTK_PAWN_ZQPER() {
		return STK_PAWN_ZQPER;
	}
	public void setSTK_PAWN_ZQPER(String sTK_PAWN_ZQPER) {
		STK_PAWN_ZQPER = sTK_PAWN_ZQPER;
	}
	public String getSTK_PAWN_ZQCERNO() {
		return STK_PAWN_ZQCERNO;
	}
	public void setSTK_PAWN_ZQCERNO(String sTK_PAWN_ZQCERNO) {
		STK_PAWN_ZQCERNO = sTK_PAWN_ZQCERNO;
	}
	public String getSTK_PAWN_REGDATE() {
		return STK_PAWN_REGDATE;
	}
	public void setSTK_PAWN_REGDATE(String sTK_PAWN_REGDATE) {
		STK_PAWN_REGDATE = sTK_PAWN_REGDATE;
	}
	public String getSTK_PAWN_STATUS() {
		return STK_PAWN_STATUS;
	}
	public void setSTK_PAWN_STATUS(String sTK_PAWN_STATUS) {
		STK_PAWN_STATUS = sTK_PAWN_STATUS;
	}
	public String getSTK_PAWN_DATE() {
		return STK_PAWN_DATE;
	}
	public void setSTK_PAWN_DATE(String sTK_PAWN_DATE) {
		STK_PAWN_DATE = sTK_PAWN_DATE;
	}
	public String getURL() {
		return URL;
	}
	public void setURL(String uRL) {
		URL = uRL;
	}
}
