package com.wanda.credit.ds.client.juxinli.bean.report;

import java.util.List;

/**
 * 出行数据
 * @author xiaobin.hou
 *
 */
public class TripInfo {
	
	/**
	 * 目的地
	 */
	private String trip_dest;
	/**
	 * 出行开始时间
	 */
	private String trip_start_time;
	/**
	 * 出行结束时间
	 */
	private String trip_end_time;
	/**
	 * 出行工具
	 */
	private List<String> trip_transportation;
	/**
	 * 同行人
	 */
	private List<String> trip_person;
	/**
	 * 出发地
	 */
	private String trip_leave;
	/**
	 * 数据来源
	 */
	private List<String> trip_data_source;	
	/**
	 * 出行时间类型
	 */
	private String trip_type;
	
	public List<String> getTrip_data_source(){
	
		return trip_data_source;
	}
	
	public void setTrip_data_source(List<String> trip_data_source){
	
		this.trip_data_source = trip_data_source;
	}
	
	public String getTrip_dest(){
	
		return trip_dest;
	}
	
	public void setTrip_dest(String trip_dest){
	
		this.trip_dest = trip_dest;
	}
	
	public String getTrip_end_time(){
	
		return trip_end_time;
	}
	
	public void setTrip_end_time(String trip_end_time){
	
		this.trip_end_time = trip_end_time;
	}
	
	public String getTrip_leave(){
	
		return trip_leave;
	}
	
	public void setTrip_leave(String trip_leave){
	
		this.trip_leave = trip_leave;
	}
	
	public List<String> getTrip_person(){
	
		return trip_person;
	}
	
	public void setTrip_person(List<String> trip_person){
	
		this.trip_person = trip_person;
	}
	
	public String getTrip_start_time(){
	
		return trip_start_time;
	}
	
	public void setTrip_start_time(String trip_start_time){
	
		this.trip_start_time = trip_start_time;
	}
	
	public List<String> getTrip_transportation(){
	
		return trip_transportation;
	}
	
	public void setTrip_transportation(List<String> trip_transportation){
	
		this.trip_transportation = trip_transportation;
	}
	
	public String getTrip_type(){
	
		return trip_type;
	}
	
	public void setTrip_type(String trip_type){
	
		this.trip_type = trip_type;
	}
	
	@Override
	public String toString(){
	
		return "TripInfo{" + "trip_data_source=" + trip_data_source + ", trip_dest='" + trip_dest + '\''
		        + ", trip_leave='" + trip_leave + '\'' + ", trip_end_time='" + trip_end_time + '\''
		        + ", trip_transportation=" + trip_transportation + ", trip_person=" + trip_person
		        + ", trip_start_time='" + trip_start_time + '\'' + ", trip_type='" + trip_type + '\'' + '}';
	}
}
