package com.wanda.credit.ds.client.juxinli.bean.report;

import java.util.List;

/**
 * 常用服务列表
 * @author xiaobin.hou
 *
 */
public class MainService {
	
	/**
	 * 服务细节表
	 */
	private List<ServiceDetail> service_details;
	
	/**
	 * 服务次数
	 */
	private String total_service_cnt;
	
	/**
	 * 服务商类型
	 */
	private String company_type;
	
	/**
	 * 服务商名字
	 */
	private String company_name;
	
	public String getCompany_type(){
	
		return company_type;
	}
	
	public void setCompany_type(String company_type){
	
		this.company_type = company_type;
	}
	
	public List<ServiceDetail> getService_details(){
	
		return service_details;
	}
	
	public void setService_details(List<ServiceDetail> service_details){
	
		this.service_details = service_details;
	}
	
	public String getTotal_service_cnt(){
	
		return total_service_cnt;
	}
	
	public void setTotal_service_cnt(String total_service_cnt){
	
		this.total_service_cnt = total_service_cnt;
	}
	
	public String getCompany_name(){
	
		return company_name;
	}
	
	public void setCompany_name(String company_name){
	
		this.company_name = company_name;
	}
	
	@Override
	public String toString(){
	
		return "MainService [service_details=" + service_details + ", total_service_cnt=" + total_service_cnt
		        + ", company_type=" + company_type + ", company_name=" + company_name + "]";
	}
	
}
