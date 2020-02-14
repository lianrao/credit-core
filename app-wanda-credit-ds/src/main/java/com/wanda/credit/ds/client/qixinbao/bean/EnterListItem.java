/**   
* @Description: 企业名称模糊查询item节点
* @author xiaobin.hou  
* @date 2016年12月19日 下午5:16:06 
* @version V1.0   
*/
package com.wanda.credit.ds.client.qixinbao.bean;

/**
 * @author xiaobin.hou
 *
 */
public class EnterListItem {
	
	private String name;//企业名称
	private String id;//企业编号-数据源的编号
	private String start_date;//成立日期 yyyy-MM-dd
	private String oper_name;//企业法人
	private String reg_no;//注册号
	private String credit_no;//社会统一信用代码
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getStart_date() {
		return start_date;
	}
	public void setStart_date(String start_date) {
		this.start_date = start_date;
	}
	public String getOper_name() {
		return oper_name;
	}
	public void setOper_name(String oper_name) {
		this.oper_name = oper_name;
	}
	public String getReg_no() {
		return reg_no;
	}
	public void setReg_no(String reg_no) {
		this.reg_no = reg_no;
	}
	public String getCredit_no() {
		return credit_no;
	}
	public void setCredit_no(String credit_no) {
		this.credit_no = credit_no;
	}
	@Override
	public String toString() {
		return "EnterListItem [name=" + name + ", id=" + id + ", start_date="
				+ start_date + ", oper_name=" + oper_name + ", reg_no="
				+ reg_no + ", credit_no=" + credit_no + "]";
	}

}
