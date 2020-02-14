package com.wanda.credit.ds.client.juxinli.bean.reportNew.vo;

/**
 * 通话行为
 * @author xiaobin.hou
 *
 */
public class Behavior {

	/**
	 * 短信条数
	 */
	private String sms_cnt;

	/**
	 * 手机号码
	 */
	private String cell_phone_num;

	/**
	 * 月流量 (MB)
	 */
	private String net_flow;

	/**
	 * 话费消费 (元)
	 */
	private String total_amount;

	/**
	 * 月主叫通话时间 (分钟)
	 */
	private String call_out_time;

	/**
	 * 月份
	 */
	private String cell_mth;

	/**
	 * 手机归属地
	 */
	private String cell_loc;

	/**
	 * 月通话次数
	 */
	private String call_cnt;

	/**
	 * 手机运营商中文名称
	 */
	private String cell_operator_zh;

	/**
	 * 主叫通话次数
	 */
	private String call_out_cnt;

	/**
	 * 手机运营商英文名称
	 */
	private String cell_operator;

	/**
	 * 月被叫通话时间 (分钟)
	 */
	private String call_in_time;

	/**
	 * 月被叫通话次数
	 */
	private String call_in_cnt;

	public String getCall_in_time() {

		return call_in_time;
	}

	public void setCall_in_time(String call_in_time) {

		this.call_in_time = call_in_time;
	}

	public String getNet_flow() {
		return net_flow;
	}

	public void setNet_flow(String net_flow) {
		this.net_flow = net_flow;
	}

	public String getTotal_amount() {
		return total_amount;
	}

	public void setTotal_amount(String total_amount) {
		this.total_amount = total_amount;
	}

	public String getCall_out_time() {
		return call_out_time;
	}

	public void setCall_out_time(String call_out_time) {
		this.call_out_time = call_out_time;
	}

	public String getCell_loc() {

		return cell_loc;
	}

	public void setCell_loc(String cell_loc) {

		this.cell_loc = cell_loc;
	}

	public String getCell_mth() {

		return cell_mth;
	}

	public void setCell_mth(String cell_mth) {

		this.cell_mth = cell_mth;
	}

	public String getCell_operator() {

		return cell_operator;
	}

	public void setCell_operator(String cell_operator) {

		this.cell_operator = cell_operator;
	}

	public String getCell_phone_num() {

		return cell_phone_num;
	}

	public void setCell_phone_num(String cell_phone_num) {

		this.cell_phone_num = cell_phone_num;
	}

	public String getSms_cnt() {

		return sms_cnt;
	}

	public void setSms_cnt(String sms_cnt) {

		this.sms_cnt = sms_cnt;
	}

	public String getCall_cnt() {

		return call_cnt;
	}

	public void setCall_cnt(String call_cnt) {

		this.call_cnt = call_cnt;
	}

	public String getCell_operator_zh() {

		return cell_operator_zh;
	}

	public void setCell_operator_zh(String cell_operator_zh) {

		this.cell_operator_zh = cell_operator_zh;
	}

	public String getCall_out_cnt() {

		return call_out_cnt;
	}

	public void setCall_out_cnt(String call_out_cnt) {

		this.call_out_cnt = call_out_cnt;
	}

	public String getCall_in_cnt() {

		return call_in_cnt;
	}

	public void setCall_in_cnt(String call_in_cnt) {

		this.call_in_cnt = call_in_cnt;
	}

	@Override
	public String toString() {

		return "Behavior [sms_cnt=" + sms_cnt + ", cell_phone_num=" + cell_phone_num + ", net_flow=" + net_flow
				+ ", total_amount=" + total_amount + ", call_out_time=" + call_out_time + ", cell_mth=" + cell_mth
				+ ", cell_loc=" + cell_loc + ", call_cnt=" + call_cnt + ", cell_operator_zh=" + cell_operator_zh
				+ ", call_out_cnt=" + call_out_cnt + ", cell_operator=" + cell_operator + ", call_in_time="
				+ call_in_time + ", call_in_cnt=" + call_in_cnt + "]";
	}

}
