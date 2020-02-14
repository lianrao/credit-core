/**   
* @Description: 聚信立交易信息表
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
@Table(name = "CPDB_DS.T_DS_JXL_MEBUSI_TRADE")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class JXLMEbusiTradePojo extends BaseDomain {

	private static final long serialVersionUID = 4028320880164751205L;
	
	/**
	 * 获取 主键
	 */
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	@Column(name = "SEQID", unique = true, nullable = false, length = 32)
	private String seqId;
	private String trade_id;
	private String requestId;
	private String name;
	private String id_card_no;
	private String cell_phone;
	private String trade_flag;
	private String ret_code;
	private Date crt_time;
	private Date upd_time;
	
	
	public String getSeqId() {
		return seqId;
	}
	public void setSeqId(String seqId) {
		this.seqId = seqId;
	}
	public String getTrade_id() {
		return trade_id;
	}
	public void setTrade_id(String trade_id) {
		this.trade_id = trade_id;
	}
	public String getRequestId() {
		return requestId;
	}
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getId_card_no() {
		return id_card_no;
	}
	public void setId_card_no(String id_card_no) {
		this.id_card_no = id_card_no;
	}
	public String getCell_phone() {
		return cell_phone;
	}
	public void setCell_phone(String cell_phone) {
		this.cell_phone = cell_phone;
	}
	public String getTrade_flag() {
		return trade_flag;
	}
	public void setTrade_flag(String trade_flag) {
		this.trade_flag = trade_flag;
	}
	public String getRet_code() {
		return ret_code;
	}
	public void setRet_code(String ret_code) {
		this.ret_code = ret_code;
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
	
	
	public String toString() {
		return "JXLMEbusiTradePojo [seqId=" + seqId + ", trade_id=" + trade_id
				+ ", requestId=" + requestId + ", name=" + name
				+ ", id_card_no=" + id_card_no + ", cell_phone=" + cell_phone
				+ ", trade_flag=" + trade_flag + ", ret_code=" + ret_code
				+ ", crt_time=" + crt_time + ", upd_time=" + upd_time + "]";
	}
	
	
	

}
