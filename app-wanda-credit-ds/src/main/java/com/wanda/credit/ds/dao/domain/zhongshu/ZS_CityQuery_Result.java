package com.wanda.credit.ds.dao.domain.zhongshu;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import com.wanda.credit.base.domain.BaseDomain;
@Entity
@Table(name = "T_DS_DMPCXY_CARIEG_QRY")
@SequenceGenerator(name = "SEQ_T_DS_DMPCXY_CARIEG_QRY", sequenceName = "SEQ_T_DS_DMPCXY_CARIEG_QRY")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ZS_CityQuery_Result  extends BaseDomain {
	private static final long serialVersionUID = 1L;
	private long id;
	
	private String seq;								
	private String provinceId;						
	private String provinceName;					
	private String provincePrefix;					
	private String CityID;							
	private String CityName;						
	private String Name;						
	private String CarNumberPrefix;					
	private String CarCodeLen;						
	private String CarEngineLen;				
	private String CarOwnerLen;
	private String ProxyEnable;
	private String trade_id;

	/**
	 * 获取 主键
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_T_DS_DMPCXY_CARIEG_QRY")
	@Column(name = "ID", unique = true, nullable = false)
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getSeq() {
		return seq;
	}
	public void setSeq(String seq) {
		this.seq = seq;
	}
	public String getProvinceId() {
		return provinceId;
	}
	public void setProvinceId(String provinceId) {
		this.provinceId = provinceId;
	}
	public String getProvinceName() {
		return provinceName;
	}
	public void setProvinceName(String provinceName) {
		this.provinceName = provinceName;
	}
	public String getProvincePrefix() {
		return provincePrefix;
	}
	public void setProvincePrefix(String provincePrefix) {
		this.provincePrefix = provincePrefix;
	}
	@Column(name="CITYID")
	public String getCityID() {
		return CityID;
	}
	public void setCityID(String cityID) {
		CityID = cityID;
	}
	@Column(name="CITYNAME")
	public String getCityName() {
		return CityName;
	}
	public void setCityName(String cityName) {
		CityName = cityName;
	}
	@Column(name="NAME")
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	@Column(name="CARNUMBERPREFIX")
	public String getCarNumberPrefix() {
		return CarNumberPrefix;
	}
	public void setCarNumberPrefix(String carNumberPrefix) {
		CarNumberPrefix = carNumberPrefix;
	}
	@Column(name="CARCODELEN")
	public String getCarCodeLen() {
		return CarCodeLen;
	}
	public void setCarCodeLen(String carCodeLen) {
		CarCodeLen = carCodeLen;
	}
	@Column(name="CARENGINELEN")
	public String getCarEngineLen() {
		return CarEngineLen;
	}
	public void setCarEngineLen(String carEngineLen) {
		CarEngineLen = carEngineLen;
	}
	@Column(name="CAROWNERLEN")
	public String getCarOwnerLen() {
		return CarOwnerLen;
	}
	public void setCarOwnerLen(String carOwnerLen) {
		CarOwnerLen = carOwnerLen;
	}
	@Column(name="PROXYENABLE")
	public String getProxyEnable() {
		return ProxyEnable;
	}
	public void setProxyEnable(String proxyEnable) {
		ProxyEnable = proxyEnable;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public String getTrade_id() {
		return trade_id;
	}
	public void setTrade_id(String trade_id) {
		this.trade_id = trade_id;
	}
}
