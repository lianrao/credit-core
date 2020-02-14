package com.wanda.credit.ds.client.guoztCar;

import java.io.Serializable;

public class Identity implements Serializable{
	private static final long serialVersionUID = 235423L;
	private String name;
	private String idcard;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIdcard() {
		return idcard;
	}

	public void setIdcard(String idcard) {
		this.idcard = idcard;
	}

}
