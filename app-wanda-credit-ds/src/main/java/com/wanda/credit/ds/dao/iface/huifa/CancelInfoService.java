package com.wanda.credit.ds.dao.iface.huifa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.base.dao.DaoService;
import com.wanda.credit.base.service.impl.BaseServiceImpl;
import com.wanda.credit.ds.dao.domain.huifa.CancelInfo;
import com.wanda.credit.ds.dao.iface.huifa.inter.ICancelInfoService;

@Service
@Transactional
public class CancelInfoService extends BaseServiceImpl<CancelInfo> implements ICancelInfoService{
	@Autowired
    private DaoService daoService;
	public void write(CancelInfo cancelInfo){
		daoService.create(cancelInfo);
	}
}
