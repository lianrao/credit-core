/**   
* @Description: TODO(用一句话描述该文件做什么) 
* @author xiaobin.hou  
* @date 2016年6月13日 下午3:55:10 
* @version V1.0   
*/
package com.wanda.credit.ds.client.juxinli.bean.mobile.origin;

/**
 * @author xiaobin.hou
 *
 */
public class Nets {
	
	private String update_time;
	private String place;
	private String net_type;
	private String start_time;
	private String cell_phone;
	private String subtotal;
	private String subflow;
	private String use_time;
	
	public String getUpdate_time() {
		return update_time;
	}
	public void setUpdate_time(String update_time) {
		this.update_time = update_time;
	}
	public String getPlace() {
		return place;
	}
	public void setPlace(String place) {
		this.place = place;
	}
	public String getNet_type() {
		return net_type;
	}
	public void setNet_type(String net_type) {
		this.net_type = net_type;
	}
	public String getStart_time() {
		return start_time;
	}
	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}
	public String getCell_phone() {
		return cell_phone;
	}
	public void setCell_phone(String cell_phone) {
		this.cell_phone = cell_phone;
	}
	public String getSubtotal() {
		return subtotal;
	}
	public void setSubtotal(String subtotal) {
		this.subtotal = subtotal;
	}
	public String getSubflow() {
		return subflow;
	}
	public void setSubflow(String subflow) {
		this.subflow = subflow;
	}
	public String getUse_time() {
		return use_time;
	}
	public void setUse_time(String use_time) {
		this.use_time = use_time;
	}
	@Override
	public String toString() {
		return "Nets [update_time=" + update_time + ", place=" + place
				+ ", net_type=" + net_type + ", start_time=" + start_time
				+ ", cell_phone=" + cell_phone + ", subtotal=" + subtotal
				+ ", subflow=" + subflow + ", use_time=" + use_time + "]";
	}
	
	

}
