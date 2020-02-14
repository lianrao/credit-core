package com.wanda.credit.ds.dao.iface.yidao;

import java.util.List;

import com.wanda.credit.base.service.IBaseService;
import com.wanda.credit.ds.dao.domain.yidao.Yidao_driver_result;

public interface IYidaoDriverService extends IBaseService<Yidao_driver_result>{
	/**
	 * 批量保存银行卡查询信息
	 * @param score
	 */
	void batchSave(List<Yidao_driver_result> result);
}
