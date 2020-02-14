/**   
 * @Description: 聚信立_电商原始数据
 * @author xiaobin.hou  
 * @date 2016年5月30日 上午9:20:08 
 * @version V1.0   
 */
package com.wanda.credit.ds.dao.iface.juxinli.ebusi;

import java.util.List;

import com.wanda.credit.base.service.IBaseService;
import com.wanda.credit.ds.dao.domain.juxinli.ebusi.EbusiBasicPojo;

public interface IJXLEBusiBasicService extends IBaseService<EbusiBasicPojo> {

	/**
	 * 查询本地是否有缓存数据
	 * 
	 * @param requestId
	 * @return
	 */
	public boolean isInCache(String requestId);

	/**
	 * 获取缓存数据
	 * 
	 * @param requestId
	 * @return
	 */
	public List<EbusiBasicPojo> getCacheData(String requestId);

	

}
