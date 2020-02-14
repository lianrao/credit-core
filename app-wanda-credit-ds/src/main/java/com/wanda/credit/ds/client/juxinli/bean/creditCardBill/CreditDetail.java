/**   
* @Description: 聚信立-引用卡账单-detail
* @author xiaobin.hou  
* @date 2016年7月25日 下午2:06:18 
* @version V1.0   
*/
package com.wanda.credit.ds.client.juxinli.bean.creditCardBill;

import java.util.List;

/**
 * @author xiaobin.hou
 *
 */
public class CreditDetail {
	
	private String datasource;
	private String bank_name;
	private String email;
	private List<CreditBillInfo> bill_info;


	public String getBank_name() {
		return bank_name;
	}
	public void setBank_name(String bank_name) {
		this.bank_name = bank_name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public List<CreditBillInfo> getBill_info() {
		return bill_info;
	}
	public void setBill_info(List<CreditBillInfo> bill_info) {
		this.bill_info = bill_info;
	}
	public String getDatasource() {
		return datasource;
	}
	public void setDatasource(String datasource) {
		this.datasource = datasource;
	}
	@Override
	public String toString() {
		return "CreditDetail [datasource=" + datasource + ", bank_name="
				+ bank_name + ", email=" + email + ", bill_info=" + bill_info
				+ "]";
	}
	
	
	

}
