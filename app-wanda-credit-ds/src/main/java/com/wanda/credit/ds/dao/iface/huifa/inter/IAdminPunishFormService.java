package com.wanda.credit.ds.dao.iface.huifa.inter;

import com.wanda.credit.base.service.IBaseService;
import com.wanda.credit.ds.dao.domain.huifa.AdminPunishForm;

public interface IAdminPunishFormService extends IBaseService<AdminPunishForm>{
	public void write(AdminPunishForm adminPunishForm);
}
