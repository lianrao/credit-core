/**   
* @Description: 快钱-反欺诈-请求参数表
* @author xiaobin.hou  
* @date 2016年8月23日 下午7:46:25 
* @version V1.0   
*/
package com.wanda.credit.ds.dao.domain.antifraud;

import java.util.Date;

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
 * @author xiaobin.hou
 *
 */
@Entity
@Table(name = "CPDB_DS.T_DS_BS_Antifraud_req")
@SequenceGenerator(name="Seq_T_DS_BS_Antifraud_req",sequenceName="CPDB_DS.Seq_T_DS_BS_Antifraud_req")  
@JsonIgnoreProperties(value = { "hibernateLazyInitializer"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class AntifraudReq extends BaseDomain {
	
	
	private static final long serialVersionUID = 8254391090925439643L;
	/**
	 * 获取 主键
	 */
	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="Seq_T_DS_BS_Antifraud_req")  
	@Column(name = "ID", unique = true, nullable = false)
	private long id;
	private String trade_id;
	private String frms_biz_key;
	private String frms_trans_time;
	private String frms_biz_code;
	private String frms_comp_city;
	private String frms_comp_district;
	private String frms_comp_province;
	private String frms_company_addr;
	private String frms_company_name;
	private String frms_company_tele;
	private String frms_user_id;
	private String frms_user_name;
	private String frms_id_no;
	private String frms_phone_no;
	private String frms_marry_status;
	private String frms_degree;
	private String frms_house_province;
	private String frms_house_city;
	private String frms_house_district;
	private String frms_house_address;
	private String frms_house_phone;
	private String frms_contact_name;
	private String frms_contact_phone;
	private String frms_contact_relation;
	private String frms_mobie_type;
	private String frms_bank_card_no;
	private String frms_bank_name;
	private String frms_card_type;
	private String frms_bank_card_phone;
	private String frms_bank_province;
	private String frms_bank_city;
	private String frms_bank_user_name;
	private String frms_device_id;
	private String frms_trans_ip;
	private String frms_locationx;
	private String frms_locationy;
	private Date create_date;
	private Date update_date;
	
	
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
	public String getFrms_biz_key() {
		return frms_biz_key;
	}
	public void setFrms_biz_key(String frms_biz_key) {
		this.frms_biz_key = frms_biz_key;
	}
	public String getFrms_trans_time() {
		return frms_trans_time;
	}
	public void setFrms_trans_time(String frms_trans_time) {
		this.frms_trans_time = frms_trans_time;
	}
	public String getFrms_biz_code() {
		return frms_biz_code;
	}
	public void setFrms_biz_code(String frms_biz_code) {
		this.frms_biz_code = frms_biz_code;
	}
	public String getFrms_comp_city() {
		return frms_comp_city;
	}
	public void setFrms_comp_city(String frms_comp_city) {
		this.frms_comp_city = frms_comp_city;
	}
	public String getFrms_comp_district() {
		return frms_comp_district;
	}
	public void setFrms_comp_district(String frms_comp_district) {
		this.frms_comp_district = frms_comp_district;
	}
	public String getFrms_comp_province() {
		return frms_comp_province;
	}
	public void setFrms_comp_province(String frms_comp_province) {
		this.frms_comp_province = frms_comp_province;
	}
	public String getFrms_company_addr() {
		return frms_company_addr;
	}
	public void setFrms_company_addr(String frms_company_addr) {
		this.frms_company_addr = frms_company_addr;
	}
	public String getFrms_company_name() {
		return frms_company_name;
	}
	public void setFrms_company_name(String frms_company_name) {
		this.frms_company_name = frms_company_name;
	}
	public String getFrms_company_tele() {
		return frms_company_tele;
	}
	public void setFrms_company_tele(String frms_company_tele) {
		this.frms_company_tele = frms_company_tele;
	}
	public String getFrms_user_id() {
		return frms_user_id;
	}
	public void setFrms_user_id(String frms_user_id) {
		this.frms_user_id = frms_user_id;
	}
	public String getFrms_user_name() {
		return frms_user_name;
	}
	public void setFrms_user_name(String frms_user_name) {
		this.frms_user_name = frms_user_name;
	}
	public String getFrms_id_no() {
		return frms_id_no;
	}
	public void setFrms_id_no(String frms_id_no) {
		this.frms_id_no = frms_id_no;
	}
	public String getFrms_phone_no() {
		return frms_phone_no;
	}
	public void setFrms_phone_no(String frms_phone_no) {
		this.frms_phone_no = frms_phone_no;
	}
	public String getFrms_marry_status() {
		return frms_marry_status;
	}
	public void setFrms_marry_status(String frms_marry_status) {
		this.frms_marry_status = frms_marry_status;
	}
	public String getFrms_degree() {
		return frms_degree;
	}
	public void setFrms_degree(String frms_degree) {
		this.frms_degree = frms_degree;
	}
	public String getFrms_house_province() {
		return frms_house_province;
	}
	public void setFrms_house_province(String frms_house_province) {
		this.frms_house_province = frms_house_province;
	}
	public String getFrms_house_city() {
		return frms_house_city;
	}
	public void setFrms_house_city(String frms_house_city) {
		this.frms_house_city = frms_house_city;
	}
	public String getFrms_house_district() {
		return frms_house_district;
	}
	public void setFrms_house_district(String frms_house_district) {
		this.frms_house_district = frms_house_district;
	}
	public String getFrms_house_address() {
		return frms_house_address;
	}
	public void setFrms_house_address(String frms_house_address) {
		this.frms_house_address = frms_house_address;
	}
	public String getFrms_house_phone() {
		return frms_house_phone;
	}
	public void setFrms_house_phone(String frms_house_phone) {
		this.frms_house_phone = frms_house_phone;
	}
	public String getFrms_contact_name() {
		return frms_contact_name;
	}
	public void setFrms_contact_name(String frms_contact_name) {
		this.frms_contact_name = frms_contact_name;
	}
	public String getFrms_contact_phone() {
		return frms_contact_phone;
	}
	public void setFrms_contact_phone(String frms_contact_phone) {
		this.frms_contact_phone = frms_contact_phone;
	}
	public String getFrms_contact_relation() {
		return frms_contact_relation;
	}
	public void setFrms_contact_relation(String frms_contact_relation) {
		this.frms_contact_relation = frms_contact_relation;
	}
	public String getFrms_mobie_type() {
		return frms_mobie_type;
	}
	public void setFrms_mobie_type(String frms_mobie_type) {
		this.frms_mobie_type = frms_mobie_type;
	}
	public String getFrms_bank_card_no() {
		return frms_bank_card_no;
	}
	public void setFrms_bank_card_no(String frms_bank_card_no) {
		this.frms_bank_card_no = frms_bank_card_no;
	}
	public String getFrms_bank_name() {
		return frms_bank_name;
	}
	public void setFrms_bank_name(String frms_bank_name) {
		this.frms_bank_name = frms_bank_name;
	}
	public String getFrms_card_type() {
		return frms_card_type;
	}
	public void setFrms_card_type(String frms_card_type) {
		this.frms_card_type = frms_card_type;
	}
	public String getFrms_bank_card_phone() {
		return frms_bank_card_phone;
	}
	public void setFrms_bank_card_phone(String frms_bank_card_phone) {
		this.frms_bank_card_phone = frms_bank_card_phone;
	}
	public String getFrms_bank_province() {
		return frms_bank_province;
	}
	public void setFrms_bank_province(String frms_bank_province) {
		this.frms_bank_province = frms_bank_province;
	}
	public String getFrms_bank_city() {
		return frms_bank_city;
	}
	public void setFrms_bank_city(String frms_bank_city) {
		this.frms_bank_city = frms_bank_city;
	}
	public String getFrms_bank_user_name() {
		return frms_bank_user_name;
	}
	public void setFrms_bank_user_name(String frms_bank_user_name) {
		this.frms_bank_user_name = frms_bank_user_name;
	}
	public String getFrms_device_id() {
		return frms_device_id;
	}
	public void setFrms_device_id(String frms_device_id) {
		this.frms_device_id = frms_device_id;
	}
	public String getFrms_trans_ip() {
		return frms_trans_ip;
	}
	public void setFrms_trans_ip(String frms_trans_ip) {
		this.frms_trans_ip = frms_trans_ip;
	}
	public String getFrms_locationx() {
		return frms_locationx;
	}
	public void setFrms_locationx(String frms_locationx) {
		this.frms_locationx = frms_locationx;
	}
	public String getFrms_locationy() {
		return frms_locationy;
	}
	public void setFrms_locationy(String frms_locationy) {
		this.frms_locationy = frms_locationy;
	}
	public Date getCreate_date() {
		return create_date;
	}
	public void setCreate_date(Date create_date) {
		this.create_date = create_date;
	}
	public Date getUpdate_date() {
		return update_date;
	}
	public void setUpdate_date(Date update_date) {
		this.update_date = update_date;
	}	

	



}
