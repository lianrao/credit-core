package com.wanda.credit.ds.client.juxinli.bean.reportNew.vo;
/**
 * 电商月消费信息汇总
 * @author xiaobin.hou
 *
 */
public class EbusinessExpense {

	/**
	 * 总购物金额
	 */
	private String all_amount;

	/**
	 * 总购物次数
	 */
	private String all_count;

	/**
	 * 汇总月份
	 */
	private String trans_mth;

	private String category;

	public String getAll_amount() {
		return all_amount;
	}

	public void setAll_amount(String all_amount) {
		this.all_amount = all_amount;
	}

	public String getAll_count() {

		return all_count;
	}

	public void setAll_count(String all_count) {

		this.all_count = all_count;
	}

	public String getTrans_mth() {

		return trans_mth;
	}

	public void setTrans_mth(String trans_mth) {

		this.trans_mth = trans_mth;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	@Override
	public String toString() {
		return "EbusinessExpense [all_amount=" + all_amount + ", all_count=" + all_count + ", trans_mth=" + trans_mth
				+ ", category=" + category + "]";
	}

}
