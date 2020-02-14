/**   
* @Description: TODO(用一句话描述该文件做什么) 
* @author xiaobin.hou  
* @date 2016年11月10日 上午11:30:55 
* @version V1.0   
*/
package com.wanda.credit.ds.dao.iface.jiAo;

import java.util.Map;

import com.wanda.credit.base.service.IBaseService;
import com.wanda.credit.ds.dao.domain.jiAo.GeoMobileCheck;

/**
 * @author xiaobin.hou
 *
 */
public interface IJiAoMobileCheckService extends IBaseService<GeoMobileCheck> {

    public GeoMobileCheck findGeoMobileCheck(String name, String cardNo, String mobileNo);
    
    public boolean inCachedCount(String name, String mobile,int days);
    public Map<String, Object> findGeoMobileCheck(String name, String mobileNo);
    /**
     * 保存二要素数据
     */
    public void saveMobileName(String trade_id,String mobile,String name,String cardNo,String data);
    
    /**
     * 保存法海负面详情
     */
    public void saveFahaiDetail(String trade_id,String ds_id,String datatype,String entryId,String content);
    /**
     * 查询法海负面详情
     */
    public String findFahaiDetail(String trade_id,String ds_id,String datatype,String entryId,int date);
    
    public String findDataByMobilename(String mobile,String name,int date);
}
