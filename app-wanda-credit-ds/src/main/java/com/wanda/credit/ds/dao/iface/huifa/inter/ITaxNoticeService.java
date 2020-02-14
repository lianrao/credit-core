package com.wanda.credit.ds.dao.iface.huifa.inter;

import com.wanda.credit.base.service.IBaseService;
import com.wanda.credit.ds.dao.domain.huifa.TaxNotice;

public interface ITaxNoticeService extends IBaseService<TaxNotice>{
	public void write(TaxNotice taxNotice);
}
