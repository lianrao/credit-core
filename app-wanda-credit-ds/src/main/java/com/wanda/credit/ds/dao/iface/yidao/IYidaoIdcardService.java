package com.wanda.credit.ds.dao.iface.yidao;

import java.util.List;

import com.wanda.credit.base.service.IBaseService;
import com.wanda.credit.ds.dao.domain.yidao.Yidao_Idcard_result;

public interface IYidaoIdcardService extends IBaseService<Yidao_Idcard_result>{
	/**
	 * 批量保存卡号查询信息
	 * @param score
	 */
	void batchSave(List<Yidao_Idcard_result> result);
}
