package com.wanda.credit.ds.client.qianhai;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.api.iface.IExecutorSecurityService;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.CommonUtil;
import com.wanda.credit.base.util.DateUtil;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.dao.domain.qianhai.BlackListVO_2_0;
import com.wanda.credit.ds.dao.iface.IBlackListService_2_0;
import com.wanda.credit.ds.iface.IDataSourceRequestor;

import net.sf.json.JSONObject;

/**
 * @title 前海工作居住地
 * @date 2016-08-09
 * @author chsh.wu
 * @version 1.0
 * */
@SuppressWarnings("unchecked")
@DataSourceClass(bindingDataSourceId="ds_qh_addressWork")
public class AddressDataSourceRequestor extends BaseQHDataSourceRequestor implements IDataSourceRequestor {
	private final Logger logger = LoggerFactory.getLogger(AddressDataSourceRequestor.class);

	@Autowired
	private IBlackListService_2_0 blackListService;

	@Autowired
	private IExecutorSecurityService synchExecutorService;

	public Map<String, Object> request(String trade_id, DataSource ds) {
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		Map<String, Object> rets = null;
		Map<String, Object> context = new HashMap<String, Object>();
		String resource_tag = Conts.TAG_SYS_ERROR;
		DataSourceLogVO logObj = new DataSourceLogVO();
		logObj.setDs_id(ds.getId());
		logObj.setReq_url(url);
		logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
		try {
			rets = new HashMap<String, Object>();
			/**input field: idNo idType ips cardNos moblieNos name reasonCode entityAuthCode
			   entityAuthDate seqNo*/
			String name = (String)ParamUtil.findValue(ds.getParams_in(), "name"); // 姓名
			String idNo = (String)ParamUtil.findValue(ds.getParams_in(), "cardNo"); // 身份证号码
			String cryptedIdNo = synchExecutorService.encrypt(idNo);

			String mobile = (String)ParamUtil.findValue(ds.getParams_in(), "mobile"); // 手机号码集
			String address = (String)ParamUtil.findValue(ds.getParams_in(), "address"); // 地址
			String queryType = (String)ParamUtil.findValue(ds.getParams_in(), "queryType"); // 地址
			
			logObj.setIncache("0");
			logger.info("{} 工作居住地数据源采集开始......", prefix);
			// 组装上下文数据
			
			context.put(NAME, name);
			context.put(IDNO, idNo);

			context.put("mobileNo", mobile);
			context.put("queryType", queryType);
			context.put("cryptedIdNo", cryptedIdNo);
			context.put("address", address);
			
			context.put("entityAuthCode", trade_id);
			context.put("entityAuthDate", createAuthDate());
			context.put(TRADE_ID, trade_id);
			
			logObj.setReq_time(new Timestamp(System.currentTimeMillis()));
			Map<String, Object> retMap = executeClient(context);
			JSONObject resltJsn = (JSONObject) retMap.get("resltJsn");
			JSONObject busiJsn = (JSONObject) retMap.get("busiJsn");
			resltJsn = (resltJsn == null ? new JSONObject() : resltJsn);
			busiJsn = (busiJsn == null ? new JSONObject() : busiJsn);
			logger.info("{} 工作居住地查询成功00",trade_id);
			String rtCode = QHDataSourceUtils.getValFromJsnObj(resltJsn, "rtCode");
			String rtMsg = QHDataSourceUtils.getValFromJsnObj(resltJsn, "rtMsg");

			// 用于数据库存储
			resltJsn.put("trade_id", trade_id);
			if (!SUCC_CODE.equals(rtCode)) {
				logger.error("{} 工作居住地查询失败00",trade_id);
				// 结果信息返回失败
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				 rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_POLICE_EXCEPTION);
				 rets.put(Conts.KEY_RET_MSG, "工作居住地查询失败");
				 return rets;
				
			} else {
				logger.info("{} 工作居住地查询成功01，包装开始...",trade_id);
				 List<BlackListVO_2_0> blacklist = blackListService.addOneBlackList(trade_id,busiJsn, context);
				 if(Conts.TAG_SYS_ERROR.equals(context.get(Conts.KEY_RET_TAG))){
					 logger.error("{} 工作居住地查询失败01",trade_id);
					 rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
					 rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_POLICE_EXCEPTION);
					 rets.put(Conts.KEY_RET_MSG, "工作居住地查询失败");
					 return rets;					 
				 }else if(CollectionUtils.isNotEmpty(blacklist)){
					 logger.info("{} 工作居住地查询成功02",trade_id);
					 //found
					 if(isERData("E000996",busiJsn)){
						 logger.info("{} 工作居住地查询成功03,查无记录",trade_id);
						 resource_tag = Conts.TAG_UNFOUND;
						 rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_UNIONPAY_MERPAINT_MID_ERROR);
						 rets.put(Conts.KEY_RET_MSG, "查无记录");
						 rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
						 return rets;
					 }else if(isERData("E000000",busiJsn)){
						 logger.info("{} 工作居住地查询成功04,查的结果",trade_id);
						 resource_tag = Conts.TAG_FOUND;
						 rets.put(Conts.KEY_RET_DATA, visitBusiData(trade_id,busiJsn));
						 rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
						 rets.put(Conts.KEY_RET_MSG, "交易成功");
						 rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
						 return rets;
					 }else{
						 logger.error("{} 工作居住地查询失败02",trade_id);
						 rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
						 rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_POLICE_EXCEPTION);
						 rets.put(Conts.KEY_RET_MSG, "工作居住地查询失败");
						 return rets;
					 }
				 }else{
					 logger.info("{} 工作居住地查询成功05,查无记录",trade_id);
					 resource_tag = Conts.TAG_UNFOUND;
					 rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_UNIONPAY_MERPAINT_MID_ERROR);
					 rets.put(Conts.KEY_RET_MSG, "查无记录");
					 rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
					 return rets;
				 }
				 // 处理 返回数据信息
			}
		} catch (Exception ex) {
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, "数据源查询异常!");
			logger.error(prefix + " 数据源处理时异常", ex);
			if (CommonUtil.isTimeoutException(ex)) {
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_TIMEOUT);
			} else {
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
			}
		} finally {
			Map<String, Object> param = CommonUtil.sliceMap(context, new String[]{NAME,IDNO,"entityAuthCode","entityAuthDate"});
			logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
			logObj.setTag(resource_tag);
			logger.info("{} 保存ds Log开始:{}" ,prefix,JSON.toJSONString(param));
			executorDtoService.writeDsLog(trade_id,logObj,true);
			executorDtoService.writeDsParamIn(trade_id, param, logObj,true);
			logger.info("{} 保存ds Log结束" ,prefix);
		}
		return rets;
	}

	@Override
	protected Map<String, Object> buildRequestBody(Map<String, Object> data) {
		Map<String, Object> body = new HashMap<String, Object>();
		body.put("batchNo", data.get(TRADE_ID));
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> record = new HashMap<String, Object>();
		record.put(NAME, data.get(NAME));
		record.put(IDNO, data.get(IDNO));
		record.put("reasonCode", getReasonCode());
		record.put("idType", idType);
		record.put("entityAuthCode", data.get("entityAuthCode"));
		record.put("entityAuthDate", data.get("entityAuthDate"));
		// 目前只支持单条查询
		record.put("seqNo", 1);
		record.putAll(CommonUtil.sliceMapIfNotBlank(data, new String[]{"address","mobileNo","queryType"}));
		list.add(record);
		body.put("records", list);
		return body;
	}
	private boolean isERData(String status,JSONObject jsnObj) {
		List<Map<String, Object>> recrdls = (List<Map<String, Object>>) jsnObj.get("records");
		for (Map<String, Object> recrd : recrdls) {
			if (status.equals(recrd.get("erCode")))
				return true;			
		}
		return false;
	}
	/**
	 * 处理业务数据给给外部使用
	 * 生成riskflag标志信息
	 **/
	private JSONObject visitBusiData(String trade_id,JSONObject jsnObj) {
		JSONObject retMap = new JSONObject();
		JSONObject result = new JSONObject();
		List<JSONObject> recrdls = (List<JSONObject>) jsnObj.get("records");

		retMap =  recrdls.get(0);
		result.put("city", retMap.get("city"));
		result.put("delta", retMap.get("delta"));
		result.put("attribute", retMap.get("ispNum"));
		result.put("province", retMap.get("province"));
		return result;
	}
	private String createAuthDate() {
		String authDate = DateUtil.getSimpleDate(new Date(), "yyyy-MM-dd");
		return authDate;
	}	
}
