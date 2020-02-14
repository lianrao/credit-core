/**   
* @Description: 聚信立-信用卡账单-账单分期
* @author xiaobin.hou  
* @date 2016年7月22日 下午2:59:06 
* @version V1.0   
*/
package com.wanda.credit.ds.client.juxinli.bean.creditCardBill;

/**
 * @author xiaobin.hou
 *
 */
public class CreditInstallment {
	
	private String installment_info;// 分期信息
	private String installment_amount;// 分期总金额
	private String installment_amount_unre;// 未入账分期金额
	private String installment_times;// 分期总期数
	private String installment_times_re;// 已入账期数
	private String installment_fee;// 分期总手续费
	private String installment_fee_unre;// 未入账手续费
	private String installment_currency;// 分期币种
	
	
	public String getInstallment_info() {
		return installment_info;
	}
	public void setInstallment_info(String installment_info) {
		this.installment_info = installment_info;
	}
	public String getInstallment_amount() {
		return installment_amount;
	}
	public void setInstallment_amount(String installment_amount) {
		this.installment_amount = installment_amount;
	}
	public String getInstallment_amount_unre() {
		return installment_amount_unre;
	}
	public void setInstallment_amount_unre(String installment_amount_unre) {
		this.installment_amount_unre = installment_amount_unre;
	}
	public String getInstallment_times() {
		return installment_times;
	}
	public void setInstallment_times(String installment_times) {
		this.installment_times = installment_times;
	}
	public String getInstallment_times_re() {
		return installment_times_re;
	}
	public void setInstallment_times_re(String installment_times_re) {
		this.installment_times_re = installment_times_re;
	}
	public String getInstallment_fee() {
		return installment_fee;
	}
	public void setInstallment_fee(String installment_fee) {
		this.installment_fee = installment_fee;
	}
	public String getInstallment_fee_unre() {
		return installment_fee_unre;
	}
	public void setInstallment_fee_unre(String installment_fee_unre) {
		this.installment_fee_unre = installment_fee_unre;
	}
	public String getInstallment_currency() {
		return installment_currency;
	}
	public void setInstallment_currency(String installment_currency) {
		this.installment_currency = installment_currency;
	}
	
	
	public String toString() {
		return "CreditInstallment [installment_info=" + installment_info
				+ ", installment_amount=" + installment_amount
				+ ", installment_amount_unre=" + installment_amount_unre
				+ ", installment_times=" + installment_times
				+ ", installment_times_re=" + installment_times_re
				+ ", installment_fee=" + installment_fee
				+ ", installment_fee_unre=" + installment_fee_unre
				+ ", installment_currency=" + installment_currency + "]";
	}
	
	
	
}
