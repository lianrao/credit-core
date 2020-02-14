/**   
* @Description: 爰金不良信息
* @author nan.liu
* @date 2018年09月1日 上午11:59:14 
* @version V1.0   
*/
package com.wanda.credit.ds.client.aijin.beans;

/**
 * @author nan.liu
 *
 */
public class YuanJin_bad_Resultdata {
	private String BadDetail;
	private String BadCnt;
	private String NewestDate;
	public String getBadDetail() {
		return BadDetail;
	}
	public void setBadDetail(String badDetail) {
		BadDetail = badDetail;
	}
	public String getBadCnt() {
		return BadCnt;
	}
	public void setBadCnt(String badCnt) {
		BadCnt = badCnt;
	}
	public String getNewestDate() {
		return NewestDate;
	}
	public void setNewestDate(String newestDate) {
		NewestDate = newestDate;
	}
	
}
