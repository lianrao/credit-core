package com.wanda.credit.ds.dao.iface.huifa.inter;

import com.wanda.credit.base.service.IBaseService;
import com.wanda.credit.ds.dao.domain.huifa.PreciseQueryInputInfo;

public interface IInputInfoService extends IBaseService<PreciseQueryInputInfo>{
	public void write(PreciseQueryInputInfo inputInfo);
}
