/**   
* @Description: 点微不良信息
* @author nan.liu
* @date 2019年04月1日 上午11:59:14 
* @version V1.0   
*/
package com.wanda.credit.ds.client.dianwei.beans;

/**
 * @author nan.liu
 */
public class DianWei_bad_Msg {
	private DianWei_bad_data data;
	private String code;
	private String msg;
	public DianWei_bad_data getData() {
		return data;
	}
	public void setData(DianWei_bad_data data) {
		this.data = data;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}

}
