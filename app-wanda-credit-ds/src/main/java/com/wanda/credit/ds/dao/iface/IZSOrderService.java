package com.wanda.credit.ds.dao.iface;

import java.util.Map;

import com.wanda.credit.base.service.IBaseService;
import com.wanda.credit.ds.dao.domain.zhongshu.ZS_Order;

public interface IZSOrderService  extends IBaseService<ZS_Order>{
	public Map<String, Object> inCached(String key,String acct_id,int months);
	public Map<String, Object> inCachedDs(String key,int months);
	public Map<String, Object> inCacheForQueryByPositionCachedDs(String key,String acct_id);
	
	public Map<String, Object> inCachedProd(String key,String key_new,String acct_id);
	public Map<String, Object> inCachedDsNew(String key,String key_new);
}
