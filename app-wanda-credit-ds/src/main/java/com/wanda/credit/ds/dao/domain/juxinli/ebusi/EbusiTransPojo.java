/**   
* @Description: 电商原始数据-交易记录 
* @author xiaobin.hou  
* @date 2016年6月7日 下午4:15:08 
* @version V1.0   
*/
package com.wanda.credit.ds.dao.domain.juxinli.ebusi;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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
@Table(name = "CPDB_DS.T_DS_JXL_EBUSI_TRANS")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class EbusiTransPojo extends BaseDomain {
	

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
	private String trans_time;
	@Column(name = "deal_time")
	private String update_time;
	private String total_price;
	private String receiver_addr;
	private String order_id;
	private String receiver_name;
	private String bill_type;
	private String zipcode;
	private String reveiver_title;
	private String deliver_fee;
	private String receiver_cell_phone;
	private String payment_type;
	private String is_success;
	private String deliver_type;
	private String reveiver_phone;
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "FK_SEQID")
	private Set<EbusiItemPojo> items = new HashSet<EbusiItemPojo>();
	@ManyToOne(cascade = {CascadeType.MERGE,CascadeType.REFRESH }, optional = true) 
	@JoinColumn(name="FK_SEQID",referencedColumnName="seqId")
	private EbusiBasicPojo basicPojo;
	private Date crt_time;
	private Date upd_time;
	
	
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
	public String getTrans_time() {
		return trans_time;
	}
	public void setTrans_time(String trans_time) {
		this.trans_time = trans_time;
	}
	public String getTotal_price() {
		return total_price;
	}
	public void setTotal_price(String total_price) {
		this.total_price = total_price;
	}
	public String getReceiver_addr() {
		return receiver_addr;
	}
	public void setReceiver_addr(String receiver_addr) {
		this.receiver_addr = receiver_addr;
	}
	public String getOrder_id() {
		return order_id;
	}
	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}
	public String getReceiver_name() {
		return receiver_name;
	}
	public void setReceiver_name(String receiver_name) {
		this.receiver_name = receiver_name;
	}
	public String getBill_type() {
		return bill_type;
	}
	public void setBill_type(String bill_type) {
		this.bill_type = bill_type;
	}
	public String getZipcode() {
		return zipcode;
	}
	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}
	public String getReveiver_title() {
		return reveiver_title;
	}
	public void setReveiver_title(String reveiver_title) {
		this.reveiver_title = reveiver_title;
	}
	public String getDeliver_fee() {
		return deliver_fee;
	}
	public void setDeliver_fee(String deliver_fee) {
		this.deliver_fee = deliver_fee;
	}
	public String getReceiver_cell_phone() {
		return receiver_cell_phone;
	}
	public void setReceiver_cell_phone(String receiver_cell_phone) {
		this.receiver_cell_phone = receiver_cell_phone;
	}

	public String getIs_success() {
		return is_success;
	}
	public void setIs_success(String is_success) {
		this.is_success = is_success;
	}
	public String getDeliver_type() {
		return deliver_type;
	}
	public void setDeliver_type(String deliver_type) {
		this.deliver_type = deliver_type;
	}
	public String getReveiver_phone() {
		return reveiver_phone;
	}
	public void setReveiver_phone(String reveiver_phone) {
		this.reveiver_phone = reveiver_phone;
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
	public String getUpdate_time() {
		return update_time;
	}
	public void setUpdate_time(String update_time) {
		this.update_time = update_time;
	}
	public Set<EbusiItemPojo> getItems() {
		return items;
	}
	public void setItems(Set<EbusiItemPojo> items) {
		this.items = items;
	}
	public EbusiBasicPojo getBasicPojo() {
		return basicPojo;
	}
	public void setBasicPojo(EbusiBasicPojo basicPojo) {
		this.basicPojo = basicPojo;
	}
	public String getPayment_type() {
		return payment_type;
	}
	public void setPayment_type(String payment_type) {
		this.payment_type = payment_type;
	}
	
	
	

}
