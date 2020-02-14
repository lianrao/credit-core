package com.wanda.credit.ds.dao.iface.pengyuan;

import com.wanda.credit.base.service.IBaseService;
import com.wanda.credit.ds.dao.domain.pengyuan.Py_pho_check;
import com.wanda.credit.ds.dao.domain.pengyuan.Py_pho_status;

public interface IPYPhoCheckService extends IBaseService<Py_pho_check>{

	boolean inCached(String name, String cardNo, String phone);

	public Py_pho_check queryPhoneCheck(String name, String cardNo, String phone);
	
	public Py_pho_status queryPhoneStatus(String name, String cardNo, String phone);

}
