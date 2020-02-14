/**   
* @Description: 聚信立-获取央行征信报告数据
* @author xiaobin.hou  
* @date 2016年7月7日 上午9:49:09 
* @version V1.0   
*/
package com.wanda.credit.ds.client.juxinli.requestor.PBOCReport;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.client.juxinli.BasicJuXinLiDataSourceRequestor;
import com.wanda.credit.ds.client.juxinli.bean.AccessToken;
import com.wanda.credit.ds.client.juxinli.bean.IgnoreFieldProcessor;
import com.wanda.credit.ds.client.juxinli.bean.PBOCReport.CreditNoOverdueAccount;
import com.wanda.credit.ds.client.juxinli.bean.PBOCReport.CreditOverdueAccount;
import com.wanda.credit.ds.client.juxinli.bean.PBOCReport.CreditRecord;
import com.wanda.credit.ds.client.juxinli.bean.PBOCReport.CreditTransaction;
import com.wanda.credit.ds.client.juxinli.bean.PBOCReport.GuaranteeDetail;
import com.wanda.credit.ds.client.juxinli.bean.PBOCReport.LoanNoOverdueAccount;
import com.wanda.credit.ds.client.juxinli.bean.PBOCReport.LoanOverdueAccount;
import com.wanda.credit.ds.client.juxinli.bean.PBOCReport.QueryDetail;
import com.wanda.credit.ds.client.juxinli.bean.PBOCReport.RecordCreditLoanInfo;
import com.wanda.credit.ds.client.juxinli.bean.PBOCReport.RecordGuarantee;
import com.wanda.credit.ds.client.juxinli.bean.PBOCReport.RecordHousingLoanInfo;
import com.wanda.credit.ds.client.juxinli.bean.PBOCReport.RecordOtherLoanInfo;
import com.wanda.credit.ds.client.juxinli.bean.PBOCReport.RecordQuery;
import com.wanda.credit.ds.client.juxinli.bean.PBOCReport.Summary;
import com.wanda.credit.ds.client.juxinli.service.IJXLPBOCReportDataService;
import com.wanda.credit.ds.client.juxinli.util.JXLConst;
import com.wanda.credit.ds.dao.domain.juxinli.PBOCReport.PBOCApplyPojo;
import com.wanda.credit.ds.dao.domain.juxinli.PBOCReport.PBOCDataGuaranteePojo;
import com.wanda.credit.ds.dao.domain.juxinli.PBOCReport.PBOCDataQueryPojo;
import com.wanda.credit.ds.dao.domain.juxinli.PBOCReport.PBOCDataRecordPojo;
import com.wanda.credit.ds.dao.domain.juxinli.PBOCReport.PBOCDataResPojo;
import com.wanda.credit.ds.dao.domain.juxinli.PBOCReport.PBOCDataSummaryPojo;
import com.wanda.credit.ds.dao.iface.juxinli.PBOCReport.IJXLPBOCDataResService;
import com.wanda.credit.ds.dao.iface.juxinli.PBOCReport.IJXLPBOCReportApplyService;
import com.wanda.credit.ds.iface.IDataSourceRequestor;

/**
 * @author xiaobin.hou
 *
 */
@DataSourceClass(bindingDataSourceId="ds_jxl_creditReport_data")
public class PBOCCreditReportDataRequestor extends
		BasicJuXinLiDataSourceRequestor implements IDataSourceRequestor {
	
	private final static Logger logger = LoggerFactory.getLogger(PBOCCreditReportDataRequestor.class);
	
	private final static String JXL_RES_ERROR = "JUXINLI_RESPONSE_EXCEPTION";
	private final static String BASIC_INFO = "basic_info";// 基本信息
	private final static String HOUSING_LOAN = "housing_loan_info";// 房贷信息
	private final static String CARD_OVERDUE = "credit_overdue_account";// 信用卡逾期明细
	private final static String CARD_NO_OVERDUE = "credit_no_overdue_account";// 信用卡未逾期明细
	private final static String CARD_LOAN = "credit_card_loan_info";// 信用卡贷款信息
	private final static String NO_OVERDUE = "loan_no_overdue_account";// 贷款未逾期明细
	private final static String OVERDUE = "loan_overdue_account";// 贷款逾期明细
	private final static String OTHER_LOAN = "other_loan_info";// 其他贷款信息
	private final static String GUARANTEE = "guarantee_info";// 为他人担保信息
	private final static String SUMMARY = "loan_summarys";// 贷款汇总信息
	private final static String PER_QUERY = "personal_query_logs";// 个人查询记录
	private final static String INS_QUERY = "institution_query_logs";// 机构查询记录
	private final static String QUERY = "query_logs";// 查询记录
	
	@Autowired
	private IJXLPBOCReportApplyService applyService;
	@Autowired
	private IJXLPBOCReportDataService pbocReportDataService;
	@Autowired
	private IJXLPBOCDataResService pbocDataResService;
	
	

	private String accessTokenUrl;
	private String creditReportDataUrl;
	private String orgAccount;
	private String clientSecret;
	private String hours;
	private int timeOut;

	public Map<String, Object> request(String trade_id, DataSource ds) {
		
		long startTime = System.currentTimeMillis();
		
		String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		
		JsonConfig config = new JsonConfig();
	    config.setJsonPropertyFilter(new IgnoreFieldProcessor(false, new String[]{"seqId","requestId","crt_time","upd_time"}));

		
		//组织返回对象
		Map<String, Object> rets = new HashMap<String, Object>();
		TreeMap<String, Object> retdata = new TreeMap<String, Object>();
		//交易日志信息数据
		DataSourceLogVO logObj = new DataSourceLogVO();
		logObj.setDs_id(ds.getId());
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));	
		logObj.setReq_url(creditReportDataUrl);
		logObj.setIncache("0");
		logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
		logObj.setState_msg("交易失败");
		logObj.setBiz_code1(JXLConst.FLAG_FAILED);
		
		try{
			logger.info("{} 获取并解析请求参数" , prefix);
			String requestId = ParamUtil.findValue(ds.getParams_in(), paramIds[0]).toString();//交易序列号
			
			try {
				Map<String, Object> paramIn = new HashMap<String, Object>();
				paramIn.put("request_id", requestId);
				DataSourceLogEngineUtil.writeParamIn(trade_id, paramIn, logObj);
			} catch (Exception e) {
				logger.error("{} 保存参数异常 {}", trade_id, e.getMessage());
			}
			
			//判断request_id是否有效
			PBOCApplyPojo applyInfo = new PBOCApplyPojo();
			applyInfo.setRequestId(requestId);
			applyInfo.setProcess_code(JXLConst.SUCCESS_CODE);
			List<PBOCApplyPojo> applyInfoList = applyService.query(applyInfo);
			
			if (applyInfoList == null || applyInfoList.size() < 1) {
				logger.info("{} 无效request_id或者该request_id提交采集请求失败", prefix);
				throw new Exception(JXLConst.REQUESTID_NO_EXIST);
			}
			
			boolean inCache = pbocDataResService.isInCache(requestId,"31200");
			
			//存在缓存数据，从缓存中获取数据
			if(inCache){
				try{
					PBOCDataResPojo resPojo = new PBOCDataResPojo();
					resPojo.setRequestId(requestId);
					List<PBOCDataResPojo> resPojoList = pbocDataResService.query(resPojo);
					if (resPojoList != null && resPojoList.size() > 0) {
						PBOCDataResPojo pbocDataResPojo = resPojoList.get(0);
						CreditTransaction creditTran = parsePojoToBean(pbocDataResPojo);
						retdata = parseToOutput(creditTran, config);
						retdata.put("res_code", "000");
						retdata.put("res_message", "成功获取数据");
						rets.clear();
						rets.put(Conts.KEY_RET_DATA, retdata);
						rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
						rets.put(Conts.KEY_RET_MSG, "请求成功");
						
						logObj.setBiz_code1(JXLConst.FLAG_SUCCESS);
						logObj.setIncache("1");
						logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
						logObj.setState_msg("交易成功");
					}else{
						inCache = false;
					}
				}catch(Exception e){
					logger.error("{} 从本地获取缓存数据异常" , prefix);
					inCache = false;
				}
				
			}

			//缓存中不存在数据，或者读取缓存数据出现异常，连接聚信立获取数据
			if(!inCache){
				
				//获取聚信立交易系统ID
				String token = applyInfoList.get(0).getToken();				
				String accessToken = getAccessToken(prefix);
				
				//http请求聚信立获取报告数据
				long postStartTime = System.currentTimeMillis();
				JsonObject dataJsonObj = getCreditReportData(creditReportDataUrl,
						clientSecret, accessToken, token, timeOut * 1000,
						prefix);
				long PostTime = System.currentTimeMillis() - postStartTime;
				logger.info("{} https请求聚信立耗时为（ms）" + PostTime, prefix);
				
				//解析返回数据，获取member节点内容
				JsonObject memberJsonObj = getMember(dataJsonObj , prefix);
				//解析member节点，拼接PBOCDataResPojo
				PBOCDataResPojo resPojo = parseToResPojo(memberJsonObj);
				//保存信息
				pbocDataResService.add(resPojo);

				int resCode = memberJsonObj.get("error_code").getAsInt();
				String resMsg = memberJsonObj.get("error_msg").getAsString();
				
				logObj.setBiz_code2("" + resCode);
				logObj.setState_msg(resMsg);
				
				switch (resCode) {
				case 31200:
					
					JsonArray asJsonArray = memberJsonObj.get("transactions")
							.getAsJsonArray();
					JsonElement transactionElement = asJsonArray.get(0);

					CreditTransaction creditTransaction = new Gson().fromJson(
							transactionElement, CreditTransaction.class);
					//保存数据
					pbocReportDataService.addReportData(creditTransaction,requestId,prefix,resPojo);

					//拼装返回报文数据
					retdata = parseToOutput(creditTransaction,config);					
					retdata.put("res_code", "000");
					retdata.put("res_message", "成功获取数据");
					
					//交易日志信息
					logObj.setBiz_code1(JXLConst.FLAG_SUCCESS);
					logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
					logObj.setState_msg(resMsg);
					break;
					
				case 31204:					
					retdata.put("res_code", "100");
					retdata.put("res_message", "没有该用户数据请稍后再试");
					break;

				default:
					retdata.put("res_code", "998");
					retdata.put("res_message", resMsg);
					break;
				}
				rets.clear();
				rets.put(Conts.KEY_RET_DATA, retdata);
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
				rets.put(Conts.KEY_RET_MSG, "请求成功");
				
			}
			
		}catch(Exception e){			
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
			}else if(JXL_RES_ERROR.equals(errMsg)){
				logObj.setState_msg("返回报文不合法");
			}else{
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
	 * @param pbocDataResPojo
	 * @return
	 * @throws Exception 
	 * @throws  
	 */
	private CreditTransaction parsePojoToBean(
			PBOCDataResPojo resPojo) throws Exception {
		CreditTransaction tran = new CreditTransaction();
		CreditRecord creditRecord = new CreditRecord();
		RecordGuarantee recordGuarantee = new RecordGuarantee();
		RecordQuery recordQuery = new RecordQuery();
		RecordCreditLoanInfo creditLoan = new RecordCreditLoanInfo();
		List<CreditNoOverdueAccount> creditNoOverdueList = new ArrayList<CreditNoOverdueAccount>();
		List<CreditOverdueAccount> creditOverdueList = new ArrayList<CreditOverdueAccount>();
		RecordHousingLoanInfo housingLoan = new RecordHousingLoanInfo();
		List<LoanNoOverdueAccount> houseNoOverdueList = new ArrayList<LoanNoOverdueAccount>();
		List<LoanOverdueAccount> houseOverdueList = new ArrayList<LoanOverdueAccount>();
		RecordOtherLoanInfo otherLoan = new RecordOtherLoanInfo();
		List<LoanNoOverdueAccount> otherNoOverdueList = new ArrayList<LoanNoOverdueAccount>();
		List<LoanOverdueAccount> otherOverdueList = new ArrayList<LoanOverdueAccount>();
		List<QueryDetail> perQueryList = new ArrayList<QueryDetail>();
		List<QueryDetail> insQueryList = new ArrayList<QueryDetail>();
		List<Summary> sumList = new ArrayList<Summary>();
		
		tran.setData_source(resPojo.getData_source());
		tran.setQueried_name(resPojo.getQuery_name());
		tran.setQueried_number(resPojo.getQuery_number());
		tran.setQueried_papers(resPojo.getQuery_papers());
		tran.setQuery_marriage(resPojo.getQuery_marriage());
		tran.setReport_id(resPojo.getReport_id());
		tran.setReport_time(resPojo.getReport_time());
		tran.setRequest_time(resPojo.getRequest_time());
		tran.setUpdate_time(resPojo.getUpdate_time());
		
		Set<PBOCDataRecordPojo> recordSet = resPojo.getRecordSet();
		
		if (recordSet != null && recordSet.size() > 0) {
			for (PBOCDataRecordPojo recordPojo : recordSet) {
				if ("1".equals(recordPojo.getCredit_record_type())) {//房贷
					if("0".equals(recordPojo.getIs_overdue())){
						LoanNoOverdueAccount houseNo = new LoanNoOverdueAccount();
						BeanUtils.copyProperties(houseNo, recordPojo);
						houseNoOverdueList.add(houseNo);
					}else if("1".equals(recordPojo.getIs_overdue())){
						LoanOverdueAccount houseOver = new LoanOverdueAccount();
						BeanUtils.copyProperties(houseOver, recordPojo);
						houseOverdueList.add(houseOver);
					}
				}else if("2".equals(recordPojo.getCredit_record_type())){//信用卡
					if("0".equals(recordPojo.getIs_overdue())){
						CreditNoOverdueAccount creditNo = new CreditNoOverdueAccount();
						BeanUtils.copyProperties(creditNo, recordPojo);
						creditNoOverdueList.add(creditNo);
					}else if("1".equals(recordPojo.getIs_overdue())){
						CreditOverdueAccount creditOver = new CreditOverdueAccount();
						BeanUtils.copyProperties(creditOver, recordPojo);
						creditOverdueList.add(creditOver);
					}
				}else if("3".equals(recordPojo.getCredit_record_type())){//其他贷款
					if("0".equals(recordPojo.getIs_overdue())){
						LoanNoOverdueAccount otherNo = new LoanNoOverdueAccount();
						BeanUtils.copyProperties(otherNo, recordPojo);
						otherNoOverdueList.add(otherNo);
					}else if("1".equals(recordPojo.getIs_overdue())){
						LoanOverdueAccount otherOver = new LoanOverdueAccount();
						BeanUtils.copyProperties(otherOver, recordPojo);
						otherOverdueList.add(otherOver);
					}
				}
			}
			housingLoan.setLoan_no_overdue_account(houseNoOverdueList);
			housingLoan.setLoan_overdue_account(houseOverdueList);
			creditLoan.setCredit_no_overdue_account(creditNoOverdueList);
			creditLoan.setCredit_overdue_account(creditOverdueList);
			otherLoan.setLoan_no_overdue_account(otherNoOverdueList);
			otherLoan.setLoan_overdue_account(otherOverdueList);
			creditRecord.setHousing_loan_info(housingLoan);
			creditRecord.setCredit_info(creditLoan);
			creditRecord.setLoan_info(otherLoan);
		}
		
		Set<PBOCDataQueryPojo> querySet = resPojo.getQuerySet();		
		if (querySet != null && querySet.size() > 0) {
			
			for (PBOCDataQueryPojo queryPojo : querySet) {
				QueryDetail queryDetail = new QueryDetail();
				BeanUtils.copyProperties(queryDetail, queryPojo);
				if ("0".equals(queryPojo.getType())) {//机构
					insQueryList.add(queryDetail);
				}else if("1".equals(queryPojo.getType())){//个人
					perQueryList.add(queryDetail);
				}
			}
			
		}
		
		Set<PBOCDataGuaranteePojo> guaranteeSet = resPojo.getGuaranteeSet();
		if (guaranteeSet != null && guaranteeSet.size() > 0) {
			List<GuaranteeDetail> guaranteeDetailList = new ArrayList<GuaranteeDetail>();
			for (PBOCDataGuaranteePojo pbocDataGuaranteePojo : guaranteeSet) {
				GuaranteeDetail detail = new GuaranteeDetail();
				BeanUtils.copyProperties(detail, pbocDataGuaranteePojo);
				guaranteeDetailList.add(detail);
			}
			recordGuarantee.setGuarantee_detail(guaranteeDetailList);
			creditRecord.setGuarantee(recordGuarantee);
		}
		
		Set<PBOCDataSummaryPojo> summarySet = resPojo.getSummarySet();		
		if (summarySet != null && summarySet.size() > 0) {			
			for (PBOCDataSummaryPojo sumPojo : summarySet) {
				Summary sum = new Summary();
				BeanUtils.copyProperties(sum, sumPojo);
				sumList.add(sum);
			}
			
		}
		creditRecord.setSummarys(sumList);
		recordQuery.setInstitution_query_details(insQueryList);
		recordQuery.setPersonal_query_details(perQueryList);
		tran.setQuery(recordQuery);
		tran.setCredit_record(creditRecord);
		return tran;
	}


	/**
	 * 拼装PBOCDataResPojo
	 * 
	 * @param memberJsonObj
	 * @return
	 */
	private PBOCDataResPojo parseToResPojo(JsonObject memberJsonObj) {
		
		PBOCDataResPojo resPojo = new PBOCDataResPojo();
		resPojo.setError_code("99999");
		String status = memberJsonObj.get("status").getAsString();
		String update_time = memberJsonObj.get("update_time").getAsString();
		JsonArray requestArgs = memberJsonObj.get("request_args")
				.getAsJsonArray();
		
		String reqToken = requestArgs.get(0).getAsJsonObject()
				.get("token").getAsString();
		String reqEnv = requestArgs.get(1).getAsJsonObject().get("env")
				.getAsString();

		String resCode = memberJsonObj.get("error_code").getAsString();
		String resMsg = memberJsonObj.get("error_msg").getAsString();
		
		resPojo.setStatus(status);
		resPojo.setEnv(reqEnv);
		resPojo.setToken(reqToken);
		resPojo.setError_code(resCode);
		resPojo.setError_msg(resMsg);
		resPojo.setUpdate_time(update_time);
		resPojo.setCreate_date(new Date());
		resPojo.setUpdate_date(new Date());
		return resPojo;
	}


	/**
	 * 构建输出结果数据
	 * @param cretitTransaction
	 * @param config 
	 * @return
	 */
	private TreeMap<String, Object> parseToOutput(CreditTransaction cretitTransaction, JsonConfig config) {

		TreeMap<String, Object> retData = new TreeMap<String, Object>();
		HashMap<String, String> basicMap = new HashMap<String, String>();
		HashMap<String, String> houseLoanMap = new HashMap<String, String>();
		HashMap<String, String> creditLoanMap = new HashMap<String, String>();
		HashMap<String, String> otherLoanMap = new HashMap<String, String>();
		HashMap<String, String> queryLogMap = new HashMap<String, String>();
		
		//基础信息
		basicMap.put("report_time", cretitTransaction.getReport_time());
		basicMap.put("update_time", cretitTransaction.getUpdate_time());
		basicMap.put("cardNo", cretitTransaction.getQueried_number());
		basicMap.put("card_type", cretitTransaction.getQueried_papers());
		basicMap.put("marriage_info", cretitTransaction.getQuery_marriage());
		//查询信息
		RecordQuery query = cretitTransaction.getQuery();
		List<QueryDetail> personal_query_details = new ArrayList<QueryDetail>();
		List<QueryDetail> institution_query_details = new ArrayList<QueryDetail>();
		if (query != null) {
			personal_query_details = query.getPersonal_query_details();
			institution_query_details = query.getInstitution_query_details();
		}
		
		List<LoanNoOverdueAccount> housingLoanNoOverdueAccount = new ArrayList<LoanNoOverdueAccount>();
		List<LoanOverdueAccount> housingLoanOverdueAccount = new ArrayList<LoanOverdueAccount>();
		List<LoanNoOverdueAccount> otherLoanNoOverdueAccount = new ArrayList<LoanNoOverdueAccount>();
		List<LoanOverdueAccount> otherLoanOverdueAccount = new ArrayList<LoanOverdueAccount>();
		List<CreditNoOverdueAccount> creditNoOverdueAccount = new ArrayList<CreditNoOverdueAccount>();
		List<CreditOverdueAccount> creditOverdueAccount = new ArrayList<CreditOverdueAccount>();
		
		
		//贷款记录
		CreditRecord credit_record = cretitTransaction.getCredit_record();
		//房贷
		RecordHousingLoanInfo housingLoan = credit_record.getHousing_loan_info();
		if (housingLoan != null) {
			housingLoanNoOverdueAccount = housingLoan.getLoan_no_overdue_account();
			housingLoanOverdueAccount = housingLoan.getLoan_overdue_account();
		}
		
		//信用卡
		RecordCreditLoanInfo creditInfo = credit_record.getCredit_info();
		if (creditInfo != null) {
			creditNoOverdueAccount = creditInfo.getCredit_no_overdue_account();
			creditOverdueAccount = creditInfo.getCredit_overdue_account();
		}
		
		//其他贷款
		RecordOtherLoanInfo loanInfo = credit_record.getLoan_info();
		if (loanInfo != null) {
			otherLoanNoOverdueAccount = loanInfo.getLoan_no_overdue_account();
			otherLoanOverdueAccount = loanInfo.getLoan_overdue_account();
		}
		
		//担保信息
		RecordGuarantee guarantee = credit_record.getGuarantee();
		List<GuaranteeDetail> guarantee_detail = new ArrayList<GuaranteeDetail>();
		if (guarantee != null) {
			guarantee_detail = guarantee.getGuarantee_detail();
		}
		
		//贷款汇总
		List<Summary> summarys = credit_record.getSummarys();
		

		retData.put(BASIC_INFO, JSONObject.fromObject(basicMap));
		
		houseLoanMap.put(OVERDUE,
				JSONArray.fromObject(housingLoanOverdueAccount, config)
						.toString());
		houseLoanMap.put(NO_OVERDUE,
				JSONArray.fromObject(housingLoanNoOverdueAccount, config)
						.toString());
		retData.put(HOUSING_LOAN, houseLoanMap);

		creditLoanMap.put(CARD_OVERDUE,
				JSONArray.fromObject(creditOverdueAccount, config).toString());
		creditLoanMap
				.put(CARD_NO_OVERDUE,
						JSONArray.fromObject(creditNoOverdueAccount, config)
								.toString());
		retData.put(CARD_LOAN, creditLoanMap);

		otherLoanMap.put(NO_OVERDUE,
				JSONArray.fromObject(otherLoanNoOverdueAccount, config)
						.toString());
		otherLoanMap.put(OVERDUE,JSONArray.fromObject(otherLoanOverdueAccount, config).toString());
		retData.put(OTHER_LOAN, otherLoanMap);

		retData.put(GUARANTEE, JSONArray.fromObject(guarantee_detail, config).toString());// 担保信息
		retData.put(SUMMARY, JSONArray.fromObject(summarys, config).toString());// 贷款汇总
		queryLogMap.put(PER_QUERY, JSONArray.fromObject(personal_query_details).toString());
		queryLogMap.put(INS_QUERY,JSONArray.fromObject(institution_query_details).toString());
		retData.put(QUERY, queryLogMap);
		
		return retData;
	}







	/**
	 * @param dataJsonObj
	 * @return
	 * @throws Exception 
	 */
	private JsonObject getMember(JsonObject dataJsonObj,String prefix) throws Exception {
		if (dataJsonObj == null) {
			logger.error("{} http请求结果为空,可能为超时或者报文解析失败 ", prefix);
			throw new Exception(JXLConst.RES_NULL);
		}
		
		String successFlag = dataJsonObj.get("success").getAsString();

		if (!"true".equals(successFlag)) {
			logger.info("{} 聚信立接口异常，返回信息为 {}", prefix, dataJsonObj);
			throw new Exception(JXLConst.RES_SUC_NOT_TRUE);
		}
		if(!dataJsonObj.has("raw_data")){
			logger.info("{} 聚信立返回数据中没有raw_data节点数据" , prefix);
			throw new Exception(JXL_RES_ERROR);
		}
		JsonObject raw_data_jsonObj = dataJsonObj.get("raw_data")
				.getAsJsonObject();

		if (!raw_data_jsonObj.has("members")) {
			logger.info("{} 聚信立返回数据中没有members节点" , prefix);
			throw new Exception(JXL_RES_ERROR);
		}
		JsonObject memberJsonObj = raw_data_jsonObj.get("members")
				.getAsJsonObject();
		
		return memberJsonObj;
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


	public void setCreditReportDataUrl(String creditReportDataUrl) {
		this.creditReportDataUrl = creditReportDataUrl;
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
