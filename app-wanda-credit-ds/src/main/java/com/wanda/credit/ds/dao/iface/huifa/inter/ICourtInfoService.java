package com.wanda.credit.ds.dao.iface.huifa.inter;

import com.wanda.credit.base.service.IBaseService;
import com.wanda.credit.ds.dao.domain.huifa.CourtInfo;

public interface ICourtInfoService extends IBaseService<CourtInfo>{
	public void write(CourtInfo courtInfo);
}
