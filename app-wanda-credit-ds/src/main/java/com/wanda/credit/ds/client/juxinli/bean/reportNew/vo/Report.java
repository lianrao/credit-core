package com.wanda.credit.ds.client.juxinli.bean.reportNew.vo;

/**
 * 报告信息
 * @author xiaobin.hou
 *
 */
public class Report {

	/**
	 * token标识
	 */
	private String token;

	/**
	 * 报告时间（UTC)
	 * 
	 */
	private String update_time;
	/**
	 * 报告编号
	 */
	private String rpt_id;

	/**
	 * 报告版本
	 */
	private String version;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getUpdate_time() {
		return update_time;
	}

	public void setUpdate_time(String update_time) {
		this.update_time = update_time;
	}

	public String getRpt_id() {
		return rpt_id;
	}

	public void setRpt_id(String rpt_id) {
		this.rpt_id = rpt_id;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	@Override
	public String toString() {
		return "Report [token=" + token + ", update_time=" + update_time + ", rpt_id=" + rpt_id + ", version=" + version
				+ "]";
	}

}
