package com.wanda.credit.ds.dao;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.base.service.impl.BaseServiceImpl;
import com.wanda.credit.ds.dao.domain.pengyuan.Py_car_illegal;
import com.wanda.credit.ds.dao.iface.pengyuan.IPyCarIllegalService;

@Service
@Transactional
public class PyCarIllegalServiceImpl extends BaseServiceImpl<Py_car_illegal>
implements IPyCarIllegalService {

}
