/**   
* @Description: 信用卡账单数据Service类
* @author xiaobin.hou  
* @date 2016年7月26日 下午2:03:52 
* @version V1.0   
*/
package com.wanda.credit.ds.client.juxinli.service;

import java.util.List;

import com.wanda.credit.ds.client.juxinli.bean.creditCardBill.CreditDetail;
import com.wanda.credit.ds.dao.domain.juxinli.creditCardBill.CreditCardBillInfoPojo;

/**
 * @author xiaobin.hou
 *
 */
public interface IJXLCreditBillDataService {

	/**
	 * @param billPojoList
	 * @param requestId 
	 */
	public boolean addData(List<CreditCardBillInfoPojo> billPojoList, String requestId);

	/**
	 * @param requestId
	 * @return
	 */
	public List<CreditDetail> loadCacheData(String requestId) throws Exception ;

}
