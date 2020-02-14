/**   
* @Description: 聚信立—公积金-原始数据Service实现类
* @author xiaobin.hou  
* @date 2016年5月30日 上午9:21:59 
* @version V1.0   
*/
package com.wanda.credit.ds.dao.iface.impl.juxinli.housefund;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.base.service.impl.BaseServiceImpl;
import com.wanda.credit.ds.dao.domain.juxinli.housefund.HouseRawDataBasicPojo;
import com.wanda.credit.ds.dao.iface.juxinli.housefund.IJXLHouseFundBasicService;

/**
 * @author xiaobin.hou
 *
 */
@Service
@Transactional
public class JXLHouseFundBasicServiceImpl extends BaseServiceImpl<HouseRawDataBasicPojo> implements
		IJXLHouseFundBasicService {
	
}
