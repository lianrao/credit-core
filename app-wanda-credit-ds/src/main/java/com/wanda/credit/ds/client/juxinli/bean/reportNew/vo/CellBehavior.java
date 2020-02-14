package com.wanda.credit.ds.client.juxinli.bean.reportNew.vo;

import java.util.List;
/**
 * 运营商数据整理 即 通话行为分析
 * @author xiaobin.hou
 *
 */
public class CellBehavior {
	
	/**
	 * 手机号码
	 */
	private String phone_num;
	
	/**
	 * 通话行为
	 */
	private List<Behavior> behavior;
	
	public List<Behavior> getBehavior(){
	
		return behavior;
	}
	
	public void setBehavior(List<Behavior> behavior){
	
		this.behavior = behavior;
	}
	
	public String getPhone_num(){
	
		return phone_num;
	}
	
	public void setPhone_num(String phone_num){
	
		this.phone_num = phone_num;
	}
	
	@Override
	public String toString(){
	
		return "CellBehavior{" + "behavior=" + behavior + ", phone_num='" + phone_num + '\'' + '}';
	}
}
