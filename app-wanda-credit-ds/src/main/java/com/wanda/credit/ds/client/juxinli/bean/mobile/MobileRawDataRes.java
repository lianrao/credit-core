package com.wanda.credit.ds.client.juxinli.bean.mobile;

import java.util.List;

import com.wanda.credit.ds.client.juxinli.bean.mobile.origin.Basic;
import com.wanda.credit.ds.client.juxinli.bean.mobile.origin.Calls;
import com.wanda.credit.ds.client.juxinli.bean.mobile.origin.Smses;
import com.wanda.credit.ds.client.juxinli.bean.mobile.origin.Transactions;

/**
 * 用于ds运营商原始数据的输出，方便与聚信立返回结果的解耦
 * @author xiaobin.hou
 *
 */
public class MobileRawDataRes {
	
	//通话记录
	private List<Calls> calls;
	//用户基本信息
	private Basic basic;
	//账单信息
	private List<Transactions> accounts;
	//短信信息
	private List<Smses> smses;	
	
	public List<Calls> getCalls() {
		return calls;
	}
	public void setCalls(List<Calls> calls) {
		this.calls = calls;
	}
	public Basic getBasic() {
		return basic;
	}
	public void setBasic(Basic basic) {
		this.basic = basic;
	}
	public List<Smses> getSmses() {
		return smses;
	}
	public void setSmses(List<Smses> smses) {
		this.smses = smses;
	}
	public List<Transactions> getAccounts() {
		return accounts;
	}
	public void setAccounts(List<Transactions> accounts) {
		this.accounts = accounts;
	}

	
}
