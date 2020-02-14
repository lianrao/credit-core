/**   
* @Description: 电商原始数据-地址信息
* @author xiaobin.hou  
* @date 2016年6月7日 下午4:01:27 
* @version V1.0   
*/
package com.wanda.credit.ds.dao.domain.juxinli.ebusi;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.annotations.GenericGenerator;

import com.wanda.credit.base.domain.BaseDomain;

/**
 * @author xiaobin.hou
 *
 */
@Entity
@Table(name = "CPDB_DS.T_DS_JXL_EBUSI_ADDR")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class EBusiAddrPojo extends BaseDomain {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * 获取 主键
	 */
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	@Column(name = "SEQID", unique = true, nullable = false, length = 32)
	private String seqId;
	private String requestId;
	private String cell_phone;
	private String email;
	@Column(name = "deal_time")
	private String update_time;
	private String receiver_addr;
	private String province;
	private String city;
	private String is_default_address;
	private String zipcode;
	private String payment_type;
	private String receiver_name;
	private String receiver_phone;
	private String receiver_title;
	private String receiver_cell_phone;
	private Date crt_time;
	private Date upd_time;
	@ManyToOne(cascade = {CascadeType.MERGE,CascadeType.REFRESH }, optional = true) 
	@JoinColumn(name="FK_SEQID",referencedColumnName="seqId")
	private EbusiBasicPojo basicPojo;
	
	
	public String getSeqId() {
		return seqId;
	}
	public void setSeqId(String seqId) {
		this.seqId = seqId;
	}
	public String getRequestId() {
		return requestId;
	}
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	public String getReceiver_addr() {
		return receiver_addr;
	}
	public void setReceiver_addr(String receiver_addr) {
		this.receiver_addr = receiver_addr;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getIs_default_address() {
		return is_default_address;
	}
	public void setIs_default_address(String is_default_address) {
		this.is_default_address = is_default_address;
	}
	public String getZipcode() {
		return zipcode;
	}
	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}
	public String getPayment_type() {
		return payment_type;
	}
	public void setPayment_type(String payment_type) {
		this.payment_type = payment_type;
	}
	public String getReceiver_name() {
		return receiver_name;
	}
	public void setReceiver_name(String receiver_name) {
		this.receiver_name = receiver_name;
	}
	public String getReceiver_phone() {
		return receiver_phone;
	}
	public void setReceiver_phone(String receiver_phone) {
		this.receiver_phone = receiver_phone;
	}
	public String getReceiver_title() {
		return receiver_title;
	}
	public void setReceiver_title(String receiver_title) {
		this.receiver_title = receiver_title;
	}
	public String getReceiver_cell_phone() {
		return receiver_cell_phone;
	}
	public void setReceiver_cell_phone(String receiver_cell_phone) {
		this.receiver_cell_phone = receiver_cell_phone;
	}
	public Date getCrt_time() {
		return crt_time;
	}
	public void setCrt_time(Date crt_time) {
		this.crt_time = crt_time;
	}
	public Date getUpd_time() {
		return upd_time;
	}
	public void setUpd_time(Date upd_time) {
		this.upd_time = upd_time;
	}
	public String getCell_phone() {
		return cell_phone;
	}
	public void setCell_phone(String cell_phone) {
		this.cell_phone = cell_phone;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getUpdate_time() {
		return update_time;
	}
	public void setUpdate_time(String update_time) {
		this.update_time = update_time;
	}
	public EbusiBasicPojo getBasicPojo() {
		return basicPojo;
	}
	public void setBasicPojo(EbusiBasicPojo basicPojo) {
		this.basicPojo = basicPojo;
	}
	
	

}
