package com.wanda.credit.ds.client.juxinli.bean.mobile;
/**
 * 用户基本信息
 * @author xiaobin.hou
 *
 */
public class UserBasicInfo {
	
	private String name;// 名称
	private String id_card_num;// 身份证号码
	private String cell_phone_num;// 手机号码
	private String home_addr;// 我的家庭地址
	private String work_tel;// 工作电话
	private String work_addr;// 工作地址
	private String home_tel;// 家庭电话
	private String cell_phone_num2;// 备用电话
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getId_card_num() {
		return id_card_num;
	}
	public void setId_card_num(String id_card_num) {
		this.id_card_num = id_card_num;
	}
	public String getCell_phone_num() {
		return cell_phone_num;
	}
	public void setCell_phone_num(String cell_phone_num) {
		this.cell_phone_num = cell_phone_num;
	}
	public String getHome_addr() {
		return home_addr;
	}
	public void setHome_addr(String home_addr) {
		this.home_addr = home_addr;
	}
	public String getWork_tel() {
		return work_tel;
	}
	public void setWork_tel(String work_tel) {
		this.work_tel = work_tel;
	}
	public String getWork_addr() {
		return work_addr;
	}
	public void setWork_addr(String work_addr) {
		this.work_addr = work_addr;
	}
	public String getHome_tel() {
		return home_tel;
	}
	public void setHome_tel(String home_tel) {
		this.home_tel = home_tel;
	}
	public String getCell_phone_num2() {
		return cell_phone_num2;
	}
	public void setCell_phone_num2(String cell_phone_num2) {
		this.cell_phone_num2 = cell_phone_num2;
	}
	
	
	public String toString() {
		return "UserBasicInfo [name=" + name + ", id_card_num=" + id_card_num
				+ ", cell_phone_num=" + cell_phone_num + ", home_addr="
				+ home_addr + ", work_tel=" + work_tel + ", work_addr="
				+ work_addr + ", home_tel=" + home_tel + ", cell_phone_num2="
				+ cell_phone_num2 + "]";
	}

	
	
}
