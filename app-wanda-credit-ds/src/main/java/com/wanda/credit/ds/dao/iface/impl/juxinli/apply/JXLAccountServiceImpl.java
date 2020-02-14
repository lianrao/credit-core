package com.wanda.credit.ds.dao.iface.impl.juxinli.apply;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.base.service.impl.BaseServiceImpl;
import com.wanda.credit.ds.dao.domain.juxinli.apply.ApplyAccountPojo;
import com.wanda.credit.ds.dao.iface.juxinli.apply.IJXLAccountService;
@Service
@Transactional
public class JXLAccountServiceImpl extends BaseServiceImpl<ApplyAccountPojo> implements IJXLAccountService {

	
}
