package com.wanda.credit.ds.client.unionpay;

import java.net.SocketTimeoutException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.apache.http.conn.ConnectTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.CardNoValidator;
import com.wanda.credit.base.util.DateUtil;
import com.wanda.credit.base.util.RequestHelper;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.dao.iface.IUnionPayPaintService;

/**
 * @Title: 外部数据源
 * @Description: 银联消费画像_用于对外接口输出
 * @author wangjing229@wanda.cn
 * @date 2018年10月17日 下午09:38:31
 * @version V1.0
 */
@DataSourceClass(bindingDataSourceId = "ds_p_consumer_portrait")
public class PersonalConsumerPortraitDataSourceRequestor extends NewBaseUnionpayPaintDsRequestor {
	private final Logger logger = LoggerFactory.getLogger(PersonalConsumerPortraitDataSourceRequestor.class);
	private final String DSID = "ds_p_consumer_portrait";

	private final String CARDNO_ERROR = "cardNo_error";
	private final String CARDID_ERROR = "cardId_error";
	private final String CARDNO_TYPE_ERROR = "cardNo_type_error";

	private final String RET_VALIDATE_RESULT = "validate_result";
//	private final String RET_ACTIVE_RESULT = "active_result";
	private final String RET_DATA_RESULT = "data_result";

	@Autowired
	private IUnionPayPaintService unionPayPaintService;
	@Autowired
	private IPropertyEngine propertyEngine;

	@Override
	public Map<String, Object> request(String trade_id, DataSource ds) {

		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		// 获取日志开关
		boolean doPrint = "1".equals(propertyEngine
				.readById("sys_log_print_switch"));
		String account_id = propertyEngine.readById("unionpay_portrait_account");
		String unionpay_url = propertyEngine.readById("unionpay_p_consumer_url");
		long startTime = System.currentTimeMillis();

		boolean inCache = false;
		// String resp = null;
//		unionpay_url= "https://warcraft-test.unionpaysmart.com/index/personal";
//		account_id = "T2010005";
	
		// 创建Map对象用于返回结果
		Map<String, Object> rets = new HashMap<String, Object>();
		rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED);
		rets.put(Conts.KEY_RET_MSG, "交易失败");
		// 标签
		Set<String> tagSet = new HashSet<String>();
		tagSet.add(Conts.TAG_SYS_ERROR);
		// 交易日志信息数据
		DataSourceLogVO logObj = new DataSourceLogVO();
		if (!StringUtil.isEmpty(ds.getId())) {
			logObj.setDs_id(ds.getId());
		} else {
			logObj.setDs_id(DSID);
		}
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));
		logObj.setReq_url(unionpay_url);
		logObj.setIncache("0");
		logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
		logObj.setState_msg("交易失败");

		try {
			// 参数落地
			Map<String, Object> paramIn = new HashMap<String, Object>();
			String cardNo = String.valueOf(ParamUtil.findValue(ds.getParams_in(), paramIds[0])); // 持卡人证件号码
			String name = String.valueOf(ParamUtil.findValue(ds.getParams_in(), paramIds[1])); // 持卡人姓名
			String cardNoType = String.valueOf(ParamUtil.findValue(ds.getParams_in(), paramIds[2])); // 持卡人证件类型
			String cardId = String.valueOf(ParamUtil.findValue(ds.getParams_in(), paramIds[3])); // 持卡卡号
			Object mobileObj = ParamUtil.findValue(ds.getParams_in(), "mobile");
			String mobile = "";
			if (!StringUtil.isEmpty(mobileObj)) {
				mobile = mobileObj.toString();
				paramIn.put("mobile", mobile);
			}
			Object emailObj = ParamUtil.findValue(ds.getParams_in(), "email");
			String email = "";
			if (!StringUtil.isEmpty(emailObj)) {
				email = emailObj.toString();
				paramIn.put("email", email);
			}
			String indices = null;
			Object indicesObj = ParamUtil.findValue(ds.getParams_in(), "indices");
			if (!StringUtil.isEmpty(indicesObj)) {
				indices = indicesObj.toString(); 
				paramIn.put("indices", indices);
			}
			Object addrObj = ParamUtil.findValue(ds.getParams_in(), "address");
			String address = "";
			if (!StringUtil.isEmpty(addrObj)) {
				address = addrObj.toString();
				paramIn.put("address", address);
			}

			logger.info("{} 保存用户输入参数", prefix);
			try {
				paramIn.put("name", name);
				paramIn.put("cardNo", cardNo);
				paramIn.put("cardType", cardNoType);
				paramIn.put("cardId", cardId);
				DataSourceLogEngineUtil.writeParamIn(trade_id, paramIn, logObj);
			} catch (Exception e) {
				logger.error("{} 保存参数异常 {}", trade_id, e.getMessage());
			}
			logger.info("{}校验参数是否合法", prefix);
			String paramErrorInfo = valiteParamIn(cardNo, cardNoType, cardId, prefix);
			
			if (CARDID_ERROR.equals(paramErrorInfo)) {
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS,
						CRSStatusEnum.STATUS_FAILED_DS_CARDID_ERROR);
				rets.put(Conts.KEY_RET_MSG, "银联卡号格式错误");
				return rets;
			} else if (CARDNO_TYPE_ERROR.equals(paramErrorInfo)) {
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS,
						CRSStatusEnum.STATUS_FAILED_DS_CARDNO_TYPE_ERROR);
				rets.put(Conts.KEY_RET_MSG, "证件类型格式错误");
				return rets;
			} else if (CARDNO_ERROR.equals(paramErrorInfo)) {
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS,
						CRSStatusEnum.STATUS_FAILED_DS_CARDNO_ERROR);
				rets.put(Conts.KEY_RET_MSG, "证件号码格式错误");
				return rets;
			}
			String cacheDays = propertyEngine.readById("unionpay_p_consumer_cachedays");//-15
			logger.info("{} 判断本地是否有缓存", prefix);
			String resp = null;
			
			Map<String, String> incacheInfo = //null;
					unionPayPaintService.isExistPerPaintDataNew(trade_id, cardId, Integer.valueOf(cacheDays));

			if (!StringUtil.isEmpty(incacheInfo)) {
				inCache = true;
				logObj.setIncache("1");
				logger.info("{} 本地有缓存数据直接获取缓存数据", prefix);

				if (!incacheInfo.containsKey("CONTENT")
						|| StringUtil.isEmpty(incacheInfo.get("CONTENT"))) {
					logger.info("{} 获取缓存数据异常直接连接银联获取数据", prefix);
					inCache = false;
				} else {
//					String 
					resp = incacheInfo.get("CONTENT");
				}
			}
			// 本地没有缓存数据或者获取缓存数据异常
			Map<String, String> params = new TreeMap<String, String>();
			if (!inCache) {
				logObj.setIncache("0");
				String currentTime = DateUtil.getSimpleDate(new Date(), "yyyyMMddHHmmssSSS");
				logger.info("{} 构建请求银联消费画像请求参数,orderId:{}", prefix,currentTime);
				// 拼装请求参数
				params.put("account", account_id);
				params.put("card", cardId);
				params.put("identityCard", cardNo);
				params.put("identityType", cardNoType);
				params.put("orderId", currentTime);
				params.put("name", name);
				params.put("mobile", mobile);
				params.put("address", address);
				if(indices!=null)
					params.put("indices", indices);//查询所有可不传

				Map<String, String> httpReqMap = buildReqMap(prefix, account_id, params);
				long httpStart = System.currentTimeMillis();
				String respResult = RequestHelper.doPost(unionpay_url, null,
						new HashMap<String, String>(), httpReqMap, null, false);
				logger.info("{} 银联消费画像http请求时间为  {}", prefix, (System.currentTimeMillis() - httpStart));
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

//				String 
				resp = decMap.get(PARSE_MSG);
				params.put("bc_flag", "0");// 个人/企业标识：0-个人，1-企业
			}
			
			if (doPrint) {
				logger.info("{} 银联消费画像返回密文解密后为 {}", prefix, resp);
			}
			// 解析消费画像数据-转成Map
			JSONObject respJsonObj = JSONObject.parseObject(resp);
			String resCode = respJsonObj.getString(KEY_CODE);
			String resMsg = respJsonObj.getString(KEY_MESSAGE);
			String statusCode = respJsonObj.getString(KEY_STATUS);
			String smartId = respJsonObj.getString(KEY_SMARTID);
			String orderId = respJsonObj.getString(KEY_ORDERID);
			logObj.setBiz_code1(resCode + "-" + statusCode + "-" + resMsg);
			logObj.setBiz_code2(smartId);
			logObj.setBiz_code3(orderId);
			
			tagSet.clear();
			tagSet.add(Conts.TAG_TST_FAIL);
			
			if (!"200".equals(resCode)) {
				logger.info("{} 调用银联消费画像失败 ", prefix);
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED);
				rets.put(Conts.KEY_RET_MSG, "个人消费能力评估失败!");
				return rets;
			}
			
			Map<String, Object> retData = new HashMap<String, Object>();
			if ("2001".equals(statusCode)) {
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
				logObj.setState_msg("交易成功");
				// 未查询到个人用户数据
				rets.clear();
				retData.put(RET_VALIDATE_RESULT, "0");
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
				rets.put(Conts.KEY_RET_MSG, "个人消费能力评估成功!");
				rets.put(Conts.KEY_RET_DATA, retData);
				
				tagSet.clear();
				tagSet.add(Conts.TAG_TST_SUCCESS);
				return rets;
			} else if ("2000".equals(statusCode)) {
				if (!inCache) {
					// 保存请求结果数据
					unionPayPaintService.saveNew(trade_id, resp, params);
				}
				
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
				logObj.setState_msg("交易成功");
				retData.put(RET_DATA_RESULT, respJsonObj.get("data"));
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
				rets.put(Conts.KEY_RET_MSG, "个人消费能力评估成功!");
				rets.put(Conts.KEY_RET_DATA, retData);
				
				tagSet.clear();
				tagSet.add(Conts.TAG_TST_SUCCESS);
			} else {
				logger.info("{} 银联返回数据有误，status节点异常 {}", prefix, resp);
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED);
				rets.put(Conts.KEY_RET_MSG, "个人消费能力评估失败!");
			}
			
			return rets;
		} catch (Exception ex) {
			logger.error("{} 数据源处理时异常：{}", prefix, ex.getMessage());
			logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
			logObj.setState_msg("交易失败");
			ex.printStackTrace();
			if ((ex instanceof ConnectTimeoutException)
					|| (ex instanceof SocketTimeoutException)) {
				// 交易日志信息数据
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_TIMEOUT);
				logObj.setState_msg("请求超时");
			} else {
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED);
				rets.put(Conts.KEY_RET_MSG, "个人消费能力评估失败!");
			}

		} finally {
			rets.put(Conts.KEY_RET_TAG,
					tagSet.toArray(new String[tagSet.size()]));
			/** 记录响应状态信息 */
			logObj.setTag(StringUtils.join(tagSet, ";"));
			logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
			DataSourceLogEngineUtil.writeLog(trade_id, logObj);
		}

		long tradeTime = System.currentTimeMillis() - startTime;
		logger.info("{} 处理银联消费画像数据总共耗时时间为（ms） {}", prefix, tradeTime);

		return rets;
	}

	/**
	 * 校验参数的是否合法
	 * 
	 * @param cardNo
	 * @param cardNoType
	 * @param cardId
	 * @return 校验成功返回 "" 否则返回错误信息
	 */
	private String valiteParamIn(String cardNo, String cardNoType,
			String cardId, String prefix) {
		StringBuffer errorBf = new StringBuffer();
		String cardIdErrorInfo = cardIdValit(cardId);
		if (!StringUtil.isEmpty(cardIdErrorInfo)) {
			logger.info("{}入参卡号码格式错误", prefix);
			errorBf.append(";").append(cardIdErrorInfo);
			return CARDID_ERROR;
		}
		HashMap<String, String> typeMap = getCardNoType();
		if (!typeMap.containsKey(cardNoType)) {
			logger.info("{} 证件类型错误,传入证件类型为:{}", prefix, cardNoType);
			errorBf.append(";").append("证件类型错误");
			return CARDNO_TYPE_ERROR;
		}
		if ("1".equals(cardNoType)) {
			String cardNoErrorInfo = CardNoValidator.validate(cardNo);
			if (!StringUtil.isEmpty(cardNoErrorInfo)) {
				logger.info("{} 身份证号码错误：{}", prefix, cardNoErrorInfo);
				errorBf.append(cardNoErrorInfo);
				return CARDNO_ERROR;
			}
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

	/**
	 *
	 * @return
	 */
	private static HashMap<String, String> getCardNoType() {
		HashMap<String, String> typeMap = new HashMap<String, String>();
		typeMap.put("1", "身份证");
		typeMap.put("2", "护照");
		typeMap.put("3", "军官证");
		typeMap.put("4", "回乡证");
		typeMap.put("5", "台胞证");
		typeMap.put("6", "国际海员证");
		typeMap.put("7", "港澳通行证");
		return typeMap;
	}

}
