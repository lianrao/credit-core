package com.wanda.credit.ds.client.juxinli.bean.ebusi.origin;

import java.util.List;

/**
 * @author xiaobin.hou 电商详单信息
 */
public class Transactions {
	
	/**
	 * 商品总价
	 */
	private String total_price;
	/**
	 * 获取数据的时间
	 */
	private String update_time;
	
	/**
	 * 交易时间
	 */
	private String trans_time;
	
	/**
	 * 送货费用
	 */
	private String delivery_fee;
	
	/**
	 * 收货地址
	 */
	private String receiver_addr;
	
	/**
	 * 交易id
	 */
	private String order_id;
	
	/**
	 * 收货人姓名
	 */
	private String receiver_name;
	
	/**
	 * 账单类型
	 */
	private String bill_type;
	
	/**
	 * 收货人邮编
	 */
	private String zipcode;
	
	/**
	 * 收货人单位
	 */
	private String receiver_title;
	
	/**
	 * 收货人手机号
	 */
	private String receiver_cell_phone;
	
	/**
	 * 付款方式
	 */
	private String payment_type;
	
	/**
	 * 送货方式
	 */
	private String delivery_type;
	
	/**
	 * 收货人电话
	 */
	private String receiver_phone;
	
	/**
	 * 发票抬头
	 */
	private String bill_title;	
	/**
	 * 商品明细
	 */
	
	private List<Items> items;
	
	/**
	 * 是否交易成功
	 */
	private boolean is_success;
	
	public String getTotal_price(){
	
		return total_price;
	}
	
	public void setTotal_price(String total_price){
	
		this.total_price = total_price;
	}
	
	public String getTrans_time(){
	
		return trans_time;
	}
	
	public void setTrans_time(String trans_time){
	
		this.trans_time = trans_time;
	}
	
	public String getDelivery_fee(){
	
		return delivery_fee;
	}
	
	public void setDelivery_fee(String delivery_fee){
	
		this.delivery_fee = delivery_fee;
	}
	
	public String getReceiver_addr(){
	
		return receiver_addr;
	}
	
	public void setReceiver_addr(String receiver_addr){
	
		this.receiver_addr = receiver_addr;
	}
	
	public String getOrder_id(){
	
		return order_id;
	}
	
	public void setOrder_id(String order_id){
	
		this.order_id = order_id;
	}
	
	public String getReceiver_name(){
	
		return receiver_name;
	}
	
	public void setReceiver_name(String receiver_name){
	
		this.receiver_name = receiver_name;
	}
	
	public String getBill_type(){
	
		return bill_type;
	}
	
	public void setBill_type(String bill_type){
	
		this.bill_type = bill_type;
	}
	
	public String getZipcode(){
	
		return zipcode;
	}
	
	public void setZipcode(String zipcode){
	
		this.zipcode = zipcode;
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
	
	public String getPayment_type(){
	
		return payment_type;
	}
	
	public void setPayment_type(String payment_type){
	
		this.payment_type = payment_type;
	}
	
	public String getDelivery_type(){
	
		return delivery_type;
	}
	
	public void setDelivery_type(String delivery_type){
	
		this.delivery_type = delivery_type;
	}
	
	public String getReceiver_phone(){
	
		return receiver_phone;
	}
	
	public void setReceiver_phone(String receiver_phone){
	
		this.receiver_phone = receiver_phone;
	}
	
	public String getBill_title(){
	
		return bill_title;
	}
	
	public void setBill_title(String bill_title){
	
		this.bill_title = bill_title;
	}
	
	public List<Items> getItems(){
	
		return items;
	}
	
	public void setItems(List<Items> items){
	
		this.items = items;
	}
	
	public boolean isIs_success(){
	
		return is_success;
	}
	
	public void setIs_success(boolean is_success){
	
		this.is_success = is_success;
	}
	
	public String toString(){
	
		return "Transactions [total_price=" + total_price + ", trans_time=" + trans_time + ", delivery_fee="
		        + delivery_fee + ", receiver_addr=" + receiver_addr + ", order_id=" + order_id + ", receiver_name="
		        + receiver_name + ", bill_type=" + bill_type + ", zipcode=" + zipcode + ", receiver_title="
		        + receiver_title + ", receiver_cell_phone=" + receiver_cell_phone + ", payment_type=" + payment_type
		        + ", delivery_type=" + delivery_type + ", receiver_phone=" + receiver_phone + ", bill_title="
		        + bill_title + ", items=" + items + ", is_success=" + is_success + "]";
	}

	public String getUpdate_time() {
		return update_time;
	}

	public void setUpdate_time(String update_time) {
		this.update_time = update_time;
	}
	
}
