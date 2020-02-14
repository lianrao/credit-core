/**   
* @Description: TODO(用一句话描述该文件做什么) 
* @author xiaobin.hou  
* @date 2016年7月20日 下午3:58:48 
* @version V1.0   
*/
package com.wanda.credit.ds.dao.iface.impl.juxinli.creditCardBill;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.base.dao.DaoService;
import com.wanda.credit.base.service.impl.BaseServiceImpl;
import com.wanda.credit.ds.dao.domain.juxinli.creditCardBill.CreditCardApplyPojo;
import com.wanda.credit.ds.dao.iface.juxinli.creditCardBill.IJXLCreditCardApplyService;

/**
 * @author xiaobin.hou
 *
 */
@Service
@Transactional
public class JXLCreditCardApplyServiceImpl extends BaseServiceImpl<CreditCardApplyPojo> implements
		IJXLCreditCardApplyService {

	@Autowired
	private DaoService daoService;
	
	public CreditCardApplyPojo queryApplyInfo(String requestId) {
		
		String hql = "From CreditCardApplyPojo a where a.requestId =:requestId and a.status in ('100','101','102','103')";
		
		CreditCardApplyPojo params = new CreditCardApplyPojo();
		params.setRequestId(requestId);
		List<CreditCardApplyPojo> applyList = daoService.findByHQL(hql, params);
		
		if (applyList != null && applyList.size() > 0 ) {
			return applyList.get(0);
		}else{
			return null;
		}
		
	}

	
	public void updateApplyInfo(CreditCardApplyPojo applyInfo) {

		daoService.update(applyInfo);
		
	}

	public CreditCardApplyPojo queryApplyInfoByStatus(String requestId,
			String status) {
		
		String hql = "From CreditCardApplyPojo a where a.requestId =:requestId and a.status =:status";
		
		CreditCardApplyPojo params = new CreditCardApplyPojo();
		params.setRequestId(requestId);
		params.setStatus(status);
		List<CreditCardApplyPojo> applyList = daoService.findByHQL(hql, params);
		
		if (applyList != null && applyList.size() > 0 ) {
			return applyList.get(0);
		}else{
			return null;
		}
	}
	
	
}
