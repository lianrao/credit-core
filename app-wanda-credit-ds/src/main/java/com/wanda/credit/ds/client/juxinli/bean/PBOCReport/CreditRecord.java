/**   
* @Description: 贷款信息记录
* @author xiaobin.hou  
* @date 2016年7月10日 下午5:36:53 
* @version V1.0   
*/
package com.wanda.credit.ds.client.juxinli.bean.PBOCReport;

import java.util.List;

/**
 * @author xiaobin.hou
 *
 */
public class CreditRecord {
	
	private RecordCreditLoanInfo credit_info;// 信用卡贷款信息
	private RecordHousingLoanInfo housing_loan_info;// 房屋贷款信息
	private List<Summary> summarys;// 贷款总计
	private RecordOtherLoanInfo loan_info;// 其他贷款信息
	private RecordGuarantee guarantee;// 为他人担保信息
	
	
	public RecordCreditLoanInfo getCredit_info() {
		return credit_info;
	}
	public void setCredit_info(RecordCreditLoanInfo credit_info) {
		this.credit_info = credit_info;
	}
	public RecordHousingLoanInfo getHousing_loan_info() {
		return housing_loan_info;
	}
	public void setHousing_loan_info(RecordHousingLoanInfo housing_loan_info) {
		this.housing_loan_info = housing_loan_info;
	}
	public List<Summary> getSummarys() {
		return summarys;
	}
	public void setSummarys(List<Summary> summarys) {
		this.summarys = summarys;
	}
	public RecordOtherLoanInfo getLoan_info() {
		return loan_info;
	}
	public void setLoan_info(RecordOtherLoanInfo loan_info) {
		this.loan_info = loan_info;
	}
	public RecordGuarantee getGuarantee() {
		return guarantee;
	}
	public void setGuarantee(RecordGuarantee guarantee) {
		this.guarantee = guarantee;
	}

}
