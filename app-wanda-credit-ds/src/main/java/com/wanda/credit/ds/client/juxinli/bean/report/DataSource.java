package com.wanda.credit.ds.client.juxinli.bean.report;

/**
 * 绑定数据源信息
 * @author xiaobin.hou
 *
 */
public class DataSource {
	
	/**
	 * 绑定的账号数据量是否足够支持数据推断
	 */
	private String status;
	
	/**
	 * 绑定的账号名称
	 */
	private String account;
	
	/**
	 * 绑定数据源的名称
	 */
	private String name;
	
	/**
	 * 最早绑定的时间
	 */
	private String binding_time;
	
	/**
	 * 数据源类型名称(中文)
	 */
	private String category_value;
	
	/**
	 * 绑定的账号是否实名认证
	 */
	private String reliability;
	
	/**
	 * 绑定数据源的变量名
	 */
	private String key;
	
	/**
	 * 数据源类型（英文）
	 */
	private String category_name;
	
	public String getAccount(){
	
		return account;
	}
	
	public void setAccount(String account){
	
		this.account = account;
	}
	
	public String getBinding_time(){
	
		return binding_time;
	}
	
	public void setBinding_time(String binding_time){
	
		this.binding_time = binding_time;
	}
	
	public String getCategory_name(){
	
		return category_name;
	}
	
	public void setCategory_name(String category_name){
	
		this.category_name = category_name;
	}
	
	public String getCategory_value(){
	
		return category_value;
	}
	
	public void setCategory_value(String category_value){
	
		this.category_value = category_value;
	}
	
	public String getKey(){
	
		return key;
	}
	
	public void setKey(String key){
	
		this.key = key;
	}
	
	public String getName(){
	
		return name;
	}
	
	public void setName(String name){
	
		this.name = name;
	}
	
	public String getReliability(){
	
		return reliability;
	}
	
	public void setReliability(String reliability){
	
		this.reliability = reliability;
	}
	
	public String getStatus(){
	
		return status;
	}
	
	public void setStatus(String status){
	
		this.status = status;
	}
	
	@Override
	public String toString(){
	
		return "DataSource{" + "account='" + account + '\'' + ", status='" + status + '\'' + ", name='" + name + '\''
		        + ", binding_time='" + binding_time + '\'' + ", category_value='" + category_value + '\''
		        + ", reliability='" + reliability + '\'' + ", key='" + key + '\'' + ", category_name='" + category_name
		        + '\'' + '}';
	}
}
