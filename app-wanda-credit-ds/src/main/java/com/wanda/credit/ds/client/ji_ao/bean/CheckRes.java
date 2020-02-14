/**   
* @Description: 查询信息 
* @author xiaobin.hou  
* @date 2016年11月1日 下午3:05:09 
* @version V1.0   
*/
package com.wanda.credit.ds.client.ji_ao.bean;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * @author xiaobin.hou
 *
 */
public class CheckRes {
	
	private CheckResDetail RS;
	private String IFT;
	
	@JSONField(name = "RS")
	public CheckResDetail getRS() {
		return RS;
	}
	public void setRS(CheckResDetail rS) {
		RS = rS;
	}
	@JSONField(name = "IFT")
	public String getIFT() {
		return IFT;
	}
	public void setIFT(String iFT) {
		IFT = iFT;
	}
	
	

}
