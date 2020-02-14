package com.wanda.credit.ds.dao.iface;

import java.util.List;
import java.util.Map;

import com.wanda.credit.base.service.IBaseService;
import com.wanda.credit.ds.dao.domain.Zhongan_Check_Result;

public interface IZhonganService extends IBaseService<Zhongan_Check_Result>{
	/**
	 * 缓存信息查询
	 * @param score
	 */
	public Map<String, Object> inCached(String name, String cardNo);
	
	public boolean inCachedCount(String name, String cardNo);
	/**
	 * 批量保存卡号查询信息
	 * @param score
	 */
	void batchSave(List<Zhongan_Check_Result> result);
}
