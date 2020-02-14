package com.wanda.credit.ds.dao.iface;

import java.util.List;

import com.wanda.credit.base.service.IBaseService;
import com.wanda.credit.ds.dao.domain.YT_Auth_Result;

public interface IYTAuthService extends IBaseService<YT_Auth_Result>{
	/**
	 * 缓存查询一致性 
	 * @param score
	 */
	boolean inCached(String name, String cardNo);
	/**
	 * 批量保存卡号查询信息
	 * @param score
	 */
	void batchSave(List<YT_Auth_Result> result);
}
