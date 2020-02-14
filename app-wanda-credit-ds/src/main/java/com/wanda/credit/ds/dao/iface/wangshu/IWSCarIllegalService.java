package com.wanda.credit.ds.dao.iface.wangshu;

import java.util.List;

import com.wanda.credit.base.service.IBaseService;
import com.wanda.credit.ds.dao.domain.zhongshu.ZS_CarIllegal_Result;

public interface IWSCarIllegalService extends IBaseService<ZS_CarIllegal_Result>{
	/**
	 * 批量保存车辆信息
	 * @param score
	 */
	void batchSave(List<ZS_CarIllegal_Result> result);
}
