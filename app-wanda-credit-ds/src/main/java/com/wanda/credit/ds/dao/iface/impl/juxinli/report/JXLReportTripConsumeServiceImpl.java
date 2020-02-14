package com.wanda.credit.ds.dao.iface.impl.juxinli.report;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.base.service.impl.BaseServiceImpl;
import com.wanda.credit.ds.dao.domain.juxinli.report.TripConsumePojo;
import com.wanda.credit.ds.dao.iface.juxinli.report.IJXLReportTripConsumeService;
@Service
@Transactional
public class JXLReportTripConsumeServiceImpl extends BaseServiceImpl<TripConsumePojo>
		implements IJXLReportTripConsumeService {
}
