/**   
* @Description: 获取央行征信报告数据-汇总
* @author xiaobin.hou  
* @date 2016年7月11日 下午4:32:37 
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
@Table(name = "CPDB_DS.T_DS_JXL_PBOC_SUMMARY")
@SequenceGenerator(name="SEQ_JXL_PBOC_CREDIT_SUMMARY",sequenceName="CPDB_DS.SEQ_JXL_PBOC_CREDIT_SUMMARY")  
@JsonIgnoreProperties(value = { "hibernateLazyInitializer"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class PBOCDataSummaryPojo extends BaseDomain {
	
	private static final long serialVersionUID = 1L;
	/**
	 * 获取 主键
	 */
	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="SEQ_JXL_PBOC_CREDIT_SUMMARY")  
	@Column(name = "SEQID", unique = true, nullable = false)
	private long seqId;
	private String guarantee_number;
	private String no_settle_account_number;
	private String acccount_number;
	private String type;
	private String overdue_account_num;
	private String overdue90_account_num;
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
	public String getGuarantee_number() {
		return guarantee_number;
	}
	public void setGuarantee_number(String guarantee_number) {
		this.guarantee_number = guarantee_number;
	}
	public String getNo_settle_account_number() {
		return no_settle_account_number;
	}
	public void setNo_settle_account_number(String no_settle_account_number) {
		this.no_settle_account_number = no_settle_account_number;
	}
	public String getAcccount_number() {
		return acccount_number;
	}
	public void setAcccount_number(String acccount_number) {
		this.acccount_number = acccount_number;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getOverdue_account_num() {
		return overdue_account_num;
	}
	public void setOverdue_account_num(String overdue_account_num) {
		this.overdue_account_num = overdue_account_num;
	}
	public String getOverdue90_account_num() {
		return overdue90_account_num;
	}
	public void setOverdue90_account_num(String overdue90_account_num) {
		this.overdue90_account_num = overdue90_account_num;
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
