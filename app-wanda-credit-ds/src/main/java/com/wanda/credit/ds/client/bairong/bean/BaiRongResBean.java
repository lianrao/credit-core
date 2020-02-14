/**   
* @Description: TODO(用一句话描述该文件做什么) 
* @author xiaobin.hou  
* @date 2016年11月1日 上午11:58:11 
* @version V1.0   
*/
package com.wanda.credit.ds.client.bairong.bean;

/**
 * @author xiaobin.hou
 *
 */
public class BaiRongResBean {
	
	private String code;
	private String msg;
	private ResData data;
    private String seq;
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
	public ResData getData() {
		return data;
	}
	public void setData(ResData data) {
		this.data = data;
	}

    public String getSeq() {
        return seq;
    }

    public void setSeq(String seq) {
        this.seq = seq;
    }
}
