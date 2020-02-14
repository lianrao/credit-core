package com.wanda.credit.ds.dao.iface;

import java.util.List;

import com.wanda.credit.base.service.IBaseService;
import com.wanda.credit.ds.dao.domain.WanDa_Auth_Result;

public interface IWanDaAuthService extends IBaseService<WanDa_Auth_Result>{
	/**
	 * 批量保存卡号查询信息
	 * @param score
	 */
	void batchSave(List<WanDa_Auth_Result> result);
}
