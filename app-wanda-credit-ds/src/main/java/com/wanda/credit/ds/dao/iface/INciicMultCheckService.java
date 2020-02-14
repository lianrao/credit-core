package com.wanda.credit.ds.dao.iface;

import java.util.List;

import com.wanda.credit.base.service.IBaseService;
import com.wanda.credit.ds.dao.domain.Nciic_Mult_Check_Result;

public interface INciicMultCheckService extends IBaseService<Nciic_Mult_Check_Result>{
	/**
	 * 缓存查询一致性 
	 * @param score
	 */
	Nciic_Mult_Check_Result inCached(String name, String cardNo);
	/**
	 * 批量保存卡号查询信息
	 * @param score
	 */
	void batchSave(List<Nciic_Mult_Check_Result> result);
}
