package com.wanda.credit.ds.dao.iface.huifa.inter;

import com.wanda.credit.base.service.IBaseService;
import com.wanda.credit.ds.dao.domain.huifa.FeeTotal;

public interface IFeeTotalService extends IBaseService<FeeTotal>{
	public void write(FeeTotal feeTotal);
}
