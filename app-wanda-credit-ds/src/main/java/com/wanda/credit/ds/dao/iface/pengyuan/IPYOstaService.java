/**   
* @Description: TODO(用一句话描述该文件做什么) 
* @author xiaobin.hou  
* @date 2016年8月16日 上午11:17:01 
* @version V1.0   
*/
package com.wanda.credit.ds.dao.iface.pengyuan;

import java.util.List;

import com.wanda.credit.base.service.IBaseService;
import com.wanda.credit.ds.dao.domain.pengyuan.Py_osta_info;

/**
 * @author xiaobin.hou
 *
 */
public interface IPYOstaService extends IBaseService<Py_osta_info> {

	/**
	 * 查询本地是否有缓存数据
	 * @param name
	 * @param crptedCardNo
	 * @return
	 */
	public List<Py_osta_info> queryCacheData(String name, String crptedCardNo, int month);

	/**
	 * 保存职业资格信息-本地有缓存，先删除后保存新数据
	 * @param domainList
	 * @param name
	 * @param crptedCardNo
	 */
	public void addOstaInfo(List<Py_osta_info> domainList, String name,
			String crptedCardNo) throws Exception;

}
