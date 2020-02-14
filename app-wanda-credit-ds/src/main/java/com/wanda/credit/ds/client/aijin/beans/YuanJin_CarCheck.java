/**   
* @Description: 爰金车辆核查
* @author nan.liu
* @date 2019年03月28日 上午11:59:14 
* @version V1.0   
*/
package com.wanda.credit.ds.client.aijin.beans;


public class YuanJin_CarCheck {
	private String responseCode;
	private String responseText;
	private String result;
	private String resultText;
	private String serialNo;
	private String clientOrderNumber;
	private YuanJin_Car_Resultdata resultData;
	public String getResponseCode() {
		return responseCode;
	}
	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}
	public String getResponseText() {
		return responseText;
	}
	public void setResponseText(String responseText) {
		this.responseText = responseText;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getResultText() {
		return resultText;
	}
	public void setResultText(String resultText) {
		this.resultText = resultText;
	}
	public String getSerialNo() {
		return serialNo;
	}
	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
	}
	public String getClientOrderNumber() {
		return clientOrderNumber;
	}
	public void setClientOrderNumber(String clientOrderNumber) {
		this.clientOrderNumber = clientOrderNumber;
	}
	public YuanJin_Car_Resultdata getResultData() {
		return resultData;
	}
	public void setResultData(YuanJin_Car_Resultdata resultData) {
		this.resultData = resultData;
	}
	
}
