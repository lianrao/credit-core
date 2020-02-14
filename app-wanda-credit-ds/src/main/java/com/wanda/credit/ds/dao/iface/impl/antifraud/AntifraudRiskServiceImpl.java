/**   
* @Description: TODO(用一句话描述该文件做什么) 
* @author xiaobin.hou  
* @date 2016年8月23日 下午8:04:56 
* @version V1.0   
*/
package com.wanda.credit.ds.dao.iface.impl.antifraud;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.base.service.impl.BaseServiceImpl;
import com.wanda.credit.ds.dao.domain.antifraud.AntifraudRisk;
import com.wanda.credit.ds.dao.iface.antifraud.AntifraudRiskService;

/**
 * @author xiaobin.hou
 *
 */
@Service
@Transactional
public class AntifraudRiskServiceImpl extends BaseServiceImpl<AntifraudRisk> implements
		AntifraudRiskService {
	
	
}
