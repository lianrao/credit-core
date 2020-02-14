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
@Table(name = "T_DS_POLICE_SIGN",schema="CPDB_DS")
@SequenceGenerator(name="SEQ_T_DS_POLICE_SIGN",sequenceName="SEQ_T_DS_POLICE_SIGN",allocationSize=1) 
@JsonIgnoreProperties(value = { "hibernateLazyInitializer"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Police_Sign_Result extends BaseDomain{
	private static final long serialVersionUID = 1L;
	/**
	 * 主键
	 */
	private long id;
	private String trade_id;
	private String sign_date;
	private String sign_data;
	
	/**
	 * 获取 主键
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_T_DS_POLICE_SIGN")
	@Column(name = "ID", unique = true, nullable = false)
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getTrade_id() {
		return trade_id;
	}
	public void setTrade_id(String trade_id) {
		this.trade_id = trade_id;
	}
	public String getSign_date() {
		return sign_date;
	}
	public void setSign_date(String sign_date) {
		this.sign_date = sign_date;
	}
	public String getSign_data() {
		return sign_data;
	}
	public void setSign_data(String sign_data) {
		this.sign_data = sign_data;
	}
}
