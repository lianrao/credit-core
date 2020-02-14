package com.wanda.credit.ds.dao.iface.juxinli.report;

import java.util.List;

import com.wanda.credit.base.service.IBaseService;
import com.wanda.credit.ds.dao.domain.juxinli.report.CellBehaviorPojo;

public interface IJXLReportCellBehaviorService extends IBaseService<CellBehaviorPojo> {

	/**
	 * 获取相同交易序列号对应的手机号（去重）
	 * @param requestId
	 * @return
	 */
	public List<String> queryUniquePhoneNum(String requestId);
	
	/**
	 * 根据交易序列号和手机号码获取数据
	 * @param requestId
	 * @param phoneNum
	 * @return
	 */
	public List<CellBehaviorPojo> queryByPhoneNumAndRequestId(String requestId,String phoneNum);
	
}
