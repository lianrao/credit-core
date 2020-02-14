/**   
* @Description: 爰金活体检测
* @author nan.liu
* @date 2018年09月1日 上午11:59:14 
* @version V1.0   
*/
package com.wanda.credit.ds.client.aijin.beans;

/**
 * @author nan.liu
 *
 */
public class YuanJin_Face {
	private String responseCode;
	private String responseText;
	private String result;
	private String resultText;
	private FaceCheckResult faceCheckResult;
	
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
	public FaceCheckResult getFaceCheckResult() {
		return faceCheckResult;
	}
	public void setFaceCheckResult(FaceCheckResult faceCheckResult) {
		this.faceCheckResult = faceCheckResult;
	}
}
