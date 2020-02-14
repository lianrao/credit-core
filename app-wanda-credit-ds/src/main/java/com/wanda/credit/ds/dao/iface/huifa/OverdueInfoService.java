package com.wanda.credit.ds.dao.iface.huifa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.base.dao.DaoService;
import com.wanda.credit.base.service.impl.BaseServiceImpl;
import com.wanda.credit.ds.dao.domain.huifa.OverdueInfo;
import com.wanda.credit.ds.dao.iface.huifa.inter.IOverdueInfoService;

@Service
@Transactional
public class OverdueInfoService extends BaseServiceImpl<OverdueInfo> implements IOverdueInfoService{
	@Autowired
    private DaoService daoService;
	public void write(OverdueInfo overdueInfo){
		daoService.create(overdueInfo);
   }
}
