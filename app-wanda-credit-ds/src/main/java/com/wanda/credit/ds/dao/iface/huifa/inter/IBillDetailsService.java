package com.wanda.credit.ds.dao.iface.huifa.inter;

import com.wanda.credit.base.service.IBaseService;
import com.wanda.credit.ds.dao.domain.huifa.BillDetails;

public interface IBillDetailsService extends IBaseService<BillDetails>{
	public void write(BillDetails billDetails);
}
