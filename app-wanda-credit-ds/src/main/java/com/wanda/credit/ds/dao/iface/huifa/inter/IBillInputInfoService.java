package com.wanda.credit.ds.dao.iface.huifa.inter;

import com.wanda.credit.base.service.IBaseService;
import com.wanda.credit.ds.dao.domain.huifa.BillInputInfo;

public interface IBillInputInfoService extends IBaseService<BillInputInfo>{
	public void write(BillInputInfo billInputInfo);
}
