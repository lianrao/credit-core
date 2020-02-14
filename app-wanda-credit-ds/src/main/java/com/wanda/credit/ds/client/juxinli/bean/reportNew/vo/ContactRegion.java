package com.wanda.credit.ds.client.juxinli.bean.reportNew.vo;

/**
 * 联系人区域汇总
 * @author xiaobin.hou
 *
 */
public class ContactRegion {
	
	/**
	 * 地区名称
	 */
	private String region_loc;
	
	/**
	 * 去重后的号码数量
	 */
	private String region_uniq_num_cnt;
	
	/**
	 * 电话呼出时间（秒）
	 */
	private String region_call_out_time;
	
	/**
	 * 平均电话呼入时间（秒）
	 */
	private String region_avg_call_in_time;
	
	/**
	 * 电话呼入时间（秒）
	 */
	private String region_call_in_time;
	
	/**
	 * 电话呼出次数
	 */
	private String region_call_out_cnt;
	
	/**
	 * 平均电话呼出时间（秒）
	 */
	private String region_avg_call_out_time;
	
	/**
	 * 电话呼入次数百分比
	 */
	private String region_call_in_cnt_pct;
	
	/**
	 * 电话呼入时间百分比
	 */
	private String region_call_in_time_pct;
	
	/**
	 * 电话呼入次数
	 */
	private String region_call_in_cnt;
	
	/**
	 * 电话呼出时间百分比
	 */
	private String region_call_out_time_pct;
	
	/**
	 * 电话呼出次数百分比
	 */
	private String region_call_out_cnt_pct;
	
	public String getRegion_avg_call_in_time(){
	
		return region_avg_call_in_time;
	}
	
	public void setRegion_avg_call_in_time(String region_avg_call_in_time){
	
		this.region_avg_call_in_time = region_avg_call_in_time;
	}
	
	public String getRegion_avg_call_out_time(){
	
		return region_avg_call_out_time;
	}
	
	public void setRegion_avg_call_out_time(String region_avg_call_out_time){
	
		this.region_avg_call_out_time = region_avg_call_out_time;
	}
	
	public String getRegion_call_in_cnt(){
	
		return region_call_in_cnt;
	}
	
	public void setRegion_call_in_cnt(String region_call_in_cnt){
	
		this.region_call_in_cnt = region_call_in_cnt;
	}
	
	public String getRegion_call_in_cnt_pct(){
	
		return region_call_in_cnt_pct;
	}
	
	public void setRegion_call_in_cnt_pct(String region_call_in_cnt_pct){
	
		this.region_call_in_cnt_pct = region_call_in_cnt_pct;
	}
	
	public String getRegion_call_in_time(){
	
		return region_call_in_time;
	}
	
	public void setRegion_call_in_time(String region_call_in_time){
	
		this.region_call_in_time = region_call_in_time;
	}
	
	public String getRegion_call_in_time_pct(){
	
		return region_call_in_time_pct;
	}
	
	public void setRegion_call_in_time_pct(String region_call_in_time_pct){
	
		this.region_call_in_time_pct = region_call_in_time_pct;
	}
	
	public String getRegion_call_out_cnt(){
	
		return region_call_out_cnt;
	}
	
	public void setRegion_call_out_cnt(String region_call_out_cnt){
	
		this.region_call_out_cnt = region_call_out_cnt;
	}
	
	public String getRegion_call_out_cnt_pct(){
	
		return region_call_out_cnt_pct;
	}
	
	public void setRegion_call_out_cnt_pct(String region_call_out_cnt_pct){
	
		this.region_call_out_cnt_pct = region_call_out_cnt_pct;
	}
	
	public String getRegion_call_out_time(){
	
		return region_call_out_time;
	}
	
	public void setRegion_call_out_time(String region_call_out_time){
	
		this.region_call_out_time = region_call_out_time;
	}
	
	public String getRegion_call_out_time_pct(){
	
		return region_call_out_time_pct;
	}
	
	public void setRegion_call_out_time_pct(String region_call_out_time_pct){
	
		this.region_call_out_time_pct = region_call_out_time_pct;
	}
	
	public String getRegion_loc(){
	
		return region_loc;
	}
	
	public void setRegion_loc(String region_loc){
	
		this.region_loc = region_loc;
	}
	
	public String getRegion_uniq_num_cnt(){
	
		return region_uniq_num_cnt;
	}
	
	public void setRegion_uniq_num_cnt(String region_uniq_num_cnt){
	
		this.region_uniq_num_cnt = region_uniq_num_cnt;
	}
	
	@Override
	public String toString(){
	
		return "ContactRegion [region_loc=" + region_loc + ", region_uniq_num_cnt=" + region_uniq_num_cnt
		        + ", region_call_out_time=" + region_call_out_time + ", region_avg_call_in_time="
		        + region_avg_call_in_time + ", region_call_in_time=" + region_call_in_time + ", region_call_out_cnt="
		        + region_call_out_cnt + ", region_avg_call_out_time=" + region_avg_call_out_time
		        + ", region_call_in_cnt_pct=" + region_call_in_cnt_pct + ", region_call_in_time_pct="
		        + region_call_in_time_pct + ", region_call_in_cnt=" + region_call_in_cnt
		        + ", region_call_out_time_pct=" + region_call_out_time_pct + ", region_call_out_cnt_pct="
		        + region_call_out_cnt_pct + "]";
	}
	
}
