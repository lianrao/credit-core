package com.wanda.credit.ds.client.qianhai;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.api.iface.IExecutorSecurityService;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.CommonUtil;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.dao.domain.qianhai.QHResultVO;
import com.wanda.credit.ds.dao.iface.ICredooService;
import com.wanda.credit.ds.iface.IDataSourceRequestor;

/**
 * @title 好信度数据源
 * @date 2016-01-26
 * @author chsh.wu
 * */
@SuppressWarnings("unchecked")
@DataSourceClass(bindingDataSourceId="ds_qh_credoo_glad")
public class CredooDataSourceRequestor extends BaseQHDataSourceRequestor implements IDataSourceRequestor {
	private final Logger logger = LoggerFactory.getLogger(CredooDataSourceRequestor.class);

	@Autowired
	private ICredooService credooService;
	@Autowired
	private IExecutorSecurityService synchExecutorService;

	private String MOBILENO = "mobileNo";
	private String CARDNO = "cardNo";

	public Map<String, Object> request(String trade_id, DataSource ds) {
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		TreeMap<String, Object> retdata = new TreeMap<String, Object>();
		Map<String, Object> rets = null;
		Map<String, Object> context = new HashMap<String, Object>();
		DataSourceLogVO logObj = new DataSourceLogVO();
		logObj.setDs_id(ds.getId());
		logObj.setReq_url(url);
		logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
		List<String> tags = new ArrayList<String>();

		try {
			rets = new HashMap<String, Object>();
			String name = ParamUtil.findValue(ds.getParams_in(), "name").toString(); // 姓名
			String idNo = ParamUtil.findValue(ds.getParams_in(), "cardNo").toString(); // 身份证号码
            String encryIdNo = synchExecutorService.encrypt(idNo);
			String cardNo = ParamUtil.findValue(ds.getParams_in(), "cardId").toString(); // 卡号
			String mobileNo = ParamUtil.findValue(ds.getParams_in(), "mobileNo").toString(); // 手机号码

			String authCode = ParamUtil.findValue(ds.getParams_in(), "authCode").toString(); // 授权码
			String authDate = ParamUtil.findValue(ds.getParams_in(), "authDate").toString(); // 授权时间

			CRSStatusEnum retStatus = CRSStatusEnum.STATUS_FAILED_DS_QIANHAI_CREDOO_EXCEPTION;
			String retMsg = "";

			// 缓存信息
			/*
			 * CredooVO inCached = credooService.inCached(name, DEFAULT_IDTYPE,
			 * idNo);
			 * 
			 * if (inCached == null) {
			 */
			logObj.setIncache("0");
			logger.info("{} 好信度数据源采集开始......", prefix);
			// 组装上下文数据
			context.put(NAME, name);
			context.put(IDNO, idNo);
			context.put(MOBILENO, mobileNo);
			context.put(CARDNO, cardNo);
			context.put("entityAuthCode", authCode);
			context.put("entityAuthDate", authDate);
			context.put(TRADE_ID, trade_id);
			logObj.setReq_time(new Timestamp(System.currentTimeMillis()));
			Map<String, Object> retMap = executeClient(context);
			logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
			JSONObject resltJsn = (JSONObject) retMap.get("resltJsn");
			JSONObject busiJsn = (JSONObject) retMap.get("busiJsn");

			String rtCode = QHDataSourceUtils.getValFromJsnObj(resltJsn, "rtCode");
			String rtMsg = QHDataSourceUtils.getValFromJsnObj(resltJsn, "rtMsg");

			// 用于数据库存储
			resltJsn.put("trade_id", trade_id);
			if (!SUCC_CODE.equals(rtCode)) {
				// 结果信息返回失败
				retMsg = "数据源查询异常";
				credooService.addQHResult(resltJsn);
				logObj.setState_msg(rtMsg);
				tags.add(Conts.TAG_SYS_ERROR);
			} else {
				retStatus = CRSStatusEnum.STATUS_SUCCESS;
				retMsg = "采集成功";
				String tag = buildTagFromQHDslog(ds.getId(), encryIdNo);
				tags.add(tag);
				Map<String, Object> retmap = visitBusiData(busiJsn, cardNo);// 必须在入库之前执行
				// 返回成功后保存结果信息 和 好信度信息
				QHResultVO result = credooService.addQHResult(resltJsn);
				credooService.addCredoo(busiJsn, result.getId());
				if(Conts.TAG_FOUND_NEWRECORDS.equals(tag)){
					qhlogService.addNewLog(trade_id,ds.getId(), name, encryIdNo);	 
				}
				// 设置retdata信息
				retdata.putAll(retmap);
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
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
				logObj.setState_msg("数据源处理时异常! 详细信息:" + ex.getMessage());
			}
			logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
		} finally {
			rets.put(Conts.KEY_RET_TAG,tags.toArray(new String[0]));
			logObj.setTag(org.apache.commons.lang.StringUtils.join(tags, ";"));
			DataSourceLogEngineUtil.writeLog(trade_id, logObj);
			Map<String, Object> param = CommonUtil.sliceMap(context, new String[] { NAME, IDNO, MOBILENO, CARDNO, "entityAuthCode", "entityAuthDate" });
			DataSourceLogEngineUtil.writeParamIn(trade_id, param, logObj);
		}
		return rets;
	}

	private String buildErrInfo(String prefix, String errCode, String errMsg) {
		// 结果信息返回失败
		String retMsg = "数据源厂商好信度信息返回异常! 异常码：" + errCode + ",异常信息:" + errMsg;
		logger.error("{} 数据源厂商好信度信息返回异常! 代码:{},错误消息:{}", new String[] { prefix, errCode, errMsg });
		return retMsg;
	}

	@Override
	protected Map<String, Object> buildRequestBody(Map<String, Object> data) {
		Map<String, Object> body = new HashMap<String, Object>();
		body.put("batchNo", buildBatchNo());
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> record = new HashMap<String, Object>();
		record.put(NAME, data.get(NAME));
		record.put(IDNO, data.get(IDNO));
		record.put(MOBILENO, data.get("mobileNo"));
		record.put(CARDNO, data.get("cardNo"));
		record.put("reasonNo", getReasonCode());
		record.put("idType", idType);
		record.put("entityAuthCode", data.get("entityAuthCode"));
		record.put("entityAuthDate", data.get("entityAuthDate"));
		// 目前只支持单条查询
		record.put("seqNo", 1);
		list.add(record);
		body.put("records", list);
		return body;
	}

	/**
	 * 处理业务数据给给外部使用 1、设置credooScore属性 2、设置卡号属性(cardId)
	 **/
	private Map<String, Object> visitBusiData(JSONObject jsnObj, String cardId) {
		HashMap<String, Object> retMap = new HashMap<String, Object>();
		List<Map<String, Object>> recrdls = (List<Map<String, Object>>) jsnObj.get("records");
		retMap.put("records", recrdls);
		String score = "N";
		for (Map<String, Object> recrd : recrdls) {
			if (recrd == null)
				continue;
			recrd.put("cardId", cardId);// 增加卡号是属性
			String tempScore = (String) recrd.get("credooScore");
			if ("N".equals(score) && StringUtils.hasText(tempScore)) {
				score = tempScore.toString();
			}
		}

		retMap.put("credooScore", score);
		return retMap;
	}
}
