package com.wanda.credit.ds.client.juxinli.bean.ebusi.origin;


/**
 * @author xiaobin.hou 商品明细
 */
public class Items {
	
	/**
	 * 交易时间
	 */
	private String trans_time;
	/**
	 * 获取数据时间
	 */
	private String update_time;
	
	/**
	 * 单价
	 */
	private String product_price;
	
	/**
	 * 数量
	 */
	private String product_cnt;
	
	/**
	 * 商品名称
	 */
	private String product_name;
	
	public String getTrans_time(){
	
		return trans_time;
	}
	
	public void setTrans_time(String trans_time){
	
		this.trans_time = trans_time;
	}
	
	public String getProduct_price(){
	
		return product_price;
	}
	
	public void setProduct_price(String product_price){
	
		this.product_price = product_price;
	}
	
	public String getProduct_cnt(){
	
		return product_cnt;
	}
	
	public void setProduct_cnt(String product_cnt){
	
		this.product_cnt = product_cnt;
	}
	
	public String getProduct_name(){
	
		return product_name;
	}
	
	public void setProduct_name(String product_name){
	
		this.product_name = product_name;
	}
	
	public String toString(){
	
		return "Items [trans_time=" + trans_time + ", product_price=" + product_price + ", product_cnt=" + product_cnt
		        + ", product_name=" + product_name + "]";
	}

	public String getUpdate_time() {
		return update_time;
	}

	public void setUpdate_time(String update_time) {
		this.update_time = update_time;
	}
	
}
