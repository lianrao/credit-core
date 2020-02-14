/**   
* @Description: 国政通活体检测
* @author nan.liu
* @date 2018年09月1日 上午11:59:14 
* @version V1.0   
*/
package com.wanda.credit.ds.client.guoztFace.bean;

/**
 * @author nan.liu
 *
 */
public class GuoZTFace {
	private String name;
	private String cardNo;
	private String trade_id;
	private String result;
	private String message;
	private String transaction_id;
	private String user_check_result;
	private String verify_result;
	private String verify_similarity;
	private String package_image;
	
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getTransaction_id() {
		return transaction_id;
	}
	public void setTransaction_id(String transaction_id) {
		this.transaction_id = transaction_id;
	}
	public String getUser_check_result() {
		return user_check_result;
	}
	public void setUser_check_result(String user_check_result) {
		this.user_check_result = user_check_result;
	}
	public String getVerify_result() {
		return verify_result;
	}
	public void setVerify_result(String verify_result) {
		this.verify_result = verify_result;
	}
	public String getVerify_similarity() {
		return verify_similarity;
	}
	public void setVerify_similarity(String verify_similarity) {
		this.verify_similarity = verify_similarity;
	}
	public String getPackage_image() {
		return package_image;
	}
	public void setPackage_image(String package_image) {
		this.package_image = package_image;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCardNo() {
		return cardNo;
	}
	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}
	public String getTrade_id() {
		return trade_id;
	}
	public void setTrade_id(String trade_id) {
		this.trade_id = trade_id;
	}
	@Override
	public String toString() {
		return "GuoZTFace [result=" + result + ", message=" + message
				+ ", transaction_id=" + transaction_id + ", user_check_result="
				+ user_check_result + ", verify_result=" + verify_result
				+ ", verify_similarity=" + verify_similarity
				+ ", package_image=" + package_image + "]";
	}
	
}
