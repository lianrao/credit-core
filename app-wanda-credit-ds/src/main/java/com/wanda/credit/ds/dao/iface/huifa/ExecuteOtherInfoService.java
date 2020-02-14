package com.wanda.credit.ds.dao.iface.huifa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.base.dao.DaoService;
import com.wanda.credit.base.service.impl.BaseServiceImpl;
import com.wanda.credit.ds.dao.domain.huifa.ExecuteOtherInfo;
import com.wanda.credit.ds.dao.iface.huifa.inter.IExecuteOtherInfoService;

@Service
@Transactional
public class ExecuteOtherInfoService extends BaseServiceImpl<ExecuteOtherInfo> implements IExecuteOtherInfoService{
	@Autowired
    private DaoService daoService;
	public void write(ExecuteOtherInfo executeOtherInfo){
		daoService.create(executeOtherInfo);
	}
}
