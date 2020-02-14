package com.wanda.credit.ds.client.juxinli.bean.report;


/**
 * 呼叫信息表
 * @author xiaobin.hou
 *
 */
public class ContactDetails {
	
	/**
	 * 发送和接收短信次数
	 */
	private String sms_cnt;
	
	/**
	 * 联系人的电话号码
	 */
	private String phone_num;
	
	/**
	 * 号码的归属地
	 */
	private String phone_num_loc;
	
	/**
	 * 在呼叫记录里面最早出现的时间
	 */
	private String trans_start;
	
	/**
	 * 被叫次数
	 */
	private String call_in_cnt;
	
	/**
	 * 呼叫次数
	 */
	private String call_cnt;
	
	/**
	 * 呼叫时间(分钟)
	 */
	private String call_len;
	
	/**
	 * 主叫次数
	 */
	private String call_out_cnt;
	
	/**
	 * 在呼叫记录里面最晚出现的时间
	 */
	private String trans_end;
	
	public String getCall_cnt(){
	
		return call_cnt;
	}
	
	public void setCall_cnt(String call_cnt){
	
		this.call_cnt = call_cnt;
	}
	
	public String getCall_in_cnt(){
	
		return call_in_cnt;
	}
	
	public void setCall_in_cnt(String call_in_cnt){
	
		this.call_in_cnt = call_in_cnt;
	}
	
	public String getCall_len(){
	
		return call_len;
	}
	
	public void setCall_len(String call_len){
	
		this.call_len = call_len;
	}
	
	public String getCall_out_cnt(){
	
		return call_out_cnt;
	}
	
	public void setCall_out_cnt(String call_out_cnt){
	
		this.call_out_cnt = call_out_cnt;
	}
	
	public String getPhone_num(){
	
		return phone_num;
	}
	
	public void setPhone_num(String phone_num){
	
		this.phone_num = phone_num;
	}
	
	public String getPhone_num_loc(){
	
		return phone_num_loc;
	}
	
	public void setPhone_num_loc(String phone_num_loc){
	
		this.phone_num_loc = phone_num_loc;
	}
	
	public String getSms_cnt(){
	
		return sms_cnt;
	}
	
	public void setSms_cnt(String sms_cnt){
	
		this.sms_cnt = sms_cnt;
	}
	
	public String getTrans_end(){
	
		return trans_end;
	}
	
	public void setTrans_end(String trans_end){
	
		this.trans_end = trans_end;
	}
	
	public String getTrans_start(){
	
		return trans_start;
	}
	
	public void setTrans_start(String trans_start){
	
		this.trans_start = trans_start;
	}
	
	@Override
	public String toString(){
	
		return "ContactDetails [sms_cnt=" + sms_cnt + ", phone_num=" + phone_num + ", phone_num_loc=" + phone_num_loc
		        + ", trans_start=" + trans_start + ", call_in_cnt=" + call_in_cnt + ", call_cnt=" + call_cnt
		        + ", call_len=" + call_len + ", call_out_cnt=" + call_out_cnt + ", trans_end=" + trans_end + "]";
	}
	
}
