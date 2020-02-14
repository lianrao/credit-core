package com.wanda.credit.ds.client.juxinli.bean.mobile;
/**
 * 外部参数Bean-用户其他信息
 * @author xiaobin.hou
 *
 */
public class ParamUserOtherInfo {
	
	private String homeAddr;
	private String workTel;
	private String workAddr;
	private String homeTel;
	private String mobileNo2;
	
	
	
	public String getHomeAddr() {
		return homeAddr;
	}
	public void setHomeAddr(String homeAddr) {
		this.homeAddr = homeAddr;
	}
	public String getWorkTel() {
		return workTel;
	}
	public void setWorkTel(String workTel) {
		this.workTel = workTel;
	}
	public String getWorkAddr() {
		return workAddr;
	}
	public void setWorkAddr(String workAddr) {
		this.workAddr = workAddr;
	}
	public String getHomeTel() {
		return homeTel;
	}
	public void setHomeTel(String homeTel) {
		this.homeTel = homeTel;
	}
	public String getMobileNo2() {
		return mobileNo2;
	}
	public void setMobileNo2(String mobileNo2) {
		this.mobileNo2 = mobileNo2;
	}
	
	
	public String toString() {
		return "ParamUserOtherInfo [homeAddr=" + homeAddr + ", workTel="
				+ workTel + ", workAddr=" + workAddr + ", homeTel=" + homeTel
				+ ", mobileNo2=" + mobileNo2 + "]";
	}
	
	

}
