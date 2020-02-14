package com.wanda.credit.ds.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.base.service.impl.BaseServiceImpl;
import com.wanda.credit.ds.dao.domain.pengyuan.Py_driver_license;
import com.wanda.credit.ds.dao.iface.pengyuan.IPyDriverLicenseService;

@Service
@Transactional
public class PyDriverLicenseServiceImpl extends BaseServiceImpl<Py_driver_license> implements IPyDriverLicenseService {

	@Override
	public Py_driver_license queryCacheResult(String name, String crptedCardNo) {
		String hql = "from Py_driver_license  t where t.name=:name and t.cardNo =:cardNo";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("name", name);		
		params.put("cardNo", crptedCardNo);
		List<Py_driver_license> ls = daoService.findByHQL(hql, params);
		if(CollectionUtils.isNotEmpty(ls))return ls.get(0);
		return null;
	}

}
