/**   
* @Description: TODO(用一句话描述该文件做什么) 
* @author xiaobin.hou  
* @date 2016年9月2日 上午9:43:41 
* @version V1.0   
*/
package com.wanda.credit.ds.dao.iface.impl.juxinli.mobile;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.base.service.impl.BaseServiceImpl;
import com.wanda.credit.ds.dao.domain.juxinli.mobile.MobileRawDataCallPojo;
import com.wanda.credit.ds.dao.iface.juxinli.mobile.IJXLMobileCallService;

/**
 * @author xiaobin.hou
 *
 */
@Service
@Transactional
public class JXLMobileCallServiceImpl extends
		BaseServiceImpl<MobileRawDataCallPojo> implements IJXLMobileCallService {
	
}
