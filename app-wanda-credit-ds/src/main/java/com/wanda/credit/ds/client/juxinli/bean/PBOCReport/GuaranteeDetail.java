/**   
* @Description: 担保明细 
* @author xiaobin.hou  
* @date 2016年7月10日 下午5:15:13 
* @version V1.0   
*/
package com.wanda.credit.ds.client.juxinli.bean.PBOCReport;

/**
 * @author xiaobin.hou
 *
 */
public class GuaranteeDetail {
	
	private String guarantee_made_time;// 担保办理日期
	private String guaranteed_name;// 被担保人姓名
	private String guaranteed_card_type;// 被担保人证件类型
	private String guaranteed_card_number;// 被担保人证件号码
	private String guarantee_made_employer;// 担保办理单位
	private String guarantee_made_type;// 担保办理类型
	private String guarantee_contract_amount;// 担保合同金额
	private String guarantee_amount;// 担保金额
	private String deadline_time;// 截至日期
	private String guarantee_balance;// 担保余额
	private String status;// 状态
	
	
	public String getGuarantee_made_time() {
		return guarantee_made_time;
	}
	public void setGuarantee_made_time(String guarantee_made_time) {
		this.guarantee_made_time = guarantee_made_time;
	}
	public String getGuaranteed_name() {
		return guaranteed_name;
	}
	public void setGuaranteed_name(String guaranteed_name) {
		this.guaranteed_name = guaranteed_name;
	}
	public String getGuaranteed_card_type() {
		return guaranteed_card_type;
	}
	public void setGuaranteed_card_type(String guaranteed_card_type) {
		this.guaranteed_card_type = guaranteed_card_type;
	}
	public String getGuaranteed_card_number() {
		return guaranteed_card_number;
	}
	public void setGuaranteed_card_number(String guaranteed_card_number) {
		this.guaranteed_card_number = guaranteed_card_number;
	}
	public String getGuarantee_made_employer() {
		return guarantee_made_employer;
	}
	public void setGuarantee_made_employer(String guarantee_made_employer) {
		this.guarantee_made_employer = guarantee_made_employer;
	}
	public String getGuarantee_made_type() {
		return guarantee_made_type;
	}
	public void setGuarantee_made_type(String guarantee_made_type) {
		this.guarantee_made_type = guarantee_made_type;
	}
	public String getGuarantee_contract_amount() {
		return guarantee_contract_amount;
	}
	public void setGuarantee_contract_amount(String guarantee_contract_amount) {
		this.guarantee_contract_amount = guarantee_contract_amount;
	}
	public String getGuarantee_amount() {
		return guarantee_amount;
	}
	public void setGuarantee_amount(String guarantee_amount) {
		this.guarantee_amount = guarantee_amount;
	}
	public String getDeadline_time() {
		return deadline_time;
	}
	public void setDeadline_time(String deadline_time) {
		this.deadline_time = deadline_time;
	}
	public String getGuarantee_balance() {
		return guarantee_balance;
	}
	public void setGuarantee_balance(String guarantee_balance) {
		this.guarantee_balance = guarantee_balance;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	@Override
	public String toString() {
		return "GuaranteeDetail [guarantee_made_time=" + guarantee_made_time
				+ ", guaranteed_name=" + guaranteed_name
				+ ", guaranteed_card_type=" + guaranteed_card_type
				+ ", guaranteed_card_number=" + guaranteed_card_number
				+ ", guarantee_made_employer=" + guarantee_made_employer
				+ ", guarantee_made_type=" + guarantee_made_type
				+ ", guarantee_contract_amount=" + guarantee_contract_amount
				+ ", guarantee_amount=" + guarantee_amount + ", deadline_time="
				+ deadline_time + ", guarantee_balance=" + guarantee_balance
				+ ", status=" + status + "]";
	}
	
	

}
