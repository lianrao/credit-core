/**   
* @Description: 信用卡账单-金额和币种 
* @author xiaobin.hou  
* @date 2016年7月22日 下午2:43:40 
* @version V1.0   
*/
package com.wanda.credit.ds.client.juxinli.bean.creditCardBill;

/**
 * @author xiaobin.hou
 *
 */
public class CreditBase {

	private String amount;
	private String currency;
	
	
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	
	
	public String toString() {
		return "CreditBase [amount=" + amount + ", currency=" + currency + "]";
	}
	
	
	
	

}
