package com.wanda.credit.ds.client.bingjian;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.ExceptionUtil;
import com.wanda.credit.base.util.IPUtils;
import com.wanda.credit.base.util.JsonFilter;
import com.wanda.credit.base.util.RequestHelper;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.client.ppxin.BasePPXDSRequestor;
import com.wanda.credit.ds.iface.IDataSourceRequestor;
/**
 * 冰鉴商标查询
 * */
@DataSourceClass(bindingDataSourceId="ds_bingjian_brand")
public class BJBrandRequestor extends BasePPXDSRequestor implements IDataSourceRequestor {
	private Logger logger = LoggerFactory.getLogger(BJBrandRequestor.class);

	@Autowired
	private IPropertyEngine propertyEngine;
	@Override
	public Map<String, Object> request(String trade_id, DataSource ds) {
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		logger.info("{} 冰鉴商标查询开始...", prefix);
		long start = System.currentTimeMillis();
		String bingjian_auth_url = propertyEngine.readById("ds_bingjian_auth_url");
		String bingjian_brand_url = propertyEngine.readById("ds_bingjian_brand_url");
		String bingjian_username = propertyEngine.readById("ds_bingjian_username");
		String bingjian_password = propertyEngine.readById("ds_bingjian_password");
		int time_out = Integer.valueOf(propertyEngine.readById("sys_http_send_timeout"));
		//初始化对象
		Map<String, Object> rets = new HashMap<String, Object>();	
		Map<String, Object> reqparam = new HashMap<String, Object>();
		//计费标签
		String resource_tag = Conts.TAG_SYS_ERROR;
		//交易日志信息数据
		DataSourceLogVO logObj = new DataSourceLogVO(trade_id);
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));
		logObj.setDs_id(ds.getId());
		logObj.setReq_url(bingjian_brand_url);
		logObj.setBiz_code3(IPUtils.getLocalIP());
		logObj.setIncache("0");
		logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
		logObj.setState_msg("交易失败");
		try{
			logger.info("{} 开始解析传入的参数" , prefix);			
			String keyword = ParamUtil.findValue(ds.getParams_in(), paramIds[0]).toString();
			String dataType = "";
			if(ParamUtil.findValue(ds.getParams_in(), "dataType")!=null){
				dataType = ParamUtil.findValue(ds.getParams_in(), "dataType").toString();
			}
			String intCls = "";
			if(ParamUtil.findValue(ds.getParams_in(), "intCls")!=null){
				intCls = ParamUtil.findValue(ds.getParams_in(), "intCls").toString();
			}
			String pageSize = "";
			if(ParamUtil.findValue(ds.getParams_in(), "pageSize")!=null){
				pageSize = ParamUtil.findValue(ds.getParams_in(), "pageSize").toString();
			}
			String pageIndex = "";
			if(ParamUtil.findValue(ds.getParams_in(), "pageIndex")!=null){
				pageIndex = ParamUtil.findValue(ds.getParams_in(), "pageIndex").toString();
			}
			logger.info("{} 解析传入的参数成功" , prefix);
			reqparam.put("keyword", keyword);
			
			String tokenId = GetBJTokenAuth.getTokenId(trade_id, bingjian_auth_url, bingjian_username, bingjian_password, time_out);
			Map<String, String> params =new HashMap<String,String>();
			params.put("keyword", keyword);
	        params.put("token_id", tokenId);
	        if(!StringUtil.isEmpty(dataType)){
	        	params.put("dataType", dataType);
	        }
	        if(!StringUtil.isEmpty(intCls)){
	        	params.put("intCls", intCls);
	        }
	        if(!StringUtil.isEmpty(pageSize)){
	        	params.put("pageSize", pageSize);
	        }
	        if(!StringUtil.isEmpty(pageIndex)){
	        	params.put("pageIndex", pageIndex);
	        }
	        String httpresult = RequestHelper.doGet(bingjian_brand_url, params, true);
        	logger.info("{} 冰鉴商标查询成功:{}", prefix,httpresult);
        	if(StringUtils.isEmpty(httpresult)){
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_POLICE_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "数据源厂商返回异常!");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				logger.warn("{} 创蓝空号厂商返回异常! ",prefix);
				return rets;
			}
        	JSONObject json = JSONObject.parseObject(httpresult);
        	if("00".equals(json.getString("status"))){
        		resource_tag = Conts.TAG_TST_SUCCESS;
        		JSONObject result = new JSONObject();
        		JSONArray array = JSONObject.parseArray(json.getString("Result"));
        		JSONArray arrayresult = new JSONArray();
        		for(Object tmp:array){
        			JSONObject obj = (JSONObject) tmp;
        			arrayresult.add(JsonFilter.getJsonKeys(obj, "Id,ImageUrl,HasImage"));
        		}
        		result.put("Paging", json.get("Paging"));
        		result.put("GroupItems", json.get("GroupItems"));
        		result.put("Result", arrayresult);
        		//拼装返回信息
    			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
    			rets.put(Conts.KEY_RET_MSG, "请求成功");
    			rets.put(Conts.KEY_RET_DATA, result);
    			rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
    			//记录日志信息
    			logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
    			logObj.setState_msg("交易成功");
        	}else{
        		rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_POLICE_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "数据源厂商返回异常!");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				logger.warn("{} 创蓝空号厂商返回异常! ",prefix);
				return rets;
        	}
			
		}catch(Exception e){
			resource_tag = Conts.TAG_SYS_ERROR;
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, "数据源处理时异常!");
			logger.error("{} 数据源处理时异常：{}",prefix,ExceptionUtil.getTrace(e));
			if (ExceptionUtil.isTimeoutException(e)) {
				resource_tag = Conts.TAG_SYS_TIMEOUT;
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_TIMEOUT);
			} else {
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
				logObj.setState_msg("数据源处理时异常! 详细信息:" + e.getMessage());
			}
			rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
		}finally{
			//保存日志信息
			logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
			logObj.setTag(resource_tag);
			logger.info("{} 保存ds Log开始..." ,prefix);
			executorDtoService.writeDsLog(trade_id,logObj,true);
			executorDtoService.writeDsParamIn(trade_id, reqparam, logObj,true);
			logger.info("{} 保存ds Log成功" ,prefix);
		}
		logger.info("{} 创蓝空号End，交易时间为(ms):{}",prefix ,(System.currentTimeMillis() - start));
		return rets;
	}
}
