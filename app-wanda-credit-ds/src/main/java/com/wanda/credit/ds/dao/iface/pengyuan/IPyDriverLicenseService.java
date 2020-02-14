package com.wanda.credit.ds.dao.iface.pengyuan;

import com.wanda.credit.base.service.IBaseService;
import com.wanda.credit.ds.dao.domain.pengyuan.Py_driver_license;

public interface IPyDriverLicenseService extends IBaseService<Py_driver_license> {

	Py_driver_license queryCacheResult(String name, String crptedCardNo);
	
}
