package com.wanda.credit.ds.dao.iface;

import java.util.List;
import java.util.Map;

import com.wanda.credit.base.service.IBaseService;
import com.wanda.credit.ds.dao.domain.Nciic_Check_Result;

public interface IGuoZCollageService extends IBaseService<Nciic_Check_Result> {
	
	/**
	 * 获取学院信息
	 * @date 2017年1月10日 下午4:06:09
	 * @author liu.nan
	 * @param tradeId
	 * @return
	 */
	List<Map<String, Object>> getCollageByName(String name);

}
