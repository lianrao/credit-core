package com.wanda.credit.ds.client.dsconfig.ext.service;

import com.wanda.credit.ds.client.dsconfig.ext.expression.RemoteCallCondExpr;

/**
 * @description  
 * @author wuchsh 
 * @version 1.0
 * @createdate 2017年3月8日 上午9:40:16 
 *  
 */
public interface ICaller {
    Object call(CallContext config);
}
