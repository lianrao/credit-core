/**   
* @Description: 聚信立_公积金_提交采集请求返回结果 
* @author xiaobin.hou  
* @date 2016年5月28日 下午4:43:00 
* @version V1.0   
*/
package com.wanda.credit.ds.client.juxinli.bean.housefund;

/**
 * @author xiaobin.hou
 *
 */
public class HouseSubmitRes {
	
	private boolean success;
	private SubmitResData data;
	
	
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public SubmitResData getData() {
		return data;
	}
	public void setData(SubmitResData data) {
		this.data = data;
	}
	
	public String toString() {
		return "HouseSubmitRes [success=" + success + ", data=" + data + "]";
	}
	
	

	
}
