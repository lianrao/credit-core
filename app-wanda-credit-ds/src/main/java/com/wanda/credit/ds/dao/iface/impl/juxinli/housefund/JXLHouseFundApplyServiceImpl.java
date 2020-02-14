/**   
* @Description: TODO(用一句话描述该文件做什么) 
* @author xiaobin.hou  
* @date 2016年5月25日 下午2:59:16 
* @version V1.0   
*/
package com.wanda.credit.ds.dao.iface.impl.juxinli.housefund;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.base.dao.DaoService;
import com.wanda.credit.base.service.impl.BaseServiceImpl;
import com.wanda.credit.ds.dao.domain.juxinli.housefund.HouseApplyInfoPojo;
import com.wanda.credit.ds.dao.iface.juxinli.housefund.IJXLHouseFundApplyService;

/**
 * @author xiaobin.hou
 *
 */
@Service
@Transactional
public class JXLHouseFundApplyServiceImpl extends BaseServiceImpl<HouseApplyInfoPojo> implements
		IJXLHouseFundApplyService {
	
	@Autowired
	private DaoService daoService;
	
	public void updateApplyInfo(HouseApplyInfoPojo applyInfoPojo){
		daoService.update(applyInfoPojo);
	}

	
}
