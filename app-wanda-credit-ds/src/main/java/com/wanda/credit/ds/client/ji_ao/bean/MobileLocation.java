/**   
* @Description: 手机号码归属地信息 
* @author xiaobin.hou  
* @date 2016年11月1日 下午2:59:13 
* @version V1.0   
*/
package com.wanda.credit.ds.client.ji_ao.bean;

/**
 * @author xiaobin.hou
 *
 */
public class MobileLocation {
	
	private String province;
	private String city;
	private String isp;
	
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getIsp() {
		return isp;
	}
	public void setIsp(String isp) {
		this.isp = isp;
	}
	@Override
	public String toString() {
		return "MobileLocation [province=" + province + ", city=" + city
				+ ", isp=" + isp + "]";
	}
	
	

}
