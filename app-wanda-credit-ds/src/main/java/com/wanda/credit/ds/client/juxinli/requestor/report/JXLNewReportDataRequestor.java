package com.wanda.credit.ds.client.juxinli.requestor.report;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
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
import com.wanda.credit.ds.client.juxinli.bean.reportNew.po.ReportData;
import com.wanda.credit.ds.client.juxinli.bean.reportNew.po.UserInfoCheck;
import com.wanda.credit.ds.client.juxinli.bean.reportNew.vo.ApplicationCheck;
import com.wanda.credit.ds.client.juxinli.bean.reportNew.vo.BehaviorCheck;
import com.wanda.credit.ds.client.juxinli.bean.reportNew.vo.CellBehavior;
import com.wanda.credit.ds.client.juxinli.bean.reportNew.vo.CollectionContact;
import com.wanda.credit.ds.client.juxinli.bean.reportNew.vo.ContactInfo;
import com.wanda.credit.ds.client.juxinli.bean.reportNew.vo.ContactRegion;
import com.wanda.credit.ds.client.juxinli.bean.reportNew.vo.DeliverAddress;
import com.wanda.credit.ds.client.juxinli.bean.reportNew.vo.EbusinessExpense;
import com.wanda.credit.ds.client.juxinli.bean.reportNew.vo.MainService;
import com.wanda.credit.ds.client.juxinli.bean.reportNew.vo.TripInfo;
import com.wanda.credit.ds.client.juxinli.service.IJXLNewReportDataService;
import com.wanda.credit.ds.client.juxinli.util.JXLConst;
import com.wanda.credit.ds.dao.domain.juxinli.apply.ApplyBasicInfoPojo;
import com.wanda.credit.ds.dao.domain.juxinli.report.PersonPojo;
import com.wanda.credit.ds.dao.iface.juxinli.report.IJXLReportPersonService;
import com.wanda.credit.ds.iface.IDataSourceRequestor;
/**
 * 根据Token获取报告 4.2版本
 * @author xiaobin.hou
 *
 */
@DataSourceClass(bindingDataSourceId="ds_jxlReportData_4_2")
public class JXLNewReportDataRequestor extends BasicJuXinLiDataSourceRequestor
		implements IDataSourceRequestor {
	
	private final static Logger logger = LoggerFactory.getLogger(JXLNewReportDataRequestor.class);
	
	private String accessTokenUrl;
	private String reportDataUrl;
	private String orgAccount;
	private String clientSecret;
	private String hours;
	private int timeOut;
	@Autowired
	private IJXLNewReportDataService newReportDataService;
	@Autowired
	private IJXLReportPersonService jxlReportPersonService;

	public Map<String, Object> request(String trade_id, DataSource ds) {
		
		
		
		String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		//组织返回对象
		Map<String, Object> rets = new HashMap<String, Object>();
		TreeMap<String, Object> retdata = new TreeMap<String, Object>();
		
		// 交易日志信息数据
		DataSourceLogVO logObj = new DataSourceLogVO();
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));
		logObj.setDs_id(ds.getId());
		logObj.setReq_url(reportDataUrl);
		logObj.setIncache("0");
		logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
		logObj.setState_msg("交易失败");
		
		logger.info("{} 聚信立根据Token获取报告数据 BEGIN" + trade_id ,prefix);
		long startTime = System.currentTimeMillis();
		//获取请求参数
		String requestId = ParamUtil.findValue(ds.getParams_in(), paramIds[0]).toString();
		
		saveParamIn(requestId,trade_id,logObj);
		/*通过requestId获取对应的Token*/
		ApplyBasicInfoPojo applyBasicInfo = requestId2Token(requestId);
		if (applyBasicInfo == null) {
			logger.info("{} 聚信立根据Token获取报告数据根据requestId获取Token失败，可能requestId不存在或失效" ,prefix);
			rets.clear();
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_REQUESTID_NOTEXSIT);
			rets.put(Conts.KEY_RET_MSG,
					CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_REQUESTID_NOTEXSIT
							.getRet_msg());

			return rets;
		}
		String token = applyBasicInfo.getToken();
		try {
			// 查看缓存中是否有数据
			boolean inCache = isIncache(requestId);
			if (inCache) {
				try {
					logger.info("{} 有缓存数据从缓存中获取", prefix);
					ReportData loadCacheData = newReportDataService
							.loadCacheData(requestId);
					parse2Output(loadCacheData, retdata);
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
					rets.put(Conts.KEY_RET_MSG, "获取报告数据成功");
					rets.put(Conts.KEY_RET_DATA, retdata);

					logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
					logObj.setState_msg("交易成功");
					logObj.setIncache("1");

					return rets;
				} catch (Exception e) {
					logger.info("{} 获取本地缓存失败直接连接聚信立获取： {}", prefix,
							e.getMessage());
				}
			}

			// 获取安全凭证码
			String accessToken = null;
			logger.info("{} 根据Token获取报告数据获取安全凭证码,该安全码的有效时长为（单位h）" + hours,
					prefix);
			Gson gson = new Gson();
			JsonObject acceptReportToken = getAcceptReportToken(accessTokenUrl,
					orgAccount, clientSecret, hours, timeOut * 1000, prefix);

			if (acceptReportToken == null) {
				logger.info("{} http请求返回内容为空", prefix);
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_TIMEOUT);
				logObj.setState_msg("请求超时:获取安全凭证码");
				
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_TIMEOUT);
				rets.put(Conts.KEY_RET_MSG, "数据源请求超时");
				
				return rets;
			}

			AccessToken accToken = gson.fromJson(acceptReportToken,
					AccessToken.class);

			if ("true".equals(accToken.getSuccess())) {
				logger.info("{} 连接聚信立获取凭证安全码,聚信立返回TRUE", prefix);
				accessToken = accToken.getAccess_token();
			} else {
				logger.info("{} 连接聚信立获取凭证安全码,聚信立返回FALSE", prefix);
				logger.info("{} 连接聚信立获取凭证码返回信息为" + accToken, prefix);
				
				logObj.setBiz_code1("jxl_token_fail");
				rets.put(Conts.KEY_RET_STATUS,
						CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_ACCESS_TOKEN_ERROR);
				rets.put(Conts.KEY_RET_MSG, "生成安全码失败，请稍后再试");

				return rets;
			}
			logger.info("{} 聚信立 根据Token获取报告数据 获取凭证安全码成功,准备获取报告数据", prefix);

			// 获取报告的主要信息
			Date nowTime = new Date();
			PersonPojo reportInfo = new PersonPojo();
			reportInfo.setRequestId(requestId);
			reportInfo.setCrt_time(nowTime);
			reportInfo.setUpd_time(nowTime);

			long postStartTime = System.currentTimeMillis();
			JsonObject httpResJsonObj = getReportDataByToken(reportDataUrl,
					clientSecret, accessToken, token, timeOut * 1000, prefix);
			long postTime = System.currentTimeMillis() - postStartTime;
			logger.info("{} https请求聚信立耗时为（ms）：{}", prefix, postTime);

			if (httpResJsonObj == null) {
				logger.info("{} http请求返回内容为空", prefix);
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_TIMEOUT);
				logObj.setState_msg("请求超时");
				
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_TIMEOUT);
				rets.put(Conts.KEY_RET_MSG, "数据源请求超时");
				return rets;
			}

			if (!(httpResJsonObj.has(JXLConst.FLAG_SUCCESS) && "true"
					.equals(httpResJsonObj.get(JXLConst.FLAG_SUCCESS)
							.getAsString()))) {

				logger.info("{} 获取报告失败，返回信息为：{}", prefix,
						httpResJsonObj.toString());
				// 聚信立返回报告错误 保存note信息
				if (httpResJsonObj.has("note")) {
					String note = httpResJsonObj.get("note").getAsString();
					logObj.setState_msg("获取报告失败：" + note);
				}
				// 将请求结果保存到Person表中
				PersonPojo per = new PersonPojo();
				per.setRequestId(requestId);
				per.setSuccess("false");
				per.setToken(token);
				per.setCrt_time(nowTime);
				per.setUpd_time(nowTime);
				jxlReportPersonService.add(per);

				return rets;
			}

			JsonElement reportDataJson = httpResJsonObj.get("report_data");
			ReportData reportData = gson.fromJson(reportDataJson,
					ReportData.class);
			// 保存报告数据
			newReportDataService.addNewReport(reportData, requestId, trade_id);
			// 将返回信息解析为输出信息
			parse2Output(reportData, retdata);
			
			//记录log信息
			logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
			logObj.setState_msg("请求成功");

			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
			rets.put(Conts.KEY_RET_MSG, "请求成功");
			rets.put(Conts.KEY_RET_DATA, retdata);

		}catch (Exception e) {
			logger.error("{} 交易处理异常 {}" , prefix , e.getMessage());
			
		}finally{
			
			logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
			DataSourceLogEngineUtil.writeLog(trade_id, logObj);
		}
				
		long tradeTime = System.currentTimeMillis() - startTime;
		logger.info("{} 根据request_id获取报告数据总共耗时时间为（ms）" + tradeTime ,prefix);
		return rets;
	}

	
	/**
	 * @param requestId
	 * @return
	 */
	private boolean isIncache(String requestId) {
		
		boolean isIn = false;
		PersonPojo person = jxlReportPersonService.queryByRequestId(requestId);
		if (person != null) {
			isIn = true;
		}
		return isIn;
	}


	/**
	 * @param reportData
	 * @param retdata
	 */
	public void parse2Output(ReportData reportData,
			TreeMap<String, Object> retData) {
		
		//用户信息核查
		List<ApplicationCheck> applyInfoCheck = reportData.getApplication_check();
		String applicationCheck = JSONObject.toJSONString(applyInfoCheck);
		retData.put(JXLConst.REPORTDATA_APPLICATION_CHECK, applicationCheck);
		//用户行为核查
		List<BehaviorCheck> behavior_check = reportData.getBehavior_check();
		String behavior = JSONObject.toJSONString(behavior_check);
		retData.put(JXLConst.REPORTDATA_BEHAVIOR_CHECK, behavior);
		//用户通话行为汇总（按月）包括呼入，呼出，流量等汇总
		List<CellBehavior> cell_behavior = reportData.getCell_behavior();
		String cellBehavior = JSONObject.toJSONString(cell_behavior);
		retData.put(JXLConst.REPORTDATA_CELL_BEHAVIOR, cellBehavior);
		//与常用联系人联系状况分析
		List<CollectionContact> collection_contact = reportData.getCollection_contact();
		String collContact = JSONObject.toJSONString(collection_contact);
		retData.put(JXLConst.REPORTDATA_COLL_CONTACT, collContact);
		//通讯行为汇总（按联系人）
		List<ContactInfo> contact_list = reportData.getContact_list();
		String contactInfo = JSONObject.toJSONString(contact_list);
		retData.put(JXLConst.REPORTDATA_CONTACT_LIST, contactInfo);
		//通讯行为汇总（按区域）
		List<ContactRegion> contact_region = reportData.getContact_region();
		String regionInfo = JSONObject.toJSONString(contact_region);
		retData.put(JXLConst.REPORTDATA_CONTACT_REGION, regionInfo);
		//电商快递收货地址信息汇总
		List<DeliverAddress> deliver_address = reportData.getDeliver_address();
		String deliverAddr = JSONObject.toJSONString(deliver_address);
		retData.put(JXLConst.REPORTDATA_DELIVER_ADDRESS, deliverAddr);
		//电商月消费金额汇总
		List<EbusinessExpense> ebusiness_expense = reportData.getEbusiness_expense();
		String ebusiExpense = JSONObject.toJSONString(ebusiness_expense);
		//电商月消费
		retData.put(JXLConst.REPORTDATA_EBUSI_EXPENSE, ebusiExpense);
		//常用电话服务汇总
		List<MainService> main_service = reportData.getMain_service();
		String mainService = JSONObject.toJSONString(main_service);
		retData.put(JXLConst.REPORTDATA_MAIN_SERVICE, mainService);
		//数据验真报告信息
//		Report report = reportData.getReport();
//		String reportInfo = JSONObject.toJSONString(report);
//		retData.put("report_info", reportInfo);
		//行程信息
		List<TripInfo> trip_info = reportData.getTrip_info();
		String tripInfo = JSONObject.toJSONString(trip_info);
		retData.put(JXLConst.REPORTDATA_TRIP_INFO, tripInfo);
		
		UserInfoCheck user_info_check = reportData.getUser_info_check();
		String blackListCheck = JSONObject.toJSONString(user_info_check);
		retData.put(JXLConst.REPORTDATA_USER_INFO_CHECK, blackListCheck);		
			
	}
	/**
	 * @param personName
	 * @param enccardNo
	 * @param encMobile
	 * @return
	 */
	protected boolean saveParamIn(String requestId,
			String trade_id, DataSourceLogVO logObj) {
		boolean isSave = true;
		try {
			Map<String, Object> paramIn = new HashMap<String, Object>();
			paramIn.put("request_id", requestId);
			DataSourceLogEngineUtil.writeParamIn(trade_id, paramIn, logObj);
		} catch (Exception e) {
			logger.info("{}保存入参信息异常{}", trade_id, e.getMessage());
			isSave = false;
		}

		return isSave;
	}

	public void setReportDataUrl(String reportDataUrl) {
		this.reportDataUrl = reportDataUrl;
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
	public void setAccessTokenUrl(String accessTokenUrl) {
		this.accessTokenUrl = accessTokenUrl;
	}
	
	//TODO 该类用来支持测试，需要删除
	public static String readFile(String path, String charSet) {

		InputStreamReader reader = null;
		BufferedReader br = null;
		StringBuffer sb = new StringBuffer("");
		try {
			reader = new InputStreamReader(new FileInputStream(path), charSet);
			br = new BufferedReader(reader);

			String str = null;

			while ((str = br.readLine()) != null) {
				sb.append(str);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
				if (br != null) {
					br.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		return sb.toString();
	}

}
