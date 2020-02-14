package com.wanda.credit.ds.dao.iface.govInfo;

import java.util.List;

import com.wanda.credit.base.domain.BaseDomain;
import com.wanda.credit.base.service.IBaseService;
import com.wanda.credit.ds.dao.domain.govInfo.Gov_basicinfo_result;

public interface IGovInfoService extends IBaseService<Gov_basicinfo_result>{
	/**
	 * 批量保存基本信息
	 * @param <T>
	 * @param score
	 */
	<T extends BaseDomain> void batchInfoSave(List<T> result);
}
