/**   
* @Description: TODO(用一句话描述该文件做什么) 
* @author xiaobin.hou  
* @date 2016年7月11日 下午4:40:21 
* @version V1.0   
*/
package com.wanda.credit.ds.dao.iface.impl.juxinli.PBOCReport;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.base.dao.DaoService;
import com.wanda.credit.base.service.impl.BaseServiceImpl;
import com.wanda.credit.ds.dao.domain.juxinli.PBOCReport.PBOCDataResPojo;
import com.wanda.credit.ds.dao.iface.juxinli.PBOCReport.IJXLPBOCDataResService;

/**
 * @author xiaobin.hou
 *
 */
@Service
@Transactional
public class JXLPBOCDataResServiceImpl extends BaseServiceImpl<PBOCDataResPojo>
		implements IJXLPBOCDataResService {

	@Autowired
	private DaoService daoService;

	public boolean isInCache(String requestId ,String code) {
		
		String hql = "select seqId From PBOCDataResPojo where requestId =:requestId and error_code =:error_code";
		
//		PBOCDataResPojo resPojo = new PBOCDataResPojo();
//		resPojo.setRequestId(requestId);
//		resPojo.setError_code("31200");
//		List<PBOCDataResPojo> resPojoList = daoService.findByHQL(hql, resPojo);
		
		Map<String, Object> params = new HashMap<String, Object>();		
		params.put("requestId", requestId);
		params.put("error_code", code);		
		long totalCount = daoService.getTotalCount(hql, params);
		
		return totalCount > 0;
	}
	
	
	
	
}
