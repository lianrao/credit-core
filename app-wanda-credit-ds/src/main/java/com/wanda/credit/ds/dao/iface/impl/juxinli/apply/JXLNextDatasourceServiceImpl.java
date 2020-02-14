package com.wanda.credit.ds.dao.iface.impl.juxinli.apply;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.base.service.impl.BaseServiceImpl;
import com.wanda.credit.ds.dao.domain.juxinli.apply.ApplyNextDataSourcePojo;
import com.wanda.credit.ds.dao.iface.juxinli.apply.IJXLNextDatasourceService;
@Service
@Transactional
public class JXLNextDatasourceServiceImpl extends BaseServiceImpl<ApplyNextDataSourcePojo> implements
		IJXLNextDatasourceService {

	public void updateNextDS(ApplyNextDataSourcePojo pojo) {
		
		if (pojo != null) {
			daoService.update(pojo);
		}
		
	}
	
}
