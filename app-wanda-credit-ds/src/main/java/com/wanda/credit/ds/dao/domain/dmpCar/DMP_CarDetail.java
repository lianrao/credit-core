package com.wanda.credit.ds.dao.domain.dmpCar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.wanda.credit.base.domain.BaseDomain;

/* 支持城市信息表 */
@Entity
@Table(name = "T_DS_DMP_WEIZHANGCITYRT",schema="CPDB_DS")
@SequenceGenerator(name = "Seq_T_DS_DMP_WEIZHANGCITYRT", sequenceName = "Seq_T_DS_DMP_WEIZHANGCITYRT")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer" })
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@XStreamAlias("carDetail")
public class DMP_CarDetail extends BaseDomain {// 建表
	private static final long serialVersionUID = -1289807774918590425L;
	private long id;
	private String trade_id ;
	private String province_code;
	private String province;
	private String city_code;
	private String city_name;
	private String engine;
	private String engineno;
	private String classa;
	private String classno;
	private DMP_CarCityMain dmpCarmain;

	public DMP_CarDetail() {
		super();
	}

	public DMP_CarDetail(long id,String trade_id,String province_code,String province,
			String city_code,String city_name,String engine,String engineno,String classa
			,String classno,DMP_CarCityMain dmpCarmain) {
		super();
		this.id = id;
		this.trade_id = trade_id;
		this.province_code = province_code;
		this.province = province;		
		this.city_code = city_code;
		this.city_name = city_name;
		this.engine = engine;		
		this.engineno = engineno;
		this.classa = classa;
		this.classno = classno;
		this.dmpCarmain = dmpCarmain;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	/**
	 * 获取 主键
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "Seq_T_DS_DMP_WEIZHANGCITYRT")
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

	public String getProvince_code() {
		return province_code;
	}

	public void setProvince_code(String province_code) {
		this.province_code = province_code;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity_code() {
		return city_code;
	}

	public void setCity_code(String city_code) {
		this.city_code = city_code;
	}

	public String getCity_name() {
		return city_name;
	}

	public void setCity_name(String city_name) {
		this.city_name = city_name;
	}

	public String getEngine() {
		return engine;
	}

	public void setEngine(String engine) {
		this.engine = engine;
	}

	public String getEngineno() {
		return engineno;
	}

	public void setEngineno(String engineno) {
		this.engineno = engineno;
	}

	public String getClassa() {
		return classa;
	}

	public void setClassa(String classa) {
		this.classa = classa;
	}

	public String getClassno() {
		return classno;
	}

	public void setClassno(String classno) {
		this.classno = classno;
	}
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "REFID")
	public DMP_CarCityMain getDmpCarmain() {
		return dmpCarmain;
	}

	public void setDmpCarmain(DMP_CarCityMain dmpCarmain) {
		this.dmpCarmain = dmpCarmain;
	}

}
