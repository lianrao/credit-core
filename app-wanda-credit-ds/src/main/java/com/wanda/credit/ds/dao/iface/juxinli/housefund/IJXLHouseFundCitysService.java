/**   
* @Description: TODO(用一句话描述该文件做什么) 
* @author xiaobin.hou  
* @date 2016年5月25日 下午2:58:11 
* @version V1.0   
*/
package com.wanda.credit.ds.dao.iface.juxinli.housefund;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.wanda.credit.base.service.IBaseService;
import com.wanda.credit.ds.dao.domain.juxinli.housefund.HouseSupCityPojo;

/**
 * @author xiaobin.hou
 *
 */
public interface IJXLHouseFundCitysService extends IBaseService<HouseSupCityPojo> {

	/**
	 * @param allCityDataList
	 */
	public void addCityDataList(List<Map<String, String>> allCityDataList);
	
	
	public TreeMap<String, Object> queryCityDataAndOutput();

}
