/**   
* @Description: 电商原始数据-基本信息 
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
@Table(name = "CPDB_DS.T_DS_JXL_EBUSI_BASIC_INFO")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class EbusiBasicPojo extends BaseDomain {
	

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
	private String token;
	@Column(name = "deal_time")
	private String update_time;
	private String cell_phone;
	private String email;
	private String security_level;
	@Column(name = "levels")
	private String level;
	private String is_validate_real_name;
	private String website_id;
	private String nickname;
	private String register_date;
	private String real_name;
	private String version;
	private String datasource;
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "FK_SEQID")
	private Set<EBusiAddrPojo> address = new HashSet<EBusiAddrPojo>();
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "FK_SEQID")
	private Set<EbusiTransPojo> transactions = new HashSet<EbusiTransPojo>();
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
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
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
	public String getSecurity_level() {
		return security_level;
	}
	public void setSecurity_level(String security_level) {
		this.security_level = security_level;
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	public String getIs_validate_real_name() {
		return is_validate_real_name;
	}
	public void setIs_validate_real_name(String is_validate_real_name) {
		this.is_validate_real_name = is_validate_real_name;
	}
	public String getWebsite_id() {
		return website_id;
	}
	public void setWebsite_id(String website_id) {
		this.website_id = website_id;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getRegister_date() {
		return register_date;
	}
	public void setRegister_date(String register_date) {
		this.register_date = register_date;
	}
	public String getReal_name() {
		return real_name;
	}
	public void setReal_name(String real_name) {
		this.real_name = real_name;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getDatasource() {
		return datasource;
	}
	public void setDatasource(String datasource) {
		this.datasource = datasource;
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
	public Set<EBusiAddrPojo> getAddress() {
		return address;
	}
	public void setAddress(Set<EBusiAddrPojo> address) {
		this.address = address;
	}
	public Set<EbusiTransPojo> getTransactions() {
		return transactions;
	}
	public void setTransactions(Set<EbusiTransPojo> transactions) {
		this.transactions = transactions;
	}
	
	

}
