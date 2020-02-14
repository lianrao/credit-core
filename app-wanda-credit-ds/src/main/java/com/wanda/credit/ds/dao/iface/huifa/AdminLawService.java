package com.wanda.credit.ds.dao.iface.huifa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.base.dao.DaoService;
import com.wanda.credit.base.service.impl.BaseServiceImpl;
import com.wanda.credit.ds.dao.domain.huifa.AdminLaw;
import com.wanda.credit.ds.dao.iface.huifa.inter.IAdminLawService;
@Service
@Transactional
public class AdminLawService extends BaseServiceImpl<AdminLaw> implements IAdminLawService{
	@Autowired
    private DaoService daoService;
	public void write(AdminLaw adminLaw){
		daoService.create(adminLaw);
	}
}
