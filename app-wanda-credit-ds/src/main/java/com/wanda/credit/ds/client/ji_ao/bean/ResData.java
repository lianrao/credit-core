/**   
* @Description: TODO(用一句话描述该文件做什么) 
* @author xiaobin.hou  
* @date 2016年11月1日 下午3:18:53 
* @version V1.0   
*/
package com.wanda.credit.ds.client.ji_ao.bean;

import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * @author xiaobin.hou
 *
 */
public class ResData {
	
	private MobileLocation ISPNUM;	
	private List<CheckRes> RSL;	
	private List<ResErrorInfo> ECL;
	
	@JSONField(name = "ISPNUM")
	public MobileLocation getISPNUM() {
		return ISPNUM;
	}
	public void setISPNUM(MobileLocation iSPNUM) {
		ISPNUM = iSPNUM;
	}
	@JSONField(name = "RSL")
	public List<CheckRes> getRSL() {
		return RSL;
	}
	public void setRSL(List<CheckRes> rSL) {
		RSL = rSL;
	}
	@JSONField(name = "ECL")
	public List<ResErrorInfo> getECL() {
		return ECL;
	}
	public void setECL(List<ResErrorInfo> eCL) {
		ECL = eCL;
	}
	
	

}
