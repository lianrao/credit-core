package com.wanda.credit.ds.dao.iface.huifa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.base.dao.DaoService;
import com.wanda.credit.base.service.impl.BaseServiceImpl;
import com.wanda.credit.ds.dao.domain.huifa.BillDetails;
import com.wanda.credit.ds.dao.iface.huifa.inter.IBillDetailsService;
@Service
@Transactional
public class BillDetailsService extends BaseServiceImpl<BillDetails> implements IBillDetailsService{
	@Autowired
    private DaoService daoService;
	@Override
	public void write(BillDetails billDetails) {
		daoService.create(billDetails);
	}

}
