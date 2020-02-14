/**   
* @Description: 央行个人征信报告提交信息处理接口 
* @author xiaobin.hou  
* @date 2016年7月8日 上午11:47:40 
* @version V1.0   
*/
package com.wanda.credit.ds.dao.iface.impl.juxinli.PBOCReport;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.base.service.impl.BaseServiceImpl;
import com.wanda.credit.ds.dao.domain.juxinli.PBOCReport.PBOCApplyPojo;
import com.wanda.credit.ds.dao.iface.juxinli.PBOCReport.IJXLPBOCReportApplyService;

/**
 * @author xiaobin.hou
 *
 */
@Service
@Transactional
public class JXLPBOCReportApplyServiceImpl extends BaseServiceImpl<PBOCApplyPojo> implements
		IJXLPBOCReportApplyService {


}
