/**   
* @Description: 房屋贷款信息
* @author xiaobin.hou  
* @date 2016年7月10日 下午5:28:25 
* @version V1.0   
*/
package com.wanda.credit.ds.client.juxinli.bean.PBOCReport;

import java.util.List;

/**
 * @author xiaobin.hou
 *
 */
public class RecordHousingLoanInfo {
	
	private List<LoanNoOverdueAccount> loan_no_overdue_account;// 从未逾期过的账户明细（精确）
	private List<LoanOverdueAccount> loan_overdue_account;// 发生过逾期的账户明细（精确）
	private List<String> overdue_account_detail;// 发生过逾期的账户明细
	private List<String> no_overdue_account_detail;// 从未逾期过的账户明细
	
	
	public List<LoanNoOverdueAccount> getLoan_no_overdue_account() {
		return loan_no_overdue_account;
	}
	public void setLoan_no_overdue_account(
			List<LoanNoOverdueAccount> loan_no_overdue_account) {
		this.loan_no_overdue_account = loan_no_overdue_account;
	}
	public List<LoanOverdueAccount> getLoan_overdue_account() {
		return loan_overdue_account;
	}
	public void setLoan_overdue_account(
			List<LoanOverdueAccount> loan_overdue_account) {
		this.loan_overdue_account = loan_overdue_account;
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
		return "RecordHousingLoanInfo [loan_no_overdue_account="
				+ loan_no_overdue_account + ", loan_overdue_account="
				+ loan_overdue_account + ", overdue_account_detail="
				+ overdue_account_detail + ", no_overdue_account_detail="
				+ no_overdue_account_detail + "]";
	}

}
