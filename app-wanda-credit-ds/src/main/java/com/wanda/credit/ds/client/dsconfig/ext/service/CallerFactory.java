package com.wanda.credit.ds.client.dsconfig.ext.service;

import com.wanda.credit.dsconfig.enums.ServiceType;

/**
 * @description  
 * @author wuchsh 
 * @version 1.0
 * @createdate 2017年3月8日 上午9:40:00 
 *  
 */
public class CallerFactory {
  public static ICaller getCaller(ServiceType type){
	   if(type.equals(ServiceType.api)){
		   return DefaultApiCaller.getInstance();
	   }else if(type.equals(ServiceType.ds)){
		   return DefaultDsCaller.getInstance();
	   }else{
		   //TODO
		   return null;
	   }
   } 
}
