package com.wanda.credit.ds.dao;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.base.service.impl.BaseServiceImpl;
import com.wanda.credit.ds.dao.domain.pengyuan.Py_pho_status;
import com.wanda.credit.ds.dao.iface.pengyuan.IPYPhoStatusService;
@Service
@Transactional
public class PYPhoStatusService extends BaseServiceImpl<Py_pho_status> implements IPYPhoStatusService{

}
