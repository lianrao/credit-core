package com.wanda.credit.ds.dao.iface.yitu;

import java.util.List;

import com.wanda.credit.base.service.IBaseService;
import com.wanda.credit.ds.dao.domain.yitu.Yitu_auth_option;

public interface IYTAuthOptionService extends IBaseService<Yitu_auth_option>{
	/**
	 * 批量保存卡号查询信息
	 * @param score
	 */
	void batchSave(List<Yitu_auth_option> result);
}
