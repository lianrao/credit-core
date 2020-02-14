package com.wanda.credit.ds.dao.domain.qxb;

import javax.persistence.MappedSuperclass;

import com.wanda.credit.base.domain.BaseDomain;

/**
 * 前海 黑名单 信息
 **/
@MappedSuperclass
public class QxbBaseDomain extends BaseDomain {
	private static final long serialVersionUID = 1L;	
	private String trade_id;
	public String getTrade_id() {
		return trade_id;
	}
	public void setTrade_id(String trade_id) {
		this.trade_id = trade_id;
	}

}
