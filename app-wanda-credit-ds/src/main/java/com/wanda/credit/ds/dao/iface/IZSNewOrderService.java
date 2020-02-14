package com.wanda.credit.ds.dao.iface;

import java.util.Map;

import com.wanda.credit.base.service.IBaseService;
import com.wanda.credit.ds.dao.domain.zhongshunew.ZS_Order;

public interface IZSNewOrderService  extends IBaseService<ZS_Order>{
	public Map<String, Object> inCached(ZS_Order order); //,String months); 
	public Map<String, Object> inCachedDs(ZS_Order order); //,String months); 
	public void saveCorpInfo(String trade_id, String ds_id, String content,String name, String creditcode,String regno,String orgcode);
//	public Map<String, Object> inCacheForQueryByPositionCachedDs(String key,String acct_id);
//	
//	public Map<String, Object> inCachedProd(String key,String key_new,String acct_id);
//	public Map<String, Object> inCachedDsNew(String key,String key_new);
}
