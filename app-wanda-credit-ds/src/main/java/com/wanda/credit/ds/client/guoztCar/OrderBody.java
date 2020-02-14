package com.wanda.credit.ds.client.guoztCar;

import java.io.Serializable;

public class OrderBody implements Serializable{
	private static final long serialVersionUID = 188888L;
	private String orderid;
	private Object paramlist;
	private String page;
	private String pagesize;
	private String productid;
	
	public OrderBody() {
		// TODO Auto-generated constructor stub
	}
	
	
	public String getProductId() {
		return productid;
	}


	public void setProductId(String productid) {
		this.productid = productid;
	}

	
	public Object getParamlist() {
		return paramlist;
	}


	public void setParamlist(Object paramlist) {
		this.paramlist = paramlist;
	}


	public String getOrderid() {
		return orderid;
	}

	public void setOrderid(String orderid) {
		this.orderid = orderid;
	}

	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}

	public String getPagesize() {
		return pagesize;
	}

	public void setPagesize(String pagesize) {
		this.pagesize = pagesize;
	}

}
