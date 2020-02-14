/**   
* @Description: 报告数据-查询信息
* @author xiaobin.hou  
* @date 2016年7月11日 下午4:43:44 
* @version V1.0   
*/
package com.wanda.credit.ds.dao.domain.juxinli.PBOCReport;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Table(name = "CPDB_DS.T_DS_JXL_PBOC_QUERY")
@SequenceGenerator(name="SEQ_JXL_PBOC_CREDIT_QUERY",sequenceName="CPDB_DS.SEQ_JXL_PBOC_CREDIT_QUERY")  
@JsonIgnoreProperties(value = { "hibernateLazyInitializer"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class PBOCDataQueryPojo extends BaseDomain {
	

	private static final long serialVersionUID = 1L;
	/**
	 * 获取 主键
	 */
	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="SEQ_JXL_PBOC_CREDIT_QUERY")  
	@Column(name = "SEQID", unique = true, nullable = false)
	private long seqId;
	private String type;
	private String query_time;
	private String query_reason;
	private String query_operator;
	private Date create_date;
	private Date update_date;
	
	@ManyToOne(cascade = {CascadeType.MERGE,CascadeType.REFRESH }, optional = true) 
	@JoinColumn(name="FK_SEQID",referencedColumnName="seqId")
	private PBOCDataResPojo res;
	
	
	
	public long getSeqId() {
		return seqId;
	}
	public void setSeqId(long seqId) {
		this.seqId = seqId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getQuery_time() {
		return query_time;
	}
	public void setQuery_time(String query_time) {
		this.query_time = query_time;
	}
	public String getQuery_reason() {
		return query_reason;
	}
	public void setQuery_reason(String query_reason) {
		this.query_reason = query_reason;
	}
	public String getQuery_operator() {
		return query_operator;
	}
	public void setQuery_operator(String query_operator) {
		this.query_operator = query_operator;
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
	public PBOCDataResPojo getRes() {
		return res;
	}
	public void setRes(PBOCDataResPojo res) {
		this.res = res;
	}

}
