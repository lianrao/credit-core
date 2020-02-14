/**   
* @Description: 请求结果中的异常信息
* @author xiaobin.hou  
* @date 2016年11月1日 下午3:16:15 
* @version V1.0   
*/
package com.wanda.credit.ds.client.ji_ao.bean;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * @author xiaobin.hou
 *
 */
public class ResErrorInfo {
	
	private String code;
	
	private String IFT;
	
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	@JSONField(name = "IFT")
	public String getIFT() {
		return IFT;
	}
	public void setIFT(String iFT) {
		IFT = iFT;
	}
	@Override
	public String toString() {
		return "ResException [code=" + code + ", IFT=" + IFT + "]";
	}

}
