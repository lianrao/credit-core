package com.wanda.credit.ds.client.juxinli.service;

import com.wanda.credit.ds.client.juxinli.bean.mobile.GetTokenReq;

public interface IJXLEBusiApplyService {
	
	public void saveApplyData(GetTokenReq tokenReq,String requestId,String token) throws Exception;

}
