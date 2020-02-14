package com.wanda.credit.ds.client.qianhai;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.CommonUtil;
import com.wanda.credit.base.util.JsonFilter;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.iface.IDataSourceRequestor;


/**
 * @title 常贷客数据源  MSC8391
 * @date 2019-12-12
 * @author nan.liu
 * */
@DataSourceClass(bindingDataSourceId="ds_qh_loanee_gladv2")
public class LoaneeV2DataSourceRequestor extends BaseQHDataSourceRequestor implements IDataSourceRequestor{
	private final Logger logger = LoggerFactory
			.getLogger(LoaneeV2DataSourceRequestor.class);

	public Map<String, Object> request(String trade_id, DataSource ds) {
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		TreeMap<String, Object> retdata = new TreeMap<String, Object>();
		Map<String,Object> paramForLog = new HashMap<String,Object>(); 
		Map<String, Object> rets = null;
		String resource_tag = Conts.TAG_SYS_ERROR;
		DataSourceLogVO logObj = new DataSourceLogVO();
		logObj.setDs_id(ds.getId());
		logObj.setReq_url(this.getUrl());
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));
		try {
			/**获取请求参数*/
			rets = new HashMap<String, Object>();
			String name = ParamUtil.findValue(ds.getParams_in(), "name")
					.toString(); // 姓名
			String idNo = ParamUtil.findValue(ds.getParams_in(), "cardNo")
					.toString(); // 身份证号码
			
			String mobile = ParamUtil.findValue(ds.getParams_in(), "mobile")
					.toString(); // 身份证号码

			String authCode = ParamUtil.findValue(ds.getParams_in(), "authCode")
					.toString(); // 授权码
			String authDate = ParamUtil.findValue(ds.getParams_in(), "authDate")
					.toString(); // 授权时间
			String statsPeriod = (String)ParamUtil.findValue(ds.getParams_in(), "statsPeriod"); // 统计周期 月份为单位

				logger.info("{} 常贷客数据源采集开始......",prefix);
				/**组装上下文数据*/ 
				Map<String, Object> context = new HashMap<String, Object>();
				context.put(TRADE_ID, trade_id);
				/**记录请求参数*/
                
                paramForLog.put(NAME, name);
                paramForLog.put(IDNO, idNo);
                paramForLog.put("mobileNo", mobile);
                paramForLog.put("entityAuthCode",authCode);
                paramForLog.put("entityAuthDate",authDate);
                paramForLog.put("statsPeriod", statsPeriod);
                /**位置最好不要轻易动 */
                context.putAll(paramForLog);
  
                /**发送请求 解析结果*/                
				Map<String, Object> retMap = executeClient(context);
				JSONObject resltJsn = JSON.parseObject(JSON.toJSONString(retMap.get("resltJsn")));
				JSONObject busiJsn = JSON.parseObject(JSON.toJSONString(retMap.get("busiJsn")));
				resltJsn = (resltJsn == null?new JSONObject() :resltJsn);
				busiJsn  = (busiJsn  == null?new JSONObject() :busiJsn);
				String rtCode = resltJsn.getString("rtCode");
				String rtMsg  = resltJsn.getString("rtMsg");
				
				logObj.setIncache("0");
				//用于数据库存储
				resltJsn.put("trade_id", trade_id); 
				if (!SUCC_CODE.equals(rtCode)) {
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_POLICE_EXCEPTION);
					rets.put(Conts.KEY_RET_MSG, "查询失败");
					rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
					logger.warn("{} 查询异常header:{}",prefix,rtMsg);
					return rets;
				}else {
					JSONArray records = busiJsn.getJSONArray("records");
					if(records.size()>0){
						JSONObject data = (JSONObject) records.get(0);
						String erCode = data.getString("erCode");
						logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC); 
						logObj.setBiz_code1(busiJsn.getString("batchNo"));

						if("E000000".equals(erCode)){
			                 retdata.putAll(JsonFilter.getJsonKeys(data, "erCode,erMsg,idNo,idType,name,seqNo"));
			                 resource_tag = Conts.TAG_FOUND;
							 logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
						 }else if("E000996".equals(erCode)){	
							logger.warn("{} 没有查询到常贷客信息",trade_id);
							resource_tag = Conts.TAG_UNFOUND;
							rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_ZS_C_NOTFOUND_EXCEPTION);
							rets.put(Conts.KEY_RET_MSG, "查无记录");
							rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
							logger.warn("{} 查询无内容",prefix);
							return rets;							 
						 }else{
							rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_POLICE_EXCEPTION);
							rets.put(Conts.KEY_RET_MSG, "数据源查询失败");
							rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
							logger.warn("{} 查询异常内容:{}",prefix,data.getString("erMsg"));
							return rets;
						 }
					}else{
						resource_tag = Conts.TAG_UNFOUND;
						rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_ZS_C_NOTFOUND_EXCEPTION);
						rets.put(Conts.KEY_RET_MSG, "查无记录");
						rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
						logger.warn("{} 查询无内容",prefix);
						return rets;
					}				
				}				
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
			rets.put(Conts.KEY_RET_DATA, retdata);
			rets.put(Conts.KEY_RET_MSG, "交易成功");
			rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
		} catch (Exception ex) {
			if(isTimeoutException(ex)){
		    	logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_TIMEOUT);	
		    	resource_tag = Conts.TAG_SYS_TIMEOUT;
			} else {
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
				logObj.setState_msg("数据源处理时异常! 详细信息:" + ex.getMessage());
			}
			rets.put(Conts.KEY_RET_STATUS,
					CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, "数据源查询异常!");
			logger.error(prefix+" 数据源处理时异常", ex);
		}finally{
			logObj.setTag(resource_tag);
	    	logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
	    	logger.info("{} 保存ds Log开始..." ,prefix);
			executorDtoService.writeDsLog(trade_id,logObj,true);
			executorDtoService.writeDsParamIn(trade_id, paramForLog, logObj,true);
			logger.info("{} 保存ds Log结束" ,prefix);
		}
		return rets;
	}	

	@Override
	protected Map<String, Object> buildRequestBody(Map<String,Object> data) {
		Map<String, Object> body = new HashMap<String, Object>();
		body.put("batchNo", buildBatchNo());
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> record = new HashMap<String, Object>();
		record.put(NAME, data.get(NAME));
		record.put(IDNO, data.get(IDNO));
		record.put("reasonCode", getReasonCode());
		record.put("idType", idType);
		record.put("entityAuthCode", data.get("entityAuthCode"));
		record.put("entityAuthDate", data.get("entityAuthDate"));
		record.putAll(CommonUtil.sliceMapIfNotBlank(data, new String[]{"moblieNo"}));
		// 目前只支持单条查询
		record.put("seqNo", 1);
		list.add(record);
		body.put("records", list);
		return body;
	}

}
