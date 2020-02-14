package com.wanda.credit.ds.dao.iface.huifa.inter;

import com.wanda.credit.base.service.IBaseService;
import com.wanda.credit.ds.dao.domain.huifa.AdminLaw;

public interface IAdminLawService extends IBaseService<AdminLaw>{
	public void write(AdminLaw adminLaw);
}
