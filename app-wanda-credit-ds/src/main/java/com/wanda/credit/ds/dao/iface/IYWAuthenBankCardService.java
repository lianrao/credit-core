package com.wanda.credit.ds.dao.iface;

import java.util.Map;

import com.alibaba.fastjson.JSONObject;

/**
 * @description  
 * @author wuchsh 
 * @version 1.0
 * @createdate 2016年9月29日 下午2:53:51 
 *  
 */
public interface IYWAuthenBankCardService {

	void addAuthenBackCard(Map<String, Object> contxt, JSONObject busiData);

}
