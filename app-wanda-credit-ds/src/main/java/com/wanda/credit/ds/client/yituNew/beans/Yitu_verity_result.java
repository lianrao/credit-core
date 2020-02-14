/**   
* @Description: 依图活体检测
* @author nan.liu
* @date 2018年09月1日 上午11:59:14 
* @version V1.0   
*/
package com.wanda.credit.ds.client.yituNew.beans;

import com.wanda.credit.base.domain.BaseDomain;

/**
 * @author nan.liu
 *
 */
public class Yitu_verity_result extends BaseDomain{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String rtn;
	private String final_verify_score;
	private String is_pass;
	private String message;
	private String similarity_query_idcard;
	private String yitu_check_score_1;
	private String yitu_check_score_2;
	public String getRtn() {
		return rtn;
	}
	public void setRtn(String rtn) {
		this.rtn = rtn;
	}
	public String getFinal_verify_score() {
		return final_verify_score;
	}
	public void setFinal_verify_score(String final_verify_score) {
		this.final_verify_score = final_verify_score;
	}
	public String getIs_pass() {
		return is_pass;
	}
	public void setIs_pass(String is_pass) {
		this.is_pass = is_pass;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getSimilarity_query_idcard() {
		return similarity_query_idcard;
	}
	public void setSimilarity_query_idcard(String similarity_query_idcard) {
		this.similarity_query_idcard = similarity_query_idcard;
	}
	public String getYitu_check_score_1() {
		return yitu_check_score_1;
	}
	public void setYitu_check_score_1(String yitu_check_score_1) {
		this.yitu_check_score_1 = yitu_check_score_1;
	}
	public String getYitu_check_score_2() {
		return yitu_check_score_2;
	}
	public void setYitu_check_score_2(String yitu_check_score_2) {
		this.yitu_check_score_2 = yitu_check_score_2;
	}
	
}
