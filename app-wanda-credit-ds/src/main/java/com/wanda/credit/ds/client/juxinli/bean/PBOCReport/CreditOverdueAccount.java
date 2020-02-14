/**   
* @Description: JavaBean 信用卡逾期明细 
* @author xiaobin.hou  
* @date 2016年7月10日 下午5:06:27 
* @version V1.0   
*/
package com.wanda.credit.ds.client.juxinli.bean.PBOCReport;

/**
 * @author xiaobin.hou
 *
 */
public class CreditOverdueAccount {
	
	private String status;// 状态
	private String overdue_amount;// 期金额
	private String deadline_time;// 截至日期
	private String credit_limit;// 用额度（折合人民币）
	private String grant_time;// 发放信用卡日期
	private String grant_account_type;// 发放信用卡账户类型
	private String grant_name;// 发放信用卡名称
	private String grant_company;// 发放信用卡单位
	private String used_quotas;// 使用额度
	private String month_of_five_year_90;// 内逾期超过90天的月份数
	private String month_of_five_year;// 内处于逾期状态的月份数
	
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getOverdue_amount() {
		return overdue_amount;
	}
	public void setOverdue_amount(String overdue_amount) {
		this.overdue_amount = overdue_amount;
	}
	public String getDeadline_time() {
		return deadline_time;
	}
	public void setDeadline_time(String deadline_time) {
		this.deadline_time = deadline_time;
	}
	public String getCredit_limit() {
		return credit_limit;
	}
	public void setCredit_limit(String credit_limit) {
		this.credit_limit = credit_limit;
	}
	public String getGrant_time() {
		return grant_time;
	}
	public void setGrant_time(String grant_time) {
		this.grant_time = grant_time;
	}
	public String getGrant_account_type() {
		return grant_account_type;
	}
	public void setGrant_account_type(String grant_account_type) {
		this.grant_account_type = grant_account_type;
	}
	public String getGrant_name() {
		return grant_name;
	}
	public void setGrant_name(String grant_name) {
		this.grant_name = grant_name;
	}
	public String getGrant_company() {
		return grant_company;
	}
	public void setGrant_company(String grant_company) {
		this.grant_company = grant_company;
	}
	public String getUsed_quotas() {
		return used_quotas;
	}
	public void setUsed_quotas(String used_quotas) {
		this.used_quotas = used_quotas;
	}
	public String getMonth_of_five_year_90() {
		return month_of_five_year_90;
	}
	public void setMonth_of_five_year_90(String month_of_five_year_90) {
		this.month_of_five_year_90 = month_of_five_year_90;
	}
	public String getMonth_of_five_year() {
		return month_of_five_year;
	}
	public void setMonth_of_five_year(String month_of_five_year) {
		this.month_of_five_year = month_of_five_year;
	}

}
