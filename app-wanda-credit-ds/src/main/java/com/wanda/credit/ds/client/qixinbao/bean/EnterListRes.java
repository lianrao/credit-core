/**   
* @Description: 企业名称模糊查询返回数据 
* @author xiaobin.hou  
* @date 2016年12月19日 下午5:20:46 
* @version V1.0   
*/
package com.wanda.credit.ds.client.qixinbao.bean;

/**
 * @author xiaobin.hou
 *
 */
public class EnterListRes {
	
	private String status;
	private String message;
	private String data;
	
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	@Override
	public String toString() {
		return "EnterListRes [status=" + status + ", message=" + message
				+ ", data=" + data + "]";
	}
	

}
