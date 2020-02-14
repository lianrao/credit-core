/**   
* @Description: TODO(用一句话描述该文件做什么) 
* @author xiaobin.hou  
* @date 2016年9月26日 下午5:05:09 
* @version V1.0   
*/
package com.wanda.credit.ds.dao.domain.juxinli.report;

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
 * 用户信息核查
 * @author xiaobin.hou
 *
 */
@Entity
@Table(name = "CPDB_DS.T_DS_JXL_REP_USER_INFO_CHECK")
@SequenceGenerator(name="SEQ_T_DS_USER_INFO_CHECK",sequenceName="CPDB_DS.SEQ_T_DS_USER_INFO_CHECK")  
@JsonIgnoreProperties(value = { "hibernateLazyInitializer"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class UserInfoCheckPojo extends BaseDomain {
	
	private static final long serialVersionUID = 1L;
	
	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="SEQ_T_DS_USER_INFO_CHECK")  
	@Column(name = "SEQID", unique = true, nullable = false)
	private long seqId;
	private String requestId;
	private String searched_org_cnt;
	private String searched_org_type;
	private String idcard_with_other_names;
	private String idcard_with_other_phones;
	private String phone_with_other_names;
	private String phone_with_other_idcards;
	private String register_org_cnt;
	private String register_org_type;
	private String arised_open_web;
	private String phone_gray_score;
	private String contacts_class1_blacklist_cnt;
	private String contacts_class2_blacklist_cnt;
	private String contacts_class1_cnt;
	private String contacts_router_cnt;
	private String contacts_router_ratio;
	
	
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
	public String getSearched_org_cnt() {
		return searched_org_cnt;
	}
	public void setSearched_org_cnt(String searched_org_cnt) {
		this.searched_org_cnt = searched_org_cnt;
	}
	public String getSearched_org_type() {
		return searched_org_type;
	}
	public void setSearched_org_type(String searched_org_type) {
		this.searched_org_type = searched_org_type;
	}
	public String getIdcard_with_other_names() {
		return idcard_with_other_names;
	}
	public void setIdcard_with_other_names(String idcard_with_other_names) {
		this.idcard_with_other_names = idcard_with_other_names;
	}
	public String getIdcard_with_other_phones() {
		return idcard_with_other_phones;
	}
	public void setIdcard_with_other_phones(String idcard_with_other_phones) {
		this.idcard_with_other_phones = idcard_with_other_phones;
	}
	public String getPhone_with_other_names() {
		return phone_with_other_names;
	}
	public void setPhone_with_other_names(String phone_with_other_names) {
		this.phone_with_other_names = phone_with_other_names;
	}
	public String getPhone_with_other_idcards() {
		return phone_with_other_idcards;
	}
	public void setPhone_with_other_idcards(String phone_with_other_idcards) {
		this.phone_with_other_idcards = phone_with_other_idcards;
	}
	public String getRegister_org_cnt() {
		return register_org_cnt;
	}
	public void setRegister_org_cnt(String register_org_cnt) {
		this.register_org_cnt = register_org_cnt;
	}
	public String getRegister_org_type() {
		return register_org_type;
	}
	public void setRegister_org_type(String register_org_type) {
		this.register_org_type = register_org_type;
	}
	public String getArised_open_web() {
		return arised_open_web;
	}
	public void setArised_open_web(String arised_open_web) {
		this.arised_open_web = arised_open_web;
	}
	public String getPhone_gray_score() {
		return phone_gray_score;
	}
	public void setPhone_gray_score(String phone_gray_score) {
		this.phone_gray_score = phone_gray_score;
	}
	public String getContacts_class1_blacklist_cnt() {
		return contacts_class1_blacklist_cnt;
	}
	public void setContacts_class1_blacklist_cnt(
			String contacts_class1_blacklist_cnt) {
		this.contacts_class1_blacklist_cnt = contacts_class1_blacklist_cnt;
	}
	public String getContacts_class2_blacklist_cnt() {
		return contacts_class2_blacklist_cnt;
	}
	public void setContacts_class2_blacklist_cnt(
			String contacts_class2_blacklist_cnt) {
		this.contacts_class2_blacklist_cnt = contacts_class2_blacklist_cnt;
	}
	public String getContacts_class1_cnt() {
		return contacts_class1_cnt;
	}
	public void setContacts_class1_cnt(String contacts_class1_cnt) {
		this.contacts_class1_cnt = contacts_class1_cnt;
	}
	public String getContacts_router_cnt() {
		return contacts_router_cnt;
	}
	public void setContacts_router_cnt(String contacts_router_cnt) {
		this.contacts_router_cnt = contacts_router_cnt;
	}
	public String getContacts_router_ratio() {
		return contacts_router_ratio;
	}
	public void setContacts_router_ratio(String contacts_router_ratio) {
		this.contacts_router_ratio = contacts_router_ratio;
	}

}
