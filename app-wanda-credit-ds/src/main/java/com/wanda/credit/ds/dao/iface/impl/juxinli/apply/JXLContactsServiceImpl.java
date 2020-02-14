package com.wanda.credit.ds.dao.iface.impl.juxinli.apply;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.base.service.impl.BaseServiceImpl;
import com.wanda.credit.ds.dao.domain.juxinli.apply.ApplyContactPojo;
import com.wanda.credit.ds.dao.iface.juxinli.apply.IJXLContactsService;
@Service
@Transactional
public class JXLContactsServiceImpl extends BaseServiceImpl<ApplyContactPojo> implements
		IJXLContactsService {
	
}
