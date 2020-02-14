/**   
* @Description: 汇总ServiceImpl 
* @author xiaobin.hou  
* @date 2016年7月11日 下午5:20:59 
* @version V1.0   
*/
package com.wanda.credit.ds.dao.iface.impl.juxinli.PBOCReport;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.base.service.impl.BaseServiceImpl;
import com.wanda.credit.ds.dao.domain.juxinli.PBOCReport.PBOCDataSummaryPojo;
import com.wanda.credit.ds.dao.iface.juxinli.PBOCReport.IJXLPBOCDataSummaryService;

/**
 * @author xiaobin.hou
 *
 */
@Service
@Transactional
public class JXLPBOCDataSummaryServiceImpl extends BaseServiceImpl<PBOCDataSummaryPojo> implements
		IJXLPBOCDataSummaryService {
	
	
}
