/**   
* @Description: 人行征信报告提交采集请求信息记录表 
* @author xiaobin.hou  
* @date 2016年7月8日 上午11:33:21 
* @version V1.0   
*/
package com.wanda.credit.ds.dao.domain.juxinli.PBOCReport;

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
@Table(name = "CPDB_DS.T_DS_JXL_PBOC_APPLY_INFO")
@SequenceGenerator(name="SEQ_JXL_PBOC_APPLY_INFO",sequenceName="CPDB_DS.SEQ_JXL_PBOC_APPLY_INFO")  
@JsonIgnoreProperties(value = { "hibernateLazyInitializer"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class PBOCApplyPojo extends BaseDomain {

	private static final long serialVersionUID = 1L;
	
	/**
	 * 获取 主键
	 */
	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="SEQ_JXL_PBOC_APPLY_INFO")  
	@Column(name = "SEQID", unique = true, nullable = false)
	private long seqId;
	private String requestId;
	private String name;
	private String cell_phone;
	private String id_card_no;
	private String account;
	private String passwd;
	private String captcha;
	private String token;
	private String sucess;
	private String type;
	private String content;
	private String process_code;
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
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCell_phone() {
		return cell_phone;
	}
	public void setCell_phone(String cell_phone) {
		this.cell_phone = cell_phone;
	}
	public String getId_card_no() {
		return id_card_no;
	}
	public void setId_card_no(String id_card_no) {
		this.id_card_no = id_card_no;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getPasswd() {
		return passwd;
	}
	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getSucess() {
		return sucess;
	}
	public void setSucess(String sucess) {
		this.sucess = sucess;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getProcess_code() {
		return process_code;
	}
	public void setProcess_code(String process_code) {
		this.process_code = process_code;
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
	public String getCaptcha() {
		return captcha;
	}
	public void setCaptcha(String captcha) {
		this.captcha = captcha;
	}

}
