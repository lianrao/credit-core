package com.wanda.credit.ds.dao.iface.huifa.inter;

import com.wanda.credit.base.service.IBaseService;
import com.wanda.credit.ds.dao.domain.huifa.ReminderNot;

public interface IReminderNotService extends IBaseService<ReminderNot>{
	public void write(ReminderNot reminderNot);
}
