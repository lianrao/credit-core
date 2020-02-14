package com.wanda.credit.ds.client.juxinli.bean.ebusi.origin;

/**
 * @author xiaobin.hou 电商基本信息
 */
public class Basic {
	
	/**
	 * 用户绑定邮箱
	 */
	private String email;
	/**
	 * 获取数据时间
	 */
	private String update_time;
	/**
	 * 用户手机号
	 */
	private String cell_phone;
	
	/**
	 * 安全等级
	 */
	private String security_level;
	
	/**
	 * 账号等级
	 */
	private String level;
	
	/**
	 * 是否认证
	 */
	private boolean is_validate_real_name;
	
	/**
	 * 网站id
	 */
	private String website_id;
	
	/**
	 * 昵称
	 */
	private String nickname;
	
	/**
	 * 注册时间
	 */
	private String register_date;
	
	/**
	 * 真实姓名
	 */
	private String real_name;
	
	public String getEmail(){
	
		return email;
	}
	
	public void setEmail(String email){
	
		this.email = email;
	}
	
	public String getCell_phone(){
	
		return cell_phone;
	}
	
	public void setCell_phone(String cell_phone){
	
		this.cell_phone = cell_phone;
	}
	
	public String getSecurity_level(){
	
		return security_level;
	}
	
	public void setSecurity_level(String security_level){
	
		this.security_level = security_level;
	}
	
	public String getLevel(){
	
		return level;
	}
	
	public void setLevel(String level){
	
		this.level = level;
	}
	
	public boolean isIs_validate_real_name(){
	
		return is_validate_real_name;
	}
	
	public void setIs_validate_real_name(boolean is_validate_real_name){
	
		this.is_validate_real_name = is_validate_real_name;
	}
	
	public String getWebsite_id(){
	
		return website_id;
	}
	
	public void setWebsite_id(String website_id){
	
		this.website_id = website_id;
	}
	
	public String getNickname(){
	
		return nickname;
	}
	
	public void setNickname(String nickname){
	
		this.nickname = nickname;
	}
	
	public String getRegister_date(){
	
		return register_date;
	}
	
	public void setRegister_date(String register_date){
	
		this.register_date = register_date;
	}
	
	public String getReal_name(){
	
		return real_name;
	}
	
	public void setReal_name(String real_name){
	
		this.real_name = real_name;
	}
	
	public String toString(){
	
		return "Basic [email=" + email + ", cell_phone=" + cell_phone + ", security_level=" + security_level
		        + ", level=" + level + ", is_validate_real_name=" + is_validate_real_name + ", website_id="
		        + website_id + ", nickname=" + nickname + ", register_date=" + register_date + ", real_name="
		        + real_name + "]";
	}

	public String getUpdate_time() {
		return update_time;
	}

	public void setUpdate_time(String update_time) {
		this.update_time = update_time;
	}
	
}
