package com.wanda.credit.ds.client.juxinli.bean.report;


/**
 * 报告信息 Created by zhashiwen on 15/11/09.
 */
public class Report {
	
	/**
	 * token标识
	 */
	private String token;
	
	/**
	 * 报告时间（UTC)
	 */
	private String updt;
	
	/**
	 * 报告编号
	 */
	private String id;
	
	/**
	 * 报告版本
	 */
	private String version;
	
	/**
	 * 报告编号（废弃）
	 */
	@Deprecated
	private String uid;
	
	public String getId(){
	
		return id;
	}
	
	public void setId(String id){
	
		this.id = id;
	}
	
	public String getToken(){
	
		return token;
	}
	
	public void setToken(String token){
	
		this.token = token;
	}
	
	public String getUid(){
	
		return uid;
	}
	
	public void setUid(String uid){
	
		this.uid = uid;
	}
	
	public String getUpdt(){
	
		return updt;
	}
	
	public void setUpdt(String updt){
	
		this.updt = updt;
	}
	
	public String getVersion(){
	
		return version;
	}
	
	public void setVersion(String version){
	
		this.version = version;
	}
	
	@Override
	public String toString(){
	
		return "Report{" + "id='" + id + '\'' + ", token='" + token + '\'' + ", updt='" + updt + '\'' + ", version='"
		        + version + '\'' + ", uid='" + uid + '\'' + '}';
	}
}
