/**   
* @Description: 查询信息 
* @author xiaobin.hou  
* @date 2016年11月1日 下午3:05:09 
* @version V1.0   
*/
package com.wanda.credit.ds.client.ji_ao.bean;

/**
 * @author xiaobin.hou
 *
 */
public class CheckResDetail {
	private String code;
	private String desc;
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	
	
	@Override
	public String toString() {
		return "CheckRes [code=" + code + ", desc=" + desc + "]";
	}
	
}
