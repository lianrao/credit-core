package com.wanda.credit.ds.client.unionpay;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

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
 * @Title: 外部数据源
 * @Description: 银联用户画像_新平台
 * @author liunan
 * @date 2019年05月10日 下午09:38:31
 * @version V1.0
 */
@DataSourceClass(bindingDataSourceId = "ds_new_unionPayPaint")
public class UnionPayPaintNewRequestor extends
		BaseUnionpayPaintDsRequestor implements IDataSourceRequestor {
	private final Logger logger = LoggerFactory
			.getLogger(UnionPayPaintNewRequestor.class);

	private final String CARDID_ERROR = "cardId_error";
	private final String RET_VALIDATE_RESULT = "validate_result";
	private final String RET_DATA_RESULT = "data_result";

	@Autowired
	private IPropertyEngine propertyEngine;

	@Override
	public Map<String, Object> request(String trade_id, DataSource ds) {

		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		// 获取日志开关
		boolean doPrint = "1".equals(propertyEngine.readById("sys_log_print_switch"));
		String account_id = propertyEngine.readById("unionpay_per_account");
		String unionpay_url = propertyEngine.readById("unionpay_personal_new_url");
		long startTime = System.currentTimeMillis();

		boolean inCache = false;

		// 创建Map对象用于返回结果
		Map<String, Object> paramIn = new HashMap<String, Object>();
		Map<String, Object> rets = new HashMap<String, Object>();
		rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED);
		rets.put(Conts.KEY_RET_MSG, "交易失败");
		String resource_tag = Conts.TAG_SYS_ERROR;
		// 交易日志信息数据
		DataSourceLogVO logObj = new DataSourceLogVO(trade_id);
		logObj.setDs_id(ds.getId());
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));
		logObj.setReq_url(unionpay_url);
		logObj.setIncache("0");
		logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
		logObj.setState_msg("交易失败");

		try {
			// 参数落地			
			String card = ParamUtil.findValue(ds.getParams_in(), paramIds[0])
					.toString(); // 持卡人证件号码

			logger.info("{} 保存用户输入参数", prefix);
			paramIn.put("card", card);
			logger.info("{}校验参数是否合法", prefix);
			String paramErrorInfo = valiteParamIn(card,prefix);
			if (CARDID_ERROR.equals(paramErrorInfo)) {
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS,
						CRSStatusEnum.STATUS_FAILED_DS_CARDID_ERROR);
				rets.put(Conts.KEY_RET_MSG, "银联卡号格式错误");
				return rets;
			}
			// 本地没有缓存数据或者获取缓存数据异常
			if (!inCache) {
				logObj.setIncache("0");
				logger.info("{} 构建请求银联个人画像请求参数", prefix);
				// 拼装请求参数
				Map<String, String> params = new TreeMap<String, String>();
				params.put("account", account_id);
				params.put("card", card);
				params.put("orderId", trade_id);

				Map<String, String> httpReqMap = buildReqMap(prefix, account_id,
						params);
				long httpStart = System.currentTimeMillis();
				String respResult = RequestHelper.doPost(unionpay_url, null,
						new HashMap<String, String>(), httpReqMap, null, false);
				logger.info("{} 银联商户画像http请求时间为  {}", prefix,
						(System.currentTimeMillis() - httpStart));
				logger.info("{} 请求返回数据：{}", prefix, respResult);

				Map<String, String> decMap = decResInfo(prefix, respResult);
				if (!(decMap.containsKey(PARSE_CODE) && "000".equals(decMap
						.get(PARSE_CODE)))) {
					logger.info("{} 解密银联返回报文失败 {}", prefix, decMap);
					logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
					logObj.setState_msg(decMap.get(PARSE_MSG));

					rets.put(Conts.KEY_RET_STATUS,CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
					rets.put(Conts.KEY_RET_MSG,CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION.getRet_msg());
					return rets;
				}

				String resp = decMap.get(PARSE_MSG);
				if (doPrint) {
					logger.info("{} 银联个人画像返回密文解密后为 {}", prefix, resp);
				}
				// 解析个人画像数据-转成Map
				JSONObject respJsonObj = JSONObject.parseObject(resp);
				String resCode = respJsonObj.getString(KEY_CODE);
				String resMsg = respJsonObj.getString(KEY_MESSAGE);
				String statusCode = respJsonObj.getString(KEY_STATUS);
				String smartId = respJsonObj.getString(KEY_SMARTID);
				String orderId = respJsonObj.getString(KEY_ORDERID);
				logObj.setBiz_code1(resCode + "-" + statusCode + "-" + resMsg);
				logObj.setBiz_code2(smartId);
				logObj.setBiz_code3(orderId);

				resource_tag = Conts.TAG_TST_FAIL;
				if (!"200".equals(resCode)) {
					logger.info("{} 调用银联个人画像失败 ", prefix);
					rets.clear();
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_PAINT_EXCEPTION);
					rets.put(Conts.KEY_RET_MSG, "个人消费能力评估失败!");
					rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
					return rets;
				}

				Map<String, Object> retData = new HashMap<String, Object>();
				if ("2001".equals(statusCode)) {
					logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
					logObj.setState_msg("交易成功");
					resource_tag = Conts.TAG_TST_SUCCESS;
					// 未查询到个人用户数据
					rets.clear();
					retData.put(RET_VALIDATE_RESULT, "0");
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
					rets.put(Conts.KEY_RET_MSG, "个人消费能力评估成功!");
					rets.put(Conts.KEY_RET_DATA, retData);
					rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
					return rets;
				} else if ("2000".equals(statusCode)) {
					resource_tag = Conts.TAG_TST_SUCCESS;
					logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
					logObj.setState_msg("交易成功");
					retData.put(RET_DATA_RESULT, respJsonObj.get("data"));
					rets.clear();
					retData.put(RET_VALIDATE_RESULT, "1");
					rets.put(Conts.KEY_RET_STATUS,
							CRSStatusEnum.STATUS_SUCCESS);
					rets.put(Conts.KEY_RET_MSG, "个人消费能力评估成功!");
					rets.put(Conts.KEY_RET_DATA, retData);
					rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				} else {
					logger.info("{} 银联返回数据有误，status节点异常 {}", prefix, resp);
					rets.clear();
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_PAINT_EXCEPTION);
					rets.put(Conts.KEY_RET_MSG, "消费画像查询失败");
					rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				}
				return rets;
			}
		} catch (Exception ex) {
			resource_tag = Conts.TAG_SYS_ERROR;
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, "数据源查询异常!");
			logger.error("{} 数据源处理时异常：{}",prefix,ExceptionUtil.getTrace(ex));
			if (ExceptionUtil.isTimeoutException(ex)) {
				resource_tag = Conts.TAG_SYS_TIMEOUT;
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_TIMEOUT);
			} else {
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
				logObj.setState_msg("数据源处理时异常! 详细信息:" + ex.getMessage());
			}
			rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});

		} finally {
			logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
			logObj.setTag(resource_tag);
			logger.info("{} 保存ds Log开始..." ,prefix);
			executorDtoService.writeDsLog(trade_id,logObj,true);
			executorDtoService.writeDsParamIn(trade_id, paramIn, logObj,true);
			logger.info("{} 保存ds Log结束" ,prefix);
		}
		long tradeTime = System.currentTimeMillis() - startTime;
		logger.info("{} 处理银联个人画像数据总共耗时时间为（ms） {}", prefix, tradeTime);
		return rets;
	}

	/**
	 * 校验参数的是否合法
	 * @param card
	 * @return 校验成功返回 "" 否则返回错误信息
	 */
	private String valiteParamIn(String cardId, String prefix) {
		StringBuffer errorBf = new StringBuffer();
		String cardIdErrorInfo = cardIdValit(cardId);
		if (!StringUtil.isEmpty(cardIdErrorInfo)) {
			logger.info("{}入参卡号码格式错误", prefix);
			errorBf.append(";").append(cardIdErrorInfo);
			return CARDID_ERROR;
		}
		return "";
	}

	/**
	 * 校验银行卡号是否合法
	 * 
	 * @param cardId
	 * @return 银行卡卡号合法返回 "" 否则返回错误信息
	 */
	private String cardIdValit(String cardId) {
		String errorInfo = "";
		if (StringUtil.isEmpty(cardId)) {
			errorInfo = "传入卡号为空";
		}
		if (!StringUtil.isPositiveInt(cardId)) {
			errorInfo = "卡号应该为数字";
		} else if (cardId.length() < 13 || cardId.length() > 19) {
			errorInfo = "银行卡卡号长度应为13位到19位之间";
		}
		return errorInfo;
	}
}
