/**   
* @Description: TODO(用一句话描述该文件做什么) 
* @author xiaobin.hou  
* @date 2016年7月26日 下午3:34:09 
* @version V1.0   
*/
package com.wanda.credit.ds.dao.iface.impl.juxinli.creditCardBill;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.base.service.impl.BaseServiceImpl;
import com.wanda.credit.ds.dao.domain.juxinli.creditCardBill.CreditCardDataResPojo;
import com.wanda.credit.ds.dao.iface.juxinli.creditCardBill.IJXLCreditCardDataResService;

/**
 * @author xiaobin.hou
 *
 */
@Service
@Transactional
public class JXLCreditCardDataResServiceImpl extends BaseServiceImpl<CreditCardDataResPojo>
		implements IJXLCreditCardDataResService {
	
}
