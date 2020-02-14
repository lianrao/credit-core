/**   
* @Description: 聚信立-信用卡账单-分期计划
* @author xiaobin.hou  
* @date 2016年7月26日 上午1:30:23 
* @version V1.0   
*/
package com.wanda.credit.ds.dao.domain.juxinli.creditCardBill;

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
@Table(name = "CPDB_DS.T_DS_JXL_CREDIT_INSTALLMENT")
@SequenceGenerator(name="SEQ_JXL_CREDIT_INSTALLMENT",sequenceName="CPDB_DS.SEQ_JXL_CREDIT_INSTALLMENT")  
@JsonIgnoreProperties(value = { "hibernateLazyInitializer"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class CreditCardInstallmentPojo extends BaseDomain{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7310403565410741292L;
	/**
	 * 获取 主键
	 */
	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="SEQ_JXL_CREDIT_INSTALLMENT")  
	@Column(name = "SEQID", unique = true, nullable = false)
	private long seqId;
	private String installment_info;
	private String installment_amount;
	private String installment_amount_unre;
	private String installment_times;
	private String installment_times_res;
	private String installment_fee;
	private String installment_fee_unre;
	private String installment_currentcy;
	private Date create_date;
	private Date update_date;
	@ManyToOne(cascade = {CascadeType.MERGE,CascadeType.REFRESH }, optional = true) 
	@JoinColumn(name="FK_SEQID",referencedColumnName="seqId")
	private CreditCardBillInfoPojo billInfo;
	
	
	
	public String getInstallment_info() {
		return installment_info;
	}
	public void setInstallment_info(String installment_info) {
		this.installment_info = installment_info;
	}
	public String getInstallment_amount() {
		return installment_amount;
	}
	public void setInstallment_amount(String installment_amount) {
		this.installment_amount = installment_amount;
	}
	public String getInstallment_amount_unre() {
		return installment_amount_unre;
	}
	public void setInstallment_amount_unre(String installment_amount_unre) {
		this.installment_amount_unre = installment_amount_unre;
	}
	public String getInstallment_times() {
		return installment_times;
	}
	public void setInstallment_times(String installment_times) {
		this.installment_times = installment_times;
	}
	public String getInstallment_times_res() {
		return installment_times_res;
	}
	public void setInstallment_times_res(String installment_times_res) {
		this.installment_times_res = installment_times_res;
	}
	public String getInstallment_fee() {
		return installment_fee;
	}
	public void setInstallment_fee(String installment_fee) {
		this.installment_fee = installment_fee;
	}
	public String getInstallment_fee_unre() {
		return installment_fee_unre;
	}
	public void setInstallment_fee_unre(String installment_fee_unre) {
		this.installment_fee_unre = installment_fee_unre;
	}
	public String getInstallment_currentcy() {
		return installment_currentcy;
	}
	public void setInstallment_currentcy(String installment_currentcy) {
		this.installment_currentcy = installment_currentcy;
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
	public long getSeqId() {
		return seqId;
	}
	public void setSeqId(long seqId) {
		this.seqId = seqId;
	}
	public CreditCardBillInfoPojo getBillInfo() {
		return billInfo;
	}
	public void setBillInfo(CreditCardBillInfoPojo billInfo) {
		this.billInfo = billInfo;
	}

}
