package com.wanda.credit.ds.client.juhe;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.ExceptionUtil;
import com.wanda.credit.base.util.RequestHelper;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.iface.IDataSourceRequestor;
/**
 * 手机携号转网
 * @author liunan
 */
@DataSourceClass(bindingDataSourceId="ds_juhe_mobile_switch")
public class JuHeSwitchnetworkRequestor extends BaseJuheDSRequestor
implements IDataSourceRequestor {
	private final  Logger logger = LoggerFactory.getLogger(JuHeSwitchnetworkRequestor.class);
	@Autowired
	private IPropertyEngine propertyEngine;
	@Override
	public Map<String, Object> request(String trade_id, DataSource ds){
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		Map<String, Object> rets = new HashMap<String, Object>();
		DataSourceLogVO logObj = new DataSourceLogVO();
		Map<String, Object> reqparam = new HashMap<String, Object>();
		String resource_tag = Conts.TAG_SYS_ERROR;
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));
		try{
			String url = propertyEngine.readById("ds_juhe_mobileswitch_url");
			logObj.setDs_id(ds.getId());
			logObj.setReq_url(url);
			logObj.setIncache("0");
			logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);

			String mobile = ParamUtil.findValue(ds.getParams_in(), "mobile").toString(); //手机号
			reqparam.put("mobile", mobile);	
			
			Map<String, String> req_params = new HashMap<>();
			req_params.put("key", propertyEngine.readById("ds_juhe_mobileswitch_key"));
			req_params.put("mobile", mobile);
			Map<String, String> header = new HashMap<>();
			Map<String,Object> rspDataMap = RequestHelper.doGetRetFull(url, req_params, header, true,null,
					"UTF-8");
			
			logger.info("{} 返回数据 {}",trade_id, rspDataMap.get("res_body_str"));
			
			JSONObject rspData = JSONObject.parseObject(String.valueOf(rspDataMap.get("res_body_str")));
			
			if (StringUtil.isEmpty(rspData) || "null".equals(rspData)) {
				logger.info("{} http请求返回结果为空" , prefix);
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_TIMEOUT);
				logObj.setState_msg("请求超时");
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "数据源处理时异常!");
				return rets;
			}
			JSONObject result = rspData.getJSONObject("result");
			logObj.setBiz_code1(rspData.getString("error_code") + "-" + rspData.getString("reason"));
			
			if(isSuccess(rspData)){
				resource_tag = Conts.TAG_TST_SUCCESS;
				logObj.setBiz_code2(result.getString("orderid"));
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
				logger.info("{} 处理成功" , prefix);
			}else if("238002".equals(rspData.getString("error_code"))){
				logger.info("{} 参数错误" , prefix);
				resource_tag = Conts.TAG_SYS_ERROR;
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_PARAM_INVALID);
				rets.put(Conts.KEY_RET_MSG, "传入参数有误");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				return rets;
			}else if("238003".equals(rspData.getString("error_code"))){
				logger.info("{} 查无记录" , prefix);
				resource_tag = Conts.TAG_SYS_ERROR;
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_ONLINE_FALI03);
				rets.put(Conts.KEY_RET_MSG, "查无记录");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				return rets;
			}else{
				resource_tag = Conts.TAG_SYS_ERROR;
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_POLICE_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "运营商调用失败!");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				return rets;
			}     
            
			rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
			rets.put(Conts.KEY_RET_DATA, buildResult(result));
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
			rets.put(Conts.KEY_RET_MSG, "采集成功!");			
		}catch(Exception ex){
			resource_tag = Conts.TAG_SYS_ERROR;
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION.getRet_msg());
			logger.error(prefix+" 数据源处理时异常：{}",ex);
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
			executorDtoService.writeDsLog(trade_id,logObj,false);
			executorDtoService.writeDsParamIn(trade_id, reqparam, logObj,false);
		}
		return rets;
	}
	public JSONObject buildResult(JSONObject json){
		JSONObject result = new JSONObject();
		result.put("old_isp", MobileWorkMap.get(json.getString("old_isp")));
		result.put("new_isp", MobileWorkMap.get(json.getString("new_isp")));
		result.put("result", json.getString("res"));
		return result;
	}
	private static Map<String, String> MobileWorkMap = new HashMap<String, String>();
	static {
		MobileWorkMap.put("T", "CTCC");//电信
		MobileWorkMap.put("M", "CMCC");//移动
		MobileWorkMap.put("U", "CUCC");//联通
	}
}
