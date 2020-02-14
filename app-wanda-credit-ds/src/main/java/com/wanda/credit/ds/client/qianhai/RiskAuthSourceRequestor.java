package com.wanda.credit.ds.client.qianhai;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.sf.json.JSONObject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.api.iface.IExecutorSecurityService;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.CommonUtil;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.dao.domain.qianhai.BlackListVO_2_0;
import com.wanda.credit.ds.dao.iface.IBlackListService_2_0;
import com.wanda.credit.ds.iface.IDataSourceRequestor;

/**
 * @title 反欺诈风险认证 M8075
 * @date 2019-05-31
 * @author chsh.wu
 * @version 1.0
 * */
@SuppressWarnings("unchecked")
@DataSourceClass(bindingDataSourceId="ds_qh_riskauth_glad")
public class RiskAuthSourceRequestor extends BaseQHDataSourceRequestor implements IDataSourceRequestor {
	private final Logger logger = LoggerFactory.getLogger(RiskAuthSourceRequestor.class);

	@Autowired
	private IBlackListService_2_0 blackListService;

	@Autowired
	private IExecutorSecurityService synchExecutorService;

	public Map<String, Object> request(String trade_id, DataSource ds) {
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		TreeMap<String, Object> retdata = new TreeMap<String, Object>();
		Map<String, Object> rets = null;
		Map<String, Object> context = new HashMap<String, Object>();
		List<String> tags = new ArrayList<String>();
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
			
			String ip = (String)ParamUtil.findValue(ds.getParams_in(), "ip"); // ip集
			String mobile = (String)ParamUtil.findValue(ds.getParams_in(), "mobile"); // 手机号码集

			String cryptedMobile = synchExecutorService.encrypt(mobile);
			
			String authCode = (String)ParamUtil.findValue(ds.getParams_in(), "authCode"); // 授权码
			String authDate = (String)ParamUtil.findValue(ds.getParams_in(), "authDate");// 授权时间

			CRSStatusEnum retStatus = CRSStatusEnum.STATUS_FAILED_DS_QIANHAI_BLACKLIST_EXCEPTION;
			String retMsg = "";

			logObj.setIncache("0");
			logger.info("{} MSC8075数据源采集开始......", prefix);
			// 组装上下文数据
			
			context.put(NAME, name);
			context.put(IDNO, idNo);
			context.put("ip", ip);
			context.put("mobileNo", mobile);

			context.put("cryptedIdNo", cryptedIdNo);
			context.put("cryptedMobile", cryptedMobile);
			
			context.put("entityAuthCode", authCode);
			context.put("entityAuthDate", authDate);
			context.put(TRADE_ID, trade_id);
			
			logObj.setReq_time(new Timestamp(System.currentTimeMillis()));
			Map<String, Object> retMap = executeClient(context);
			JSONObject resltJsn = (JSONObject) retMap.get("resltJsn");
			JSONObject busiJsn = (JSONObject) retMap.get("busiJsn");
			resltJsn = (resltJsn == null ? new JSONObject() : resltJsn);
			busiJsn = (busiJsn == null ? new JSONObject() : busiJsn);

			String rtCode = QHDataSourceUtils.getValFromJsnObj(resltJsn, "rtCode");
			String rtMsg = QHDataSourceUtils.getValFromJsnObj(resltJsn, "rtMsg");

			// 用于数据库存储
			resltJsn.put("trade_id", trade_id);
			if (!SUCC_CODE.equals(rtCode)) {
				// 结果信息返回失败
				retMsg = rtMsg;
				logObj.setState_msg(retMsg);
				tags.add(Conts.TAG_SYS_ERROR);
				logger.error("{} MSC8075查询失败 {}",trade_id,retMsg);
			} else {
				retStatus = CRSStatusEnum.STATUS_SUCCESS;
				retMsg = "采集成功";
				 List<BlackListVO_2_0> blacklist = blackListService.addOneBlackList(trade_id,busiJsn, context);
				 if(Conts.TAG_SYS_ERROR.equals(context.get(Conts.KEY_RET_TAG))){
					 //error
					 retStatus = CRSStatusEnum.STATUS_FAILED_DS_QIANHAI_BLACKLIST_EXCEPTION;
					 retMsg = (String)context.get("erMsg");
					 logObj.setState_msg(retMsg);
					 tags.add(Conts.TAG_SYS_ERROR);
					 logger.error("{} 黑名单查询失败 {}",trade_id,retMsg);
				 }else if(CollectionUtils.isNotEmpty(blacklist)){
					 //found
					 retdata.put("found", "1");
					 retdata.putAll(visitBusiData(trade_id,busiJsn));
					 String tag = buildTagFromQHDslog(ds.getId(),cryptedIdNo);
					 tags.add(tag);
					 logObj.setBiz_code1(tag);
					 logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
					 if(Conts.TAG_FOUND_NEWRECORDS.equals(tag)){
					      qhlogService.addNewLog(trade_id,ds.getId(), name, cryptedIdNo);	 
					   }
				 }else{
					 //not found
					 retdata.put("riskFlag","0");
					 retdata.put("found", "0");
					 String tag = Conts.TAG_UNFOUND;
					 tags.add(tag);
					 logObj.setBiz_code1(tag);
					 logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
					 logger.warn("{} 没有查询到黑名单信息",trade_id);
					 if(Conts.TAG_FOUND_NEWRECORDS.equals(tag)){
					      qhlogService.addNewLog(trade_id,ds.getId(), name, cryptedIdNo);	 
					   }
				 }
				 // 处理 返回数据信息
			}
			rets.put(Conts.KEY_RET_STATUS, retStatus);
			rets.put(Conts.KEY_RET_DATA, retdata);
			rets.put(Conts.KEY_RET_MSG, retMsg);
		} catch (Exception ex) {
			tags.clear();
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, "数据源查询异常!");
			logger.error(prefix + " 数据源处理时异常", ex);
			if (CommonUtil.isTimeoutException(ex)) {
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_TIMEOUT);
				tags.add(Conts.TAG_SYS_TIMEOUT);
			} else {
				tags.add(Conts.TAG_SYS_ERROR);
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
			}
		} finally {
			rets.put(Conts.KEY_RET_TAG,tags.toArray(new String[0]));
			logObj.setTag(StringUtils.join(tags, ";"));
			logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
			DataSourceLogEngineUtil.writeLog(trade_id, logObj);
			Map<String, Object> param = CommonUtil.sliceMap(context, new String[]{NAME,IDNO,"ip","entityAuthCode","entityAuthDate"});
			param.put("moblieNo", context.get("cryptedMobile"));
			DataSourceLogEngineUtil.writeParamIn(trade_id, param,logObj);
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

		record.put("reasonNo", "01");
		record.put("idType", idType);
		record.put("entityAuthCode", data.get("entityAuthCode"));
		record.put("entityAuthDate", data.get("entityAuthDate"));
		// 目前只支持单条查询
		record.put("seqNo", 1);
		record.putAll(CommonUtil.sliceMapIfNotBlank(data, new String[]{"ip","mobileNo"}));
		list.add(record);
		body.put("records", list);
		return body;
	}

	/**
	 * 处理业务数据给给外部使用
	 * 生成riskflag标志信息
	 **/
	private Map<String, Object> visitBusiData(String trade_id,JSONObject jsnObj) {
		HashMap<String, Object> retMap = new HashMap<String, Object>();
		List<Map<String, Object>> recrdls = (List<Map<String, Object>>) jsnObj.get("records");
		List<Map<String, Object>> retrnList = new ArrayList<Map<String,Object>>();
		String riskFlag = "0";// 默认规则是通过
		for (Map<String, Object> recrd : recrdls) {
			if (recrd == null || !"E000000".equals(recrd.get("erCode")))
				continue;
			retrnList.add(recrd);
			if("0".equals(riskFlag)){
				riskFlag = handleRiskFlag(trade_id,recrd);
			}			
		}
		logger.info("{} riskFlag->{} ",trade_id,riskFlag);
		retMap.put("records",  retrnList);
		retMap.put("riskFlag", riskFlag);

		return retMap;
	}

	/**快易花的拒绝规则 1 代表拒绝 0 代表通过*/
	private String handleRiskFlag(String trade_id,Map<String, Object> recrd) {
		String riskFlag = "0";// 默认规则是通过
		String sourceId = (String)recrd.get("sourceId");
		String rskScore = (String)recrd.get("rskScore");
		String rskMark = (String)recrd.get("rskMark");
		String dataBuildTime = (String)recrd.get("dataBuildTime");
		if("A".equals(sourceId) && StringUtils.isNotBlank(rskScore)){
			int rskScoreForInt = Integer.valueOf(rskScore);
			if(rskScoreForInt>=20 && rskScoreForInt<=45){riskFlag = "1";};
		}else if("B".equals(sourceId) && ("B1".equals(rskMark) || "B2".equals(rskMark)) &&
				StringUtils.isNotBlank(dataBuildTime)){
			try {
				Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dataBuildTime+" 23:59:59");
				if(new Date().compareTo(DateUtils.addYears(date, 2))<=0){riskFlag = "1";}
			} catch (ParseException e) {
				logger.error(trade_id +" 日期格式转换错误 ",e);
			}
			
		}else if("C".equals(sourceId) && ("C1".equals(rskMark) || "C2".equals(rskMark) ||
				"C3".equals(rskMark) || "C4".equals(rskMark)) &&
				StringUtils.isNotBlank(dataBuildTime)){
			try {
				Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dataBuildTime+" 23:59:59");
				if(new Date().compareTo(DateUtils.addYears(date, 2))<=0){riskFlag = "1";}
			} catch (ParseException e) {
				logger.error(trade_id +" 日期格式转换错误 ",e);
			}
			
		}
		return riskFlag;
	}
	
	public static void main(String[] args) throws ParseException {
		List<String> tags = new ArrayList<String>();
         System.out.println(">>"+StringUtils.join(tags, ","));
        tags.add("found");
        System.out.println(">>"+StringUtils.join(tags, ","));
        tags.add("found1");
        System.out.println(">>"+StringUtils.join(tags, ","));

	}
}
