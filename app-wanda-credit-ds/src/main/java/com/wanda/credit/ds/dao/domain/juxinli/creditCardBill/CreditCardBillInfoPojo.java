/**   
* @Description: 聚信立-信用卡账单数据-账单信息
* @author xiaobin.hou  
* @date 2016年7月26日 上午12:00:01 
* @version V1.0   
*/
package com.wanda.credit.ds.dao.domain.juxinli.creditCardBill;

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
@Table(name = "CPDB_DS.T_DS_JXL_CREDIT_BILL_INFO")
@SequenceGenerator(name="SEQ_JXL_CREDIT_BILL_INFO",sequenceName="CPDB_DS.SEQ_JXL_CREDIT_BILL_INFO")  
@JsonIgnoreProperties(value = { "hibernateLazyInitializer"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class CreditCardBillInfoPojo extends BaseDomain {
	
	
	private static final long serialVersionUID = -4926794930408225425L;
	/**
	 * 获取 主键
	 */
	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="SEQ_JXL_CREDIT_BILL_INFO")  
	@Column(name = "SEQID", unique = true, nullable = false)
	private long seqId;
	private String requestId;
	private String received;
	@Column(name = "fromed")
	private String from;
	private String datasource;
	private String bank_name;
	private String email;
	private String internaldate;
	private String card_number;
	private String user_name;
	private String statement_date;
	private String payment_due_date;
	private String statement_cycle;
	private Date create_date;
	private Date update_date;
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "FK_SEQID")
	private Set<CreditCardAmtPojo> amtSet = new HashSet<CreditCardAmtPojo>();
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "FK_SEQID")
	private Set<CreditCardTransSumPojo> transSumSet = new HashSet<CreditCardTransSumPojo>();
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "FK_SEQID")
	private Set<CreditCardTransDetailPojo> transDetailSet = new HashSet<CreditCardTransDetailPojo>();
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "FK_SEQID")
	private Set<CreditCardInstallmentPojo> installmentSet = new HashSet<CreditCardInstallmentPojo>();
	
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
	public String getDatasource() {
		return datasource;
	}
	public void setDatasource(String datasource) {
		this.datasource = datasource;
	}
	public String getBank_name() {
		return bank_name;
	}
	public void setBank_name(String bank_name) {
		this.bank_name = bank_name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getInternaldate() {
		return internaldate;
	}
	public void setInternaldate(String internaldate) {
		this.internaldate = internaldate;
	}
	public String getCard_number() {
		return card_number;
	}
	public void setCard_number(String card_number) {
		this.card_number = card_number;
	}
	public String getUser_name() {
		return user_name;
	}
	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
	public String getStatement_date() {
		return statement_date;
	}
	public void setStatement_date(String statement_date) {
		this.statement_date = statement_date;
	}
	public String getPayment_due_date() {
		return payment_due_date;
	}
	public void setPayment_due_date(String payment_due_date) {
		this.payment_due_date = payment_due_date;
	}
	public String getStatement_cycle() {
		return statement_cycle;
	}
	public void setStatement_cycle(String statement_cycle) {
		this.statement_cycle = statement_cycle;
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
	public Set<CreditCardAmtPojo> getAmtSet() {
		return amtSet;
	}
	public void setAmtSet(Set<CreditCardAmtPojo> amtSet) {
		this.amtSet = amtSet;
	}
	public Set<CreditCardTransSumPojo> getTransSumSet() {
		return transSumSet;
	}
	public void setTransSumSet(Set<CreditCardTransSumPojo> transSumSet) {
		this.transSumSet = transSumSet;
	}
	public Set<CreditCardTransDetailPojo> getTransDetailSet() {
		return transDetailSet;
	}
	public void setTransDetailSet(Set<CreditCardTransDetailPojo> transDetailSet) {
		this.transDetailSet = transDetailSet;
	}
	public Set<CreditCardInstallmentPojo> getInstallmentSet() {
		return installmentSet;
	}
	public void setInstallmentSet(Set<CreditCardInstallmentPojo> installmentSet) {
		this.installmentSet = installmentSet;
	}
	public String getReceived() {
		return received;
	}
	public void setReceived(String received) {
		this.received = received;
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	
	
}
