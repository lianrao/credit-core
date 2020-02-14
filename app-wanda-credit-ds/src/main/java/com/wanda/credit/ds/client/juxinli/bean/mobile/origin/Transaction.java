package com.wanda.credit.ds.client.juxinli.bean.mobile.origin;

import java.util.List;

/**
 *  数据内容
 */

public class Transaction {
	
	private String token;
	
	private String datasource;
	
	private List<Calls> calls;
	
	private Basic basic;
	
	private List<Transactions> transactions;
	
	private List<Smses> smses;
	
	private List<Nets> nets;
	
	public String getToken(){
	
		return token;
	}
	
	public void setToken(String token){
	
		this.token = token;
	}
	
	public String getDatasource(){
	
		return datasource;
	}
	
	public void setDatasource(String datasource){
	
		this.datasource = datasource;
	}
	
	public List<Calls> getCalls(){
	
		return calls;
	}
	
	public void setCalls(List<Calls> calls){
	
		this.calls = calls;
	}
	
	public Basic getBasic(){
	
		return basic;
	}
	
	public void setBasic(Basic basic){
	
		this.basic = basic;
	}
	
	public List<Transactions> getTransactions(){
	
		return transactions;
	}
	
	public void setTransactions(List<Transactions> transactions){
	
		this.transactions = transactions;
	}
	
	public List<Smses> getSmses(){
	
		return smses;
	}
	
	public void setSmses(List<Smses> smses){
	
		this.smses = smses;
	}
	
	@Override
	public String toString(){
	
		return "Transaction [token=" + token + ", datasource=" + datasource + ", calls=" + calls + ", basic=" + basic
		        + ", transactions=" + transactions + ", smses=" + smses + "]";
	}

	public List<Nets> getNets() {
		return nets;
	}

	public void setNets(List<Nets> nets) {
		this.nets = nets;
	}
	
}
