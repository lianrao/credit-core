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
@Table(name = "T_DS_HF_BILLDETAILS")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer" })
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class BillDetails extends BaseDomain {
	private static final long serialVersionUID = 1L;
	private String id;
	private String trade_id;
	private String refid;
	private Integer logid; // 记录主键Id
	private String keyword; // 对应查询关键字
	private String paymoney; // 此次扣费金额
	private String remark; // 描述
	private String posttime; // 扣费时间
	
	public BillDetails() {
		super();
	}

	public BillDetails(String trade_id, String refid, Integer logid,
			String keyword, String paymoney, String remark, String posttime) {
		super();
		this.trade_id = trade_id;
		this.refid = refid;
		this.logid = logid;
		this.keyword = keyword;
		this.paymoney = paymoney;
		this.remark = remark;
		this.posttime = posttime;
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

	public String getRefid() {
		return refid;
	}

	public void setRefid(String refid) {
		this.refid = refid;
	}

	public Integer getLogid() {
		return logid;
	}

	public void setLogid(Integer logid) {
		this.logid = logid;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getPaymoney() {
		return paymoney;
	}

	public void setPaymoney(String paymoney) {
		this.paymoney = paymoney;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	public String getPosttime() {
		return posttime;
	}

	public void setPosttime(String posttime) {
		this.posttime = posttime;
	}

	@Override
	public String toString() {
		return "BillDetails [refid=" + refid + ", logid=" + logid
				+ ", keyword=" + keyword + ", paymoney=" + paymoney
				+ ", remark=" + remark + ", posttime=" + posttime + "]";
	}
}
