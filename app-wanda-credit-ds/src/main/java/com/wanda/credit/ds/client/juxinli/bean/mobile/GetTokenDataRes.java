package com.wanda.credit.ds.client.juxinli.bean.mobile;

import com.wanda.credit.ds.client.juxinli.bean.ebusi.MobileEBusiDataSource;

public class GetTokenDataRes {
	
	private String token;
	private String coll_phone_num;
	private MobileEBusiDataSource datasource;
	
	
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getColl_phone_num() {
		return coll_phone_num;
	}
	public void setColl_phone_num(String coll_phone_num) {
		this.coll_phone_num = coll_phone_num;
	}
	public MobileEBusiDataSource getDatasource() {
		return datasource;
	}
	public void setDatasource(MobileEBusiDataSource datasource) {
		this.datasource = datasource;
	}
	
	public String toString() {
		return "GetTokenDataRes [token=" + token + ", coll_phone_num="
				+ coll_phone_num + ", datasource=" + datasource + "]";
	}
	
	

}
