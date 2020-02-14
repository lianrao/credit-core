package com.wanda.credit.ds.dao.iface.huifa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.base.dao.DaoService;
import com.wanda.credit.base.service.impl.BaseServiceImpl;
import com.wanda.credit.ds.dao.domain.huifa.ReminderNot;
import com.wanda.credit.ds.dao.iface.huifa.inter.IReminderNotService;

@Service
@Transactional
public class ReminderNotService extends BaseServiceImpl<ReminderNot> implements IReminderNotService{
	@Autowired
    private DaoService daoService;
	public void write(ReminderNot reminderNot){
		daoService.create(reminderNot);
   }
}
