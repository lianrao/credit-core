/**   
* @Description: TODO(用一句话描述该文件做什么) 
* @author xiaobin.hou  
* @date 2016年7月26日 下午1:46:20 
* @version V1.0   
*/
package com.wanda.credit.ds.dao.iface.impl.juxinli.creditCardBill;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.base.dao.DaoService;
import com.wanda.credit.base.service.impl.BaseServiceImpl;
import com.wanda.credit.ds.dao.domain.juxinli.creditCardBill.CreditCardBillInfoPojo;
import com.wanda.credit.ds.dao.iface.juxinli.creditCardBill.IJXLCreditCardBillInfoService;

/**
 * @author xiaobin.hou
 *
 */
@Service
@Transactional
public class JXLCreditCardBillInfoServiceImpl extends BaseServiceImpl<CreditCardBillInfoPojo>
		implements IJXLCreditCardBillInfoService {

	@Autowired
	private DaoService daoService;
	
	public List<String> queryDataSource(String requestId) {
		
		
		String sql = "SELECT DISTINCT(b.datasource) as DSNAME FROM cpdb_ds.t_ds_jxl_credit_bill_info b WHERE b.requestid = ?";
		
		List<Map<String, Object>> queryForList = daoService.getJdbcTemplate().queryForList(sql, requestId);
		
		List<String> dsList = new ArrayList<String>();
		if (queryForList != null && queryForList.size() > 0) {
			for (Map<String, Object> map : queryForList) {
				String dataSource = (String)map.get("DSNAME");
				dsList.add(dataSource);
			}
		}
	
		return dsList;
	
	}
	
	
}
