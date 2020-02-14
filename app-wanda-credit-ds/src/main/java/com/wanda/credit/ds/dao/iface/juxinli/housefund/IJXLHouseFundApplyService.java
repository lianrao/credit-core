/**   
* @Description: TODO(用一句话描述该文件做什么) 
* @author xiaobin.hou  
* @date 2016年5月25日 下午2:58:11 
* @version V1.0   
*/
package com.wanda.credit.ds.dao.iface.juxinli.housefund;

import com.wanda.credit.base.service.IBaseService;
import com.wanda.credit.ds.dao.domain.juxinli.housefund.HouseApplyInfoPojo;

/**
 * @author xiaobin.hou
 *
 */
public interface IJXLHouseFundApplyService extends IBaseService<HouseApplyInfoPojo> {
	
	public void updateApplyInfo(HouseApplyInfoPojo applyInfoPojo);
	

}
