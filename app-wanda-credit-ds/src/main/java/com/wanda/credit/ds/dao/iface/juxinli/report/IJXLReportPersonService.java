package com.wanda.credit.ds.dao.iface.juxinli.report;

import com.wanda.credit.base.service.IBaseService;
import com.wanda.credit.ds.dao.domain.juxinli.report.PersonPojo;

public interface IJXLReportPersonService extends IBaseService<PersonPojo> {

	public PersonPojo queryByRequestId(String requestId);

}
