package com.wanda.credit.ds.client.juxinli.service;

import com.wanda.credit.ds.dao.domain.juxinli.mobile.MobileMemberPojo;
import com.wanda.credit.ds.dao.domain.juxinli.mobile.MobileRawDataOwnerPojo;

public interface IJXLMobileRawDataService {
	
	public void saveRawData(MobileMemberPojo memberPojo, String requestId)  throws Exception;
	
	public MobileRawDataOwnerPojo loadRawData(String requestId) throws Exception;

}
