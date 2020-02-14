package com.wanda.credit.ds.dao.domain.juxinli.mobile;

import java.util.List;

import com.wanda.credit.ds.client.juxinli.bean.mobile.origin.Basic;

public class MobileTransactionPojo {
	
	private String token;
	
	private String version;
	
	private String datasource;
	
	private List<MobileRawDataCallPojo> calls;
	
	private Basic basic;
	
	private List<MobileRawDataAccountPojo> transactions;
	
	private List<MobileRawDataNetPojo> nets;
	
	private List<MobileRawDataSmsesPojo> smses;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getDatasource() {
		return datasource;
	}

	public void setDatasource(String datasource) {
		this.datasource = datasource;
	}

	public List<MobileRawDataCallPojo> getCalls() {
		return calls;
	}

	public void setCalls(List<MobileRawDataCallPojo> calls) {
		this.calls = calls;
	}

	public Basic getBasic() {
		return basic;
	}

	public void setBasic(Basic basic) {
		this.basic = basic;
	}

	public List<MobileRawDataAccountPojo> getTransactions() {
		return transactions;
	}

	public void setTransactions(List<MobileRawDataAccountPojo> transactions) {
		this.transactions = transactions;
	}

	public List<MobileRawDataNetPojo> getNets() {
		return nets;
	}

	public void setNets(List<MobileRawDataNetPojo> nets) {
		this.nets = nets;
	}

	public List<MobileRawDataSmsesPojo> getSmses() {
		return smses;
	}

	public void setSmses(List<MobileRawDataSmsesPojo> smses) {
		this.smses = smses;
	}

	

	
	
	

}
