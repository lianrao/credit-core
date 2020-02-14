package com.wanda.credit.ds.dao.iface;


public interface IQHLogService{	
	public int queryLogOnCurrMonth(String ds_id,
			String encryCardNo)throws Exception;
	
	public void addNewLog(String trade_id,String ds_id,
			String name,String encryCardNo)throws Exception;
}
