package com.wanda.credit.ds.client.juxinli.bean.reportNew.vo;

import java.util.List;

/**
 * 电商送货信息分析 之 收货人列表
 * @author xiaobin.hou
 *
 */
public class Receiver {

	/**
	 * 送货次数
	 */
	private String count;

	/**
	 * 送货金额
	 */
	private String amount;

	/**
	 * 收货人姓名
	 */
	private String name;

	/**
	 * 收货人电话号码
	 */
	private List<String> phone_num_list;

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getCount() {

		return count;
	}

	public void setCount(String count) {

		this.count = count;
	}

	public String getName() {

		return name;
	}

	public void setName(String name) {

		this.name = name;
	}

	public List<String> getPhone_num_list() {

		return phone_num_list;
	}

	public void setPhone_num_list(List<String> phone_num_list) {

		this.phone_num_list = phone_num_list;
	}

	@Override
	public String toString() {

		return "Receiver [count=" + count + ", amount=" + amount + ", name=" + name + ", phone_num_list="
				+ phone_num_list + "]";
	}

}
