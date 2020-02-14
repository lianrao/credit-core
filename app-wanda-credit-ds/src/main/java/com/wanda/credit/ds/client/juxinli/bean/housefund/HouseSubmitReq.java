/**   
* @Description: TODO(用一句话描述该文件做什么) 
* @author xiaobin.hou  
* @date 2016年5月28日 下午4:43:00 
* @version V1.0   
*/
package com.wanda.credit.ds.client.juxinli.bean.housefund;

/**
 * @author xiaobin.hou
 *
 */
public class HouseSubmitReq {
	
	private String website;//数据源英文缩写
	private String sort;//数据源编码
	private String type;//采集方式
	private String id_card_num;//身份证号码
	private String account;//公积金网站账号
	private String cell_phone_num;//手机号
	private String password;//公积金网站密码
	private String name;//姓名
	public String getWebsite() {
		return website;
	}
	public void setWebsite(String website) {
		this.website = website;
	}
	public String getSort() {
		return sort;
	}
	public void setSort(String sort) {
		this.sort = sort;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getId_card_num() {
		return id_card_num;
	}
	public void setId_card_num(String id_card_num) {
		this.id_card_num = id_card_num;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getCell_phone_num() {
		return cell_phone_num;
	}
	public void setCell_phone_num(String cell_phone_num) {
		this.cell_phone_num = cell_phone_num;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String toString() {
		return "HouseSubmitReq [website=" + website + ", sort=" + sort
				+ ", type=" + type + ", id_card_num=" + id_card_num
				+ ", account=" + account + ", cell_phone_num=" + cell_phone_num
				+ ", password=" + password + ", name=" + name + "]";
	}


	
}
