package com.wanda.credit.ds.dao.domain.yidao;

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
 * 营业执照信息
 * 
 * @author shenziqiang
 *
 */

@Entity
@Table(name = "T_DS_YIDAO_VECARD_RESULT")
@SequenceGenerator(name = "SEQ_T_DS_YIDAO_VECARD_RESULT", sequenceName = "SEQ_T_DS_YIDAO_VECARD_RESULT")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Yidao_veCard_result extends BaseDomain {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long id;
	private String trade_id;
	private String recotype;
	private String req_image;
	private String error;
	private String details;
	private String plateno;
	private String  vehicletype;
	private String  veaddress;
	private String  usecharacter;
	private String engineno;
	private String  model ;
	private String vin ;
	private String  registerdate ;
	private String issuedate;
	private String cropped_image;
	private String owner;
	
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	public String getCropped_image() {
		return cropped_image;
	}
	public void setCropped_image(String cropped_image) {
		this.cropped_image = cropped_image;
	}
	/**
	 * 获取 主键
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_T_DS_YIDAO_VECARD_RESULT")
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
	public String getRecotype() {
		return recotype;
	}
	public void setRecotype(String recotype) {
		this.recotype = recotype;
	}
	public String getReq_image() {
		return req_image;
	}
	public void setReq_image(String req_image) {
		this.req_image = req_image;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public String getDetails() {
		return details;
	}
	public void setDetails(String details) {
		this.details = details;
	}
	public String getPlateno() {
		return plateno;
	}
	public void setPlateno(String plateno) {
		this.plateno = plateno;
	}
	public String getVehicletype() {
		return vehicletype;
	}
	public void setVehicletype(String vehicletype) {
		this.vehicletype = vehicletype;
	}
	public String getVeaddress() {
		return veaddress;
	}
	public void setVeaddress(String veaddress) {
		this.veaddress = veaddress;
	}
	public String getUsecharacter() {
		return usecharacter;
	}
	public void setUsecharacter(String usecharacter) {
		this.usecharacter = usecharacter;
	}
	public String getEngineno() {
		return engineno;
	}
	public void setEngineno(String engineno) {
		this.engineno = engineno;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public String getVin() {
		return vin;
	}
	public void setVin(String vin) {
		this.vin = vin;
	}
	public String getRegisterdate() {
		return registerdate;
	}
	public void setRegisterdate(String registerdate) {
		this.registerdate = registerdate;
	}
	public String getIssuedate() {
		return issuedate;
	}
	public void setIssuedate(String issuedate) {
		this.issuedate = issuedate;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
}
