package com.wanda.credit.ds.dao.iface.impl.juxinli.report;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.base.service.impl.BaseServiceImpl;
import com.wanda.credit.ds.dao.domain.juxinli.report.ContactDetailsPojo;
import com.wanda.credit.ds.dao.iface.juxinli.report.IJXLReportContactDetailsService;
@Service
@Transactional
public class JXLReportContactDetailsServiceImpl extends BaseServiceImpl<ContactDetailsPojo>
		implements IJXLReportContactDetailsService {
	
}
