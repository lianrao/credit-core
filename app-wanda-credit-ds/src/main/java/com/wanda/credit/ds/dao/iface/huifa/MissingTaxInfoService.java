package com.wanda.credit.ds.dao.iface.huifa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.base.dao.DaoService;
import com.wanda.credit.base.service.impl.BaseServiceImpl;
import com.wanda.credit.ds.dao.domain.huifa.MissingTaxInfo;
import com.wanda.credit.ds.dao.iface.huifa.inter.IMissingTaxInfoService;

@Service
@Transactional
public class MissingTaxInfoService extends BaseServiceImpl<MissingTaxInfo> implements IMissingTaxInfoService{
	@Autowired
    private DaoService daoService;
	public void write(MissingTaxInfo missingTaxInfo){
		daoService.create(missingTaxInfo);
	}
}
