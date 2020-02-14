package com.wanda.credit.ds.dao.iface.huifa.inter;

import com.wanda.credit.base.service.IBaseService;
import com.wanda.credit.ds.dao.domain.huifa.RegisterInfo;

public interface IRegisterInfoService extends IBaseService<RegisterInfo>{
	public void write(RegisterInfo registerInfo);
}
