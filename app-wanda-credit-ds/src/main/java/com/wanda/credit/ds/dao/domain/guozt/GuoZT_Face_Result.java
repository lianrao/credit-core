/**   
* @Description: 国政通活体检测
* @author nan.liu
* @date 2018年09月1日 上午11:59:14 
* @version V1.0   
*/
package com.wanda.credit.ds.dao.domain.guozt;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.wanda.credit.base.domain.BaseDomain;

/**
 * @author nan.liu
 */
@Entity
@Table(name = "T_DS_GUOZT_FACE_RESULT",schema="CPDB_DS")
@SequenceGenerator(name="SEQ_T_DS_GUOZT_FACE_RESULT",sequenceName="SEQ_T_DS_GUOZT_FACE_RESULT",allocationSize=1) 
@JsonIgnoreProperties(value = { "hibernateLazyInitializer"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class GuoZT_Face_Result extends BaseDomain{
	private static final long serialVersionUID = 1L;
	/**
	 * 主键
	 */
	private long id;
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
	
	/**
	 * 获取 主键
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_T_DS_GUOZT_FACE_RESULT")
	@Column(name = "ID", unique = true, nullable = false)
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
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
