/**   
* @Description: TODO(用一句话描述该文件做什么) 
* @author xiaobin.hou  
* @date 2016年8月16日 上午11:18:20 
* @version V1.0   
*/
package com.wanda.credit.ds.dao;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.base.dao.DaoService;
import com.wanda.credit.base.service.impl.BaseServiceImpl;
import com.wanda.credit.ds.dao.domain.pengyuan.Py_osta_info;
import com.wanda.credit.ds.dao.iface.pengyuan.IPYOstaService;

/**
 * @author xiaobin.hou
 *
 */
@Service
@Transactional
public class PYOstaServiceImpl extends BaseServiceImpl<Py_osta_info> implements
		IPYOstaService {
	
	@Autowired
	private DaoService daoService;

		
	public List<Py_osta_info> queryCacheData(String name, String crptedCardNo, int month) {

		String hql = "from Py_osta_info osta where osta.name =:name and osta.cardNo =:cardNo and osta.treatResult = '1' and to_char(create_date,'YYYY-MM') =:month";
		
		DateFormat format = new SimpleDateFormat("yyyy-MM");
		String formatDate = format.format(new Date());
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("name", name);
		params.put("cardNo", crptedCardNo);
		params.put("month", formatDate);
		List<Py_osta_info> findList = daoService.findByHQL(hql, params);
		return findList;
	}



	public void addOstaInfo(List<Py_osta_info> domainList, String name,
			String crptedCardNo) throws Exception {

		StringBuffer sqlBf = new StringBuffer();
		sqlBf.append("delete from T_DS_PY_Vocational v where v.name = '").append(name).append("' and v.CARDNO = '")
			.append(crptedCardNo).append("'");
		
		daoService.getJdbcTemplate().execute(sqlBf.toString());
		
		this.add(domainList);
		
	}
	
	
	
}
