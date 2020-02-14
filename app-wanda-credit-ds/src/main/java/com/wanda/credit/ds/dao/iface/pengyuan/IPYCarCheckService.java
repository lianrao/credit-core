package com.wanda.credit.ds.dao.iface.pengyuan;

import java.util.List;

import com.wanda.credit.base.service.IBaseService;
import com.wanda.credit.ds.dao.domain.pengyuan.Py_car_check;

public interface IPYCarCheckService extends IBaseService<Py_car_check>{

	/**
	 * 查询车辆信息
	 * @param score
	 */
	List<Py_car_check> inCached(String name, String cardNo, String licenseNo,String carType);

	/**
	 * 批量保存车辆查询信息
	 * @param score
	 */
	void batchSave(List<Py_car_check> result);

}
