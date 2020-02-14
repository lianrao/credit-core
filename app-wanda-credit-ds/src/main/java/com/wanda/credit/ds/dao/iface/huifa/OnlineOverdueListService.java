package com.wanda.credit.ds.dao.iface.huifa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.base.dao.DaoService;
import com.wanda.credit.base.service.impl.BaseServiceImpl;
import com.wanda.credit.ds.dao.domain.huifa.OnlineOverdueList;
import com.wanda.credit.ds.dao.iface.huifa.inter.IOnlineOverdueListService;

@Service
@Transactional
public class OnlineOverdueListService extends BaseServiceImpl<OnlineOverdueList> implements IOnlineOverdueListService{
	@Autowired
    private DaoService daoService;
	public void write(OnlineOverdueList onlineOverdueList){
		daoService.create(onlineOverdueList);
   }
}
