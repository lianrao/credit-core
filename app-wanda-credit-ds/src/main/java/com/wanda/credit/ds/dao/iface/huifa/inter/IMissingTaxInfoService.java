package com.wanda.credit.ds.dao.iface.huifa.inter;

import com.wanda.credit.base.service.IBaseService;
import com.wanda.credit.ds.dao.domain.huifa.MissingTaxInfo;

public interface IMissingTaxInfoService extends IBaseService<MissingTaxInfo>{
	public void write(MissingTaxInfo missingTaxInfo);
}
