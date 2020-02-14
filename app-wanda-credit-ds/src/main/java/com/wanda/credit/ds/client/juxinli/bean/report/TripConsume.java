package com.wanda.credit.ds.client.juxinli.bean.report;

/**
 * 出行消费列表
 * @author xiaobin.hou
 *
 */
public class TripConsume {
	
	/**
	 * 月度消费频次(/人次)
	 */
	private String count;
	
	/**
	 * 酒店月消费
	 */
	private String hotel_spend;
	
	/**
	 * 月度总消费
	 */
	private String total_spend;
	
	/**
	 * 机票月消费
	 */
	private String flight_spend;
	
	/**
	 * 火车月消费
	 */
	private String train_spend;
	
	/**
	 * 下单时间(按月汇总)
	 */
	private String order_date;
	
	public String getCount(){
	
		return count;
	}
	
	public void setCount(String count){
	
		this.count = count;
	}
	
	public String getFlight_spend(){
	
		return flight_spend;
	}
	
	public void setFlight_spend(String flight_spend){
	
		this.flight_spend = flight_spend;
	}
	
	public String getHotel_spend(){
	
		return hotel_spend;
	}
	
	public void setHotel_spend(String hotel_spend){
	
		this.hotel_spend = hotel_spend;
	}
	
	public String getOrder_date(){
	
		return order_date;
	}
	
	public void setOrder_date(String order_date){
	
		this.order_date = order_date;
	}
	
	public String getTotal_spend(){
	
		return total_spend;
	}
	
	public void setTotal_spend(String total_spend){
	
		this.total_spend = total_spend;
	}
	
	public String getTrain_spend(){
	
		return train_spend;
	}
	
	public void setTrain_spend(String train_spend){
	
		this.train_spend = train_spend;
	}
	
	public String toString(){
	
		return "TripConsume [count=" + count + ", hotel_spend=" + hotel_spend + ", total_spend=" + total_spend
		        + ", flight_spend=" + flight_spend + ", train_spend=" + train_spend + ", order_date=" + order_date
		        + "]";
	}
	
}
