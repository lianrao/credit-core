package com.wanda.credit.ds.dao.iface.xiaoshi;


import java.util.List;
import com.wanda.credit.ds.dao.domain.xiaoshi.XiaoShi_visit_flow;

public interface IXiaoshiVisitFlowService{
	/**
	 * 批量保存卡号查询信息
	 * @param score
	 */
	void batchSave(XiaoShi_visit_flow result);
}
