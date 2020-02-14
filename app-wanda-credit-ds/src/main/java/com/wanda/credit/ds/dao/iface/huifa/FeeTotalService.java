package com.wanda.credit.ds.dao.iface.huifa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.base.dao.DaoService;
import com.wanda.credit.base.service.impl.BaseServiceImpl;
import com.wanda.credit.ds.dao.domain.huifa.FeeTotal;
import com.wanda.credit.ds.dao.iface.huifa.inter.IFeeTotalService;
@Service
@Transactional
public class FeeTotalService extends BaseServiceImpl<FeeTotal> implements IFeeTotalService{
	private static final long serialVersionUID = 1L;

	@Autowired
    private DaoService daoService;
	public void write(FeeTotal feeTotal){
		daoService.create(feeTotal);
	}
}
