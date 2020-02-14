/**   
* @Description: 普惠金融 - 用户手机号是否注册
* @author xiaobin.hou  
* @date 2016年11月10日 上午11:26:23 
* @version V1.0   
*/
package com.wanda.credit.ds.dao.domain.phjr;

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
@Table(name = "CPDB_MK.T_ETL_MH_PHMOBILECHK")
@SequenceGenerator(name="SEQ_T_ETL_MH_PHMOBILECHK",sequenceName="CPDB_MK.SEQ_T_ETL_MH_PHMOBILECHK",schema="CPDB_MK")  
@JsonIgnoreProperties(value = { "hibernateLazyInitializer"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class PHMobileRegister extends BaseDomain {
	
	
	private static final long serialVersionUID = 1L;
	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="SEQ_T_ETL_MH_PHMOBILECHK")  
	@Column(name = "ID", unique = true, nullable = false)
	private long id;
	private String trade_id;
	private String mh_busno;
	private String mobile;
	private String device_id;
	private String is_success;
	private String registerFlag;
	private String registerFlagDesc;
	private String realnameFlag;
	private String realnameFlagDesc;
	private String marketingCues;
	private String marketingCuesDesc;
	private String hasLoginPassword;
	private String hasLoginPasswordDesc;
	private String ext_1;
	private String ext_2;
	private Date create_date;
	private Date update_date;
	
	
	public Date getCreate_date() {
		return create_date;
	}
	public void setCreate_date(Date create_date) {
		this.create_date = create_date;
	}
	public Date getUpdate_date() {
		return update_date;
	}
	public void setUpdate_date(Date update_date) {
		this.update_date = update_date;
	}
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
	public String getMh_busno() {
		return mh_busno;
	}
	public void setMh_busno(String mh_busno) {
		this.mh_busno = mh_busno;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getDevice_id() {
		return device_id;
	}
	public void setDevice_id(String device_id) {
		this.device_id = device_id;
	}
	public String getIs_success() {
		return is_success;
	}
	public void setIs_success(String is_success) {
		this.is_success = is_success;
	}
	public String getRegisterFlag() {
		return registerFlag;
	}
	public void setRegisterFlag(String registerFlag) {
		this.registerFlag = registerFlag;
	}
	public String getRegisterFlagDesc() {
		return registerFlagDesc;
	}
	public void setRegisterFlagDesc(String registerFlagDesc) {
		this.registerFlagDesc = registerFlagDesc;
	}
	public String getRealnameFlag() {
		return realnameFlag;
	}
	public void setRealnameFlag(String realnameFlag) {
		this.realnameFlag = realnameFlag;
	}
	public String getRealnameFlagDesc() {
		return realnameFlagDesc;
	}
	public void setRealnameFlagDesc(String realnameFlagDesc) {
		this.realnameFlagDesc = realnameFlagDesc;
	}
	public String getMarketingCues() {
		return marketingCues;
	}
	public void setMarketingCues(String marketingCues) {
		this.marketingCues = marketingCues;
	}
	public String getMarketingCuesDesc() {
		return marketingCuesDesc;
	}
	public void setMarketingCuesDesc(String marketingCuesDesc) {
		this.marketingCuesDesc = marketingCuesDesc;
	}
	public String getHasLoginPassword() {
		return hasLoginPassword;
	}
	public void setHasLoginPassword(String hasLoginPassword) {
		this.hasLoginPassword = hasLoginPassword;
	}
	public String getHasLoginPasswordDesc() {
		return hasLoginPasswordDesc;
	}
	public void setHasLoginPasswordDesc(String hasLoginPasswordDesc) {
		this.hasLoginPasswordDesc = hasLoginPasswordDesc;
	}
	public String getExt_1() {
		return ext_1;
	}
	public void setExt_1(String ext_1) {
		this.ext_1 = ext_1;
	}
	public String getExt_2() {
		return ext_2;
	}
	public void setExt_2(String ext_2) {
		this.ext_2 = ext_2;
	}

	
	
	
	
	

}
