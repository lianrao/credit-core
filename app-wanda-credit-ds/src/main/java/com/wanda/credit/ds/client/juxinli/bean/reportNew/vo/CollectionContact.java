package com.wanda.credit.ds.client.juxinli.bean.reportNew.vo;

import java.util.List;
/**
 * 常用联系人联系信息分析
 * @author xiaobin.hou
 *
 */
public class CollectionContact {

	/**
	 * 和联系人最早联系的时间
	 */
	private String begin_date;

	/**
	 * 电商送货总金额
	 */
	private String total_amount;

	/**
	 * 和联系人最晚联系的时间
	 */
	private String end_date;

	/**
	 * 电商送货总数
	 */
	private String total_count;

	/**
	 * 呼叫信息表
	 */
	private List<ContactDetails> contact_details;

	/**
	 * 联系人姓名
	 */
	private String contact_name;

	public String getBegin_date() {

		return begin_date;
	}

	public void setBegin_date(String begin_date) {

		this.begin_date = begin_date;
	}

	public List<ContactDetails> getContact_details() {

		return contact_details;
	}

	public void setContact_details(List<ContactDetails> contact_details) {

		this.contact_details = contact_details;
	}

	public String getContact_name() {

		return contact_name;
	}

	public void setContact_name(String contact_name) {

		this.contact_name = contact_name;
	}

	public String getEnd_date() {

		return end_date;
	}

	public void setEnd_date(String end_date) {

		this.end_date = end_date;
	}

	public String getTotal_amount() {
		return total_amount;
	}

	public void setTotal_amount(String total_amount) {
		this.total_amount = total_amount;
	}

	public String getTotal_count() {

		return total_count;
	}

	public void setTotal_count(String total_count) {

		this.total_count = total_count;
	}

	@Override
	public String toString() {

		return "CollectionContact [begin_date=" + begin_date + ", total_amount=" + total_amount + ", end_date="
				+ end_date + ", total_count=" + total_count + ", contact_details=" + contact_details + ", contact_name="
				+ contact_name + "]";
	}

}
