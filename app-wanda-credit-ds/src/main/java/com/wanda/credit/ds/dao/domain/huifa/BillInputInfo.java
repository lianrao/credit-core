package com.wanda.credit.ds.dao.domain.huifa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.annotations.GenericGenerator;

import com.wanda.credit.base.domain.BaseDomain;

@Entity
@Table(name = "T_DS_HF_BILLINPUTINFO")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer" })
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class BillInputInfo extends BaseDomain {
	private static final long serialVersionUID = 1L;
	private String id;
	private String trade_id;
	private String inputPg;
	private String inputPz;
	private String totalnumber; //结果总数
	private String totalmoney;  //账户总充值金额
	private String moneynow;    //帐号当前余额
	private String success;
	private String messsage;
	private String content;
	
	public BillInputInfo() {
		super();
	}

	public BillInputInfo(String trade_id, String inputPg, String inputPz,
			String totalnumber, String totalmoney, String moneynow,
			String success, String messsage, String content) {
		super();
		this.trade_id = trade_id;
		this.inputPg = inputPg;
		this.inputPz = inputPz;
		this.totalnumber = totalnumber;
		this.totalmoney = totalmoney;
		this.moneynow = moneynow;
		this.success = success;
		this.messsage = messsage;
		this.content = content;
	}
	/**
	 * 获取 主键
	 */
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	@Column(name = "ID", unique = true, nullable = false, length = 32)
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTrade_id() {
		return trade_id;
	}

	public void setTrade_id(String trade_id) {
		this.trade_id = trade_id;
	}

	public String getInputPg() {
		return inputPg;
	}

	public void setInputPg(String inputPg) {
		this.inputPg = inputPg;
	}

	public String getInputPz() {
		return inputPz;
	}

	public void setInputPz(String inputPz) {
		this.inputPz = inputPz;
	}

	public String getTotalnumber() {
		return totalnumber;
	}

	public void setTotalnumber(String totalnumber) {
		this.totalnumber = totalnumber;
	}

	public String getTotalmoney() {
		return totalmoney;
	}

	public void setTotalmoney(String totalmoney) {
		this.totalmoney = totalmoney;
	}

	public String getMoneynow() {
		return moneynow;
	}

	public void setMoneynow(String moneynow) {
		this.moneynow = moneynow;
	}

	public String getSuccess() {
		return success;
	}

	public void setSuccess(String success) {
		this.success = success;
	}

	public String getMesssage() {
		return messsage;
	}

	public void setMesssage(String messsage) {
		this.messsage = messsage;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public String toString() {
		return "BillInputInfo [inputPg=" + inputPg + ", inputPz=" + inputPz
				+ ", totalnumber=" + totalnumber + ", totalmoney=" + totalmoney
				+ ", moneynow=" + moneynow + ", success=" + success
				+ ", messsage=" + messsage + ", content=" + content + "]";
	}
}
