package com.wanda.credit.ds.client.juxinli.bean.ebusi.origin;

import java.util.List;

/**
 * @author zhashiwen 电商数据内容
 */
public class Transaction {
	
	private String token;
	
	private String version;
	
	private String datasource;
	
	private List<Address> address;
	
	private Basic basic;
	
	private List<Transactions> transactions;
	
	public String getDatasource(){
	
		return datasource;
	}
	
	public void setDatasource(String datasource){
	
		this.datasource = datasource;
	}
	
	public List<Address> getAddress(){
	
		return address;
	}
	
	public void setAddress(List<Address> address){
	
		this.address = address;
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
	
	public String getToken(){
	
		return token;
	}
	
	public void setToken(String token){
	
		this.token = token;
	}
	
	@Override
	public String toString(){
	
		return "Transaction [token=" + token + ", datasource=" + datasource + ", address=" + address + ", basic="
		        + basic + ", transactions=" + transactions + "]";
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
	
}
