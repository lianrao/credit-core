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
public class DianWei_bad_data {
	private String name;
	private String cid;
	private String respCode;
	private String respDesc;
	private DianWei_bad_detail detail;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCid() {
		return cid;
	}
	public void setCid(String cid) {
		this.cid = cid;
	}
	public String getRespCode() {
		return respCode;
	}
	public void setRespCode(String respCode) {
		this.respCode = respCode;
	}
	public String getRespDesc() {
		return respDesc;
	}
	public void setRespDesc(String respDesc) {
		this.respDesc = respDesc;
	}
	public DianWei_bad_detail getDetail() {
		return detail;
	}
	public void setDetail(DianWei_bad_detail detail) {
		this.detail = detail;
	}
}
