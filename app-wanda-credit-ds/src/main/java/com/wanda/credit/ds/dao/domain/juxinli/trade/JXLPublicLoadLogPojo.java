/**   
* @Description: 聚信立获取数据信息表
* @author xiaobin.hou  
* @date 2016年4月1日 上午11:05:36 
* @version V1.0   
*/
package com.wanda.credit.ds.dao.domain.juxinli.trade;

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
 * @author xiaobin.hou
 *
 */
@Entity
@Table(name = "CPDB_DS.T_DS_JXL_PUBLIC_LOAD_LOG")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class JXLPublicLoadLogPojo extends BaseDomain {

	private static final long serialVersionUID = 4028320880164751205L;
	
	/**
	 * 获取 主键
	 */
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	@Column(name = "SEQID", unique = true, nullable = false, length = 32)
	private String seqId;
	private String requestId; // 交易序列号
	private int load_times; // 申请数据次数
	private String load_result; // 最后申请数据结果
	private String reqid_type; // 供应商类型
	private Date crt_time; // 创建时间
	private Date upd_time; // 更新时间
	
	
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
	public int getLoad_times() {
		return load_times;
	}
	public void setLoad_times(int load_times) {
		this.load_times = load_times;
	}
	public String getLoad_result() {
		return load_result;
	}
	public void setLoad_result(String load_result) {
		this.load_result = load_result;
	}
	public String getReqid_type() {
		return reqid_type;
	}
	public void setReqid_type(String reqid_type) {
		this.reqid_type = reqid_type;
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
	
	@Override
	public String toString() {
		return "JXLPublicLoadLogPojo [seqId=" + seqId + ", requestId="
				+ requestId + ", load_times=" + load_times + ", load_result="
				+ load_result + ", reqid_type=" + reqid_type + ", crt_time="
				+ crt_time + ", upd_time=" + upd_time + "]";
	}

	
	
	
	

}
