/**   
* @Description: 聚信立-信用卡-接口数据
* @author xiaobin.hou  
* @date 2016年7月22日 下午3:14:38 
* @version V1.0   
*/
package com.wanda.credit.ds.client.juxinli.bean.creditCardBill;

import java.util.List;

/**
 * @author xiaobin.hou
 *
 */
public class CreditTransaction {
	
	private String token;
	private List<CreditDetail> detail;
	
	
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public List<CreditDetail> getDetail() {
		return detail;
	}
	public void setDetail(List<CreditDetail> detail) {
		this.detail = detail;
	}
	
	
	public String toString() {
		return "CreditTransaction [token=" + token + ", detail=" + detail + "]";
	}
	

	
	
}
