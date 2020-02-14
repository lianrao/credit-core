package com.wanda.credit.ds.dao.iface.impl.juxinli.report;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.base.service.impl.BaseServiceImpl;
import com.wanda.credit.ds.dao.domain.juxinli.report.EbusinessExpensePojo;
import com.wanda.credit.ds.dao.iface.juxinli.report.IJXLReportEbusinessExpenseService;
@Service
@Transactional
public class JXLReportEbusinessExpenseServiceImpl extends BaseServiceImpl<EbusinessExpensePojo>
		implements IJXLReportEbusinessExpenseService {
	
}
