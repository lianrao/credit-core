package com.wanda.credit.ds.dao.iface.juxinli.report;

import java.util.List;

import com.wanda.credit.base.service.IBaseService;
import com.wanda.credit.ds.dao.domain.juxinli.report.ContactInfoPojo;

public interface IJXLReportContactInfoService extends IBaseService<ContactInfoPojo> {

	public List<ContactInfoPojo> queryByRequestId(String requestId);

}
