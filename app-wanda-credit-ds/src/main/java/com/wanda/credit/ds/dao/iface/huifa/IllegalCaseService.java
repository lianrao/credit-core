package com.wanda.credit.ds.dao.iface.huifa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.base.dao.DaoService;
import com.wanda.credit.base.service.impl.BaseServiceImpl;
import com.wanda.credit.ds.dao.domain.huifa.IllegalCase;
import com.wanda.credit.ds.dao.iface.huifa.inter.IIllegalCaseService;

@Service
@Transactional
public class IllegalCaseService extends BaseServiceImpl<IllegalCase> implements IIllegalCaseService{
	@Autowired
    private DaoService daoService;
	public void write(IllegalCase illegalCase){
		daoService.create(illegalCase);
	}
}
