package com.wanda.credit.ds.dao.iface.juxinli.apply;

import com.wanda.credit.base.service.IBaseService;
import com.wanda.credit.ds.dao.domain.juxinli.apply.ApplyNextDataSourcePojo;

public interface IJXLNextDatasourceService extends IBaseService<ApplyNextDataSourcePojo> {

	public void updateNextDS(ApplyNextDataSourcePojo pojo);

}
