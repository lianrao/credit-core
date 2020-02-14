/**   
* @Description: 获取报告数据响应POJO类 
* @author xiaobin.hou  
* @date 2016年7月8日 上午11:45:47 
* @version V1.0   
*/
package com.wanda.credit.ds.dao.iface.juxinli.PBOCReport;

import com.wanda.credit.base.service.IBaseService;
import com.wanda.credit.ds.dao.domain.juxinli.PBOCReport.PBOCDataResPojo;

/**
 * @author xiaobin.hou
 *
 */
public interface IJXLPBOCDataResService extends IBaseService<PBOCDataResPojo> {

	/**
	 * @param requestId
	 * @return
	 */
	public boolean isInCache(String requestId,String code);

}
