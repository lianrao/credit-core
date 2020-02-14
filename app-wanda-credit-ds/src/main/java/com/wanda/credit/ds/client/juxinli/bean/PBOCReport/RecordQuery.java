/**   
* @Description: 查询信息 
* @author xiaobin.hou  
* @date 2016年7月10日 下午5:20:27 
* @version V1.0   
*/
package com.wanda.credit.ds.client.juxinli.bean.PBOCReport;

import java.util.List;

/**
 * @author xiaobin.hou
 *
 */
public class RecordQuery {
	
	private List<QueryDetail> personal_query_details;// 个人查询记录明细
	private List<QueryDetail> institution_query_details;// 机构查询记录明细
	
	
	public List<QueryDetail> getPersonal_query_details() {
		return personal_query_details;
	}
	public void setPersonal_query_details(List<QueryDetail> personal_query_details) {
		this.personal_query_details = personal_query_details;
	}
	public List<QueryDetail> getInstitution_query_details() {
		return institution_query_details;
	}
	public void setInstitution_query_details(
			List<QueryDetail> institution_query_details) {
		this.institution_query_details = institution_query_details;
	}

	
	
}
