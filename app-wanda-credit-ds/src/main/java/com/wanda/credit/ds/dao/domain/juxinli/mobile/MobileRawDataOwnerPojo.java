package com.wanda.credit.ds.dao.domain.juxinli.mobile;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.annotations.GenericGenerator;

import com.wanda.credit.base.domain.BaseDomain;
/**
 * 运营商原始数据-用户信息
 * @author xiaobin.hou
 *
 */
@Entity
@Table(name = "CPDB_DS.T_DS_JXL_OPER_OWNER_INFO")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class MobileRawDataOwnerPojo extends BaseDomain {
	
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
	private String version;
	private String datasource;
	@Column(name="deal_time")
	private String update_time;
	private String cell_phone;
	@Column(name="id_card_no")
	private String idcard;
	private String reg_time;
	private String real_name;
	private String error_code;
	private String error_msg;
	private Date crt_date;
	private Date upd_date;
	
//	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//	@JoinColumn(name = "FK_SEQID")
//	private Set<MobileRawDataCallPojo> callSet = new HashSet<MobileRawDataCallPojo>();
//	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//	@JoinColumn(name = "FK_SEQID")
//	private Set<MobileRawDataSmsesPojo> smseSet = new HashSet<MobileRawDataSmsesPojo>();
//	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//	@JoinColumn(name = "FK_SEQID")
//	private Set<MobileRawDataNetPojo> netSet = new HashSet<MobileRawDataNetPojo>();
//	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//	@JoinColumn(name = "FK_SEQID")
//	private Set<MobileRawDataAccountPojo> accountSet = new HashSet<MobileRawDataAccountPojo>();
	@Transient 
	private Set<MobileRawDataCallPojo> callSet = new HashSet<MobileRawDataCallPojo>();
	@Transient
	private Set<MobileRawDataSmsesPojo> smseSet = new HashSet<MobileRawDataSmsesPojo>();
	@Transient
	private Set<MobileRawDataNetPojo> netSet = new HashSet<MobileRawDataNetPojo>();
	@Transient
	private Set<MobileRawDataAccountPojo> accountSet = new HashSet<MobileRawDataAccountPojo>();
	
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
	public String getCell_phone() {
		return cell_phone;
	}
	public void setCell_phone(String cell_phone) {
		this.cell_phone = cell_phone;
	}
	public String getReg_time() {
		return reg_time;
	}
	public void setReg_time(String reg_time) {
		this.reg_time = reg_time;
	}
	public String getReal_name() {
		return real_name;
	}
	public void setReal_name(String real_name) {
		this.real_name = real_name;
	}
	public String getError_code() {
		return error_code;
	}
	public void setError_code(String error_code) {
		this.error_code = error_code;
	}
	public String getError_msg() {
		return error_msg;
	}
	public void setError_msg(String error_msg) {
		this.error_msg = error_msg;
	}
	public Date getCrt_date() {
		return crt_date;
	}
	public void setCrt_date(Date crt_date) {
		this.crt_date = crt_date;
	}
	public Date getUpd_date() {
		return upd_date;
	}
	public void setUpd_date(Date upd_date) {
		this.upd_date = upd_date;
	}
	public Set<MobileRawDataCallPojo> getCallSet() {
		return callSet;
	}
	public void setCallSet(Set<MobileRawDataCallPojo> callSet) {
		this.callSet = callSet;
	}
	public Set<MobileRawDataSmsesPojo> getSmseSet() {
		return smseSet;
	}
	public void setSmseSet(Set<MobileRawDataSmsesPojo> smseSet) {
		this.smseSet = smseSet;
	}
	public Set<MobileRawDataNetPojo> getNetSet() {
		return netSet;
	}
	public void setNetSet(Set<MobileRawDataNetPojo> netSet) {
		this.netSet = netSet;
	}
	public Set<MobileRawDataAccountPojo> getAccountSet() {
		return accountSet;
	}
	public void setAccountSet(Set<MobileRawDataAccountPojo> accountSet) {
		this.accountSet = accountSet;
	}
	public String getIdcard() {
		return idcard;
	}
	public void setIdcard(String idcard) {
		this.idcard = idcard;
	}
	public String getUpdate_time() {
		return update_time;
	}
	public void setUpdate_time(String update_time) {
		this.update_time = update_time;
	}

}
