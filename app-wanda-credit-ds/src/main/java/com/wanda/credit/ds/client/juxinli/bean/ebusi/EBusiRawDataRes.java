package com.wanda.credit.ds.client.juxinli.bean.ebusi;

import java.util.List;

import com.wanda.credit.ds.client.juxinli.bean.ebusi.origin.Address;
import com.wanda.credit.ds.client.juxinli.bean.ebusi.origin.Transactions;
import com.wanda.credit.ds.client.juxinli.bean.mobile.origin.Basic;

public class EBusiRawDataRes {
	
	
	private String datasource;	
	private List<Address> addresses;	
	private Basic basic;	
	private List<Transactions> trades;
	
	
	public String getDatasource() {
		return datasource;
	}
	public void setDatasource(String datasource) {
		this.datasource = datasource;
	}
	public List<Address> getAddresses() {
		return addresses;
	}
	public void setAddresses(List<Address> addresses) {
		this.addresses = addresses;
	}
	public Basic getBasic() {
		return basic;
	}
	public void setBasic(Basic basic) {
		this.basic = basic;
	}
	public List<Transactions> getTrades() {
		return trades;
	}
	public void setTrades(List<Transactions> trades) {
		this.trades = trades;
	} 
	
	

}
