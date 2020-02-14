package com.wanda.credit.ds.dao.iface;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import com.wanda.credit.ds.dao.domain.qianhai.BlackListVO_2_0;

public interface IBlackListService_2_0{	
	public List<BlackListVO_2_0> addOneBlackList(String trade_id,
			JSONObject blackListJsn,Map<String,Object> ctx)throws Exception;
}
