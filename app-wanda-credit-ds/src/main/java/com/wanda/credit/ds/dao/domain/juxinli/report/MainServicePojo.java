package com.wanda.credit.ds.dao.domain.juxinli.report;

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
 * 常用服务列表
 * @author xiaobin.hou
 *
 */
@Entity
@Table(name = "CPDB_DS.T_DS_JXL_REP_MAIN_SERVICE")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class MainServicePojo extends BaseDomain{

	private static final long serialVersionUID = 1L;
	
	private String seqId;
	private String requestId;
	private String company_name;
	private String company_type;
	private String total_service_cnt;
	private Date crt_time;
	private Date upd_time;
	private Set<ServiceDetailPojo> service_details = new HashSet<ServiceDetailPojo>();
	
	/**
	 * 获取 主键
	 */
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	@Column(name = "SEQID", unique = true, nullable = false, length = 32)
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
	public String getCompany_name() {
		return company_name;
	}
	public void setCompany_name(String company_name) {
		this.company_name = company_name;
	}
	public String getCompany_type() {
		return company_type;
	}
	public void setCompany_type(String company_type) {
		this.company_type = company_type;
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
	@Column(name="SERVICE_CNT")
	public String getTotal_service_cnt() {
		return total_service_cnt;
	}
	public void setTotal_service_cnt(String total_service_cnt) {
		this.total_service_cnt = total_service_cnt;
	}
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name="FK_SEQID")
	public Set<ServiceDetailPojo> getService_details() {
		return service_details;
	}
	public void setService_details(Set<ServiceDetailPojo> service_details) {
		this.service_details = service_details;
	}
	
	
	
	
}
