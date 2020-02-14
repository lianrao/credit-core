package com.wanda.credit.ds.dao.domain.dmpCar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.wanda.credit.base.domain.BaseDomain;

/**
 * 国政通学历
 */
@Entity
@Table(name = "T_DS_DMP_WEIZHANGRESULT",schema="CPDB_DS")
@SequenceGenerator(name = "Seq_T_DS_DMP_WEIZHANGRESULT", sequenceName = "Seq_T_DS_DMP_WEIZHANGRESULT")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer" })
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class DMP_carBreak extends BaseDomain {
	private static final long serialVersionUID = 1L;
	private long id;
	private String trade_id;
	private String city;
	private String hphm;
	private String hpzl;
	private String engineno;
	private String classno;
	private String code;
	private String msg;
	private String seq;
	private String province;
	private String date;
	private String area;
	private String act;
	private String acode;
	private String fen;
	private String money;
	private String handled;
	//新增违章城市、文书编号
	private String wzcity;
	private String archiveno;
	/**
	 * 获取 主键
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Seq_T_DS_DMP_WEIZHANGRESULT")
	@Column(name = "ID", unique = true, nullable = false)
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTrade_id() {
		return trade_id;
	}

	public void setTrade_id(String trade_id) {
		this.trade_id = trade_id;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getHphm() {
		return hphm;
	}

	public void setHphm(String hphm) {
		this.hphm = hphm;
	}

	public String getEngineno() {
		return engineno;
	}

	public void setEngineno(String engineno) {
		this.engineno = engineno;
	}

	public String getClassno() {
		return classno;
	}

	public void setClassno(String classno) {
		this.classno = classno;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getSeq() {
		return seq;
	}

	public void setSeq(String seq) {
		this.seq = seq;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getAct() {
		return act;
	}

	public void setAct(String act) {
		this.act = act;
	}

	public String getAcode() {
		return acode;
	}

	public void setAcode(String acode) {
		this.acode = acode;
	}

	public String getFen() {
		return fen;
	}

	public void setFen(String fen) {
		this.fen = fen;
	}

	public String getMoney() {
		return money;
	}

	public void setMoney(String money) {
		this.money = money;
	}

	public String getHandled() {
		return handled;
	}

	public void setHandled(String handled) {
		this.handled = handled;
	}

	public String getHpzl() {
		return hpzl;
	}

	public void setHpzl(String hpzl) {
		this.hpzl = hpzl;
	}
	@Column(name = "ADATE")
	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getWzcity() {
		return wzcity;
	}

	public void setWzcity(String wzcity) {
		this.wzcity = wzcity;
	}

	public String getArchiveno() {
		return archiveno;
	}

	public void setArchiveno(String archiveno) {
		this.archiveno = archiveno;
	}
}
