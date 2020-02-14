/**   
* @Description: TODO(用一句话描述该文件做什么) 
* @author xiaobin.hou  
* @date 2016年8月9日 下午3:26:27 
* @version V1.0   
*/
package com.wanda.credit.ds.dao.iface.impl.wUnionpay;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.base.service.impl.BaseServiceImpl;
import com.wanda.credit.ds.dao.domain.wUnionpay.UnionPayPreLoanPojo;
import com.wanda.credit.ds.dao.iface.wUnionpay.UnionPayPreLoanService;

/**
 * @author xiaobin.hou
 *
 */
@Service
@Transactional
public class UnionPayPreLoanServiceImpl extends BaseServiceImpl<UnionPayPreLoanPojo> implements
		UnionPayPreLoanService {
	
}
