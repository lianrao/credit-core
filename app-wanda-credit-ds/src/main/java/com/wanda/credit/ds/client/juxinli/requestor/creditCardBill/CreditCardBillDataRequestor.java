/**   
* @Description: 聚信立-信用卡账单-获取账单数据 
* @author xiaobin.hou  
* @date 2016年7月22日 上午9:34:14 
* @version V1.0   
*/
package com.wanda.credit.ds.client.juxinli.requestor.creditCardBill;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.wanda.credit.api.dto.DataSource;
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
import com.wanda.credit.ds.client.juxinli.bean.creditCardBill.CreditBase;
import com.wanda.credit.ds.client.juxinli.bean.creditCardBill.CreditBillInfo;
import com.wanda.credit.ds.client.juxinli.bean.creditCardBill.CreditDetail;
import com.wanda.credit.ds.client.juxinli.bean.creditCardBill.CreditInstallment;
import com.wanda.credit.ds.client.juxinli.bean.creditCardBill.CreditTransDetail;
import com.wanda.credit.ds.client.juxinli.bean.creditCardBill.CreditTransSum;
import com.wanda.credit.ds.client.juxinli.bean.creditCardBill.CreditTransaction;
import com.wanda.credit.ds.client.juxinli.service.IJXLCreditBillDataService;
import com.wanda.credit.ds.client.juxinli.util.JXLConst;
import com.wanda.credit.ds.dao.domain.juxinli.creditCardBill.CreditCardAmtPojo;
import com.wanda.credit.ds.dao.domain.juxinli.creditCardBill.CreditCardApplyPojo;
import com.wanda.credit.ds.dao.domain.juxinli.creditCardBill.CreditCardBillInfoPojo;
import com.wanda.credit.ds.dao.domain.juxinli.creditCardBill.CreditCardDataResPojo;
import com.wanda.credit.ds.dao.domain.juxinli.creditCardBill.CreditCardInstallmentPojo;
import com.wanda.credit.ds.dao.domain.juxinli.creditCardBill.CreditCardTransDetailPojo;
import com.wanda.credit.ds.dao.domain.juxinli.creditCardBill.CreditCardTransSumPojo;
import com.wanda.credit.ds.dao.iface.juxinli.creditCardBill.IJXLCreditCardApplyService;
import com.wanda.credit.ds.dao.iface.juxinli.creditCardBill.IJXLCreditCardDataResService;
import com.wanda.credit.ds.iface.IDataSourceRequestor;

/**
 * @author xiaobin.hou
 *
 */
@DataSourceClass(bindingDataSourceId="ds_jxl_creditCardBill_data")
public class CreditCardBillDataRequestor extends
		BasicJuXinLiDataSourceRequestor implements IDataSourceRequestor {
	
	private final static Logger logger = LoggerFactory.getLogger(CreditCardBillDataRequestor.class);
	private final static String SUBMIT_SUC = "102";
	private final static String JXL_RES_ERROR = "JUXINLI_RESPONSE_EXCEPTION";
	
	private final static String CREDIT_BANK_INFO 	= "bank_info";//银行信息
	private final static String CREDIT_BILL_INFO 	= "bill_info";//账单信息
	private final static String CREDIT_BILL_BASIC 	= "basic_info";//账单基本信息
	private final static String CREDIT_LIMIT 		= "credit_limit";//信用额度
	private final static String CREDIT_CASH_LIMIT 	= "cash_limit";//现金最大额度
	private final static String CREDIT_CUR_BALANCE 	= "current_balance";//本期还款金额
	private final static String CREDIT_MIN_PAY 		= "minimum_payment";//最低还款金额
	private final static String CREDIT_TRANS_SUM 	= "transaction_summary";//交易汇总
	private final static String CREDIT_TRANS_DEL 	= "transaction_detail";//交易明细
	private final static String CREDIT_INSTALL_PLAN = "installment_plan";//账单分期计划
	
	@Autowired
	private IJXLCreditCardApplyService applyService;
	@Autowired
	private IJXLCreditBillDataService billDataService;
	@Autowired
	private IJXLCreditCardDataResService dataResService;
	private String accessTokenUrl;
	private String creditBillDataUrl;
	private String orgAccount;
	private String clientSecret;
	private String hours;
	private int timeOut;
	
	

	public Map<String, Object> request(String trade_id, DataSource ds) {

		
		long startTime = System.currentTimeMillis();
		
		String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		
		JsonConfig config = new JsonConfig();
	    config.setJsonPropertyFilter(new IgnoreFieldProcessor(false, new String[]{"seqId"}));

		
		//组织返回对象
		Map<String, Object> rets = new HashMap<String, Object>();
		TreeMap<String, Object> retdata = new TreeMap<String, Object>();
		//持久化对象
		CreditCardDataResPojo dataResPojo = new CreditCardDataResPojo();
		//交易日志信息数据
		DataSourceLogVO logObj = new DataSourceLogVO();
		logObj.setDs_id(ds.getId());
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));	
		logObj.setReq_url(creditBillDataUrl);
		logObj.setIncache("0");
		logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
		logObj.setState_msg("交易失败");
		logObj.setBiz_code1(JXLConst.FLAG_FAILED);
		
		try{
			logger.info("{} 获取并解析请求参数" , prefix);
			String requestId = ParamUtil.findValue(ds.getParams_in(), paramIds[0]).toString();//交易序列号
			
			dataResPojo.setRequestId(requestId);
			logObj.setBiz_code3(requestId);
			
			try {
				Map<String, Object> paramIn = new HashMap<String, Object>();
				paramIn.put("request_id", requestId);
				DataSourceLogEngineUtil.writeParamIn(trade_id, paramIn, logObj);
			} catch (Exception e) {
				logger.error("{} 保存参数异常 {}", trade_id, e.getMessage());
			}
			
			//判断request_id是否有效
			CreditCardApplyPojo applyInfo = applyService.queryApplyInfoByStatus(requestId,SUBMIT_SUC);
			
			if (applyInfo == null) {
				logger.info("{} 该request_id没有完成采集请求 {}", prefix ,requestId);
				throw new Exception(JXLConst.REQUESTID_NO_EXIST);
			}
			
			//判断是否有缓存数据
			boolean inCache = false;
			if ("1".equals(applyInfo.getLoad_data())) {
				inCache = true;
			}
			//存在缓存数据，从缓存中获取数据
			if(inCache){
				try{
					List<CreditDetail> detailList = billDataService
							.loadCacheData(requestId);
					CreditTransaction creditTransBean = new CreditTransaction();
					creditTransBean.setDetail(detailList);
					// 将对象转化成Map对象输出
					retdata = parseBeanToOut(creditTransBean, config);
					retdata.put(JXLConst.RES_CODE, "000");
					retdata.put(JXLConst.RES_MSG, "获取信用卡账单数据成功");
					rets.clear();
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
					rets.put(Conts.KEY_RET_MSG, "获取数据成功");
					rets.put(Conts.KEY_RET_DATA, retdata);

					logObj.setIncache("1");
					logObj.setBiz_code1(JXLConst.FLAG_SUCCESS);
					logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
					logObj.setState_msg("交易成功");
					
				}catch(Exception e){
					logger.error("{} 从本地获取缓存数据异常 {}" , prefix , e.getMessage());
					inCache = false;
				}
				
			}

			//缓存中不存在数据，或者读取缓存数据出现异常，连接聚信立获取数据
			if(!inCache){
				logObj.setIncache("0");
				//获取聚信立交易系统ID
				String token = applyInfo.getToken();				
				String accessToken = getAccessToken(prefix);
				
				//http请求聚信立获取报告数据
				long postStartTime = System.currentTimeMillis();
				JsonObject dataJsonObj = getCreditReportData(creditBillDataUrl,
						clientSecret, accessToken, token, timeOut * 1000,
						prefix);
				long PostTime = System.currentTimeMillis() - postStartTime;
				logger.info("{} https请求聚信立耗时为（ms）" + PostTime, prefix);
				
				
				if (dataJsonObj == null) {
					logger.error("{} http请求结果为空,可能为超时或者报文解析失败 ", prefix);
					throw new Exception(JXLConst.RES_NULL);
				}
				
				if (dataJsonObj.has("update_time") && dataJsonObj.has("request_args")) {
					dataResPojo.setResponse_time(dataJsonObj.get("update_time").getAsString());
					JsonArray requestArgs = dataJsonObj.get("request_args")
							.getAsJsonArray();

					if (requestArgs.size() >= 2 ) {
						String reqToken = requestArgs.get(0).getAsJsonObject()
								.get("token").getAsString();
						String reqEnv = requestArgs.get(1).getAsJsonObject().get("env")
								.getAsString();
						
						dataResPojo.setToken(reqToken);
						dataResPojo.setEnv(reqEnv);
					}
					
				}
				
				if (!dataJsonObj.has("status")) {
					logger.error("{} 返回报文没有status节点为空" , prefix);
					throw new Exception(JXL_RES_ERROR);
				}
				
				String statusFlag = dataJsonObj.get("status").getAsString();
				dataResPojo.setStatus(statusFlag);
				if (!"success".equals(statusFlag)) {
					logger.info("{} 接口调用异常返回信息为 {}", prefix, dataJsonObj);
					throw new Exception(JXL_RES_ERROR);
				}
				
				int errorCode = dataJsonObj.get("error_code").getAsInt();
				String errorMsg = dataJsonObj.get("error_msg").getAsString();
				
				dataResPojo.setError_code(errorCode + "");
				dataResPojo.setError_msg(errorMsg);
				
				logObj.setBiz_code2(errorCode + "");
				
				switch (errorCode) {
				case 31200:
					JsonArray transArray = dataJsonObj.get("transactions").getAsJsonArray();
					if (transArray.size() > 0) {
						JsonObject transData = (JsonObject)transArray.get(0);
						//将JSON数据转化成对象
						CreditTransaction creditTransaction = new Gson().fromJson(transData, CreditTransaction.class);
						//将对象转化成持久化对象
						List<CreditCardBillInfoPojo> billPojoList = parseBeanToPojo(creditTransaction,requestId);
						
						//保存数据并将以获取数据为更新为1
						billDataService.addData(billPojoList,requestId);
						
						//将对象转化成Map对象输出
						retdata = parseBeanToOut(creditTransaction,config);
					}else{
						retdata.put("bill_result_size", 0);
					}
					
					retdata.put(JXLConst.RES_CODE, "000");
					retdata.put(JXLConst.RES_MSG, "获取信用卡账单数据成功");
					rets.clear();
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
					rets.put(Conts.KEY_RET_MSG, "获取数据成功");
					rets.put(Conts.KEY_RET_DATA, retdata);
					
					logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
					logObj.setState_msg("交易成功");
					logObj.setBiz_code1(JXLConst.FLAG_SUCCESS);
					
					
					break;
				case 31204:
					retdata.clear();
					retdata.put(JXLConst.RES_CODE, "010");
					retdata.put(JXLConst.RES_MSG, "该账户暂时没有信用卡账单数据");//该账户没有信用卡账单或信用卡账单数据抓取中
					rets.clear();
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
					rets.put(Conts.KEY_RET_MSG, "获取数据成功");
					rets.put(Conts.KEY_RET_DATA, retdata);
						
					break;

				default:
					rets.clear();
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED);
					rets.put(Conts.KEY_RET_MSG, "获取数据成功");
					break;
				}
				
				try {
					dataResPojo.setCreate_date(new Date());
					dataResPojo.setUpdate_date(new Date());
					dataResService.add(dataResPojo);
				} catch (Exception e) {
					logger.info("{} 保存抓取数据响应结果失败" , prefix);
				}
			}
			
		
			
		}catch(Exception e){	
			e.printStackTrace();
			logger.error("{} 获取央行个人征信报告异常 {}" , prefix , e.getMessage());
			String errMsg = e.getMessage();	
			
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED);
			rets.put(Conts.KEY_RET_MSG, "请求失败");
			
			if (JXLConst.REQUESTID_NO_EXIST.equals(errMsg)) {
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_REQUESTID_NOTEXSIT);
				rets.put(Conts.KEY_RET_MSG, "非法request_id");
				
				logObj.setState_msg("request_id错误");
			}else if(JXLConst.RES_NULL.equals(errMsg)){
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_TIMEOUT);
				logObj.setState_msg("网络超时");
			}else {
				logObj.setState_msg("其他异常");
			}
		}finally{
			try {
				/** 记录响应状态信息 */
				logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
				DataSourceLogEngineUtil.writeLog(trade_id, logObj);
			} catch (Exception e) {
				logger.error("{} 日志表数据保存异常 {}", prefix, e.getMessage());
			}
		}
		
		
		
		long tradeTime = System.currentTimeMillis() - startTime;
		logger.info("{} 聚信立-公积金提交采集请求总共耗时时间为（ms） {}", prefix, tradeTime);
		
		return rets;
	
	}
	
	/**
	 * 将JavaBean转化成持久化对象
	 * @param requestId 
	 * @param creditTransaction
	 * @throws Exception 
	 * @throws IllegalAccessException 
	 */
	private List<CreditCardBillInfoPojo> parseBeanToPojo(CreditTransaction creditTrans, String requestId) throws Exception {
		
		List<CreditCardBillInfoPojo> billPojoList = new ArrayList<CreditCardBillInfoPojo>();

		if (creditTrans == null) {
			return billPojoList;
		}

		List<CreditDetail> detailList = creditTrans.getDetail();
		
		if (detailList == null || detailList.size() < 1) {
			return billPojoList;
		}
		
		Date nowTime = new Date();
		
		for (CreditDetail creditDetail : detailList) {
			
			
			String bank_name = creditDetail.getBank_name();
			String data_source = creditDetail.getDatasource();
			String email = creditDetail.getEmail();
			
			
			
			List<CreditBillInfo> billList = creditDetail.getBill_info();
			if (billList != null && billList.size() > 0) {
				
				for (CreditBillInfo bill : billList) {
					
					CreditCardBillInfoPojo billPojo = new CreditCardBillInfoPojo();
					billPojo.setRequestId(requestId);
					billPojo.setBank_name(bank_name);
					billPojo.setDatasource(data_source);
					billPojo.setEmail(email);
					billPojo.setCard_number(bill.getCard_number());
					billPojo.setUser_name(bill.getUser_name());
					billPojo.setInternaldate(bill.getInternaldate());
					billPojo.setStatement_cycle(bill.getStatement_cycle());
					billPojo.setStatement_date(bill.getStatement_date());
					billPojo.setPayment_due_date(bill.getPayment_due_date());
					billPojo.setCreate_date(nowTime);
					billPojo.setUpdate_date(nowTime);
					billPojo.setReceived(bill.getReceived());
					billPojo.setFrom(bill.getFrom());
					
					Set<CreditCardAmtPojo>  amtPojoSet = new HashSet<CreditCardAmtPojo>();
					
					List<CreditBase> creditLimitList = bill.getCredit_limit();
					if (creditLimitList != null && creditLimitList.size() > 0) {
						for (CreditBase creditBase : creditLimitList) {
							CreditCardAmtPojo amtPojo = new CreditCardAmtPojo();
							BeanUtils.copyProperties(amtPojo, creditBase);
							amtPojo.setKey_code("CREDIT");
							amtPojo.setCreate_date(nowTime);
							amtPojo.setUpdate_date(nowTime);
							
							amtPojoSet.add(amtPojo);
						}
					}
					List<CreditBase> cashLimitList = bill.getCash_advance_limit();
					if (cashLimitList != null && cashLimitList.size() > 0) {
						for (CreditBase creditBase : cashLimitList) {
							CreditCardAmtPojo amtPojo = new CreditCardAmtPojo();
							BeanUtils.copyProperties(amtPojo, creditBase);
							amtPojo.setKey_code("CASH");
							amtPojo.setCreate_date(nowTime);
							amtPojo.setUpdate_date(nowTime);
							
							amtPojoSet.add(amtPojo);
						}
					}
					List<CreditBase> balanceList = bill.getCurrent_balance();
					if (balanceList != null && balanceList.size() > 0) {
						for (CreditBase creditBase : balanceList) {
							CreditCardAmtPojo amtPojo = new CreditCardAmtPojo();
							BeanUtils.copyProperties(amtPojo, creditBase);
							creditBase.getAmount();
							amtPojo.setKey_code("BALANCE");
							amtPojo.setCreate_date(nowTime);
							amtPojo.setUpdate_date(nowTime);
							
							amtPojoSet.add(amtPojo);
						}
					}
					List<CreditBase> minPayList = bill.getMinimum_payment_due();
					if (minPayList != null && minPayList.size() > 0) {
						for (CreditBase creditBase : minPayList) {
							CreditCardAmtPojo amtPojo = new CreditCardAmtPojo();
							BeanUtils.copyProperties(amtPojo, creditBase);
							amtPojo.setKey_code("MINIMUM_PAY");
							amtPojo.setCreate_date(nowTime);
							amtPojo.setUpdate_date(nowTime);
							
							amtPojoSet.add(amtPojo);
						}
					}
					
					billPojo.setAmtSet(amtPojoSet);
					
					List<CreditInstallment> installmentList = bill.getInstallment_plan_info();
					if (installmentList != null && installmentList.size() > 0) {
						Set<CreditCardInstallmentPojo> installPojoSet = new HashSet<CreditCardInstallmentPojo>();
						for (CreditInstallment installment : installmentList) {
							CreditCardInstallmentPojo installPojo = new CreditCardInstallmentPojo();
							BeanUtils.copyProperties(installPojo, installment);
							installPojo.setCreate_date(nowTime);
							installPojo.setUpdate_date(nowTime);
							
							installPojoSet.add(installPojo);
						}
						
						billPojo.setInstallmentSet(installPojoSet);
					}
					List<CreditTransDetail> transDetaiList = bill.getTransaction_detail();
					if (transDetaiList != null && transDetaiList.size() > 0) {
						Set<CreditCardTransDetailPojo> detailPojoSet = new HashSet<CreditCardTransDetailPojo>();
						for (CreditTransDetail transDetail : transDetaiList) {
							CreditCardTransDetailPojo detailPojo = new CreditCardTransDetailPojo();
							BeanUtils.copyProperties(detailPojo, transDetail);
							detailPojo.setCreate_date(nowTime);
							detailPojo.setUpdate_date(nowTime);
							
							detailPojoSet.add(detailPojo);
						}
						
						billPojo.setTransDetailSet(detailPojoSet);
					}
					List<CreditTransSum> transSumList = bill.getTransaction_summary();
					if (transSumList != null && transSumList.size() > 0) {
						Set<CreditCardTransSumPojo> sumPojoSet = new HashSet<CreditCardTransSumPojo>();
						for (CreditTransSum transSum : transSumList) {
							CreditCardTransSumPojo sumPojo = new CreditCardTransSumPojo();
							BeanUtils.copyProperties(sumPojo, transSum);
							sumPojo.setCreate_date(nowTime);
							sumPojo.setUpdate_date(nowTime);
							
							sumPojoSet.add(sumPojo);
						}
						
						billPojo.setTransSumSet(sumPojoSet);
					}
					
					billPojoList.add(billPojo);
				}
				
				
			}
		}
		
		
		return billPojoList;
		
	}

	/**
	 *  将返回报文解析成输出数据
	 * @param creditTransaction
	 * @return
	 */
	private TreeMap<String, Object> parseBeanToOut(
			CreditTransaction creditTrans,JsonConfig config) {
		
		List<CreditDetail> detailList = creditTrans.getDetail();
		if (detailList == null || detailList.size() < 1) {
			return null;
		}
		TreeMap<String, Object> retData = new TreeMap<String, Object>();
		List<Map<String, Object>> billDetailList = new ArrayList<Map<String,Object>>();

		retData.put("bill_result_size", detailList.size());
		
		for (CreditDetail detail : detailList) {
			
			Map<String, Object> billDetailMap = new HashMap<String, Object>();
			
			Map<String, String> bankMap = new HashMap<String, String>();
			String bank_name = detail.getBank_name();
			String data_source = detail.getDatasource();
			String email = detail.getEmail();
			bank_name = StringUtil.isEmpty(bank_name) ? "" : bank_name;
			data_source = StringUtil.isEmpty(data_source) ? "" : data_source;
			email = StringUtil.isEmpty(email) ? "" : email;
			bankMap.put("bank_name", bank_name);
			bankMap.put("bank_code", data_source);
			bankMap.put("email", email);
			
			billDetailMap.put(CREDIT_BANK_INFO, JSONObject.fromObject(bankMap));
			
			List<CreditBillInfo> billList = detail.getBill_info();
			if (billList != null && billList.size() > 0) {
				List<Map<String, String>> billMapList = new ArrayList<Map<String,String>>();
				for (CreditBillInfo billInfo : billList) {
					Map<String, String> billMap = new HashMap<String, String>();
					
					Map<String, String> billBasic = new HashMap<String, String>();					
					billBasic.put(
							"card_no",
							billInfo.getCard_number() == null ? "" : billInfo
									.getCard_number());
					billBasic.put(
							"card_user",
							billInfo.getUser_name() == null ? "" : billInfo
									.getUser_name());
					billBasic.put("statement_date",
							billInfo.getStatement_date() == null ? ""
									: billInfo.getStatement_date());
					billBasic.put("payment_due_date",
							billInfo.getPayment_due_date() == null ? ""
									: billInfo.getPayment_due_date());
					billBasic.put("statement_cycle",
							billInfo.getStatement_cycle() == null ? ""
									: billInfo.getStatement_cycle());
					billBasic.put(
							"received",
							billInfo.getReceived() == null ? "" : billInfo
									.getReceived());
					billBasic.put("from", billInfo.getFrom() == null ? ""
							: billInfo.getFrom());

					billMap.put(CREDIT_BILL_BASIC, JSONObject.fromObject(billBasic).toString());
					
					List<CreditBase> creditLimitList = billInfo.getCredit_limit();
					billMap.put(CREDIT_LIMIT, JSONArray.fromObject(creditLimitList,config).toString());
					List<CreditBase> cashLimitList = billInfo.getCash_advance_limit();
					billMap.put(CREDIT_CASH_LIMIT, JSONArray.fromObject(cashLimitList,config).toString());
					List<CreditBase> currentBalanceList = billInfo.getCurrent_balance();
					billMap.put(CREDIT_CUR_BALANCE, JSONArray.fromObject(currentBalanceList,config).toString());
					List<CreditBase> minPayList = billInfo.getMinimum_payment_due();
					billMap.put(CREDIT_MIN_PAY, JSONArray.fromObject(minPayList, config).toString());
					List<CreditTransSum> transSumList = billInfo.getTransaction_summary();
					billMap.put(CREDIT_TRANS_SUM, JSONArray.fromObject(transSumList, config).toString());
					List<CreditTransDetail> transDetailList = billInfo.getTransaction_detail();
					billMap.put(CREDIT_TRANS_DEL, JSONArray.fromObject(transDetailList, config).toString());
					List<CreditInstallment> installPlanList = billInfo.getInstallment_plan_info();
					billMap.put(CREDIT_INSTALL_PLAN, JSONArray.fromObject(installPlanList, config).toString());
					
					billMapList.add(billMap);
					
				}
				
				billDetailMap.put(CREDIT_BILL_INFO, billMapList);
				
			}
			
			billDetailList.add(billDetailMap);
		}
		
		retData.put("bill_result_size", detailList.size());
		retData.put("bill_result_detail", billDetailList);
		return retData;
	}

	

	
	/**
	 * @param url
	 * @param clientSecret
	 * @param accessToken
	 * @param token
	 * @param timeOut
	 * @param prefix
	 * @return
	 */
	private JsonObject getCreditReportData(String url,
			String clientSecret, String accessToken, String token, int timeOut,
			String prefix) {
		StringBuffer reportDataUrlBf = new StringBuffer();
		reportDataUrlBf.append(url).append("?").append(JXLConst.CLIENT_SECRET)
				.append("=").append(clientSecret).append("&")
				.append(JXLConst.ACCESS_TOKEN).append("=").append(accessToken)
				.append("&").append(JXLConst.COLL_TOKEN).append("=")
				.append(token);

		logger.info("{} 根据Token获取央行征信报告数据URL为{}", prefix,
				reportDataUrlBf.toString());

		JsonObject jsonResponse = getJsonResponse(reportDataUrlBf.toString(),
				timeOut, prefix);

		return jsonResponse;
	}







	/**
	 * @param accessTokenUrl2
	 * @param orgAccount2
	 * @param clientSecret2
	 * @param hours2
	 * @param i
	 * @param prefix
	 * @return
	 * @throws Exception 
	 */
	private String getAccessToken(String prefix) throws Exception {
		String accessToken = null;
		//获取安全凭证码
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
			accessToken =  accToken.getAccess_token();
		} else {
			logger.info("{} 连接聚信立获取凭证安全码,聚信立返回FALSE", prefix);
			logger.info("{} 连接聚信立获取凭证码返回信息为" + accToken, prefix);
			throw new Exception("access_token_res_false");
		}
		logger.info("{} 获取凭证安全码成功,准备获取报告数据", prefix);
		
		return accessToken;
	}


	public void setAccessTokenUrl(String accessTokenUrl) {
		this.accessTokenUrl = accessTokenUrl;
	}


	public void setCreditBillDataUrl(String creditBillDataUrl) {
		this.creditBillDataUrl = creditBillDataUrl;
	}


	public void setOrgAccount(String orgAccount) {
		this.orgAccount = orgAccount;
	}


	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}


	public void setHours(String hours) {
		this.hours = hours;
	}


	public void setTimeOut(int timeOut) {
		this.timeOut = timeOut;
	}



}


