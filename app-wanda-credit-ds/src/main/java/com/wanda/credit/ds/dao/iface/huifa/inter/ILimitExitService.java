package com.wanda.credit.ds.dao.iface.huifa.inter;

import com.wanda.credit.base.service.IBaseService;
import com.wanda.credit.ds.dao.domain.huifa.LimitExit;

public interface ILimitExitService extends IBaseService<LimitExit>{
	public void write(LimitExit limitExit);
}
