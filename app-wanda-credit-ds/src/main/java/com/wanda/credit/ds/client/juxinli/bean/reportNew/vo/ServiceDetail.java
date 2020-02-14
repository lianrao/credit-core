package com.wanda.credit.ds.client.juxinli.bean.reportNew.vo;
/**
 * 常用服务信息汇总 之 服务细节表
 * @author xiaobin.hou
 *
 */
public class ServiceDetail {
	
	/**
	 * 服务次数
	 */
	private String interact_cnt;
	
	/**
	 * 月份
	 */
	private String interact_mth;
	
	public String getInteract_cnt(){
	
		return interact_cnt;
	}
	
	public void setInteract_cnt(String interact_cnt){
	
		this.interact_cnt = interact_cnt;
	}
	
	public String getInteract_mth(){
	
		return interact_mth;
	}
	
	public void setInteract_mth(String interact_mth){
	
		this.interact_mth = interact_mth;
	}
	
	@Override
	public String toString(){
	
		return "ServiceDetail [interact_cnt=" + interact_cnt + ", interact_mth=" + interact_mth + "]";
	}
}
