package com.wanda.credit.ds.dao.iface.impl.juxinli.report;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.base.service.impl.BaseServiceImpl;
import com.wanda.credit.ds.dao.domain.juxinli.report.TripInfoPojo;
import com.wanda.credit.ds.dao.iface.juxinli.report.IJXLReportTripInfoService;
@Service
@Transactional
public class JXLReportTripInfoServiceImpl extends BaseServiceImpl<TripInfoPojo> implements
		IJXLReportTripInfoService {
	
}
