/**   
 * @Description: 聚信立_公积金原始数据_service
 * @author xiaobin.hou  
 * @date 2016年5月30日 上午9:20:08 
 * @version V1.0   
 */
package com.wanda.credit.ds.dao.iface.juxinli.housefund;

import java.util.List;

import com.wanda.credit.base.service.IBaseService;
import com.wanda.credit.ds.dao.domain.juxinli.housefund.HouseFormPojo;

/**
 * @author ou.guohao
 *
 */
public interface IJXLHouseFormService extends IBaseService<HouseFormPojo> {

	/**
	 * 批量保存公积金表单
	 * 
	 * @date 2016年6月2日 下午5:55:51
	 * @author ou.guohao
	 * @param houseForms
	 */
	void saveHouseFormBatch(List<HouseFormPojo> houseForms);

}
