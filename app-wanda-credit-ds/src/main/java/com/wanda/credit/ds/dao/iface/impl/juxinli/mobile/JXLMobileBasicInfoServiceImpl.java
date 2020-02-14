package com.wanda.credit.ds.dao.iface.impl.juxinli.mobile;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanda.credit.base.service.impl.BaseServiceImpl;
import com.wanda.credit.ds.dao.domain.juxinli.mobile.MobileRawDataOwnerPojo;
import com.wanda.credit.ds.dao.iface.juxinli.mobile.IJXLMobileBasicInfoService;
@Service
@Transactional
public class JXLMobileBasicInfoServiceImpl extends BaseServiceImpl<MobileRawDataOwnerPojo> implements
		IJXLMobileBasicInfoService {

	public MobileRawDataOwnerPojo queryByRequestIdAndCode(String requestId,String errorCode) {
		
		String hql = "FROM MobileRawDataOwnerPojo WHERE requestId =:requestId AND error_code =:errorCode";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("requestId", requestId);
		params.put("errorCode", errorCode);
		return daoService.findOneByHQL(hql, params);
	}
	
}
