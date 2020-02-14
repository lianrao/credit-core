/**   
* @Description: 信用卡账单-获取数据应答信息 
* @author xiaobin.hou  
* @date 2016年7月25日 下午11:42:11 
* @version V1.0   
*/
package com.wanda.credit.ds.dao.domain.juxinli.creditCardBill;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
@Table(name = "CPDB_DS.T_DS_JXL_CREDIT_RES_INFO")
@SequenceGenerator(name="SEQ_JXL_CREDIT_RES_INFO",sequenceName="CPDB_DS.SEQ_JXL_CREDIT_RES_INFO")  
@JsonIgnoreProperties(value = { "hibernateLazyInitializer"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class CreditCardDataResPojo extends BaseDomain {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 获取 主键
	 */
	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="SEQ_JXL_CREDIT_RES_INFO")  
	@Column(name = "SEQID", unique = true, nullable = false)
	private long seqId;
	private String requestId;
	private String status;
	private String response_time;
	private String token;
	private String env;
	private String error_code;
	private String error_msg;
	private Date create_date;
	private Date update_date;
	
	
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
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getResponse_time() {
		return response_time;
	}
	public void setResponse_time(String response_time) {
		this.response_time = response_time;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getEnv() {
		return env;
	}
	public void setEnv(String env) {
		this.env = env;
	}
	public String getError_code() {
		return error_code;
	}
	public void setError_code(String error_code) {
		this.error_code = error_code;
	}
	public String getError_msg() {
		return error_msg;
	}
	public void setError_msg(String error_msg) {
		this.error_msg = error_msg;
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

}
