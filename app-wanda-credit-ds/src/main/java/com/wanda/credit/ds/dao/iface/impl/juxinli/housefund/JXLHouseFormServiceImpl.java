/**   
 * @Description: TODO(用一句话描述该文件做什么) 
 * @author ou.guohao
 * @date 2016年6月2日 下午2:59:16 
 * @version V1.0   
 */
package com.wanda.credit.ds.dao.iface.impl.juxinli.housefund;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.base.dao.DaoService;
import com.wanda.credit.base.service.impl.BaseServiceImpl;
import com.wanda.credit.ds.dao.domain.juxinli.housefund.HouseFormPojo;
import com.wanda.credit.ds.dao.iface.juxinli.housefund.IJXLHouseFormService;

@Service
@Transactional
public class JXLHouseFormServiceImpl extends BaseServiceImpl<HouseFormPojo> implements IJXLHouseFormService {

	@Autowired
	private DaoService daoService;

	@Override
	public void saveHouseFormBatch(List<HouseFormPojo> houseForms) {
		String hql;
		for (HouseFormPojo houseForm : houseForms) {
			Map<String, Object> params = new HashMap<String, Object>();
			hql = "select t from HouseFormPojo t where t.regioCode=:regioCode and t.sortId=:sortId";
			params.put("regioCode", houseForm.getRegioCode());
			params.put("sortId", houseForm.getSortId());
			HouseFormPojo temp = daoService.findOneByNamedHQL(hql, params);
			if (temp != null) {
				daoService.remove(temp);
			}
			daoService.create(houseForm);
		}
	}

}
