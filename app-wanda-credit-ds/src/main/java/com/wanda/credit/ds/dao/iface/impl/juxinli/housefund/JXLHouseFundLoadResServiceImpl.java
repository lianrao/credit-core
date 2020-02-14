/**   
* @Description: 聚信立-公积金-结果
* @author xiaobin.hou  
* @date 2016年5月30日 下午8:04:44 
* @version V1.0   
*/
package com.wanda.credit.ds.dao.iface.impl.juxinli.housefund;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.base.service.impl.BaseServiceImpl;
import com.wanda.credit.ds.dao.domain.juxinli.housefund.HouseRawDataResPojo;
import com.wanda.credit.ds.dao.iface.juxinli.housefund.IJXLHouseFundLoadResService;

/**
 * @author xiaobin.hou
 *
 */
@Service
@Transactional
public class JXLHouseFundLoadResServiceImpl extends BaseServiceImpl<HouseRawDataResPojo>
		implements IJXLHouseFundLoadResService {
	
}
