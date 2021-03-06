package com.wanda.credit.ds.dao.iface.huifa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.base.dao.DaoService;
import com.wanda.credit.base.service.impl.BaseServiceImpl;
import com.wanda.credit.ds.dao.domain.huifa.LimitExit;
import com.wanda.credit.ds.dao.iface.huifa.inter.ILimitExitService;

@Service
@Transactional
public class LimitExitService extends BaseServiceImpl<LimitExit> implements ILimitExitService{
	@Autowired
    private DaoService daoService;
	public void write(LimitExit limitExit){
		daoService.create(limitExit);
	}
}
