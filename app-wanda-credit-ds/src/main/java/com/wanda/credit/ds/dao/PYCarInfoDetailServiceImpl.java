package com.wanda.credit.ds.dao;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.base.service.impl.BaseServiceImpl;
import com.wanda.credit.ds.dao.domain.pengyuan.PY_car_info_detail;
import com.wanda.credit.ds.dao.iface.pengyuan.IPYCarInfoDetailService;

@Service
@Transactional
public class PYCarInfoDetailServiceImpl extends BaseServiceImpl<PY_car_info_detail> implements IPYCarInfoDetailService {

}
