package com.wanda.credit.ds.dao.domain.juxinli.mobile;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.annotations.GenericGenerator;

import com.wanda.credit.base.domain.BaseDomain;
/**
 * 运营商原始数据-账单信息
 * @author xiaobin.hou
 *
 */
@Entity
@Table(name = "CPDB_DS.T_DS_JXL_OPER_BILL")
@JsonIgnoreProperties(value = {"hibernateLazyInitializer"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class MobileRawDataAccountPojo extends BaseDomain {
	private static final long serialVersionUID = 1L;
	/**
	 * 获取 主键
	 */
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	@Column(name = "SEQID", unique = true, nullable = false, length = 32)
	private String seqId;
	private String requestId;
	@Column(name="DEAL_TIME")
	private String update_time;
	private String total_amt;
	private String bill_cycle;
	private String pay_amt;
	private String plan_amt;
	private String cell_phone;
	private Date crt_time;	
	private Date upd_time;
	private String fk_seqId;
//	@ManyToOne(cascade = {CascadeType.MERGE,CascadeType.REFRESH }, optional = true) 
//	@JoinColumn(name="FK_SEQID",referencedColumnName="seqId")
//	private MobileRawDataOwnerPojo owerInfo;

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
	public String getUpdate_time() {
		return update_time;
	}

	public void setUpdate_time(String update_time) {
		this.update_time = update_time;
	}

	public String getTotal_amt() {
		return total_amt;
	}

	public void setTotal_amt(String total_amt) {
		this.total_amt = total_amt;
	}

	public String getBill_cycle() {
		return bill_cycle;
	}

	public void setBill_cycle(String bill_cycle) {
		this.bill_cycle = bill_cycle;
	}

	public String getPay_amt() {
		return pay_amt;
	}

	public void setPay_amt(String pay_amt) {
		this.pay_amt = pay_amt;
	}

	public String getPlan_amt() {
		return plan_amt;
	}

	public void setPlan_amt(String plan_amt) {
		this.plan_amt = plan_amt;
	}

	public String getCell_phone() {
		return cell_phone;
	}

	public void setCell_phone(String cell_phone) {
		this.cell_phone = cell_phone;
	}
	@JsonIgnore
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
//	public MobileRawDataOwnerPojo getOwerInfo() {
//		return owerInfo;
//	}
//
//	public void setOwerInfo(MobileRawDataOwnerPojo owerInfo) {
//		this.owerInfo = owerInfo;
//	}

	public String getFk_seqId() {
		return fk_seqId;
	}

	public void setFk_seqId(String fk_seqId) {
		this.fk_seqId = fk_seqId;
	}

	
	
}
