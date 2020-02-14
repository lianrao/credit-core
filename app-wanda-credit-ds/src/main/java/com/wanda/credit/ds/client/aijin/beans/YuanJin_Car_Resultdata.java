/**   
* @Description: 爰金车辆核查
* @author nan.liu
* @date 2019年03月28日 上午11:59:14 
* @version V1.0   
*/
package com.wanda.credit.ds.client.aijin.beans;

public class YuanJin_Car_Resultdata {
	private String validFrom;
	private String validTo;
	private String firstIssueDate;
	private String licenseStatus;
	private String licenseClass;
	private String licenseType;
	private String gender;
	public String getValidFrom() {
		return validFrom;
	}
	public void setValidFrom(String validFrom) {
		this.validFrom = validFrom;
	}
	public String getValidTo() {
		return validTo;
	}
	public void setValidTo(String validTo) {
		this.validTo = validTo;
	}
	public String getFirstIssueDate() {
		return firstIssueDate;
	}
	public void setFirstIssueDate(String firstIssueDate) {
		this.firstIssueDate = firstIssueDate;
	}
	public String getLicenseStatus() {
		return licenseStatus;
	}
	public void setLicenseStatus(String licenseStatus) {
		this.licenseStatus = licenseStatus;
	}
	public String getLicenseClass() {
		return licenseClass;
	}
	public void setLicenseClass(String licenseClass) {
		this.licenseClass = licenseClass;
	}
	public String getLicenseType() {
		return licenseType;
	}
	public void setLicenseType(String licenseType) {
		this.licenseType = licenseType;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
}
