/**   
* @Description: 爰金不良信息
* @author nan.liu
* @date 2018年09月1日 上午11:59:14 
* @version V1.0   
*/
package com.wanda.credit.ds.client.aijin.beans;

/**
 * @author nan.liu
 */
public class YuanJin_bad_Msg {
	private YuanJin_bad_Resultdata ResultData;
	private String ClientNo;
	private String SerialNo;
	private String ResponseCode;
	private String ResponseText;
	
	private String Result;
	private String ResultText;
	public YuanJin_bad_Resultdata getResultData() {
		return ResultData;
	}
	public void setResultData(YuanJin_bad_Resultdata resultData) {
		ResultData = resultData;
	}
	public String getClientNo() {
		return ClientNo;
	}
	public void setClientNo(String clientNo) {
		ClientNo = clientNo;
	}
	public String getSerialNo() {
		return SerialNo;
	}
	public void setSerialNo(String serialNo) {
		SerialNo = serialNo;
	}
	public String getResponseCode() {
		return ResponseCode;
	}
	public void setResponseCode(String responseCode) {
		ResponseCode = responseCode;
	}
	public String getResponseText() {
		return ResponseText;
	}
	public void setResponseText(String responseText) {
		ResponseText = responseText;
	}
	public String getResult() {
		return Result;
	}
	public void setResult(String result) {
		Result = result;
	}
	public String getResultText() {
		return ResultText;
	}
	public void setResultText(String resultText) {
		ResultText = resultText;
	}
	
}
