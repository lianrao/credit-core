package com.wanda.credit.ds.dao.iface.huifa.inter;

import com.wanda.credit.base.service.IBaseService;
import com.wanda.credit.ds.dao.domain.huifa.OldLaiInfo;

public interface IOldLaiInfoService extends IBaseService<OldLaiInfo>{
	public void write(OldLaiInfo oldLaiInfo);
}
