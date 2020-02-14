package com.wanda.credit.ds.dao.domain.juxinli.report;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.annotations.GenericGenerator;

import com.wanda.credit.base.domain.BaseDomain;
/**
 * 通话行为分析
 * @author xiaobin.hou
 *
 */
@Entity
@Table(name = "CPDB_DS.T_DS_JXL_REP_CELL_BEHAVIOR")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class CellBehaviorPojo extends BaseDomain {
	
	private static final long serialVersionUID = 1L;
	
	private String seqId;
	private String requestId;
	private String cell_operator;
	private String cell_operator_zh;
	private String phone_num;
	private String cell_loc;
	private String cell_mth;
	private String net_flow;
	private String total_amt;
	private String call_cnt;
	private String call_out_cnt;
	private String call_out_time;
	private String call_in_cnt;
	private String call_in_time;
	private String sms_cnt;
	private Date crt_time;
	private Date upd_time;
	
	/**
	 * 获取 主键
	 */
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	@Column(name = "SEQID", unique = true, nullable = false, length = 32)
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
	public String getCell_operator() {
		return cell_operator;
	}
	public void setCell_operator(String cell_operator) {
		this.cell_operator = cell_operator;
	}
	public String getCell_operator_zh() {
		return cell_operator_zh;
	}
	public void setCell_operator_zh(String cell_operator_zh) {
		this.cell_operator_zh = cell_operator_zh;
	}
	public String getPhone_num() {
		return phone_num;
	}
	public void setPhone_num(String phone_num) {
		this.phone_num = phone_num;
	}
	public String getCell_loc() {
		return cell_loc;
	}
	public void setCell_loc(String cell_loc) {
		this.cell_loc = cell_loc;
	}
	public String getCell_mth() {
		return cell_mth;
	}
	public void setCell_mth(String cell_mth) {
		this.cell_mth = cell_mth;
	}
	public String getNet_flow() {
		return net_flow;
	}
	public void setNet_flow(String net_flow) {
		this.net_flow = net_flow;
	}
	public String getTotal_amt() {
		return total_amt;
	}
	public void setTotal_amt(String total_amt) {
		this.total_amt = total_amt;
	}
	public String getCall_cnt() {
		return call_cnt;
	}
	public void setCall_cnt(String call_cnt) {
		this.call_cnt = call_cnt;
	}
	public String getCall_out_cnt() {
		return call_out_cnt;
	}
	public void setCall_out_cnt(String call_out_cnt) {
		this.call_out_cnt = call_out_cnt;
	}
	public String getCall_out_time() {
		return call_out_time;
	}
	public void setCall_out_time(String call_out_time) {
		this.call_out_time = call_out_time;
	}
	public String getCall_in_cnt() {
		return call_in_cnt;
	}
	public void setCall_in_cnt(String call_in_cnt) {
		this.call_in_cnt = call_in_cnt;
	}
	public String getCall_in_time() {
		return call_in_time;
	}
	public void setCall_in_time(String call_in_time) {
		this.call_in_time = call_in_time;
	}
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
	public String getSms_cnt() {
		return sms_cnt;
	}
	public void setSms_cnt(String sms_cnt) {
		this.sms_cnt = sms_cnt;
	}
	

	
	
	

}
