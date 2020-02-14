/**   
* @Description: 聚信立_信用卡账单-账单信息Service
* @author xiaobin.hou  
* @date 2016年7月20日 下午3:56:48 
* @version V1.0   
*/
package com.wanda.credit.ds.dao.iface.juxinli.creditCardBill;

import java.util.List;

import com.wanda.credit.base.service.IBaseService;
import com.wanda.credit.ds.dao.domain.juxinli.creditCardBill.CreditCardBillInfoPojo;

/**
 * @author xiaobin.hou
 *
 */
public interface IJXLCreditCardBillInfoService extends IBaseService<CreditCardBillInfoPojo> {

	/**
	 * @param requestId
	 */
	public List<String> queryDataSource(String requestId);

	

}
