package com.wanda.credit.ds.dao.domain.yixin;

import javax.persistence.MappedSuperclass;

import com.wanda.credit.base.domain.BaseDomain;

/**
 * @description  
 * @author wuchsh 
 * @version 1.0
 * @createdate 2016年6月12日 下午4:42:02 
 *  
 */
@MappedSuperclass
public class YXCommonDomain extends BaseDomain{
	private String trade_id;
	private String name;
	private String idType;
	private String idNo;
	private String queryReason;
	public String getTrade_id() {
		return trade_id;
	}
	public void setTrade_id(String trade_id) {
		this.trade_id = trade_id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getIdType() {
		return idType;
	}
	public void setIdType(String idType) {
		this.idType = idType;
	}
	public String getIdNo() {
		return idNo;
	}
	public void setIdNo(String idNo) {
		this.idNo = idNo;
	}
	public String getQueryReason() {
		return queryReason;
	}
	public void setQueryReason(String queryReason) {
		this.queryReason = queryReason;
	}

}
