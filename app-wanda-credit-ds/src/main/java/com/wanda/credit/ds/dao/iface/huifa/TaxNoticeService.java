package com.wanda.credit.ds.dao.iface.huifa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.base.dao.DaoService;
import com.wanda.credit.base.service.impl.BaseServiceImpl;
import com.wanda.credit.ds.dao.domain.huifa.TaxNotice;
import com.wanda.credit.ds.dao.iface.huifa.inter.ITaxNoticeService;

@Service
@Transactional
public class TaxNoticeService extends BaseServiceImpl<TaxNotice> implements ITaxNoticeService{
	@Autowired
    private DaoService daoService;
	public void write(TaxNotice  taxNotice){
		daoService.create( taxNotice);
   }
}
