/**   
* @Description: 国政通活体检测
* @author nan.liu
* @date 2018年09月1日 上午11:59:14 
* @version V1.0   
*/
package com.wanda.credit.ds.dao.domain.police;

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
@Table(name = "T_DS_POLICE_FACE_RESULT",schema="CPDB_DS")
@SequenceGenerator(name="SEQ_T_DS_POLICE_FACE_RESULT",sequenceName="SEQ_T_DS_POLICE_FACE_RESULT",allocationSize=1) 
@JsonIgnoreProperties(value = { "hibernateLazyInitializer"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Police_Face_Result extends BaseDomain{
	private static final long serialVersionUID = 1L;
	/**
	 * 主键
	 */
	private long id;
	private String name;
	private String cardNo;
	private String trade_id;
	private String photo_id;
	private String success;
	private String authResultRetainData;
	private String timeStamp;
	private String businessSerialNumber;
	private String errorDesc;
	private String appName;
	private String authResult;
	private String customNumber;
	private long total_cost;
	private long request_cost;
	private long auth_cost;
	
	/**
	 * 获取 主键
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_T_DS_POLICE_FACE_RESULT")
	@Column(name = "ID", unique = true, nullable = false)
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
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
	public String getPhoto_id() {
		return photo_id;
	}
	public void setPhoto_id(String photo_id) {
		this.photo_id = photo_id;
	}
	public String getSuccess() {
		return success;
	}
	public void setSuccess(String success) {
		this.success = success;
	}
	public String getAuthResultRetainData() {
		return authResultRetainData;
	}
	public void setAuthResultRetainData(String authResultRetainData) {
		this.authResultRetainData = authResultRetainData;
	}
	public String getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}
	public String getBusinessSerialNumber() {
		return businessSerialNumber;
	}
	public void setBusinessSerialNumber(String businessSerialNumber) {
		this.businessSerialNumber = businessSerialNumber;
	}
	public String getErrorDesc() {
		return errorDesc;
	}
	public void setErrorDesc(String errorDesc) {
		this.errorDesc = errorDesc;
	}
	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	public String getAuthResult() {
		return authResult;
	}
	public void setAuthResult(String authResult) {
		this.authResult = authResult;
	}
	public String getCustomNumber() {
		return customNumber;
	}
	public void setCustomNumber(String customNumber) {
		this.customNumber = customNumber;
	}
	public long getTotal_cost() {
		return total_cost;
	}
	public void setTotal_cost(long total_cost) {
		this.total_cost = total_cost;
	}
	public long getRequest_cost() {
		return request_cost;
	}
	public void setRequest_cost(long request_cost) {
		this.request_cost = request_cost;
	}
	public long getAuth_cost() {
		return auth_cost;
	}
	public void setAuth_cost(long auth_cost) {
		this.auth_cost = auth_cost;
	}
	
}
