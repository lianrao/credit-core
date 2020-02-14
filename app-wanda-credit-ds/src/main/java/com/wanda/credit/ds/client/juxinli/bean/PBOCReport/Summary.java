/**   
* @Description: TODO(用一句话描述该文件做什么) 
* @author xiaobin.hou  
* @date 2016年7月10日 下午5:08:10 
* @version V1.0   
*/
package com.wanda.credit.ds.client.juxinli.bean.PBOCReport;

/**
 * @author xiaobin.hou
 *
 */
public class Summary {
	
	private String guarantee_number;// 为他人担保笔数
	private String no_settle_account_number;// 未结清/未销户账户数
	private String account_number;// 账户数
	private String type;// 信贷类型
	private String overdue_account_num;// 逾期的账户数
	private String overdue90_account_num;// 90天以上逾期的账户数
	
	
	public String getGuarantee_number() {
		return guarantee_number;
	}
	public void setGuarantee_number(String guarantee_number) {
		this.guarantee_number = guarantee_number;
	}
	public String getNo_settle_account_number() {
		return no_settle_account_number;
	}
	public void setNo_settle_account_number(String no_settle_account_number) {
		this.no_settle_account_number = no_settle_account_number;
	}
	public String getAccount_number() {
		return account_number;
	}
	public void setAccount_number(String account_number) {
		this.account_number = account_number;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getOverdue_account_num() {
		return overdue_account_num;
	}
	public void setOverdue_account_num(String overdue_account_num) {
		this.overdue_account_num = overdue_account_num;
	}
	public String getOverdue90_account_num() {
		return overdue90_account_num;
	}
	public void setOverdue90_account_num(String overdue90_account_num) {
		this.overdue90_account_num = overdue90_account_num;
	}
	@Override
	public String toString() {
		return "LoanSummary [guarantee_number=" + guarantee_number
				+ ", no_settle_account_number=" + no_settle_account_number
				+ ", account_number=" + account_number + ", type=" + type
				+ ", overdue_account_num=" + overdue_account_num
				+ ", overdue90_account_num=" + overdue90_account_num + "]";
	}
	
	

}
