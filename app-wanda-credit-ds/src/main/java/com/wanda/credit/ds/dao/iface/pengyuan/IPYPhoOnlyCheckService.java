package com.wanda.credit.ds.dao.iface.pengyuan;

import com.wanda.credit.base.service.IBaseService;
import com.wanda.credit.ds.dao.domain.pengyuan.Py_pho_only_check;


public interface IPYPhoOnlyCheckService extends IBaseService<Py_pho_only_check>{

	boolean inCached(String name, String cardNo, String phone);

	public Py_pho_only_check queryPhoneCheck(String name, String cardNo, String phone);
}
