package com.wanda.credit.ds.dao.iface;

import java.util.List;
import java.util.Map;

import com.wanda.credit.base.service.IBaseService;
import com.wanda.credit.ds.dao.domain.Guozt_Black_Car_Result;


public interface IGuoZTBlackCarService extends IBaseService<Guozt_Black_Car_Result>{
	/**
	 * 批量保存卡号查询信息
	 * @param score
	 */
	void batchSave(List<Guozt_Black_Car_Result> result);
	
	/**
	 * 更新token值
	 * @param score
	 */
	void updateToken(String tokenStr,String trade_id);
	/**
	 * 获取token值
	 * @param score
	 */
	String getTokenSql(String trade_id);
}
