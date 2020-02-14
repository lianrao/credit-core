/**   
* @Description: 点微不良信息
* @author nan.liu
* @date 2019年04月1日 上午11:59:14 
* @version V1.0   
*/
package com.wanda.credit.ds.client.dianwei.beans;

import java.util.List;

/**
 * @author nan.liu
 */
public class DianWei_bad_detail {
	private String name;
	private String cid;
	private List<DianWei_bad_badDetail> badDetail;
	private String newestDate;
	private String provinceNo;
	private String badCnt;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCid() {
		return cid;
	}
	public void setCid(String cid) {
		this.cid = cid;
	}
	public List<DianWei_bad_badDetail> getBadDetail() {
		return badDetail;
	}
	public void setBadDetail(List<DianWei_bad_badDetail> badDetail) {
		this.badDetail = badDetail;
	}
	public String getNewestDate() {
		return newestDate;
	}
	public void setNewestDate(String newestDate) {
		this.newestDate = newestDate;
	}
	public String getProvinceNo() {
		return provinceNo;
	}
	public void setProvinceNo(String provinceNo) {
		this.provinceNo = provinceNo;
	}
	public String getBadCnt() {
		return badCnt;
	}
	public void setBadCnt(String badCnt) {
		this.badCnt = badCnt;
	}
}
