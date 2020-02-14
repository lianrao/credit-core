package com.wanda.credit.ds.client.juxinli.bean.reportNew.po;

import java.util.List;
/**
 * 
 * 用户查询信息 DICT
 * 
 * @author xiaobin.hou
 *
 */
public class CheckSearchInfo {

	private String searched_org_cnt;

	private List<String> searched_org_type;

	private List<String> idcard_with_other_names;

	private List<String> idcard_with_other_phones;

	private List<String> phone_with_other_names;

	private List<String> phone_with_other_idcards;

	private String register_org_cnt;

	private List<String> register_org_type;

	private List<String> arised_open_web;

	public String getSearched_org_cnt() {
		return searched_org_cnt;
	}

	public void setSearched_org_cnt(String searched_org_cnt) {
		this.searched_org_cnt = searched_org_cnt;
	}

	public List<String> getSearched_org_type() {
		return searched_org_type;
	}

	public void setSearched_org_type(List<String> searched_org_type) {
		this.searched_org_type = searched_org_type;
	}

	public List<String> getIdcard_with_other_names() {
		return idcard_with_other_names;
	}

	public void setIdcard_with_other_names(List<String> idcard_with_other_names) {
		this.idcard_with_other_names = idcard_with_other_names;
	}

	public List<String> getIdcard_with_other_phones() {
		return idcard_with_other_phones;
	}

	public void setIdcard_with_other_phones(List<String> idcard_with_other_phones) {
		this.idcard_with_other_phones = idcard_with_other_phones;
	}

	public List<String> getPhone_with_other_names() {
		return phone_with_other_names;
	}

	public void setPhone_with_other_names(List<String> phone_with_other_names) {
		this.phone_with_other_names = phone_with_other_names;
	}

	public List<String> getPhone_with_other_idcards() {
		return phone_with_other_idcards;
	}

	public void setPhone_with_other_idcards(List<String> phone_with_other_idcards) {
		this.phone_with_other_idcards = phone_with_other_idcards;
	}

	public String getRegister_org_cnt() {
		return register_org_cnt;
	}

	public void setRegister_org_cnt(String register_org_cnt) {
		this.register_org_cnt = register_org_cnt;
	}

	public List<String> getRegister_org_type() {
		return register_org_type;
	}

	public void setRegister_org_type(List<String> register_org_type) {
		this.register_org_type = register_org_type;
	}

	public List<String> getArised_open_web() {
		return arised_open_web;
	}

	public void setArised_open_web(List<String> arised_open_web) {
		this.arised_open_web = arised_open_web;
	}

	@Override
	public String toString() {
		return "checkSearchInfo [searched_org_cnt=" + searched_org_cnt + ", searched_org_type=" + searched_org_type
				+ ", idcard_with_other_names=" + idcard_with_other_names + ", idcard_with_other_phones="
				+ idcard_with_other_phones + ", phone_with_other_names=" + phone_with_other_names
				+ ", phone_with_other_idcards=" + phone_with_other_idcards + ", register_org_cnt=" + register_org_cnt
				+ ", register_org_type=" + register_org_type + ", arised_open_web=" + arised_open_web + "]";
	}

}
