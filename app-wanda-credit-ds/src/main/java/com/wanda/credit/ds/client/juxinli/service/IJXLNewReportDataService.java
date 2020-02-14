package com.wanda.credit.ds.client.juxinli.service;

import com.wanda.credit.ds.client.juxinli.bean.reportNew.po.ReportData;


public interface IJXLNewReportDataService {

	/**
	 * 保存报告数据
	 * @param reportData
	 * @param requestId
	 * @param trade_id
	 */
	public void addNewReport(ReportData reportData, String requestId, String trade_id);

	/**
	 * @param requestId
	 * @throws Exception 
	 */
	public ReportData loadCacheData(String requestId) throws Exception;
	
	
}
