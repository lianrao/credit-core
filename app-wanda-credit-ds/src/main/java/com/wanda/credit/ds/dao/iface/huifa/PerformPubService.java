package com.wanda.credit.ds.dao.iface.huifa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.base.dao.DaoService;
import com.wanda.credit.base.service.impl.BaseServiceImpl;
import com.wanda.credit.ds.dao.domain.huifa.PerformPub;
import com.wanda.credit.ds.dao.iface.huifa.inter.IPerformPubService;

@Service
@Transactional
public class PerformPubService extends BaseServiceImpl<PerformPub> implements IPerformPubService{
	@Autowired
    private DaoService daoService;
	public void write(PerformPub performPub){
		daoService.create(performPub);
   }
}
