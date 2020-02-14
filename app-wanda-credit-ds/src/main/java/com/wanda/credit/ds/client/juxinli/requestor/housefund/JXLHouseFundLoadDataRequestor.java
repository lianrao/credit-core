/**   
 * @Description: 聚信立_公积金_获取公积金数据
 * @author xiaobin.hou  
 * @date 2016年5月24日 下午2:49:57 
 * @version V1.0   
 */
package com.wanda.credit.ds.client.juxinli.requestor.housefund;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.api.iface.IExecutorSecurityService;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.client.juxinli.BasicJuXinLiDataSourceRequestor;
import com.wanda.credit.ds.client.juxinli.bean.AccessToken;
import com.wanda.credit.ds.client.juxinli.bean.IgnoreFieldProcessor;
import com.wanda.credit.ds.client.juxinli.bean.housefund.House_Fund_Basic;
import com.wanda.credit.ds.client.juxinli.bean.housefund.House_Fund_Loan_Basic;
import com.wanda.credit.ds.client.juxinli.bean.housefund.RawDataDetail;
import com.wanda.credit.ds.client.juxinli.bean.housefund.RawDataOverdue;
import com.wanda.credit.ds.client.juxinli.bean.housefund.RawDataPayment;
import com.wanda.credit.ds.client.juxinli.service.IJXLHouseRawDataDealService;
import com.wanda.credit.ds.client.juxinli.util.JXLConst;
import com.wanda.credit.ds.dao.domain.juxinli.housefund.HouseApplyInfoPojo;
import com.wanda.credit.ds.dao.domain.juxinli.housefund.HouseRawDataBasicPojo;
import com.wanda.credit.ds.dao.domain.juxinli.housefund.HouseRawDataDetailPojo;
import com.wanda.credit.ds.dao.domain.juxinli.housefund.HouseRawDataLoanPojo;
import com.wanda.credit.ds.dao.domain.juxinli.housefund.HouseRawDataOverduePojo;
import com.wanda.credit.ds.dao.domain.juxinli.housefund.HouseRawDataPaymentPojo;
import com.wanda.credit.ds.dao.domain.juxinli.housefund.HouseRawDataResPojo;
import com.wanda.credit.ds.dao.iface.juxinli.housefund.IJXLHouseFundApplyService;
import com.wanda.credit.ds.dao.iface.juxinli.housefund.IJXLHouseFundLoadResService;
import com.wanda.credit.ds.iface.IDataSourceRequestor;

/**
 * @author xiaobin.hou
 *
 */
@DataSourceClass(bindingDataSourceId="ds_jxlHouseFund_rawData")
public class JXLHouseFundLoadDataRequestor extends
		BasicJuXinLiDataSourceRequestor implements IDataSourceRequestor {

	private final static Logger logger = LoggerFactory
			.getLogger(JXLHouseFundLoadDataRequestor.class);
	@Autowired
	private IJXLHouseFundApplyService jxlHouseFundApplyService;
	@Autowired
	private IJXLHouseRawDataDealService jxlHouseRawDataDealService;
	@Autowired
	private IJXLHouseFundLoadResService jxlHouseFundLoadResService;
	@Autowired
	private IExecutorSecurityService synchExecutorService;


	private String accessTokenUrl;
	private String houseFundRawDataUrl;
	private String orgAccount;
	private String clientSecret;
	private String hours;
	private int timeOut;

	public Map<String, Object> request(String trade_id, DataSource ds) {
		
		String accessToken;
		String token;

		long startTime = System.currentTimeMillis();
		String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		// 组织返回对象
		Map<String, Object> rets = new HashMap<String, Object>();
		HouseRawDataResPojo resPojo = new HouseRawDataResPojo();
		resPojo.setTrade_id(trade_id);
		TreeMap<String, Object> retdata = new TreeMap<String, Object>();

		logger.info("{} 连接聚信立提交公积金采集申请开始", prefix);
		// 交易日志信息数据
		DataSourceLogVO logObj = new DataSourceLogVO();
		logObj.setDs_id(ds.getId());
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));
		logObj.setReq_url(houseFundRawDataUrl);
		logObj.setIncache("0");
		logObj.setBiz_code1(JXLConst.FLAG_FAILED);
		String resource_tag = Conts.TAG_SYS_ERROR;
		try {
			logger.info("{} 开始解析传入的参数", prefix);
			String requestId = ParamUtil.findValue(ds.getParams_in(),
					paramIds[0]).toString();
			logger.info("{} 解析传入的参数成功", prefix);
			/* 保存请求参数 */
			resPojo.setRequestId(requestId);
			
			try {
				Map<String, Object> paramIn = new HashMap<String, Object>();
				paramIn.put("request_id", requestId);
				DataSourceLogEngineUtil.writeParamIn(trade_id, paramIn, logObj);
			} catch (Exception e) {
				logger.error("{} 保存参数异常 {}", trade_id, e.getMessage());
			}

			// 判断requestId是否有效
			HouseApplyInfoPojo applyInfo = new HouseApplyInfoPojo();
			applyInfo.setRequestId(requestId);
			applyInfo.setProcess_code(JXLConst.SUCCESS_CODE);
			List<HouseApplyInfoPojo> subSucList = jxlHouseFundApplyService
					.query(applyInfo);

			if (subSucList == null || subSucList.size() < 1) {
				logger.info("{} 无效request_id或者该request_id提交采集请求失败", prefix);
				throw new Exception("no_use_requestId");
			}

			// 判断缓存中是否有数据

			boolean inCache = jxlHouseRawDataDealService.inCache(requestId);

			if (inCache) {
				logger.info("本地有缓存数据从缓存中获取数据", prefix);
				try {
					HouseRawDataBasicPojo queryRawData = jxlHouseRawDataDealService
							.queryRawData(requestId);
					
					HouseRawDataBasicPojo decRawData = decodeRawData(queryRawData);
					resource_tag = Conts.TAG_TST_SUCCESS;
					retdata = parseToOutput(decRawData);

					rets.clear();
					rets.put(Conts.KEY_RET_DATA, retdata);
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
					rets.put(Conts.KEY_RET_MSG, "获取数据成功");

					logger.info("{} 获取缓存数据成功", prefix);
					logObj.setIncache("1");
					logObj.setBiz_code1(JXLConst.FLAG_SUCCESS);
				} catch (Exception e) {
					logger.info("{} 获取缓存数据失败", prefix);
					inCache = false;
				}

			}

			if (!inCache) {

				// 根据requestId获取Token
				token = subSucList.get(0).getToken();

				resPojo.setToken(token);
//				token = "625c8b4ebe2546e9a25cf54122d61c7a";

				Gson gson = new Gson();

				logger.info("{} 连接聚信立获取安全凭证码,该安全码的有效时长为（单位h）" + hours, prefix);

				JsonObject acceptReportToken = getAcceptReportToken(
						accessTokenUrl, orgAccount, clientSecret, hours,
						timeOut * 1000, prefix);

				if (acceptReportToken == null) {
					logger.info("{} 连接聚信立获取安全凭证码失败,可能网络不通", prefix);
					throw new Exception("access_token_res_null");
				}

				AccessToken accToken = gson.fromJson(acceptReportToken,
						AccessToken.class);

				if ("true".equals(accToken.getSuccess())) {
					logger.info("{} 连接聚信立获取凭证安全码,聚信立返回TRUE", prefix);
					accessToken = accToken.getAccess_token();
				} else {
					logger.info("{} 连接聚信立获取凭证安全码,聚信立返回FALSE", prefix);
					logger.info("{} 连接聚信立获取凭证码返回信息为" + accToken, prefix);
					throw new Exception("access_token_res_false");
				}
				logger.info("{} 获取凭证安全码成功,准备获取报告数据", prefix);

				JsonObject rawDataResJson = null;
				long postStartTime = System.currentTimeMillis();
				rawDataResJson = getHouseFundData(houseFundRawDataUrl,
						clientSecret, accessToken, token, timeOut * 1000,
						prefix);
				long PostTime = System.currentTimeMillis() - postStartTime;
				logger.info("{} https请求聚信立耗时为（ms）" + PostTime, prefix);


				if (rawDataResJson == null) {
					logger.error("{} http请求结果为空,可能为超时或者报文解析失败 ", prefix);
					throw new Exception(JXLConst.RES_NULL);
				}

				String successFlag = rawDataResJson.get("success")
						.getAsString();

				if (!"true".equals(successFlag)) {
					logger.info("{} 聚信立端异常，聚信立返回信息为 {}", prefix, rawDataResJson);
					throw new Exception(JXLConst.RES_SUC_NOT_TRUE);
				}

				JsonObject raw_data_jsonObj = rawDataResJson.get("raw_data")
						.getAsJsonObject();

				JsonObject memberJsonObj = raw_data_jsonObj.get("members")
						.getAsJsonObject();

				String status = memberJsonObj.get("status").getAsString();
				JsonArray requestArgs = memberJsonObj.get("request_args")
						.getAsJsonArray();

				String reqToken = requestArgs.get(0).getAsJsonObject()
						.get("token").getAsString();
				String reqEnv = requestArgs.get(1).getAsJsonObject().get("env")
						.getAsString();

				if (!"success".equals(status)) {
					logger.info("{} 接口调用失败", prefix);
					logger.info("{} http请求结果为 {}", prefix, rawDataResJson);
					logObj.setState_msg(rawDataResJson.get("note")
							.getAsString());
					throw new Exception(JXLConst.RES_SUC_NOT_TRUE);
				}

				int resCode = memberJsonObj.get("error_code").getAsInt();
				String resMsg = memberJsonObj.get("error_msg").getAsString();

				resPojo.setEnv(reqEnv);
				resPojo.setToken(reqToken);
				resPojo.setStatus(status);
				resPojo.setVersion("");
				resPojo.setError_code(resCode + "");
				resPojo.setError_msg(resMsg);

				logObj.setBiz_code2(resCode + "");

				switch (resCode) {
				case 31200:

					JsonArray asJsonArray = memberJsonObj.get("transactions")
							.getAsJsonArray();
					JsonElement transactionElement = asJsonArray.get(0);

					HouseRawDataBasicPojo rawDataPojo = gson.fromJson(
							transactionElement, HouseRawDataBasicPojo.class);

					retdata = parseToOutput(rawDataPojo);
					try{
						jxlHouseRawDataDealService.addHouseRawData(rawDataPojo,requestId);
					}catch (Exception e) {
						logger.info("{} 保存公积金原始数据异常 {}" , prefix , e.getMessage());;
					}
					

					logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
					logObj.setState_msg(resMsg);
					logObj.setBiz_code1(JXLConst.FLAG_SUCCESS);
					resource_tag = Conts.TAG_TST_SUCCESS;
					rets.clear();
					rets.put(Conts.KEY_RET_DATA, retdata);
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
					rets.put(Conts.KEY_RET_MSG, "获取数据成功");

					break;

				default:
					logger.info("{} 请求数据失败 ", prefix);
					logger.info("{} http请求返回结果为 {} ", prefix, rawDataResJson);

					logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
					logObj.setState_msg(resMsg);

					rets.clear();
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED);
					rets.put(Conts.KEY_RET_MSG,
							CRSStatusEnum.STATUS_FAILED.getRet_msg());

					break;
				}
			}

		} catch (Exception e) {

			rets.clear();
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED);
			rets.put(Conts.KEY_RET_MSG,
					CRSStatusEnum.STATUS_FAILED.getRet_msg());

			String message = e.getMessage();
			if (JXLConst.RES_NULL.equals(message)) {
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_TIMEOUT);
				logObj.setState_msg("请求超时");
			}
			if ("access_token_res_false".equals(message)
					|| "access_token_res_null".equals(message)) {

				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_ACCESS_TOKEN_ERROR);
				rets.put(Conts.KEY_RET_MSG, "生成安全码失败，请稍后再试");
			}
			if ("no_use_requestId".equals(message)) {
				rets.clear();
				rets.put(
						Conts.KEY_RET_STATUS,
						CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_REQUESTID_NOTEXSIT);
				rets.put(Conts.KEY_RET_MSG, "request_id无效或未完成采集请求");
			} else {
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
				logObj.setState_msg("交易失败");
			}

		}

		logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
		/** 记录响应状态信息 */
		try {
			DataSourceLogEngineUtil.writeLog(trade_id, logObj);

			Date nowTime = new Date();
			resPojo.setCrt_time(nowTime);
			resPojo.setUpd_time(nowTime);
			jxlHouseFundLoadResService.add(resPojo);
		} catch (Exception e) {
			logger.error("{} 日志表数据保存异常 {}", prefix, e.getMessage());
		}
		rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
		long tradeTime = System.currentTimeMillis() - startTime;
		logger.info("{} 聚信立-公积金提交采集请求总共耗时时间为（ms） {}", prefix, tradeTime);
		return rets;
	}

	/**
	 * @param queryRawData
	 * @return
	 * @throws Exception 
	 */
	private HouseRawDataBasicPojo decodeRawData(HouseRawDataBasicPojo queryRawData) throws Exception {

		if (queryRawData == null) {
			return null;
		}

		if (!StringUtil.isEmpty(queryRawData.getId_card()) && !queryRawData.getId_card().contains("*")) {
			queryRawData.setId_card(synchExecutorService.decrypt(queryRawData.getId_card()));
		}
		if (!StringUtil.isEmpty(queryRawData.getFund_num()) && !queryRawData.getFund_num().contains("*")) {
			queryRawData.setFund_num(synchExecutorService.decrypt(queryRawData.getFund_num()));
		}
		if (!StringUtil.isEmpty(queryRawData.getOpen_bank_account()) && !queryRawData.getOpen_bank_account().contains("*")) {
			queryRawData.setOpen_bank_account(synchExecutorService.decrypt(queryRawData.getOpen_bank_account()));
		}
		
		Set<HouseRawDataLoanPojo> loanSet = queryRawData.getLoan_info();
		
		if (loanSet != null && loanSet.size() > 0) {
			for (HouseRawDataLoanPojo loan : loanSet) {
				if (!StringUtil.isEmpty(loan.getBank_account()) && !loan.getBank_account().contains("*")) {
					loan.setBank_account(synchExecutorService.decrypt(loan.getBank_account()));
				}
				if (!StringUtil.isEmpty(loan.getLoan_idcard()) && !loan.getLoan_idcard().contains("*")) {
					loan.setLoan_idcard(synchExecutorService.decrypt(loan.getLoan_idcard()));
				}
			}
		}
		
		return queryRawData;
	}

	/**
	 * @param rawDataPojo
	 * @return
	 * @throws Exception
	 * @throws
	 */
	private TreeMap<String, Object> parseToOutput(
			HouseRawDataBasicPojo rawDataPojo) throws Exception {
		TreeMap<String, Object> retData = new TreeMap<String, Object>();
		
		JsonConfig config = new JsonConfig();
        config.setJsonPropertyFilter(new IgnoreFieldProcessor(false, new String[]{"seqId","requestId"}));

		House_Fund_Basic fundBasic = new House_Fund_Basic();
		BeanUtils.copyProperties(fundBasic, rawDataPojo);

		List<RawDataDetail> detailList = new ArrayList<RawDataDetail>();
		Set<HouseRawDataDetailPojo> details = rawDataPojo.getDetails();
		for (HouseRawDataDetailPojo detailPojo : details) {
			RawDataDetail detail = new RawDataDetail();
			BeanUtils.copyProperties(detail, detailPojo);
			detailList.add(detail);
		}

		List<Map<String, String>> loanList = new ArrayList<Map<String, String>>();
		Set<HouseRawDataLoanPojo> loanInfoPojoSet = rawDataPojo.getLoan_info();

		if (loanInfoPojoSet != null && loanInfoPojoSet.size() > 0) {
			for (HouseRawDataLoanPojo loanPojo : loanInfoPojoSet) {
				Map<String, String> loanMap = new HashMap<String, String>();
				House_Fund_Loan_Basic loanBasic = new House_Fund_Loan_Basic();
				BeanUtils.copyProperties(loanBasic, loanPojo);
//				loanMap.put("loan_basic", JSONObject.toJSONString(loanBasic));
				loanMap.put("loan_basic", net.sf.json.JSONObject.fromObject(loanBasic, config).toString());

				Set<HouseRawDataPaymentPojo> payment_details = loanPojo
						.getPayment_details();
				List<RawDataPayment> payMentList = new ArrayList<RawDataPayment>();
				if (payment_details != null && payment_details.size() > 0) {
					for (HouseRawDataPaymentPojo paymentPojo : payment_details) {
						RawDataPayment payment = new RawDataPayment();
						BeanUtils.copyProperties(payment, paymentPojo);
						payMentList.add(payment);
					}
				}

//				loanMap.put("loan_payment",JSONObject.toJSONString(payMentList));
				loanMap.put("loan_payment", JSONArray.fromObject(payMentList, config).toString());

				Set<HouseRawDataOverduePojo> overdue_details = loanPojo
						.getOverdue_details();
				List<RawDataOverdue> overdueList = new ArrayList<RawDataOverdue>();
				if (overdue_details != null && overdue_details.size() > 0) {
					for (HouseRawDataOverduePojo overduePojo : overdue_details) {
						RawDataOverdue overdue = new RawDataOverdue();
						BeanUtils.copyProperties(overdue, overduePojo);
						overdueList.add(overdue);

					}
				}

//				loanMap.put("loan_overdue",JSONObject.toJSONString(overdueList));
				loanMap.put("loan_overdue", JSONArray.fromObject(overdueList, config).toString());

				loanList.add(loanMap);

			}
		}

		retData.put("house_fund_basic", net.sf.json.JSONObject.fromObject(fundBasic, config).toString());
		retData.put("house_fund_details", JSONArray.fromObject(detailList, config).toString());
		retData.put("house_fund_loans", loanList);
		return retData;
	}

	/**
	 * 
	 * @param url
	 * @param clientSecret
	 * @param accessToken
	 * @param token
	 * @param timeOut
	 * @param prefix
	 * @return
	 */
	private JsonObject getHouseFundData(String url, String clientSecret,
			String accessToken, String token, int timeOut, String prefix) {
		StringBuffer reportDataUrlBf = new StringBuffer();
		reportDataUrlBf.append(url).append("?").append(JXLConst.CLIENT_SECRET)
				.append("=").append(clientSecret).append("&")
				.append(JXLConst.ACCESS_TOKEN).append("=").append(accessToken)
				.append("&").append(JXLConst.COLL_TOKEN).append("=")
				.append(token);

		logger.info("{} 根据Token获取住房公积金数据URL为{}", prefix,
				reportDataUrlBf.toString());

		JsonObject jsonResponse = getJsonResponse(reportDataUrlBf.toString(),
				timeOut, prefix);

		return jsonResponse;
	}

	public String getHouseFundRawDataUrl() {
		return houseFundRawDataUrl;
	}

	public void setHouseFundRawDataUrl(String houseFundRawDataUrl) {
		this.houseFundRawDataUrl = houseFundRawDataUrl;
	}

	public String getOrgAccount() {
		return orgAccount;
	}

	public void setOrgAccount(String orgAccount) {
		this.orgAccount = orgAccount;
	}

	public String getClientSecret() {
		return clientSecret;
	}

	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}

	public String getHours() {
		return hours;
	}

	public void setHours(String hours) {
		this.hours = hours;
	}

	public int getTimeOut() {
		return timeOut;
	}

	public void setTimeOut(int timeOut) {
		this.timeOut = timeOut;
	}

	public String getAccessTokenUrl() {
		return accessTokenUrl;
	}

	public void setAccessTokenUrl(String accessTokenUrl) {
		this.accessTokenUrl = accessTokenUrl;
	}

}
