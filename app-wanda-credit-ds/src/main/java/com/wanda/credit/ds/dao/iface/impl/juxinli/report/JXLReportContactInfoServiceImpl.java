package com.wanda.credit.ds.dao.iface.impl.juxinli.report;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.base.service.impl.BaseServiceImpl;
import com.wanda.credit.ds.dao.domain.juxinli.report.ContactInfoPojo;
import com.wanda.credit.ds.dao.iface.juxinli.report.IJXLReportContactInfoService;
@Service
@Transactional
public class JXLReportContactInfoServiceImpl extends BaseServiceImpl<ContactInfoPojo>
		implements IJXLReportContactInfoService {

	public List<ContactInfoPojo> queryByRequestId(String requestId) {
		
		String hql = "FROM ContactInfoPojo c WHERE c.requestId =:requestId";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("requestId", requestId);
		return daoService.findByHQL(hql, params);
	}
	
}
