package com.wanda.credit.ds.dao.iface.huifa.inter;

import com.wanda.credit.base.service.IBaseService;
import com.wanda.credit.ds.dao.domain.huifa.LimitHighConsum;

public interface ILimitHighConsumService extends IBaseService<LimitHighConsum>{
	public void write(LimitHighConsum limitHighConsum);
}
