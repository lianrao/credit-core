/**   
* @Description: 查询接口-业务参数-查询卡片数据
* @author xiaobin.hou  
* @date 2016年8月5日 上午11:27:07 
* @version V1.0   
*/
package com.wanda.credit.ds.client.w_unionpay.bean;

/**
 * @author xiaobin.hou
 *
 */
public class CustomerInfo {
	
	private String certifTp;// 证件类型
	private String certifId;// 证件号码
	private String cardNo;// 银行卡号
	private String phoneNo;// 发卡行预留手机号
	private String customerNm;// 姓名
	
	public String getCertifTp() {
		return certifTp;
	}
	public void setCertifTp(String certifTp) {
		this.certifTp = certifTp;
	}
	public String getCertifId() {
		return certifId;
	}
	public void setCertifId(String certifId) {
		this.certifId = certifId;
	}
	public String getCardNo() {
		return cardNo;
	}
	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}
	public String getPhoneNo() {
		return phoneNo;
	}
	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}
	public String getCustomerNm() {
		return customerNm;
	}
	public void setCustomerNm(String customerNm) {
		this.customerNm = customerNm;
	}
	
	@Override
	public String toString() {
		return "CustomerInfo [certifTp=" + certifTp + ", certifId=" + certifId
				+ ", cardNo=" + cardNo + ", phoneNo=" + phoneNo
				+ ", customerNm=" + customerNm + "]";
	}
	
	
	
	

}
