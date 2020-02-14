/**   
* @Description: javaBean 逾期账户明细
* @author xiaobin.hou  
* @date 2016年7月10日 下午4:57:11 
* @version V1.0   
*/
package com.wanda.credit.ds.client.juxinli.bean.PBOCReport;

/**
 * @author xiaobin.hou
 *
 */
public class LoanNoOverdueAccount {
	
	private String deadline_time;
	private String balance;
	private String grant_amount_type;
	private String clear_time;
	private String grant_time;
	private String grant_amount;
	private String grant_name;
	private String grant_company;
	private String expiration_time;
	
	
	public String getDeadline_time() {
		return deadline_time;
	}
	public void setDeadline_time(String deadline_time) {
		this.deadline_time = deadline_time;
	}
	public String getBalance() {
		return balance;
	}
	public void setBalance(String balance) {
		this.balance = balance;
	}
	public String getGrant_amount_type() {
		return grant_amount_type;
	}
	public void setGrant_amount_type(String grant_amount_type) {
		this.grant_amount_type = grant_amount_type;
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
	public String getExpiration_time() {
		return expiration_time;
	}
	public void setExpiration_time(String expiration_time) {
		this.expiration_time = expiration_time;
	}
	
	
	@Override
	public String toString() {
		return "LoanNoOverdueAccount [deadline_time=" + deadline_time
				+ ", balance=" + balance + ", grant_amount_type="
				+ grant_amount_type + ", clear_time=" + clear_time
				+ ", grant_time=" + grant_time + ", grant_amount="
				+ grant_amount + ", grant_name=" + grant_name
				+ ", grant_company=" + grant_company + ", expiration_time="
				+ expiration_time + "]";
	}


}
