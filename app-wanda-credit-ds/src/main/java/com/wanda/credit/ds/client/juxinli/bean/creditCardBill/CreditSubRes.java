/**   
* @Description: 聚信立-信用卡账单提交采集请求请求结果
* @author xiaobin.hou  
* @date 2016年7月21日 下午3:34:47 
* @version V1.0   
*/
package com.wanda.credit.ds.client.juxinli.bean.creditCardBill;

/**
 * @author xiaobin.hou
 *
 */
public class CreditSubRes {
	
	private boolean success;
	private CreditSubResData data;
	
	
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public CreditSubResData getData() {
		return data;
	}
	public void setData(CreditSubResData data) {
		this.data = data;
	}
	
	
	public String toString() {
		return "CreditSubRes [success=" + success + ", data=" + data + "]";
	}
	
	

}
