package com.wanda.credit.ds.dao.iface.impl.juxinli.report;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.base.service.impl.BaseServiceImpl;
import com.wanda.credit.ds.dao.domain.juxinli.report.PersonPojo;
import com.wanda.credit.ds.dao.iface.juxinli.report.IJXLReportPersonService;
@Service
@Transactional
public class JXLReportPersonServiceImpl extends BaseServiceImpl<PersonPojo> implements
		IJXLReportPersonService {

	public PersonPojo queryByRequestId(String requestId) {
		
		String hql = "FROM PersonPojo p WHERE p.requestId =:requestId AND p.success =:success";
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("requestId", requestId);
		params.put("success", "true");
		
		return daoService.findOneByHQL(hql, params);
	}
	
}
