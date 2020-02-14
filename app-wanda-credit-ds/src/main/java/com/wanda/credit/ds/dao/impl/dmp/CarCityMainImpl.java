package com.wanda.credit.ds.dao.impl.dmp;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.base.service.impl.BaseServiceImpl;
import com.wanda.credit.ds.dao.domain.dmpCar.DMP_CarCityMain;
import com.wanda.credit.ds.dao.iface.dmp.ICarCityMain;

@Service
@Transactional
public class CarCityMainImpl extends BaseServiceImpl<DMP_CarCityMain> implements ICarCityMain {

	private static final long serialVersionUID = -3980145778504782396L;
}
