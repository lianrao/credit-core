/**   
* @Description: 快钱-反欺诈-风险明细 
* @author xiaobin.hou  
* @date 2016年8月23日 下午7:53:34 
* @version V1.0   
*/
package com.wanda.credit.ds.dao.domain.antifraud;

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
@Table(name = "CPDB_DS.T_DS_BS_Antifraud_rspdtl")
@SequenceGenerator(name="Seq_T_DS_BS_Antifraud_rspdtl",sequenceName="CPDB_DS.Seq_T_DS_BS_Antifraud_rspdtl")  
@JsonIgnoreProperties(value = { "hibernateLazyInitializer"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class AntifraudRisk extends BaseDomain {
	
	private static final long serialVersionUID = 149223890319136064L;
	/**
	 * 获取 主键
	 */
	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="Seq_T_DS_BS_Antifraud_rspdtl")  
	@Column(name = "ID", unique = true, nullable = false)
	private long id;
	private String trade_id;
	private String uuid;
	private String createtime;
	private String weight;
	private String rulename;
	private String score;
	private String verify_code;
	private String verify_fail_control;
	private String verify_name;
	private String verify_priority;
	private String verify_succ_control;
	private String notify_code;
	private String notify_name;
	private String notify_priority;
	private String comments;
	private String rulepackagename;
	private Date create_date;
	private Date update_date;
	
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getTrade_id() {
		return trade_id;
	}
	public void setTrade_id(String trade_id) {
		this.trade_id = trade_id;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getCreatetime() {
		return createtime;
	}
	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}
	public String getWeight() {
		return weight;
	}
	public void setWeight(String weight) {
		this.weight = weight;
	}
	public String getRulename() {
		return rulename;
	}
	public void setRulename(String rulename) {
		this.rulename = rulename;
	}
	public String getScore() {
		return score;
	}
	public void setScore(String score) {
		this.score = score;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public String getRulepackagename() {
		return rulepackagename;
	}
	public void setRulepackagename(String rulepackagename) {
		this.rulepackagename = rulepackagename;
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
	public String getVerify_code() {
		return verify_code;
	}
	public void setVerify_code(String verify_code) {
		this.verify_code = verify_code;
	}
	public String getVerify_fail_control() {
		return verify_fail_control;
	}
	public void setVerify_fail_control(String verify_fail_control) {
		this.verify_fail_control = verify_fail_control;
	}
	public String getVerify_name() {
		return verify_name;
	}
	public void setVerify_name(String verify_name) {
		this.verify_name = verify_name;
	}
	public String getVerify_priority() {
		return verify_priority;
	}
	public void setVerify_priority(String verify_priority) {
		this.verify_priority = verify_priority;
	}
	public String getVerify_succ_control() {
		return verify_succ_control;
	}
	public void setVerify_succ_control(String verify_succ_control) {
		this.verify_succ_control = verify_succ_control;
	}
	public String getNotify_code() {
		return notify_code;
	}
	public void setNotify_code(String notify_code) {
		this.notify_code = notify_code;
	}
	public String getNotify_name() {
		return notify_name;
	}
	public void setNotify_name(String notify_name) {
		this.notify_name = notify_name;
	}
	public String getNotify_priority() {
		return notify_priority;
	}
	public void setNotify_priority(String notify_priority) {
		this.notify_priority = notify_priority;
	}

}
