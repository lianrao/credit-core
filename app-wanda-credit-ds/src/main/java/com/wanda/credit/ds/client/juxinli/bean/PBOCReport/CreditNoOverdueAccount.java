/**   
* @Description: 信用卡未逾期明细 
* @author xiaobin.hou  
* @date 2016年7月10日 下午5:05:34 
* @version V1.0   
*/
package com.wanda.credit.ds.client.juxinli.bean.PBOCReport;

/**
 * @author xiaobin.hou
 *
 */
public class CreditNoOverdueAccount {
	
	private String status;// 状态
	private String overdraft_balance;// 支余额（余额）
	private String deadline_time;// 截至日期
	private String credit_limit;// 用额度（折合人民币）
	private String grant_time;// 发放信用卡日期
	private String grant_account_type;// 发放信用卡账户类型
	private String grant_name;// 发放信用卡名称
	private String grant_company;// 发放信用卡单位
	private String used_quotas;// 使用额度
	
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getOverdraft_balance() {
		return overdraft_balance;
	}
	public void setOverdraft_balance(String overdraft_balance) {
		this.overdraft_balance = overdraft_balance;
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
	@Override
	public String toString() {
		return "CreditNoOverdueAccount [status=" + status
				+ ", overdraft_balance=" + overdraft_balance
				+ ", deadline_time=" + deadline_time + ", credit_limit="
				+ credit_limit + ", grant_time=" + grant_time
				+ ", grant_account_type=" + grant_account_type
				+ ", grant_name=" + grant_name + ", grant_company="
				+ grant_company + ", used_quotas=" + used_quotas + "]";
	}
	
	

}
