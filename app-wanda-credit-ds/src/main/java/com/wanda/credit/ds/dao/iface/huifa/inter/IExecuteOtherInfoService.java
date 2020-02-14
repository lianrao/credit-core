package com.wanda.credit.ds.dao.iface.huifa.inter;

import com.wanda.credit.base.service.IBaseService;
import com.wanda.credit.ds.dao.domain.huifa.ExecuteOtherInfo;

public interface IExecuteOtherInfoService extends IBaseService<ExecuteOtherInfo>{
	public void write(ExecuteOtherInfo executeOtherInfo);
}
