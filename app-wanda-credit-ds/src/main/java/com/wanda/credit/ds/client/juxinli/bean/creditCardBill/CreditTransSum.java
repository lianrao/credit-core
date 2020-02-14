/**   
* @Description: 聚信立-信用卡账单-当月账户汇总
* @author xiaobin.hou  
* @date 2016年7月22日 下午2:59:06 
* @version V1.0   
*/
package com.wanda.credit.ds.client.juxinli.bean.creditCardBill;

/**
 * @author xiaobin.hou
 *
 */
public class CreditTransSum {
	
	private String pre_statement	;//	上期账单金额	
	private String pre_payment		;//	上期还款金额	
	private String cur_statement	;//	本期新增金额	
	private String cur_adjustment	;//	本期调整金额	
	private String cycle_interest	;//	循环利息		
	private String trans_currency	;//	交易币种
	
	
	public String getPre_statement() {
		return pre_statement;
	}
	public void setPre_statement(String pre_statement) {
		this.pre_statement = pre_statement;
	}
	public String getPre_payment() {
		return pre_payment;
	}
	public void setPre_payment(String pre_payment) {
		this.pre_payment = pre_payment;
	}
	public String getCur_statement() {
		return cur_statement;
	}
	public void setCur_statement(String cur_statement) {
		this.cur_statement = cur_statement;
	}
	public String getCur_adjustment() {
		return cur_adjustment;
	}
	public void setCur_adjustment(String cur_adjustment) {
		this.cur_adjustment = cur_adjustment;
	}
	public String getCycle_interest() {
		return cycle_interest;
	}
	public void setCycle_interest(String cycle_interest) {
		this.cycle_interest = cycle_interest;
	}
	public String getTrans_currency() {
		return trans_currency;
	}
	public void setTrans_currency(String trans_currency) {
		this.trans_currency = trans_currency;
	}
	
	
	public String toString() {
		return "CreditTransSum [pre_statement=" + pre_statement
				+ ", pre_payment=" + pre_payment + ", cur_statement="
				+ cur_statement + ", cur_adjustment=" + cur_adjustment
				+ ", cycle_interest=" + cycle_interest + ", trans_currency="
				+ trans_currency + "]";
	}

}
