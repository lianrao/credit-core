package com.wanda.credit.ds.client.guoztCar;

import java.io.Serializable;

public class Receive implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 99999L;
	private String ret;
	private Object dat;
	private String ver;
	private String msg;

	public Receive() {
		// TODO Auto-generated constructor stub
	}

	public Object getDat() {
		return dat;
	}

	public void setDat(Object dat) {
		this.dat = dat;
	}

	public String getRet() {
		return ret;
	}

	public void setRet(String ret) {
		this.ret = ret;
	}

	public String getVer() {
		return ver;
	}

	public void setVer(String ver) {
		this.ver = ver;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

}
