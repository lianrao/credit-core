/**   
* @Description: 聚信立-信用卡账单提交采集请求请求结果-数据节点
* @author xiaobin.hou  
* @date 2016年7月21日 下午3:36:44 
* @version V1.0   
*/
package com.wanda.credit.ds.client.juxinli.bean.creditCardBill;

/**
 * @author xiaobin.hou
 *
 */
public class CreditSubResData {
	
	private String type;
	private String content;
	private int process_code;

	
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public int getProcess_code() {
		return process_code;
	}
	public void setProcess_code(int process_code) {
		this.process_code = process_code;
	}
	
	public String toString() {
		return "CreditSubResData [type=" + type + ", content=" + content
				+ ", process_code=" + process_code + "]";
	}
	
	

}
