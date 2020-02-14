package com.wanda.credit.ds.client.juxinli.bean.ebusi.origin;


/**
 * @author zhashiwen 电商收货地址
 */
public class Address {
	
	/**
	 * 收货地址
	 */
	private String receiver_addr;
	/**
	 * 获取数据的时间
	 */
	private String update_time;
	
	/**
	 * 地址省份
	 */
	private String province;
	
	/**
	 * 地址城市
	 */
	private String city;
	
	/**
	 * 是否是默认地址
	 */
	private boolean is_default_address;
	
	/**
	 * 邮编
	 */
	private String zipcode;
	
	/**
	 * 付款方式
	 */
	private String payment_type;
	
	/**
	 * 收货人姓名
	 */
	private String receiver_name;
	
	/**
	 * 收货人电话
	 */
	private String receiver_phone;
	
	/**
	 * 收货人单位
	 */
	private String receiver_title;
	
	/**
	 * 收货人手机号
	 */
	private String receiver_cell_phone;
	
	public String getReceiver_addr(){
	
		return receiver_addr;
	}
	
	public void setReceiver_addr(String receiver_addr){
	
		this.receiver_addr = receiver_addr;
	}
	
	public String getProvince(){
	
		return province;
	}
	
	public void setProvince(String province){
	
		this.province = province;
	}
	
	public String getCity(){
	
		return city;
	}
	
	public void setCity(String city){
	
		this.city = city;
	}
	
	public boolean isIs_default_address(){
	
		return is_default_address;
	}
	
	public void setIs_default_address(boolean is_default_address){
	
		this.is_default_address = is_default_address;
	}
	
	public String getZipcode(){
	
		return zipcode;
	}
	
	public void setZipcode(String zipcode){
	
		this.zipcode = zipcode;
	}
	
	public String getPayment_type(){
	
		return payment_type;
	}
	
	public void setPayment_type(String payment_type){
	
		this.payment_type = payment_type;
	}
	
	public String getReceiver_name(){
	
		return receiver_name;
	}
	
	public void setReceiver_name(String receiver_name){
	
		this.receiver_name = receiver_name;
	}
	
	public String getReceiver_phone(){
	
		return receiver_phone;
	}
	
	public void setReceiver_phone(String receiver_phone){
	
		this.receiver_phone = receiver_phone;
	}
	
	public String getReceiver_title(){
	
		return receiver_title;
	}
	
	public void setReceiver_title(String receiver_title){
	
		this.receiver_title = receiver_title;
	}
	
	public String getReceiver_cell_phone(){
	
		return receiver_cell_phone;
	}
	
	public void setReceiver_cell_phone(String receiver_cell_phone){
	
		this.receiver_cell_phone = receiver_cell_phone;
	}
	
	public String toString(){
	
		return "Address [receiver_addr=" + receiver_addr + ", province=" + province + ", city=" + city
		        + ", is_default_address=" + is_default_address + ", zipcode=" + zipcode + ", payment_type="
		        + payment_type + ", receiver_name=" + receiver_name + ", receiver_phone=" + receiver_phone
		        + ", receiver_title=" + receiver_title + ", receiver_cell_phone=" + receiver_cell_phone + "]";
	}

	public String getUpdate_time() {
		return update_time;
	}

	public void setUpdate_time(String update_time) {
		this.update_time = update_time;
	}
	
}
