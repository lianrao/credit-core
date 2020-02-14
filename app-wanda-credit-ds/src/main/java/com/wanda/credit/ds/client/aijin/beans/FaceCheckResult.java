/**   
* @Description: 国政通活体检测
* @author nan.liu
* @date 2018年09月1日 上午11:59:14 
* @version V1.0   
*/
package com.wanda.credit.ds.client.aijin.beans;

/**
 * @author nan.liu
 *
 */
public class FaceCheckResult {
	private String score;
	private String faceResult;
	private String faceResultText;
	private String citizenResult;
	private String citizenResultText;
	public String getScore() {
		return score;
	}
	public void setScore(String score) {
		this.score = score;
	}
	public String getFaceResult() {
		return faceResult;
	}
	public void setFaceResult(String faceResult) {
		this.faceResult = faceResult;
	}
	public String getFaceResultText() {
		return faceResultText;
	}
	public void setFaceResultText(String faceResultText) {
		this.faceResultText = faceResultText;
	}
	public String getCitizenResult() {
		return citizenResult;
	}
	public void setCitizenResult(String citizenResult) {
		this.citizenResult = citizenResult;
	}
	public String getCitizenResultText() {
		return citizenResultText;
	}
	public void setCitizenResultText(String citizenResultText) {
		this.citizenResultText = citizenResultText;
	}
}
