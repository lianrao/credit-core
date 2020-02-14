package com.wanda.credit.ds.dao.iface;

import java.util.List;

import com.wanda.credit.base.service.IBaseService;
import com.wanda.credit.ds.dao.domain.ZT_Score_Result;

public interface IZTScoreService extends IBaseService<ZT_Score_Result>{
	/**
	 * 批量保存卡号查询信息
	 * @param score
	 */
	void batchSave(List<ZT_Score_Result> score);
}
