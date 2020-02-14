/**   
* @Description: TODO(用一句话描述该文件做什么) 
* @author xiaobin.hou  
* @date 2016年9月2日 上午9:42:39 
* @version V1.0   
*/
package com.wanda.credit.ds.dao.iface.impl.juxinli.mobile;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.base.service.impl.BaseServiceImpl;
import com.wanda.credit.ds.dao.domain.juxinli.mobile.MobileRawDataAccountPojo;
import com.wanda.credit.ds.dao.iface.juxinli.mobile.IJXLMobileAccountService;

/**
 * @author xiaobin.hou
 *
 */
@Service
@Transactional
public class JXLMobileAccountServiceImpl extends
		BaseServiceImpl<MobileRawDataAccountPojo> implements
		IJXLMobileAccountService {
	
}
