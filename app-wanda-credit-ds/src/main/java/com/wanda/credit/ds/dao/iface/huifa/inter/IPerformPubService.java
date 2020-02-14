package com.wanda.credit.ds.dao.iface.huifa.inter;

import com.wanda.credit.base.service.IBaseService;
import com.wanda.credit.ds.dao.domain.huifa.PerformPub;

public interface IPerformPubService extends IBaseService<PerformPub>{
	public void write(PerformPub performPub);
}
