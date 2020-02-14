/**   
* @Description: 聚信立-信用卡 消费金额
* @author xiaobin.hou  
* @date 2016年7月26日 上午12:34:58 
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
@Table(name = "CPDB_DS.T_DS_JXL_CREDIT_AMT_INFO")
@SequenceGenerator(name="SEQ_JXL_CREDIT_AMT_INFO",sequenceName="CPDB_DS.SEQ_JXL_CREDIT_AMT_INFO",schema="CPDB_DS")  
@JsonIgnoreProperties(value = { "hibernateLazyInitializer"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class CreditCardAmtPojo extends BaseDomain {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8609425684929025055L;
	/**
	 * 获取 主键
	 */
	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="SEQ_JXL_CREDIT_AMT_INFO")  
	@Column(name = "SEQID", unique = true, nullable = false)
	private long seqId;
	private String key_code;
	private String amount;
	private String currency;
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
	public String getKey_code() {
		return key_code;
	}
	public void setKey_code(String key_code) {
		this.key_code = key_code;
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
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}

}
