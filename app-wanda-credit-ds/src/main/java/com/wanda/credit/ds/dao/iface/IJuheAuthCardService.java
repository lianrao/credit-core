package com.wanda.credit.ds.dao.iface;

import java.util.Map;

import com.alibaba.fastjson.JSONObject;

/**
 * @description  
 * @author wuchsh 
 * @version 1.0
 * @createdate 2017年5月23日 下午2:15:47 
 *  
 */
public interface IJuheAuthCardService {

	void saveAuthCard3(String tradeId,String name, String cardNo, String cardId, JSONObject object) throws Exception;

	void saveAuthCard4(String tradeId,String name, String cardNo, String cardId, String phone,
			JSONObject object) throws Exception;
	
	public Map<String, Object> queryCache(String prefix,String name, String cardNo,
			String cardId, String mobile, int cacheTime);

}
