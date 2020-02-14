/**   
* @Description: TODO(用一句话描述该文件做什么) 
* @author xiaobin.hou  
* @date 2016年5月25日 下午2:59:16 
* @version V1.0   
*/
package com.wanda.credit.ds.dao.iface.impl.juxinli.housefund;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.base.dao.DaoService;
import com.wanda.credit.base.service.impl.BaseServiceImpl;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.ds.dao.domain.juxinli.housefund.HouseSupCityPojo;
import com.wanda.credit.ds.dao.iface.juxinli.housefund.IJXLHouseFundCitysService;

/**
 * @author xiaobin.hou
 *
 */
@Service
@Transactional
public class JXLHouseFundCitysServiceImpl extends BaseServiceImpl<HouseSupCityPojo> implements
		IJXLHouseFundCitysService {
	
	@Autowired
	private DaoService daoService;

	public void addCityDataList(List<Map<String, String>> allCityDataList) {
		
		daoService.executeSql("delete from CPDB_DS.T_DS_JXL_HOUSING_CITY_CODE");
		Date nowTime = new Date();
		List<HouseSupCityPojo> cityPojoList = new ArrayList<HouseSupCityPojo>();
		for (Map<String, String> cityMap : allCityDataList) {
			HouseSupCityPojo cityPojo = new HouseSupCityPojo();
			cityPojo.setCreate_time(nowTime);
			cityPojo.setUpdate_time(nowTime);
			String code = cityMap.get("code");
			String fullCode = cityMap.get("fullcode");
			cityPojo.setName(cityMap.get("name"));
			cityPojo.setCode(code);
			cityPojo.setFull_code(fullCode);
			cityPojo.setLevels(cityMap.get("level"));
			cityPojo.setCategory(cityMap.get("category"));
			
			if (!StringUtil.isEmpty(code) && !StringUtil.isEmpty(fullCode)) {
				if (fullCode.length() > code.length()) {
					cityPojo.setParent_code(fullCode.substring(0,fullCode.indexOf(code)));
				}else{
					cityPojo.setParent_code(code);
				}
			}
			
			cityPojoList.add(cityPojo);
		}
		
		daoService.create(cityPojoList);
		
	}
	
	public TreeMap<String, Object> queryCityDataAndOutput(){
		
		List<Object[]> findAllBySql = daoService.findAllBySql("select distinct c.parent_code from CPDB_DS.T_DS_JXL_HOUSING_CITY_CODE c");
		
		if (findAllBySql == null || findAllBySql.size() < 1) {			
			return null;
		}
//		REGION_CODE
		String sql = "select c.NAME as NAME,c.CODE as CODE,c.FULL_CODE as FULL_CODE,c.LEVELS as LEVELS from CPDB_DS.T_DS_JXL_HOUSING_CITY_CODE c where c.PARENT_CODE = ?";
		
		int i = 0;
		
		TreeMap<String, Object> cityData = new TreeMap<String, Object>();
		
		for (Object[] objects : findAllBySql) {
			
			i++;
			
			List<Map<String, Object>> queryForList = daoService.getJdbcTemplate().queryForList(sql, objects[0]);
		
			cityData.put("province_" + i, queryForList);
		}
		
		return cityData;
		
		
	}
}
