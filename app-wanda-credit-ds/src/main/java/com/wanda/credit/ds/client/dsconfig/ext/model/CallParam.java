package com.wanda.credit.ds.client.dsconfig.ext.model;

/**
 * @description
 * @author wuchsh
 * @version 1.0
 * @createdate 2017年3月8日 上午10:28:00
 * 
 */
public class CallParam {
	private String name;
	private Object value;

	public String getName() {
		return name;
	}

	public CallParam setName(String name) {
		this.name = name;
		return this;
	}

	public Object getValue() {
		return value;
	}

	public CallParam setValue(Object value) {
		this.value = value;
		return this;
	}

}
