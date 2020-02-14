/**   
* @Description: 信用卡贷款信息 
* @author xiaobin.hou  
* @date 2016年7月10日 下午5:29:01 
* @version V1.0   
*/
package com.wanda.credit.ds.client.juxinli.bean.PBOCReport;

import java.util.List;

/**
 * @author xiaobin.hou
 *
 */
public class RecordCreditLoanInfo {
	
	private List<CreditNoOverdueAccount> credit_no_overdue_account;// 从未逾期过的贷记卡及透支未超过60天的准贷记卡账户明细（精确）
	private List<CreditOverdueAccount> credit_overdue_account;// 发生过逾期的贷记卡账户明细（精确）
	private List<String> overdue_account_detail;// 发生过逾期的贷记卡账户明细
	private List<String> no_overdue_account_detail;// 从未逾期过的贷记卡及透支未超过60天的准贷记卡账户明细
	
	
	public List<CreditNoOverdueAccount> getCredit_no_overdue_account() {
		return credit_no_overdue_account;
	}
	public void setCredit_no_overdue_account(
			List<CreditNoOverdueAccount> credit_no_overdue_account) {
		this.credit_no_overdue_account = credit_no_overdue_account;
	}
	public List<CreditOverdueAccount> getCredit_overdue_account() {
		return credit_overdue_account;
	}
	public void setCredit_overdue_account(
			List<CreditOverdueAccount> credit_overdue_account) {
		this.credit_overdue_account = credit_overdue_account;
	}
	public List<String> getOverdue_account_detail() {
		return overdue_account_detail;
	}
	public void setOverdue_account_detail(List<String> overdue_account_detail) {
		this.overdue_account_detail = overdue_account_detail;
	}
	public List<String> getNo_overdue_account_detail() {
		return no_overdue_account_detail;
	}
	public void setNo_overdue_account_detail(List<String> no_overdue_account_detail) {
		this.no_overdue_account_detail = no_overdue_account_detail;
	}
	@Override
	public String toString() {
		return "RecordCreditLoanInfo [credit_no_overdue_account="
				+ credit_no_overdue_account + ", credit_overdue_account="
				+ credit_overdue_account + ", overdue_account_detail="
				+ overdue_account_detail + ", no_overdue_account_detail="
				+ no_overdue_account_detail + "]";
	}

	
}
