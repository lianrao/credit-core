/**   
* @Description: TODO(用一句话描述该文件做什么) 
* @author xiaobin.hou  
* @date 2016年5月24日 下午7:08:22 
* @version V1.0   
*/
package com.wanda.credit.ds.client.juxinli.bean.housefund;

import java.util.List;

/**
 * @author xiaobin.hou
 *
 */
public class JXLAllCitiesRes {
	
	private boolean success;
	private String message;
	private List<JXLHouseCitiyInfo> data;
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public List<JXLHouseCitiyInfo> getData() {
		return data;
	}
	public void setData(List<JXLHouseCitiyInfo> data) {
		this.data = data;
	}
	
	public String toString() {
		return "JXLAllCitiesRes [success=" + success + ", message=" + message
				+ ", data=" + data + "]";
	}
	
	

}
