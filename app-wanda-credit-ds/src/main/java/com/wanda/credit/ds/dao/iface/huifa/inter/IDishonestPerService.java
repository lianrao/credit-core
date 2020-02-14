package com.wanda.credit.ds.dao.iface.huifa.inter;

import com.wanda.credit.base.service.IBaseService;
import com.wanda.credit.ds.dao.domain.huifa.DishonestPer;

public interface IDishonestPerService extends IBaseService<DishonestPer>{
	public void write(DishonestPer dishonestPer);
}
