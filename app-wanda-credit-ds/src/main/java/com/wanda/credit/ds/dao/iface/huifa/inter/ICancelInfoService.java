package com.wanda.credit.ds.dao.iface.huifa.inter;

import com.wanda.credit.base.service.IBaseService;
import com.wanda.credit.ds.dao.domain.huifa.CancelInfo;

public interface ICancelInfoService extends IBaseService<CancelInfo>{
	public void write(CancelInfo cancelInfo);
}
