package com.wanda.credit.ds.dao.iface;

import java.util.List;
import java.util.Map;

import com.wanda.credit.base.service.IBaseService;
import com.wanda.credit.ds.dao.domain.Nciic_Check_Result;

public interface IDiDiWanDaService extends IBaseService<Nciic_Check_Result>{
	/**
	 * 查询风险清单 
	 * @param score
	 */
	List<Map<String, Object>> queryCrime(String cardNo, String trade_ids);
	/**
	 * 查询白名单
	 * @param score
	 */
	List<Map<String, Object>> queryWhite(String cardNo, String trade_ids);
}
