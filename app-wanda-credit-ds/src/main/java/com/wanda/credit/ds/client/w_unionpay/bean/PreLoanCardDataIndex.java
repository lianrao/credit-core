/**   
* @Description: 贷前银联数据-银联返回指标
* @author xiaobin.hou  
* @date 2016年8月9日 下午4:44:56 
* @version V1.0   
*/
package com.wanda.credit.ds.client.w_unionpay.bean;

/**
 * @author xiaobin.hou
 *
 */
public class PreLoanCardDataIndex {
	
	private String c_no_setl_m_L12m;// 卡片最近12个月有交易的月份数 数值型,不保留小数
	private String c_setl_amt_L12m;// 卡片最近12个月交易金额 数值型,保留2位小数，单位元
	private String c_setl_unit_L12m;// 卡片最近12个月交易笔数 数值型,不保留小数
	private String c_cr_incr_amt_L3m_r;// 卡片最近3个月贷方交易金额增长率 数值型,保留4位小数
	private String c_dr_incr_amt_L3m_r;// 卡片最近3个月借方交易金额增长率 数值型,保留4位小数
	private String c_m_trx_city;// 卡片最近3个月主要交易城市 字符型
	private String c_m_trx_prov;// 卡片最近3个月主要交易省份 字符型
	private String settleMonth;//数据年月
	
	
	public String getC_no_setl_m_L12m() {
		return c_no_setl_m_L12m;
	}
	public void setC_no_setl_m_L12m(String c_no_setl_m_L12m) {
		this.c_no_setl_m_L12m = c_no_setl_m_L12m;
	}
	public String getC_setl_amt_L12m() {
		return c_setl_amt_L12m;
	}
	public void setC_setl_amt_L12m(String c_setl_amt_L12m) {
		this.c_setl_amt_L12m = c_setl_amt_L12m;
	}
	public String getC_setl_unit_L12m() {
		return c_setl_unit_L12m;
	}
	public void setC_setl_unit_L12m(String c_setl_unit_L12m) {
		this.c_setl_unit_L12m = c_setl_unit_L12m;
	}
	public String getC_cr_incr_amt_L3m_r() {
		return c_cr_incr_amt_L3m_r;
	}
	public void setC_cr_incr_amt_L3m_r(String c_cr_incr_amt_L3m_r) {
		this.c_cr_incr_amt_L3m_r = c_cr_incr_amt_L3m_r;
	}
	public String getC_dr_incr_amt_L3m_r() {
		return c_dr_incr_amt_L3m_r;
	}
	public void setC_dr_incr_amt_L3m_r(String c_dr_incr_amt_L3m_r) {
		this.c_dr_incr_amt_L3m_r = c_dr_incr_amt_L3m_r;
	}
	public String getC_m_trx_city() {
		return c_m_trx_city;
	}
	public void setC_m_trx_city(String c_m_trx_city) {
		this.c_m_trx_city = c_m_trx_city;
	}
	public String getC_m_trx_prov() {
		return c_m_trx_prov;
	}
	public void setC_m_trx_prov(String c_m_trx_prov) {
		this.c_m_trx_prov = c_m_trx_prov;
	}
	public String getSettleMonth() {
		return settleMonth;
	}
	public void setSettleMonth(String settleMonth) {
		this.settleMonth = settleMonth;
	}
	
	
	@Override
	public String toString() {
		return "PreLoanCardDataIndex [c_no_setl_m_L12m=" + c_no_setl_m_L12m
				+ ", c_setl_amt_L12m=" + c_setl_amt_L12m
				+ ", c_setl_unit_L12m=" + c_setl_unit_L12m
				+ ", c_cr_incr_amt_L3m_r=" + c_cr_incr_amt_L3m_r
				+ ", c_dr_incr_amt_L3m_r=" + c_dr_incr_amt_L3m_r
				+ ", c_m_trx_city=" + c_m_trx_city + ", c_m_trx_prov="
				+ c_m_trx_prov + ", settleMonth=" + settleMonth + "]";
	}

}
