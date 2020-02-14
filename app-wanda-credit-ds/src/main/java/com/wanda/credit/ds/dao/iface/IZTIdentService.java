package com.wanda.credit.ds.dao.iface;

import java.util.List;

import com.wanda.credit.base.service.IBaseService;
import com.wanda.credit.ds.dao.domain.ZT_Ident_Result;

public interface IZTIdentService extends IBaseService<ZT_Ident_Result>{
	/**
	 * 批量保存卡号查询信息 
	 * @param score
	 */
	void batchSave(List<ZT_Ident_Result> score);
}
