package com.wanda.credit.ds.dao.iface.yidao;

import java.util.List;

import com.wanda.credit.base.service.IBaseService;
import com.wanda.credit.ds.dao.domain.yidao.Yidao_bank_result;

public interface IYidaoBankService extends IBaseService<Yidao_bank_result>{
	/**
	 * 批量保存银行卡查询信息
	 * @param score
	 */
	void batchSave(List<Yidao_bank_result> result);
}
