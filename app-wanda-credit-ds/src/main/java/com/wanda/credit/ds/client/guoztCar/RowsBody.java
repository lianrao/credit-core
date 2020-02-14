package com.wanda.credit.ds.client.guoztCar;

import java.io.Serializable;

public class RowsBody implements Serializable {

	private static final long serialVersionUID = -234324234L;
	private int total;
	private String rows;

	public RowsBody() {
		// TODO Auto-generated constructor stub
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public String getRows() {
		return rows;
	}

	public void setRows(String rows) {
		this.rows = rows;
	}

}
