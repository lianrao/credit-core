/**   
* @Description: 公积金_原始数据_逾期信息 
* @author xiaobin.hou  
* @date 2016年5月29日 下午6:27:17 
* @version V1.0   
*/
package com.wanda.credit.ds.client.juxinli.bean.housefund;

/**
 * @author xiaobin.hou
 *
 */
public class RawDataOverdue {
	
	private String overdue_date;// 逾期日期
	private String overdue_principal;// 逾期本金
	private String overdue_interest;// 逾期利息
	private String overdue_penalty;// 逾期罚息
	private String overdue_summary;// 合计
	
	
	
	public String getOverdue_date() {
		return overdue_date;
	}
	public void setOverdue_date(String overdue_date) {
		this.overdue_date = overdue_date;
	}
	public String getOverdue_principal() {
		return overdue_principal;
	}
	public void setOverdue_principal(String overdue_principal) {
		this.overdue_principal = overdue_principal;
	}
	public String getOverdue_interest() {
		return overdue_interest;
	}
	public void setOverdue_interest(String overdue_interest) {
		this.overdue_interest = overdue_interest;
	}
	public String getOverdue_penalty() {
		return overdue_penalty;
	}
	public void setOverdue_penalty(String overdue_penalty) {
		this.overdue_penalty = overdue_penalty;
	}
	public String getOverdue_summary() {
		return overdue_summary;
	}
	public void setOverdue_summary(String overdue_summary) {
		this.overdue_summary = overdue_summary;
	}
	
	
	public String toString() {
		return "RawDataOverdue [overdue_date=" + overdue_date
				+ ", overdue_principal=" + overdue_principal
				+ ", overdue_interest=" + overdue_interest
				+ ", overdue_penalty=" + overdue_penalty + ", overdue_summary="
				+ overdue_summary + "]";
	}

}
