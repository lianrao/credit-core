/**   
* @Description: 聚信立——电商原始数据-service实现
* @author xiaobin.hou  
* @date 2016年6月8日 上午9:12:20 
* @version V1.0   
*/
package com.wanda.credit.ds.dao.iface.impl.juxinli.ebusi;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.base.dao.DaoService;
import com.wanda.credit.base.service.impl.BaseServiceImpl;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.ds.dao.domain.juxinli.ebusi.EbusiBasicPojo;
import com.wanda.credit.ds.dao.iface.juxinli.ebusi.IJXLEBusiBasicService;

@Service
@Transactional
public class JXLEBusiBasicServiceImpl extends BaseServiceImpl<EbusiBasicPojo> implements
		IJXLEBusiBasicService {

	@Autowired
	private DaoService daoService;
	/**
	 * 查询数据中是否有缓存数据
	 */
	public boolean isInCache(String requestId) {
		
		String hql = "from EbusiBasicPojo where requestId =:requestId";
		
		Map<String, Object> params = new HashMap<String, Object>();
		
		params.put("requestId", requestId);
		
		List<Object> findByHQL = daoService.findByHQL(hql, params);
		
		return findByHQL.size() > 0;
	}
	/**
	 * 获取缓存中的数据
	 */
	public List<EbusiBasicPojo> getCacheData(String requestId) {
		
		if (StringUtil.isEmpty(requestId)) {
			return null;
		}
		
		if (!isInCache(requestId)) {
			return null;
		}
		
		String hql = "from EbusiBasicPojo where requestId =:requestId";
		
		Map<String, Object> params = new HashMap<String, Object>();
		
		params.put("requestId", requestId);
		
		List<EbusiBasicPojo> basicPojoList = daoService.findByHQL(hql, params);
		
		return basicPojoList;
	}
	
}
