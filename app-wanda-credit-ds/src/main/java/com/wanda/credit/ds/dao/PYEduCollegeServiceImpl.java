package com.wanda.credit.ds.dao;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.base.service.impl.BaseServiceImpl;
import com.wanda.credit.ds.dao.domain.Py_edu_college;
import com.wanda.credit.ds.dao.iface.pengyuan.IPYEduCollegeService;
@Service
@Transactional
public class PYEduCollegeServiceImpl extends BaseServiceImpl<Py_edu_college> implements IPYEduCollegeService {


}
