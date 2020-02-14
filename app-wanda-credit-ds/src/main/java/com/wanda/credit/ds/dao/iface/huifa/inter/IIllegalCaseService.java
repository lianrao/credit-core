package com.wanda.credit.ds.dao.iface.huifa.inter;

import com.wanda.credit.base.service.IBaseService;
import com.wanda.credit.ds.dao.domain.huifa.IllegalCase;

public interface IIllegalCaseService extends IBaseService<IllegalCase>{
	public void write(IllegalCase illegalCase);
}
