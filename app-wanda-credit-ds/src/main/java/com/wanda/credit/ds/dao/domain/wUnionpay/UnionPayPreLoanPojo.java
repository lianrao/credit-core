/**   
* @Description: W项目-贷前交易信息表
* @author xiaobin.hou  
* @date 2016年8月9日 下午3:09:22 
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
@Table(name = "CPDB_DS.T_DS_YL_PRELOAN_REQ")
@SequenceGenerator(name="SEQ_T_DS_YL_PRELOAN_REQ",sequenceName="CPDB_DS.SEQ_T_DS_YL_PRELOAN_REQ")  
@JsonIgnoreProperties(value = { "hibernateLazyInitializer"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class UnionPayPreLoanPojo extends BaseDomain {
	
	private static final long serialVersionUID = -3819879890530501278L;
	/**
	 * 获取 主键
	 */
	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="SEQ_T_DS_YL_PRELOAN_REQ")  
	@Column(name = "ID", unique = true, nullable = false)
	private long id;
	private String trade_id;
	private String orderId;
	private String reqSeq;
	private String respSeq;
	private String respCode;
	private String respMsg;
	private String idCard;
	private String name;
	private String card;
	private String mobile;
	private String last_month;
	private String txn_months;
	private String amt_12mons;
	private String cnt_12mons;
	private String cre_3mons_rat;
	private String deb_3mons_rat;
	private String city_3mons;
	private String prov_3mons;
	private Date reqtime;
	private Date resptime;
	private Date create_time;
	private Date update_time;
	
	
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
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getReqSeq() {
		return reqSeq;
	}
	public void setReqSeq(String reqSeq) {
		this.reqSeq = reqSeq;
	}
	public String getRespSeq() {
		return respSeq;
	}
	public void setRespSeq(String respSeq) {
		this.respSeq = respSeq;
	}
	public String getRespCode() {
		return respCode;
	}
	public void setRespCode(String respCode) {
		this.respCode = respCode;
	}
	public String getRespMsg() {
		return respMsg;
	}
	public void setRespMsg(String respMsg) {
		this.respMsg = respMsg;
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
	public String getLast_month() {
		return last_month;
	}
	public void setLast_month(String last_month) {
		this.last_month = last_month;
	}
	public String getTxn_months() {
		return txn_months;
	}
	public void setTxn_months(String txn_months) {
		this.txn_months = txn_months;
	}
	public String getAmt_12mons() {
		return amt_12mons;
	}
	public void setAmt_12mons(String amt_12mons) {
		this.amt_12mons = amt_12mons;
	}
	public String getCnt_12mons() {
		return cnt_12mons;
	}
	public void setCnt_12mons(String cnt_12mons) {
		this.cnt_12mons = cnt_12mons;
	}
	public String getCre_3mons_rat() {
		return cre_3mons_rat;
	}
	public void setCre_3mons_rat(String cre_3mons_rat) {
		this.cre_3mons_rat = cre_3mons_rat;
	}
	public String getDeb_3mons_rat() {
		return deb_3mons_rat;
	}
	public void setDeb_3mons_rat(String deb_3mons_rat) {
		this.deb_3mons_rat = deb_3mons_rat;
	}
	public String getCity_3mons() {
		return city_3mons;
	}
	public void setCity_3mons(String city_3mons) {
		this.city_3mons = city_3mons;
	}
	public String getProv_3mons() {
		return prov_3mons;
	}
	public void setProv_3mons(String prov_3mons) {
		this.prov_3mons = prov_3mons;
	}
	public Date getReqtime() {
		return reqtime;
	}
	public void setReqtime(Date reqtime) {
		this.reqtime = reqtime;
	}
	public Date getResptime() {
		return resptime;
	}
	public void setResptime(Date resptime) {
		this.resptime = resptime;
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
