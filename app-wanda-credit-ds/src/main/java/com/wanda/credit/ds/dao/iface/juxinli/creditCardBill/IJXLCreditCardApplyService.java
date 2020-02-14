/**   
* @Description: 聚信立_信用卡账单提交采集请求service
* @author xiaobin.hou  
* @date 2016年7月20日 下午3:56:48 
* @version V1.0   
*/
package com.wanda.credit.ds.dao.iface.juxinli.creditCardBill;

import com.wanda.credit.base.service.IBaseService;
import com.wanda.credit.ds.dao.domain.juxinli.creditCardBill.CreditCardApplyPojo;

/**
 * @author xiaobin.hou
 *
 */
public interface IJXLCreditCardApplyService extends IBaseService<CreditCardApplyPojo> {

	/**
	 * 查询采集请求信息
	 * @param requestId
	 * @return
	 */
	public CreditCardApplyPojo queryApplyInfo(String requestId);

	/**
	 * @param applyInfo
	 */
	public void updateApplyInfo(CreditCardApplyPojo applyInfo);

	/**
	 * 根据交易序列号和状态获取采集信息
	 * @param requestId
	 * @param submitSuc
	 * @return
	 */
	public CreditCardApplyPojo queryApplyInfoByStatus(String requestId,
			String status);

}
