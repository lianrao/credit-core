/**   
* @Description: 贷前银联数据-数据信息
* @author xiaobin.hou  
* @date 2016年8月9日 下午4:56:51 
* @version V1.0   
*/
package com.wanda.credit.ds.client.w_unionpay.bean;

/**
 * @author xiaobin.hou
 *
 */
public class PreLoanCardData {
	
	private PreLoanCardDataIndex cardData;
	private String cardNo;//卡号
	
	
	
	public PreLoanCardDataIndex getCardData() {
		return cardData;
	}
	public void setCardData(PreLoanCardDataIndex cardData) {
		this.cardData = cardData;
	}
	public String getCardNo() {
		return cardNo;
	}
	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}
	
	
	@Override
	public String toString() {
		return "PreLoanCardData [cardData=" + cardData + ", cardNo=" + cardNo
				+ "]";
	}
	
	
	

}
