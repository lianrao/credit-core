/**   
* @Description: TODO(用一句话描述该文件做什么) 
* @author xiaobin.hou  
* @date 2016年8月15日 下午4:28:24 
* @version V1.0   
*/
package com.wanda.credit.ds.client.pengyuan.bean;

/**
 * @author xiaobin.hou
 *
 */
public class Py_osta_Item {
	
	private String certificateID;
	private String occupation;
	private String level;
	private String banZhengRiQi;
	private String submitOrgName;
	private String cityName;
	private String avgSalary;
	private String avgNationalSalary;
	private String avgProvinceSalary;
	private String infoDate;
	
	
	/**
	 * 
	 */
	public Py_osta_Item() {
		super();
	}
	
	public String getCertificateID() {
		return certificateID;
	}
	public void setCertificateID(String certificateID) {
		this.certificateID = certificateID;
	}
	public String getOccupation() {
		return occupation;
	}
	public void setOccupation(String occupation) {
		this.occupation = occupation;
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	public String getBanZhengRiQi() {
		return banZhengRiQi;
	}
	public void setBanZhengRiQi(String banZhengRiQi) {
		this.banZhengRiQi = banZhengRiQi;
	}
	public String getSubmitOrgName() {
		return submitOrgName;
	}
	public void setSubmitOrgName(String submitOrgName) {
		this.submitOrgName = submitOrgName;
	}
	public String getCityName() {
		return cityName;
	}
	public void setCityName(String cityName) {
		this.cityName = cityName;
	}
	public String getAvgSalary() {
		return avgSalary;
	}
	public void setAvgSalary(String avgSalary) {
		this.avgSalary = avgSalary;
	}
	public String getAvgNationalSalary() {
		return avgNationalSalary;
	}
	public void setAvgNationalSalary(String avgNationalSalary) {
		this.avgNationalSalary = avgNationalSalary;
	}
	public String getAvgProvinceSalary() {
		return avgProvinceSalary;
	}
	public void setAvgProvinceSalary(String avgProvinceSalary) {
		this.avgProvinceSalary = avgProvinceSalary;
	}
	public String getInfoDate() {
		return infoDate;
	}
	public void setInfoDate(String infoDate) {
		this.infoDate = infoDate;
	}
	@Override
	public String toString() {
		return "Py_osta_Item [certificateID=" + certificateID + ", occupation="
				+ occupation + ", level=" + level + ", banZhengRiQi="
				+ banZhengRiQi + ", submitOrgName=" + submitOrgName
				+ ", cityName=" + cityName + ", avgSalary=" + avgSalary
				+ ", avgNationalSalary=" + avgNationalSalary
				+ ", avgProvinceSalary=" + avgProvinceSalary + ", infoDate="
				+ infoDate + "]";
	}
	
	

}
