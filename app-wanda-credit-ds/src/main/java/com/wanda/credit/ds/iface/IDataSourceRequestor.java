package com.wanda.credit.ds.iface;

import java.util.Map;

import com.wanda.credit.api.dto.DataSource;

public interface IDataSourceRequestor {
	/**
	 * 参数校验
	 * @param trade_id
	 * @param ds
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> valid(String trade_id, DataSource ds); 
	/**
	 * 数据源请求
	 * @param trade_id
	 * @param ds
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> request(String trade_id, DataSource ds);
}
