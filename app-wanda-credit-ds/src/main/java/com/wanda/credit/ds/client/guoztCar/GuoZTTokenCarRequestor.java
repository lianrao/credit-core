package com.wanda.credit.ds.client.guoztCar;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.apache.commons.lang.StringUtils;

import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.ExceptionUtil;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.dao.iface.IGuoZTBlackCarService;
import com.wanda.credit.ds.iface.IDataSourceRequestor;
@DataSourceClass(bindingDataSourceId="ds_guozt_cartoken")
public class GuoZTTokenCarRequestor extends BaseGuoZTCarSourcesRequestor
		implements IDataSourceRequestor {
	private final  Logger logger = LoggerFactory.getLogger(GuoZTTokenCarRequestor.class);
	
	@Autowired
	private IGuoZTBlackCarService blackCarService;
	@Autowired
	public IPropertyEngine propertyEngine;
	@Override
	public Map<String, Object> request(String trade_id, DataSource ds){
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		String guozt_url = propertyEngine.readById("ds_guoztCar_url");
		TreeMap<String, Object> retdata = new TreeMap<String, Object>();
		Map<String, Object> rets = null;
		DataSourceLogVO logObj = new DataSourceLogVO();
		Map<String, Object> reqparam = new HashMap<String, Object>();
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));//log请求时间
		String resource_tag = Conts.TAG_TST_FAIL;
		try{			
			logger.info("{}国政通租车token调用开始...", new String[] { prefix });
			logObj.setDs_id("ds_guozt_cartoken");
			rets = new HashMap<String, Object>();
	 		String trade_ids = ParamUtil.findValue(ds.getParams_in(), paramIds[0]).toString();   //姓名 
			reqparam.put("trade_ids", trade_ids);			
			logObj.setReq_url(guozt_url);
			logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
			String token = null;
			token = getToken(prefix,guozt_url,true);
			if(!StringUtils.isNotEmpty(token)){//如果获取token失败，返回失败
				logger.warn("{}令牌获取失败!", new String[] { prefix });
				logObj.setState_msg("token获取失败");
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_GUOZT_CAR_FAIL);
				rets.put(Conts.KEY_RET_MSG, "租车黑名单信息查询失败!");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				return rets;
			}else{
				logger.warn("{}令牌获取成功!", new String[] { prefix });
				resource_tag = Conts.TAG_TST_SUCCESS;
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
				blackCarService.updateToken(token, trade_ids);
				retdata.put("token", token);
			}
		}catch(Exception ex){
			resource_tag = Conts.TAG_SYS_ERROR;
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, "数据源处理时异常! 详细信息:"+ex.getMessage());
			logger.error("{} 数据源处理时异常：{}",prefix,ExceptionUtil.getTrace(ex));
			if (ExceptionUtil.isTimeoutException(ex)) {
				resource_tag = Conts.TAG_SYS_TIMEOUT;
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_TIMEOUT);
			} else {
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
				logObj.setState_msg("数据源处理时异常! 详细信息:" + ex.getMessage());
			}
			rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
		}finally {
			logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
			logObj.setTag(resource_tag);
			DataSourceLogEngineUtil.writeLog(trade_id, logObj);
			DataSourceLogEngineUtil.writeParamIn(trade_id, reqparam, logObj);
		}
		rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
		rets.put(Conts.KEY_RET_DATA, retdata);
		rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
		rets.put(Conts.KEY_RET_MSG, "采集成功!");
		return rets;
	}
}
