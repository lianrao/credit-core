package com.wanda.credit.ds.dao.iface.huifa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.base.dao.DaoService;
import com.wanda.credit.base.service.impl.BaseServiceImpl;
import com.wanda.credit.ds.dao.domain.huifa.IdentifyDishonestTax;
import com.wanda.credit.ds.dao.iface.huifa.inter.IIdentifyDishonestTaxService;

@Service
@Transactional
public class IdentifyDishonestTaxService extends BaseServiceImpl<IdentifyDishonestTax> implements IIdentifyDishonestTaxService{
	@Autowired
    private DaoService daoService;
	public void write(IdentifyDishonestTax identifyDishonestTax){
		daoService.create(identifyDishonestTax);
	}
}
