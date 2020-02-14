package com.wanda.credit.ds.dao.iface.impl.juxinli.report;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.base.service.impl.BaseServiceImpl;
import com.wanda.credit.ds.dao.domain.juxinli.report.CellBehaviorPojo;
import com.wanda.credit.ds.dao.iface.juxinli.report.IJXLReportCellBehaviorService;
@Service
@Transactional
public class JXLReportCellBehaviorServiceImpl extends BaseServiceImpl<CellBehaviorPojo>
		implements IJXLReportCellBehaviorService {

	public List<String> queryUniquePhoneNum(String requestId) {
		
		String hql = "select distinct(cb.phone_num) from CellBehaviorPojo cb where cb.requestId =:requestId group by cb.phone_num";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("requestId", requestId);
		List<String> phoneNums = daoService.findByHQL(hql, params);
		
		return phoneNums;
	}

	public List<CellBehaviorPojo> queryByPhoneNumAndRequestId(String requestId,String phoneNum) {
		
		String hql = "from CellBehaviorPojo p where p.requestId =:requestId and p.phone_num =:phone_num";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("requestId", requestId);
		params.put("phone_num", phoneNum);
		List<CellBehaviorPojo> pojoList = daoService.findByHQL(hql, params);
		return pojoList;
	}
	
}
