/**   
* @Description: javaBean 发生过逾期的账户明细 
* @author xiaobin.hou  
* @date 2016年7月10日 下午4:51:52 
* @version V1.0   
*/
package com.wanda.credit.ds.client.juxinli.bean.PBOCReport;

/**
 * @author xiaobin.hou
 *
 */
public class LoanOverdueAccount {
	
	private String month_of_five_year_90;
	private String month_of_five_year;
	private String grant_amount_type;
	private String overdue_amount;
	private String clear_time;
	private String grant_time;
	private String grant_amount;
	private String grant_name;
	private String grant_company;
	private String balance;
	private String expiration_time;
	private String deadline_time;
	
	
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
	public String getGrant_amount_type() {
		return grant_amount_type;
	}
	public void setGrant_amount_type(String grant_amount_type) {
		this.grant_amount_type = grant_amount_type;
	}
	public String getOverdue_amount() {
		return overdue_amount;
	}
	public void setOverdue_amount(String overdue_amount) {
		this.overdue_amount = overdue_amount;
	}
	public String getClear_time() {
		return clear_time;
	}
	public void setClear_time(String clear_time) {
		this.clear_time = clear_time;
	}
	public String getGrant_time() {
		return grant_time;
	}
	public void setGrant_time(String grant_time) {
		this.grant_time = grant_time;
	}
	public String getGrant_amount() {
		return grant_amount;
	}
	public void setGrant_amount(String grant_amount) {
		this.grant_amount = grant_amount;
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
	public String getBalance() {
		return balance;
	}
	public void setBalance(String balance) {
		this.balance = balance;
	}
	public String getExpiration_time() {
		return expiration_time;
	}
	public void setExpiration_time(String expiration_time) {
		this.expiration_time = expiration_time;
	}
	public String getDeadline_time() {
		return deadline_time;
	}
	public void setDeadline_time(String deadline_time) {
		this.deadline_time = deadline_time;
	}
	
	
	@Override
	public String toString() {
		return "LoanOverdueAccount [month_of_five_year_90="
				+ month_of_five_year_90 + ", month_of_five_year="
				+ month_of_five_year + ", grant_amount_type="
				+ grant_amount_type + ", overdue_amount=" + overdue_amount
				+ ", clear_time=" + clear_time + ", grant_time=" + grant_time
				+ ", grant_amount=" + grant_amount + ", grant_name="
				+ grant_name + ", grant_company=" + grant_company
				+ ", balance=" + balance + ", expiration_time="
				+ expiration_time + ", deadline_time=" + deadline_time + "]";
	}


}
