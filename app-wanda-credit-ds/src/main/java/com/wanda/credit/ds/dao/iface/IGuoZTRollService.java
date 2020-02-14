package com.wanda.credit.ds.dao.iface;

import java.util.List;

import com.wanda.credit.base.service.IBaseService;
import com.wanda.credit.ds.dao.domain.Guozt_Roll_check_result;

public interface IGuoZTRollService extends IBaseService<Guozt_Roll_check_result> {
	
	/**
	 * 批量保存就业潜力查询信息
	 * @param score
	 */
	void batchSave(List<Guozt_Roll_check_result> result);
	/**
	 * 查询学院评分
	 * @param score
	 */
	public String findScore(String collage);
}
