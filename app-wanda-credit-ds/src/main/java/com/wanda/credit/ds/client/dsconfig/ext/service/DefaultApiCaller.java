package com.wanda.credit.ds.client.dsconfig.ext.service;
/**
 * @description  
 * @author wuchsh 
 * @version 1.0
 * @createdate 2017年3月8日 上午9:46:24 
 *  
 */
public class DefaultApiCaller implements ICaller{
	private static DefaultApiCaller instance = null;
	public static ICaller getInstance() {
		if(instance == null){
			instance = new DefaultApiCaller();
		}
		return instance;
	}
	
	@Override
	public Object call(CallContext config) {
		return null;
	}

	

}
