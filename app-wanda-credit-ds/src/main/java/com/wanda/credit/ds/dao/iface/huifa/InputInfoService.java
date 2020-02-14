package com.wanda.credit.ds.dao.iface.huifa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.base.dao.DaoService;
import com.wanda.credit.base.service.impl.BaseServiceImpl;
import com.wanda.credit.ds.dao.domain.huifa.PreciseQueryInputInfo;
import com.wanda.credit.ds.dao.iface.huifa.inter.IInputInfoService;
@Service
@Transactional
public class InputInfoService extends BaseServiceImpl<PreciseQueryInputInfo> implements IInputInfoService{
	@Autowired
    private DaoService daoService;
	public void write(PreciseQueryInputInfo inputInfo){
		daoService.create(inputInfo);
	}
}
