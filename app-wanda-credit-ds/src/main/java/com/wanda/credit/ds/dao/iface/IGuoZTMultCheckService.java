package com.wanda.credit.ds.dao.iface;

import java.util.List;

import com.wanda.credit.base.service.IBaseService;
import com.wanda.credit.ds.dao.domain.Guozt_Mult_Check_Result;


public interface IGuoZTMultCheckService extends IBaseService<Guozt_Mult_Check_Result>{
	/**
	 * 缓存查询一致性 
	 * @param score
	 */
	Guozt_Mult_Check_Result inCached(String name, String cardNo);
	/**
	 * 缓存查询数据是否存在
	 * @param score
	 */
	boolean inCachedMult(String name, String cardNo,int days);
	/**
	 * 批量保存卡号查询信息
	 * @param score
	 */
	void batchSave(List<Guozt_Mult_Check_Result> result);
}
