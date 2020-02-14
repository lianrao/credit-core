/**   
* @Description: 提交采集请求信息表 
* @author xiaobin.hou  
* @date 2016年7月20日 下午3:48:57 
* @version V1.0   
*/
package com.wanda.credit.ds.dao.domain.juxinli.creditCardBill;

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
@Table(name = "CPDB_DS.T_DS_JXL_CREDIT_APPLY_INFO")
@SequenceGenerator(name="SEQ_JXL_CREDIT_APPLY_INFO",sequenceName="CPDB_DS.SEQ_JXL_CREDIT_APPLY_INFO")  
@JsonIgnoreProperties(value = { "hibernateLazyInitializer"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class CreditCardApplyPojo extends BaseDomain {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * 获取 主键
	 */
	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="SEQ_JXL_CREDIT_APPLY_INFO")  
	@Column(name = "SEQID", unique = true, nullable = false)
	private long seqId;
	private String requestId;
	private String token;
	private String name;
	private String cell_phone;
	private String id_card_no;
	private String email;
	private String loginType;
	private String status;
	private String process_code;
	private String apply_type;
	private String content;
	private String load_data;
	private Date create_date;
	private Date update_date;
	
	
	public long getSeqId() {
		return seqId;
	}
	public void setSeqId(long seqId) {
		this.seqId = seqId;
	}
	public String getRequestId() {
		return requestId;
	}
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCell_phone() {
		return cell_phone;
	}
	public void setCell_phone(String cell_phone) {
		this.cell_phone = cell_phone;
	}
	public String getId_card_no() {
		return id_card_no;
	}
	public void setId_card_no(String id_card_no) {
		this.id_card_no = id_card_no;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getLoginType() {
		return loginType;
	}
	public void setLoginType(String loginType) {
		this.loginType = loginType;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getProcess_code() {
		return process_code;
	}
	public void setProcess_code(String process_code) {
		this.process_code = process_code;
	}
	public String getApply_type() {
		return apply_type;
	}
	public void setApply_type(String apply_type) {
		this.apply_type = apply_type;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
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
	public String getLoad_data() {
		return load_data;
	}
	public void setLoad_data(String load_data) {
		this.load_data = load_data;
	}

	
}
