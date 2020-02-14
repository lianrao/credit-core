package com.wanda.credit.ds.dao.iface.wangshu;

import java.util.List;

import com.wanda.credit.base.service.IBaseService;
import com.wanda.credit.ds.dao.domain.zhongshu.ZS_CityQuery_Result;

public interface IWSCityQueryResultService extends IBaseService<ZS_CityQuery_Result>{
	/**
	 * 批量保存城市信息
	 * @param score
	 */
	void batchSave(List<ZS_CityQuery_Result> result);
}
