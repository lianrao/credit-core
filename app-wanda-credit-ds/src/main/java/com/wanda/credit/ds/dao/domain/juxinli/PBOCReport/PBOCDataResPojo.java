/**   
* @Description: 获取央行征信报告数据信息记录
* @author xiaobin.hou  
* @date 2016年7月11日 下午4:32:37 
* @version V1.0   
*/
package com.wanda.credit.ds.dao.domain.juxinli.PBOCReport;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
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
@Table(name = "CPDB_DS.T_DS_JXL_PBOC_RES_INFO")
@SequenceGenerator(name="SEQ_JXL_PBOC_CREDIT_RES_INFO",sequenceName="CPDB_DS.SEQ_JXL_PBOC_CREDIT_RES_INFO")  
@JsonIgnoreProperties(value = { "hibernateLazyInitializer"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class PBOCDataResPojo extends BaseDomain {
	
	private static final long serialVersionUID = 1L;
	/**
	 * 获取 主键
	 */
	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="SEQ_JXL_PBOC_CREDIT_RES_INFO")  
	@Column(name = "SEQID", unique = true, nullable = false)
	private long seqId;
	private String requestId;
	private String status;
	private String update_time;
	private String token;
	private String env;
	private String report_time;
	private String get_data_time;
	private String data_source;
	private String request_time;
	private String query_number;
	private String query_papers;
	private String query_marriage;
	private String query_name;
	private String version;
	private String report_id;
	private String error_code;
	private String error_msg;
	private Date create_date;
	private Date update_date;
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "FK_SEQID")
	private Set<PBOCDataGuaranteePojo> guaranteeSet = new HashSet<PBOCDataGuaranteePojo>();
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "FK_SEQID")
	private Set<PBOCDataQueryPojo> querySet = new HashSet<PBOCDataQueryPojo>();
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "FK_SEQID")
	private Set<PBOCDataRecordPojo> recordSet = new HashSet<PBOCDataRecordPojo>();
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "FK_SEQID")
	private Set<PBOCDataSummaryPojo> summarySet = new HashSet<PBOCDataSummaryPojo>();
	
	
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
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getUpdate_time() {
		return update_time;
	}
	public void setUpdate_time(String update_time) {
		this.update_time = update_time;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getEnv() {
		return env;
	}
	public void setEnv(String env) {
		this.env = env;
	}
	public String getReport_time() {
		return report_time;
	}
	public void setReport_time(String report_time) {
		this.report_time = report_time;
	}
	public String getGet_data_time() {
		return get_data_time;
	}
	public void setGet_data_time(String get_data_time) {
		this.get_data_time = get_data_time;
	}
	public String getData_source() {
		return data_source;
	}
	public void setData_source(String data_source) {
		this.data_source = data_source;
	}
	public String getRequest_time() {
		return request_time;
	}
	public void setRequest_time(String request_time) {
		this.request_time = request_time;
	}
	public String getQuery_number() {
		return query_number;
	}
	public void setQuery_number(String query_number) {
		this.query_number = query_number;
	}
	public String getQuery_papers() {
		return query_papers;
	}
	public void setQuery_papers(String query_papers) {
		this.query_papers = query_papers;
	}
	public String getQuery_marriage() {
		return query_marriage;
	}
	public void setQuery_marriage(String query_marriage) {
		this.query_marriage = query_marriage;
	}
	public String getQuery_name() {
		return query_name;
	}
	public void setQuery_name(String query_name) {
		this.query_name = query_name;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getReport_id() {
		return report_id;
	}
	public void setReport_id(String report_id) {
		this.report_id = report_id;
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
	public Set<PBOCDataGuaranteePojo> getGuaranteeSet() {
		return guaranteeSet;
	}
	public void setGuaranteeSet(Set<PBOCDataGuaranteePojo> guaranteeSet) {
		this.guaranteeSet = guaranteeSet;
	}
	public Set<PBOCDataQueryPojo> getQuerySet() {
		return querySet;
	}
	public void setQuerySet(Set<PBOCDataQueryPojo> querySet) {
		this.querySet = querySet;
	}
	public Set<PBOCDataRecordPojo> getRecordSet() {
		return recordSet;
	}
	public void setRecordSet(Set<PBOCDataRecordPojo> recordSet) {
		this.recordSet = recordSet;
	}
	public Set<PBOCDataSummaryPojo> getSummarySet() {
		return summarySet;
	}
	public void setSummarySet(Set<PBOCDataSummaryPojo> summarySet) {
		this.summarySet = summarySet;
	}
	
	
	
	

}
