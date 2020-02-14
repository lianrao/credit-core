package com.wanda.credit.ds.dao.domain.juxinli.apply;

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
 * 运营商&电商 用户基本信息
 * @author xiaobin.hou
 *
 */
@Entity
@Table(name = "CPDB_DS.T_DS_JXL_ORIG_APPLY_INFO")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ApplyBasicInfoPojo extends BaseDomain {
	
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
	private String remark;
	private String name;
	private String id_card_no;
	private String cell_phone;
	private String cell_phone2;
	private String home_addr;
	private String work_tel;
	private String work_addr;
	private String home_tel;
	private String skip_mobile;
	private String success;
	private Date crt_time;
	private Date upd_time;
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "FK_SEQID")
	private Set<ApplyContactPojo> contactPojoSet = new HashSet<ApplyContactPojo>();
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "FK_SEQID")
	private Set<ApplyWebsitPojo> websitPojoSet = new HashSet<ApplyWebsitPojo>();
	
	
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
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getId_card_no() {
		return id_card_no;
	}
	public void setId_card_no(String id_card_no) {
		this.id_card_no = id_card_no;
	}
	public String getCell_phone() {
		return cell_phone;
	}
	public void setCell_phone(String cell_phone) {
		this.cell_phone = cell_phone;
	}
	public String getCell_phone2() {
		return cell_phone2;
	}
	public void setCell_phone2(String cell_phone2) {
		this.cell_phone2 = cell_phone2;
	}
	public String getHome_addr() {
		return home_addr;
	}
	public void setHome_addr(String home_addr) {
		this.home_addr = home_addr;
	}
	public String getWork_tel() {
		return work_tel;
	}
	public void setWork_tel(String work_tel) {
		this.work_tel = work_tel;
	}
	public String getHome_tel() {
		return home_tel;
	}
	public void setHome_tel(String home_tel) {
		this.home_tel = home_tel;
	}
	public String getSkip_mobile() {
		return skip_mobile;
	}
	public void setSkip_mobile(String skip_mobile) {
		this.skip_mobile = skip_mobile;
	}
	public String getSuccess() {
		return success;
	}
	public void setSuccess(String success) {
		this.success = success;
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
	public Set<ApplyContactPojo> getContactPojoSet() {
		return contactPojoSet;
	}
	public void setContactPojoSet(Set<ApplyContactPojo> contactPojoSet) {
		this.contactPojoSet = contactPojoSet;
	}
	public Set<ApplyWebsitPojo> getWebsitPojoSet() {
		return websitPojoSet;
	}
	public void setWebsitPojoSet(Set<ApplyWebsitPojo> websitPojoSet) {
		this.websitPojoSet = websitPojoSet;
	}
	public String getWork_addr() {
		return work_addr;
	}
	public void setWork_addr(String work_addr) {
		this.work_addr = work_addr;
	}
	
	

}
