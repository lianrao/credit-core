package com.wanda.credit.ds.dao.iface.huifa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.base.dao.DaoService;
import com.wanda.credit.base.service.impl.BaseServiceImpl;
import com.wanda.credit.ds.dao.domain.huifa.OldLaiInfo;
import com.wanda.credit.ds.dao.iface.huifa.inter.IOldLaiInfoService;

@Service
@Transactional
public class OldLaiInfoService extends BaseServiceImpl<OldLaiInfo> implements IOldLaiInfoService{
	@Autowired
    private DaoService daoService;
	public void write(OldLaiInfo oldLaiInfo){
		daoService.create(oldLaiInfo);
   }
}
