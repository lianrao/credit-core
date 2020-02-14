package com.wanda.credit.ds.dao.iface.huifa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.base.dao.DaoService;
import com.wanda.credit.base.service.impl.BaseServiceImpl;
import com.wanda.credit.ds.dao.domain.huifa.BillInputInfo;
import com.wanda.credit.ds.dao.iface.huifa.inter.IBillInputInfoService;
@Service
@Transactional
public class BillInputInfoService extends BaseServiceImpl<BillInputInfo> implements IBillInputInfoService{
	@Autowired
    private DaoService daoService;
	@Override
	public void write(BillInputInfo billInputInfo) {
		daoService.create(billInputInfo);
	}

}
