/**   
* @Description: TODO(用一句话描述该文件做什么) 
* @author xiaobin.hou  
* @date 2016年11月10日 上午11:30:55 
* @version V1.0   
*/
package com.wanda.credit.ds.dao.iface.juhe;

import com.wanda.credit.base.service.IBaseService;
import com.wanda.credit.ds.dao.domain.jiAo.GeoMobileCheck;

/**
 * @author xiaobin.hou
 *
 */
public interface IJuHeCarSearchService extends IBaseService<GeoMobileCheck> {

    /**
     * 保存聚合车辆信息
     */
    public void saveCarDetail(String trade_id,String ds_id,String name,String carNumber,String content);
    /**
     * 查询聚合车辆信息
     */
    public String findCarDetail(String ds_id,String name,String carNumber,int days);
    /**
	 * 查询数据库是否存在此数据
	 * @param score
	 */
	public boolean inCachedCount(String name, String carNumber,String dsid,int days);
}
