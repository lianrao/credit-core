/**   
* @Description: 聚信立-信用卡账单-交易汇总
* @author xiaobin.hou  
* @date 2016年7月26日 上午1:15:17 
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
@Table(name = "CPDB_DS.T_DS_JXL_CREDIT_TRANS_SUMMARY")
@SequenceGenerator(name="SEQ_JXL_CREDIT_TRANS_SUM",sequenceName="CPDB_DS.SEQ_JXL_CREDIT_TRANS_SUM")  
@JsonIgnoreProperties(value = { "hibernateLazyInitializer"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class CreditCardTransSumPojo extends BaseDomain {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6121498887427013664L;
	/**
	 * 获取 主键
	 */
	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="SEQ_JXL_CREDIT_TRANS_SUM")  
	@Column(name = "SEQID", unique = true, nullable = false)
	private long seqId;
	private String pre_statement;
	private String pre_payment;
	private String cur_statement;
	private String cur_adjustment;
	private String cycle_interest;
	private String trans_currency;
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

	public String getPre_statement() {
		return pre_statement;
	}

	public void setPre_statement(String pre_statement) {
		this.pre_statement = pre_statement;
	}

	public String getPre_payment() {
		return pre_payment;
	}

	public void setPre_payment(String pre_payment) {
		this.pre_payment = pre_payment;
	}

	public String getCur_statement() {
		return cur_statement;
	}

	public void setCur_statement(String cur_statement) {
		this.cur_statement = cur_statement;
	}
	
	public String getCycle_interest() {
		return cycle_interest;
	}

	public void setCycle_interest(String cycle_interest) {
		this.cycle_interest = cycle_interest;
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

	public CreditCardBillInfoPojo getBillInfo() {
		return billInfo;
	}

	public void setBillInfo(CreditCardBillInfoPojo billInfo) {
		this.billInfo = billInfo;
	}

	public String getCur_adjustment() {
		return cur_adjustment;
	}

	public void setCur_adjustment(String cur_adjustment) {
		this.cur_adjustment = cur_adjustment;
	}

}
