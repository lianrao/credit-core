package com.wanda.credit.ds.dao.iface.huifa.inter;

import com.wanda.credit.base.service.IBaseService;
import com.wanda.credit.ds.dao.domain.huifa.OverdueInfo;

public interface IOverdueInfoService extends IBaseService<OverdueInfo>{
	public void write(OverdueInfo overdueInfo);
}
