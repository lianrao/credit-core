/**   
* @Description: 担保信息
* @author xiaobin.hou  
* @date 2016年7月10日 下午5:24:38 
* @version V1.0   
*/
package com.wanda.credit.ds.client.juxinli.bean.PBOCReport;

import java.util.List;

/**
 * @author xiaobin.hou
 *
 */
public class RecordGuarantee {
	
	private List<GuaranteeDetail> guarantee_detail;
	private List<String> guarantee_info;
	
	
	public List<GuaranteeDetail> getGuarantee_detail() {
		return guarantee_detail;
	}
	public void setGuarantee_detail(List<GuaranteeDetail> guarantee_detail) {
		this.guarantee_detail = guarantee_detail;
	}
	public List<String> getGuarantee_info() {
		return guarantee_info;
	}
	public void setGuarantee_info(List<String> guarantee_info) {
		this.guarantee_info = guarantee_info;
	}

}
