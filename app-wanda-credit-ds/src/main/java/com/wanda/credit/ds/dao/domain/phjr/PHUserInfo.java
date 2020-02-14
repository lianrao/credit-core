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
@Table(name = "CPDB_MK.T_ETL_MH_PHUSERINFO")
@SequenceGenerator(name = "SEQ_T_ETL_MH_PHUSERINFO", sequenceName = "CPDB_MK.SEQ_T_ETL_MH_PHUSERINFO", schema = "CPDB_MK")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer" })
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class PHUserInfo extends BaseDomain {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_T_ETL_MH_PHUSERINFO")
	@Column(name = "ID", unique = true, nullable = false)
	private long id;
	private String trade_id;
	private String mh_busno;
	private String mobile;
	private String device_id;
	private String is_success;
	private String login_token;
	private String pass_id;
	private String user_id;
	private String name;
	private String cardtype;
	private String cardno;
	private String idcard_address;
	private String valid_start;
	private String valid_end;
	private String auth_result;
	private String auth_timecost;
	private String opertype;
	private String photo1_fid;
	private String photo2_fid;
	private String photo3_fid;
	private String photo4_fid;
	private String ext_1;
	private String ext_2;
	private Date create_date;
	private Date update_date;
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
	public String getLogin_token() {
		return login_token;
	}
	public void setLogin_token(String login_token) {
		this.login_token = login_token;
	}
	public String getPass_id() {
		return pass_id;
	}
	public void setPass_id(String pass_id) {
		this.pass_id = pass_id;
	}
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCardtype() {
		return cardtype;
	}
	public void setCardtype(String cardtype) {
		this.cardtype = cardtype;
	}
	public String getCardno() {
		return cardno;
	}
	public void setCardno(String cardno) {
		this.cardno = cardno;
	}
	public String getIdcard_address() {
		return idcard_address;
	}
	public void setIdcard_address(String idcard_address) {
		this.idcard_address = idcard_address;
	}
	public String getValid_start() {
		return valid_start;
	}
	public void setValid_start(String valid_start) {
		this.valid_start = valid_start;
	}
	public String getValid_end() {
		return valid_end;
	}
	public void setValid_end(String valid_end) {
		this.valid_end = valid_end;
	}
	public String getAuth_result() {
		return auth_result;
	}
	public void setAuth_result(String auth_result) {
		this.auth_result = auth_result;
	}
	public String getAuth_timecost() {
		return auth_timecost;
	}
	public void setAuth_timecost(String auth_timecost) {
		this.auth_timecost = auth_timecost;
	}
	public String getOpertype() {
		return opertype;
	}
	public void setOpertype(String opertype) {
		this.opertype = opertype;
	}
	public String getPhoto1_fid() {
		return photo1_fid;
	}
	public void setPhoto1_fid(String photo1_fid) {
		this.photo1_fid = photo1_fid;
	}
	public String getPhoto2_fid() {
		return photo2_fid;
	}
	public void setPhoto2_fid(String photo2_fid) {
		this.photo2_fid = photo2_fid;
	}
	public String getPhoto3_fid() {
		return photo3_fid;
	}
	public void setPhoto3_fid(String photo3_fid) {
		this.photo3_fid = photo3_fid;
	}
	public String getPhoto4_fid() {
		return photo4_fid;
	}
	public void setPhoto4_fid(String photo4_fid) {
		this.photo4_fid = photo4_fid;
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
	public String getIs_success() {
		return is_success;
	}
	public void setIs_success(String is_success) {
		this.is_success = is_success;
	}

	

}
