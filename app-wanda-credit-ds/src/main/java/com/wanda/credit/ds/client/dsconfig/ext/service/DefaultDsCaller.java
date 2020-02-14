package com.wanda.credit.ds.client.dsconfig.ext.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.api.iface.IDataSourceService;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.client.dsconfig.ext.model.CallParam;
import com.wanda.credit.dsconfig.main.ResolveContext;

/**
 * @description  
 * @author wuchsh 
 * @version 1.0
 * @createdate 2017年3月8日 上午9:47:14 
 *  
 */
public class DefaultDsCaller implements ICaller{
	private final  Logger logger = LoggerFactory.getLogger(DefaultDsCaller.class);

	private static DefaultDsCaller instance = null;
	
	public static ICaller getInstance() {
		if(instance == null){
			instance = new DefaultDsCaller();
		}
		return instance;
	}
	
	@Override
	public Object call(final CallContext config) {
		final IDataSourceService dataSourceService = ResolveContext.getBean("dataSourceService");
		Map<String,Object> params= parseParams(config.getParams());
		final DataSource ds = buildDataSourceVO(config.getServiceId(),params);
		final Map<String,Object> modelData = ResolveContext.getModelData();
		final String tradeId = ResolveContext.getTradeId();
		try {
			if(!config.isSync()){
				AsyncExecutor.asynOperate(new Runnable() {
					public void run() {
						Map<String, Object> result = null;
						try {
							result = dataSourceService.fetch(tradeId, ds);
						} catch (Exception e) {
							logger.error("数据源调用异常",e);
						}
						modelData.put(config.getHandle(), result);
					}
				});
			}else{
				Map<String,Object> result = dataSourceService.fetch(ResolveContext.getTradeId(), ds);
				ResolveContext.setModelData(config.getHandle(), result);
			}
		} catch (Exception e) {
			logger.error("{} 数据源【 {}】调用异常", ResolveContext.getTradeId(),config.getServiceId());
			throw new RuntimeException(e);
		}
		return null;
	}
	
	private Map<String, Object> parseParams(List<CallParam> params) {
		Map<String,Object> paramsMap = new HashMap<String,Object>();
		if(CollectionUtils.isNotEmpty(params)){
			for(CallParam param : params){
				paramsMap.put(param.getName(), param.getValue());
			}
		}		
		return paramsMap;
	}

	private DataSource buildDataSourceVO(String ds_id,Map<String,Object> paramsMap) {
	    DataSource dsvo = new DataSource();
	    dsvo.setId(ds_id);
	    dsvo.setRefProdCode("");
	    try {
			dsvo.setParams_in(ParamUtil.convertParams(paramsMap));
		} catch (Exception e) {
			logger.error("{} 数据源【 {}】参数构造异常", ResolveContext.getTradeId(),ds_id);
			throw new RuntimeException(e);
		}
	    return dsvo;
	}
	

}
