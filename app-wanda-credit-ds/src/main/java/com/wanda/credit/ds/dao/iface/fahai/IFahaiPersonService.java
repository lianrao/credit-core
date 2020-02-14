package com.wanda.credit.ds.dao.iface.fahai;

import com.alibaba.fastjson.JSONArray;
import com.wanda.credit.base.service.IBaseService;
import com.wanda.credit.ds.dao.domain.Nciic_Check_Result;

public interface IFahaiPersonService extends IBaseService<Nciic_Check_Result>{
	
	/**
	 * 批量保存卡号查询信息
	 * @param score
	 */
	void batchSave(JSONArray result,String trade_id);
	
}
