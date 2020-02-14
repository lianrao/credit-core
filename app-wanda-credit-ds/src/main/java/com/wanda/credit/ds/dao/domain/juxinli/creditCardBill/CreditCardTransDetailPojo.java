/**   
* @Description: 聚信立-信用卡账单-交易明细 
* @author xiaobin.hou  
* @date 2016年7月26日 上午1:23:39 
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
@Table(name = "CPDB_DS.T_DS_JXL_CREDIT_TRANS_DETAIL")
@SequenceGenerator(name="SEQ_JXL_CREDIT_TRANS_DETAIL",sequenceName="CPDB_DS.SEQ_JXL_CREDIT_TRANS_DETAIL")  
@JsonIgnoreProperties(value = { "hibernateLazyInitializer"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class CreditCardTransDetailPojo extends BaseDomain {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2456461139683630207L;
	/**
	 * 获取 主键
	 */
	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="SEQ_JXL_CREDIT_TRANS_DETAIL")  
	@Column(name = "SEQID", unique = true, nullable = false)
	private long seqId;
	private String trans_type;
	private String trans_date;
	private String posted_date;
	private String trans_description;
	private String trans_amount;
	private String trans_currency;
	private String payment_amount;
	private String payment_currency;
	private Date create_date;
	private Date update_date;
	@ManyToOne(cascade = {CascadeType.MERGE,CascadeType.REFRESH }, optional = true) 
	@JoinColumn(name="FK_SEQID",referencedColumnName="seqId")
	private CreditCardBillInfoPojo billInfo;
	
	
	public long getSeqId() {
		return seqId;
	}
	public void setSeqId(long seqId) {
		this.seqId = seqId;
	}
	public String getTrans_type() {
		return trans_type;
	}
	public void setTrans_type(String trans_type) {
		this.trans_type = trans_type;
	}
	public String getTrans_date() {
		return trans_date;
	}
	public void setTrans_date(String trans_date) {
		this.trans_date = trans_date;
	}
	public String getPosted_date() {
		return posted_date;
	}
	public void setPosted_date(String posted_date) {
		this.posted_date = posted_date;
	}
	public String getTrans_amount() {
		return trans_amount;
	}
	public void setTrans_amount(String trans_amount) {
		this.trans_amount = trans_amount;
	}
	public String getTrans_currency() {
		return trans_currency;
	}
	public void setTrans_currency(String trans_currency) {
		this.trans_currency = trans_currency;
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
	public String getTrans_description() {
		return trans_description;
	}
	public void setTrans_description(String trans_description) {
		this.trans_description = trans_description;
	}
	public String getPayment_amount() {
		return payment_amount;
	}
	public void setPayment_amount(String payment_amount) {
		this.payment_amount = payment_amount;
	}
	public String getPayment_currency() {
		return payment_currency;
	}
	public void setPayment_currency(String payment_currency) {
		this.payment_currency = payment_currency;
	}
	public CreditCardBillInfoPojo getBillInfo() {
		return billInfo;
	}
	public void setBillInfo(CreditCardBillInfoPojo billInfo) {
		this.billInfo = billInfo;
	}

}
