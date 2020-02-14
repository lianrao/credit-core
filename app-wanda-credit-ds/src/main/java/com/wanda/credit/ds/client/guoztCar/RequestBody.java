package com.wanda.credit.ds.client.guoztCar;

import java.io.Serializable;

public class RequestBody implements Serializable {
	private static final long serialVersionUID = -2343214321L;
	private String cmd;
	private Object dat;
	private String ver;
	private String src;

	public RequestBody() {
		// TODO Auto-generated constructor stub
	}

	public Object getDat() {
		return dat;
	}

	public void setDat(Object dat) {
		this.dat = dat;
	}

	public String getCmd() {
		return cmd;
	}

	public void setCmd(String cmd) {
		this.cmd = cmd;
	}

	public String getVer() {
		return ver;
	}

	public void setVer(String ver) {
		this.ver = ver;
	}

	public String getSrc() {
		return src;
	}

	public void setSrc(String src) {
		this.src = src;
	}

}
