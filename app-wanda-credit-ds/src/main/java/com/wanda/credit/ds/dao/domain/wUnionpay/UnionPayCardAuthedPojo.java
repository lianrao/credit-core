/**   
* @Description: W项目-银行卡鉴权一致信息表
* @author xiaobin.hou  
* @date 2016年8月9日 下午2:58:48 
* @version V1.0   
*/
package com.wanda.credit.ds.dao.domain.wUnionpay;

import java.util.Date;

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
 * @author xiaobin.hou
 *
 */
@Entity
@Table(name = "CPDB_DS.T_DS_YL_AUTHED")
@SequenceGenerator(name="SEQ_T_DS_YL_AUTHED",sequenceName="CPDB_DS.SEQ_T_DS_YL_AUTHED")  
@JsonIgnoreProperties(value = { "hibernateLazyInitializer"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class UnionPayCardAuthedPojo extends BaseDomain {
	
	private static final long serialVersionUID = -1729369652666398020L;
	
	/**
	 * 获取 主键
	 */
	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="SEQ_T_DS_YL_AUTHED")  
	@Column(name = "ID", unique = true, nullable = false)
	private long id;
	private String idCard;//身份证号
	private String name;
	private String card;//银行卡卡号
	private String mobile;
	private String card_pan;//缩略卡号
	private String mobile_attrib;//手机号归属地
	private String idcard_attrib;//身份证号码归属地
	private String birthday;//出生日期
	private String sex;//性别
	private Date create_time;
	private Date update_time;
	
	
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getIdCard() {
		return idCard;
	}
	public void setIdCard(String idCard) {
		this.idCard = idCard;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCard() {
		return card;
	}
	public void setCard(String card) {
		this.card = card;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getCard_pan() {
		return card_pan;
	}
	public void setCard_pan(String card_pan) {
		this.card_pan = card_pan;
	}
	public String getMobile_attrib() {
		return mobile_attrib;
	}
	public void setMobile_attrib(String mobile_attrib) {
		this.mobile_attrib = mobile_attrib;
	}
	public String getIdcard_attrib() {
		return idcard_attrib;
	}
	public void setIdcard_attrib(String idcard_attrib) {
		this.idcard_attrib = idcard_attrib;
	}
	public String getBirthday() {
		return birthday;
	}
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public Date getCreate_time() {
		return create_time;
	}
	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}
	public Date getUpdate_time() {
		return update_time;
	}
	public void setUpdate_time(Date update_time) {
		this.update_time = update_time;
	}

	
	
}
