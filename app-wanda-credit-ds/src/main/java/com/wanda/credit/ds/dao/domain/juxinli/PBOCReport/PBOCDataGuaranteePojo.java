/**   
* @Description: 获取央行征信报告数据-贷款记录
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
@Table(name = "CPDB_DS.T_DS_JXL_PBOC_GUARANTEE")
@SequenceGenerator(name="SEQ_JXL_PBOC_GUARANTEE",sequenceName="CPDB_DS.SEQ_JXL_PBOC_GUARANTEE")  
@JsonIgnoreProperties(value = { "hibernateLazyInitializer"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class PBOCDataGuaranteePojo extends BaseDomain {
	
	private static final long serialVersionUID = 1L;
	/**
	 * 获取 主键
	 */
	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="SEQ_JXL_PBOC_GUARANTEE")  
	@Column(name = "SEQID", unique = true, nullable = false)
	private long seqId;
	private String guarantee_made_time;
	private String guaranteed_name;
	private String guaranteed_card_type;
	private String guaranteed_card_number;
	private String guarantee_made_employer;
	private String guarantee_made_type;
	private String guarantee_contract_amount;
	private String deadline_time;
	private String guarantee_amount;
	private String guarantee_balance;
	private String status;
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
	public String getGuarantee_made_time() {
		return guarantee_made_time;
	}
	public void setGuarantee_made_time(String guarantee_made_time) {
		this.guarantee_made_time = guarantee_made_time;
	}
	public String getGuaranteed_name() {
		return guaranteed_name;
	}
	public void setGuaranteed_name(String guaranteed_name) {
		this.guaranteed_name = guaranteed_name;
	}
	public String getGuaranteed_card_type() {
		return guaranteed_card_type;
	}
	public void setGuaranteed_card_type(String guaranteed_card_type) {
		this.guaranteed_card_type = guaranteed_card_type;
	}
	public String getGuaranteed_card_number() {
		return guaranteed_card_number;
	}
	public void setGuaranteed_card_number(String guaranteed_card_number) {
		this.guaranteed_card_number = guaranteed_card_number;
	}
	public String getGuarantee_made_employer() {
		return guarantee_made_employer;
	}
	public void setGuarantee_made_employer(String guarantee_made_employer) {
		this.guarantee_made_employer = guarantee_made_employer;
	}
	public String getGuarantee_made_type() {
		return guarantee_made_type;
	}
	public void setGuarantee_made_type(String guarantee_made_type) {
		this.guarantee_made_type = guarantee_made_type;
	}
	public String getGuarantee_contract_amount() {
		return guarantee_contract_amount;
	}
	public void setGuarantee_contract_amount(String guarantee_contract_amount) {
		this.guarantee_contract_amount = guarantee_contract_amount;
	}
	public String getDeadline_time() {
		return deadline_time;
	}
	public void setDeadline_time(String deadline_time) {
		this.deadline_time = deadline_time;
	}
	public String getGuarantee_amount() {
		return guarantee_amount;
	}
	public void setGuarantee_amount(String guarantee_amount) {
		this.guarantee_amount = guarantee_amount;
	}
	public String getGuarantee_balance() {
		return guarantee_balance;
	}
	public void setGuarantee_balance(String guarantee_balance) {
		this.guarantee_balance = guarantee_balance;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
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
