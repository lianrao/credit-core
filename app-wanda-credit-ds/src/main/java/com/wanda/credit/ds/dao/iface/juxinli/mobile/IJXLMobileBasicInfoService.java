package com.wanda.credit.ds.dao.iface.juxinli.mobile;

import com.wanda.credit.base.service.IBaseService;
import com.wanda.credit.ds.dao.domain.juxinli.mobile.MobileRawDataOwnerPojo;

public interface IJXLMobileBasicInfoService extends IBaseService<MobileRawDataOwnerPojo> {

	/**
	 * 根据requestId和error_code查询是否有成功的缓存数据
	 * @param requestId
	 * @param errorCode
	 * @return
	 */
	public MobileRawDataOwnerPojo queryByRequestIdAndCode(String requestId,String errorCode);

}
