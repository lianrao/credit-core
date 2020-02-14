package com.wanda.credit.ds.client.juxinli.service;

import com.wanda.credit.ds.dao.domain.juxinli.housefund.HouseRawDataBasicPojo;


public interface IJXLHouseRawDataDealService {

	/**
	 * @param rawDataPojo
	 * @param requestId
	 */
	public void addHouseRawData(HouseRawDataBasicPojo rawDataPojo, String requestId) throws Exception;
	
	
	public HouseRawDataBasicPojo queryRawData(String requestId);


	/**
	 * @param requestId
	 * @return
	 */
	public boolean inCache(String requestId);
	
	

}
