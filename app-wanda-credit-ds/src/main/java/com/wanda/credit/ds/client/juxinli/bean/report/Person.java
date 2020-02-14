package com.wanda.credit.ds.client.juxinli.bean.report;

/**
 * 申请人信息
 * @author xiaobin.hou
 *
 */
public class Person {
	
	/**
	 * 申请人出生省份
	 */
	private String province;
	
	/**
	 * 申请人出生城市
	 */
	private String city;
	
	/**
	 * 申请人性别
	 */
	private String gender;
	
	/**
	 * 申请人年龄
	 */
	private String age;
	
	/**
	 * 申请人星座
	 */
	private String sign;
	
	/**
	 * 申请人出生省份(和province相同)
	 */
	private String state;
	
	/**
	 * 解析身份信息是否成功
	 */
	private String status;
	
	/**
	 * 申请人姓名
	 */
	private String real_name;
	
	/**
	 * 申请人出生县
	 */
	private String region;
	
	/**
	 * 申请人身份证号码
	 */
	private String id_card_num;
	

	
	public String getCity(){
	
		return city;
	}
	
	public void setCity(String city){
	
		this.city = city;
	}
	
	public String getGender(){
	
		return gender;
	}
	
	public void setGender(String gender){
	
		this.gender = gender;
	}
	
	public String getId_card_num(){
	
		return id_card_num;
	}
	
	public void setId_card_num(String id_card_num){
	
		this.id_card_num = id_card_num;
	}
	
	public String getProvince(){
	
		return province;
	}
	
	public void setProvince(String province){
	
		this.province = province;
	}
	
	public String getReal_name(){
	
		return real_name;
	}
	
	public void setReal_name(String real_name){
	
		this.real_name = real_name;
	}
	
	public String getRegion(){
	
		return region;
	}
	
	public void setRegion(String region){
	
		this.region = region;
	}
	
	public String getSign(){
	
		return sign;
	}
	
	public void setSign(String sign){
	
		this.sign = sign;
	}
	
	public String getState(){
	
		return state;
	}
	
	public void setState(String state){
	
		this.state = state;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
