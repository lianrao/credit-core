/**   
* @Description: 央行征信报告数据Service
* @author xiaobin.hou  
* @date 2016年7月11日 下午6:59:27 
* @version V1.0   
*/
package com.wanda.credit.ds.client.juxinli.service;

import com.wanda.credit.ds.client.juxinli.bean.PBOCReport.CreditTransaction;
import com.wanda.credit.ds.dao.domain.juxinli.PBOCReport.PBOCDataResPojo;

/**
 * @author xiaobin.hou
 *
 */
public interface IJXLPBOCReportDataService {

	/**
	 * @param cretitTransaction
	 * @param prefix 
	 * @param resPojo 
	 */
	public void addReportData(CreditTransaction cretitTransaction,String requestId, String prefix, PBOCDataResPojo resPojo);

}
