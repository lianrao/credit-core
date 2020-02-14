/**   
* @Description: 远鉴活体检测
* @author nan.liu
* @date 2018年09月1日 上午11:59:14 
* @version V1.0   
*/
package com.wanda.credit.ds.client.yuanjian.beans;

/**
 * @author nan.liu
 *
 */
public class YuanJian_Face {
	private String errorCode;
	private String errorMsg;
	private String success;
	private YuanJian_Data data;
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	public String getSuccess() {
		return success;
	}
	public void setSuccess(String success) {
		this.success = success;
	}
	public YuanJian_Data getData() {
		return data;
	}
	public void setData(YuanJian_Data data) {
		this.data = data;
	}
	
}
