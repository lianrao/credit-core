package com.wanda.credit.ds.client.juxinli;

import java.util.ArrayList;
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

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.api.iface.IExecutorSecurityService;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.DESUtils;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.client.juxinli.bean.AccessToken;
import com.wanda.credit.ds.client.juxinli.bean.IgnoreFieldProcessor;
import com.wanda.credit.ds.client.juxinli.bean.mobile.origin.Basic;
import com.wanda.credit.ds.client.juxinli.bean.mobile.origin.Calls;
import com.wanda.credit.ds.client.juxinli.bean.mobile.origin.Members;
import com.wanda.credit.ds.client.juxinli.bean.mobile.origin.Nets;
import com.wanda.credit.ds.client.juxinli.bean.mobile.origin.RawData;
import com.wanda.credit.ds.client.juxinli.bean.mobile.origin.Smses;
import com.wanda.credit.ds.client.juxinli.bean.mobile.origin.Transaction;
import com.wanda.credit.ds.client.juxinli.bean.mobile.origin.Transactions;
import com.wanda.credit.ds.client.juxinli.service.IJXLMobileRawDataService;
import com.wanda.credit.ds.client.juxinli.util.JXLConst;
import com.wanda.credit.ds.dao.domain.juxinli.apply.ApplyBasicInfoPojo;
import com.wanda.credit.ds.dao.domain.juxinli.mobile.MobileMemberPojo;
import com.wanda.credit.ds.dao.domain.juxinli.mobile.MobileRawDataAccountPojo;
import com.wanda.credit.ds.dao.domain.juxinli.mobile.MobileRawDataCallPojo;
import com.wanda.credit.ds.dao.domain.juxinli.mobile.MobileRawDataOwnerPojo;
import com.wanda.credit.ds.dao.domain.juxinli.mobile.MobileRawDataSmsesPojo;
import com.wanda.credit.ds.iface.IDataSourceRequestor;
/**
 * 根据Token获取运营商原始数据
 * 
 * @author xiaobin.hou
 *
 */
@DataSourceClass(bindingDataSourceId="ds_jxlMobileRawData")
public class JXLMobileRawDataRequestor extends BasicJuXinLiDataSourceRequestor implements
		IDataSourceRequestor {
	
	private final static Logger logger = LoggerFactory.getLogger(JXLMobileRawDataRequestor.class);
	
	@Autowired
	private IJXLMobileRawDataService jxlMobileRawDataService;
	@Autowired
	private IExecutorSecurityService synchExecutorService;
	
	private String accessToken;
	private String token;
	
	private String httpsReportTokenUrl;
	private String httpsRawDataUrl;
	private String orgAccount;
	private String clientSecret;
	private String hours;
	private int timeOut;


	public Map<String, Object> request(String trade_id, DataSource ds) {
		
		String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		
		//组织返回对象
		Map<String, Object> rets = new HashMap<String, Object>();
		TreeMap<String, Object> retdata = new TreeMap<String, Object>();
		
		logger.info("{} 开始根据采集token获取运营商原始数据 BEGIN" + trade_id ,prefix);
		long startTime = System.currentTimeMillis();
		boolean doSave = true;
		//获取请求参数
		String requestId = ParamUtil.findValue(ds.getParams_in(), paramIds[0]).toString();
		Object isOn = ParamUtil.findValue(ds.getParams_in(), JXLConst.SWITCH_FLAG);
		if (isOn != null && JXLConst.SWITCH_ON.equals(isOn.toString())) {
			doSave = false;
		}
		
		logger.info("{} 聚信立根据Token获取运营商原始数据请求requestId为" + requestId ,prefix);
		
		//通过requestId获取对应的Token
		ApplyBasicInfoPojo applyBasicInfo = requestId2Token(requestId);
		if (applyBasicInfo == null) {
			logger.info("{} 聚信立根据Token获取运营商原始数据：根据requestId获取对应Token失败，可能requestId不存在" ,prefix);
			rets.clear();
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_REQUESTID_NOTEXSIT);
			rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_REQUESTID_NOTEXSIT.getRet_msg());
			if(doSave){
				saveTradeInfo(trade_id, JXLConst.TF_GET_MOBILE_RAWDATA,
						CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_REQUESTID_NOTEXSIT
								.getRet_sub_code(), null, null, null, requestId);
			}
	
			return rets;
		}
		token = applyBasicInfo.getToken();
		/*查询缓存中是否有数据 BEGIN*/
		try {
			MobileRawDataOwnerPojo queryRawDataPojo = jxlMobileRawDataService.loadRawData(requestId);
			if (queryRawDataPojo != null) {
				
				//将返回结果转成输出报文
				TreeMap<String, Object> queryRawData = parseToOutPut(queryRawDataPojo);
				logger.info("{} 获取缓存数据成功，直接返回数据" ,prefix);
				rets.put(Conts.KEY_RET_DATA, queryRawData);
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
				rets.put(Conts.KEY_RET_MSG, "获取原始数据成功");
				if(doSave){
					saveTradeInfo(trade_id, JXLConst.TF_GET_MOBILE_RAWDATA,
							CRSStatusEnum.STATUS_SUCCESS.getRet_sub_code(), null,
							null, null, requestId);
				}
				
				return rets;
			}else{
				logger.info("{} 本地没有对应的缓存数据，直接连接聚信立获取运营商原始数据" ,prefix);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("{} 获取本地缓存数据异常，连接聚信立获取运营商原始数据" + ex.getMessage() ,prefix);
		}
		/*查询缓存中是否有数据 END*/
		
		logger.info("{} 根据采集token获取移动运营商原始数据获取安全凭证码,该安全码的有效时长为（单位h）" + hours ,prefix);
		Gson gson = new Gson();
		try{
			JsonObject acceptReportToken = getAcceptReportToken(httpsReportTokenUrl,orgAccount, clientSecret, hours,timeOut * 1000,prefix);
			
			if(acceptReportToken == null){
				logger.info("{} 连接聚信立获取安全凭证码失败,可能网络不通" ,prefix);
				throw new Exception("连接聚信立获取安全凭证码返回为null");
			}
			
			AccessToken accToken = gson.fromJson(acceptReportToken, AccessToken.class);
			
			if("true".equals(accToken.getSuccess())){
				logger.info("{} 连接聚信立获取凭证安全码,聚信立返回TRUE" ,prefix);
				accessToken = accToken.getAccess_token();
			}else{
				logger.info("{} 连接聚信立获取凭证安全码,聚信立返回FALSE" ,prefix);
				logger.info("{} 连接聚信立获取凭证码返回信息为" + accToken, prefix);
				throw new Exception("连接聚信立获取安全码聚信立返回FALSE");
			}
			
		}catch(Exception e){
			logger.error("{} 连接聚信立获取凭证安全码异常",e.getMessage() ,prefix);
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_ACCESS_TOKEN_ERROR);
			rets.put(Conts.KEY_RET_MSG, "生成安全码失败，请稍后再试");
			if (doSave) {
				saveTradeInfo(trade_id, JXLConst.TF_GET_MOBILE_RAWDATA,
						CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_ACCESS_TOKEN_ERROR
								.getRet_sub_code(), null, null, null, requestId);
			}
	
			return rets;
		}		
		logger.info("{} 连接聚信立获取凭证安全码成功,准备获取原数据信息" ,prefix);
		
		JsonObject rawDataJson = null;
		RawData rawDataObj = null;
		String note = "";
		
		try{
			long postStartTime = System.currentTimeMillis();
			rawDataJson = getMobileRawDataByToken(httpsRawDataUrl, clientSecret, accessToken, token, timeOut * 1000,prefix);
			long PostTime = System.currentTimeMillis() - postStartTime;
			logger.info("{} https请求聚信立耗时为（ms）" + PostTime ,prefix);
			
			if(rawDataJson != null && rawDataJson.get(JXLConst.FLAG_SUCCESS) != null){
				if("true".equals(rawDataJson.get(JXLConst.FLAG_SUCCESS).getAsString())){
					
					JsonObject dataJson = rawDataJson.get("raw_data").getAsJsonObject();
					
					if(dataJson != null){
						//将JSON数据转化成对象
						rawDataObj = gson.fromJson(dataJson, RawData.class);
						
						if(rawDataObj != null){
							
							Members members = rawDataObj.getMembers();
							/*将数据转化成domain对象*/
							try {
								logger.info("{} 聚信立根据Token获取运营商原始数据-解析数据并将数据保存到数据库 BEGIN" ,prefix);
								JsonObject membersJson = dataJson.get("members").getAsJsonObject();
								MobileMemberPojo memberPojo = gson.fromJson(membersJson, MobileMemberPojo.class);								
								jxlMobileRawDataService.saveRawData(memberPojo,requestId);
								logger.info("{} 聚信立根据Token获取运营商原始数据-解析数据并将数据保存到数据库 END" ,prefix);
							} catch (Exception e) {
								e.printStackTrace();
								logger.error("{} 聚信立根据Token获取运营商原始数据将数据保存到数据库异常: " + e.getMessage() ,prefix);
							}							
							/**/
							
							String status = members.getStatus();
							
							if(JXLConst.FLAG_SUCCESS.equals(status)){
								logger.info("{} 连接聚信立获取运营商原始数据返回状态码为" + members.getError_code() ,prefix);
								if(31200 == members.getError_code()){
									logger.info("{} 聚信立根据token获取运营商原始数据成功" ,prefix);
									List<Transaction> transactions = members.getTransactions();
									
									Transaction tr = new Transaction();
									if(transactions != null && transactions.size() > 0){
										logger.info("{} 聚信立 根据token获取运营商原始数据返回信息条数为" + transactions.size() ,prefix);
										tr = transactions.get(0);
									}else{
										logger.info("{} 聚信立 根据token获取运营商原始数据返回信息通话信息为0" ,prefix);
									}
									
									//用户基本信息
									retdata.put(JXLConst.MOBILE_RAWDATA_BASIC, JSONObject.toJSONString(tr.getBasic()));
									//用户通话记录
									retdata.put(JXLConst.MOBILE_RAWDATA_CALL, JSONObject.toJSONString(tr.getCalls()));
									//短信信息
									retdata.put(JXLConst.MOBILE_RAWDATA_SMSE, JSONObject.toJSONString(tr.getSmses()));
									//账单信息
									retdata.put(JXLConst.MOBILE_RAWDATA_ACCOUNT, JSONObject.toJSONString(tr.getTransactions()));
									//流量信息暂不对外提供流量信息
									retdata.put(JXLConst.MOBILE_RAWDATA_NET, JSONObject.toJSONString(tr.getNets()));
									retdata.put(JXLConst.MOBILE_RAWDATA_NET, JSONObject.toJSONString(new ArrayList<Nets>()));
								
									
									rets.put(Conts.KEY_RET_DATA, retdata);
									rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
									rets.put(Conts.KEY_RET_MSG, "获取原始数据成功");
								}else{
									//获取运营商原始数据请求失败
									rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED);
									rets.put(Conts.KEY_RET_MSG, "获取原始数据失败");
								}
							}else{
								logger.info("{} 聚信立根据Token获取运营商原始数据status标识不为TRUE" ,prefix);
								//获取运营商原始数据请求失败
								rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED);
								rets.put(Conts.KEY_RET_MSG, "获取原始数据失败");
								
							}
						}else{
							logger.info("根据token获取运营商原始数据转化成对象失败，返回信息为" + dataJson ,prefix);
							throw new Exception("PARSE_RAWDATA2JSON_FAIL");
						}
					}else{
						throw new Exception(JXLConst.RES_DATA_NULL);
					}
				}else{
					logger.info("根据token获取运营商原始数据返回结果为" + rawDataJson ,prefix);
					JsonElement noteEle = rawDataJson.get("note");
					note = (noteEle != null) ? noteEle.getAsString() : "获取报告原始数据失败";
					throw new Exception(JXLConst.RES_SUC_NOT_TRUE);
				}
			}else{
				throw new Exception(JXLConst.RES_NULL);
			}
		} catch (Exception e) {
			String errMsg = e.getMessage();

			if (JXLConst.RES_NULL.equals(errMsg)) {
				logger.error("{} 根据token获取运营商原始数据聚信立返回结果为null或者没有success节点" ,prefix);
				rets.put(Conts.KEY_RET_STATUS,CRSStatusEnum.STATUS_FAILED_SYS_DS_TIMEOUT);
				rets.put(Conts.KEY_RET_MSG, "服务请求超时");
			} else if (JXLConst.RES_SUC_NOT_TRUE.equals(errMsg)) {
				logger.error("{} 根据token获取运营商原始数据聚信立返回success节点内容为" + rawDataJson.get("success") ,prefix);
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED);
				if (note != null && note.contains("token")) {
					note = note.replaceAll("token", "request_id");
				}
				rets.put(Conts.KEY_RET_MSG, note);
			} else if (JXLConst.RES_DATA_NULL.equals(errMsg)) {
				logger.error("{} 根据token获取运营商原始数据聚信立返回data节点对应内容为NULL" ,prefix);
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED);
				rets.put(Conts.KEY_RET_MSG, "获取原始数据失败");
			} else if ("PARSE_RAWDATA2JSON_FAIL".equals(errMsg)) {
				logger.error("{} 根据token获取运营商原始数据将原始数据转化成对象失败" ,prefix);
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED);
				rets.put(Conts.KEY_RET_MSG, "获取原始数据失败");
			}else {
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED);
				rets.put(Conts.KEY_RET_MSG, "获取原始数据失败");
			}

		}
		
		String retCode = "";
		if (rets.containsKey(Conts.KEY_RET_STATUS)) {
			CRSStatusEnum retstatus = CRSStatusEnum.valueOf(rets.get(Conts.KEY_RET_STATUS).toString());
			retCode = retstatus.getRet_sub_code();
		}
		if (doSave) {
			saveTradeInfo(trade_id, JXLConst.TF_GET_MOBILE_RAWDATA, retCode, "", "", "", requestId);
		}
		
			
		
		logger.info("{} 开始根据采集token获取运营商原始数据 END" + trade_id ,prefix);
		long tradeTime = System.currentTimeMillis() - startTime;
		logger.info("{} 根据token获取运营商原始数据总共耗时时间为（ms）" + tradeTime ,prefix);
		
		return rets;
	}

	/**
	 * @param queryRawData
	 * @return
	 * @throws Exception 
	 */
	public TreeMap<String, Object> parseToOutPut(
			MobileRawDataOwnerPojo rawDataPojo) throws Exception {
		TreeMap<String, Object> retdata = new TreeMap<String, Object>();
		
		Basic owerBasicInfo = new Basic();
		String decCellPhone = synchExecutorService.decrypt(rawDataPojo.getCell_phone());
		
		owerBasicInfo.setCell_phone(decCellPhone);
		owerBasicInfo.setIdcard(rawDataPojo.getIdcard());
		owerBasicInfo.setReal_name(rawDataPojo.getReal_name());
		owerBasicInfo.setReg_time(rawDataPojo.getReg_time());
		owerBasicInfo.setUpdate_time(rawDataPojo.getUpdate_time());
		
		Set<MobileRawDataAccountPojo> accountSet = rawDataPojo.getAccountSet();
		List<Transactions> transList = new ArrayList<Transactions>();
		if (accountSet != null && accountSet.size() > 0) {			
			for (MobileRawDataAccountPojo accountPojo : accountSet) {
				Transactions trans = new Transactions();
				BeanUtils.copyProperties(trans, accountPojo);
				trans.setCell_phone(decCellPhone);
				transList.add(trans);
			}
		}
		
		
		Set<MobileRawDataCallPojo> callSet = rawDataPojo.getCallSet();
		List<Calls> callList = new ArrayList<Calls>();
		if (callSet != null && callSet.size() > 0) {
			for (MobileRawDataCallPojo callPojo : callSet) {
				Calls call = new Calls();
				BeanUtils.copyProperties(call, callPojo);
				
				call.setCell_phone(decCellPhone);
				if (!StringUtil.isEmpty(callPojo.getOther_cell_phone())) {
					call.setOther_cell_phone(DESUtils.decode(
							Conts.KEY_DESENC_KEY, callPojo.getOther_cell_phone()));
				}
				callList.add(call);
			}
		}
		
		List<Nets> netsList = new ArrayList<Nets>();
//		Set<MobileRawDataNetPojo> netSet = rawDataPojo.getNetSet();
//		if (netSet != null && netSet.size()>0) {
//			for (MobileRawDataNetPojo netPojo : netSet) {
//				Nets net = new Nets();
//				BeanUtils.copyProperties(net, netPojo);
//				net.setCell_phone(decCellPhone);
//				net.setUpdate_time(rawDataPojo.getUpdate_time());
//				netsList.add(net);
//			}
//		}
		
		Set<MobileRawDataSmsesPojo> smseSet = rawDataPojo.getSmseSet();
		List<Smses> smseList = new ArrayList<Smses>();
		
		if (smseSet != null && smseSet.size() > 0) {
			for (MobileRawDataSmsesPojo smsePojo : smseSet) {
				
				Smses smse = new Smses();				
				BeanUtils.copyProperties(smse, smsePojo);
				
				smse.setCell_phone(decCellPhone);
				if (!StringUtil.isEmpty(smsePojo.getOther_cell_phone())) {
					smse.setOther_cell_phone(DESUtils.decode(
							Conts.KEY_DESENC_KEY, smsePojo.getOther_cell_phone()));
				}
				
				smseList.add(smse);
			}
		}
		
		
	
		
		JsonConfig config = new JsonConfig();
        config.setJsonPropertyFilter(new IgnoreFieldProcessor(false, new String[]{"seqId","requestId","crt_time","upd_time","owerInfo","mainService"}));
		
        retdata.put(JXLConst.MOBILE_RAWDATA_ACCOUNT, JSONArray.fromObject(transList, config).toString());
        retdata.put(JXLConst.MOBILE_RAWDATA_CALL, JSONArray.fromObject(callList, config).toString());
        retdata.put(JXLConst.MOBILE_RAWDATA_NET, JSONArray.fromObject(netsList, config).toString());
        retdata.put(JXLConst.MOBILE_RAWDATA_SMSE, JSONArray.fromObject(smseList, config).toString());
        retdata.put(JXLConst.MOBILE_RAWDATA_BASIC, JSONObject.toJSONString(owerBasicInfo));

		return retdata;
	}

	public String getHttpsReportTokenUrl() {
		return httpsReportTokenUrl;
	}


	public void setHttpsReportTokenUrl(String httpsReportTokenUrl) {
		this.httpsReportTokenUrl = httpsReportTokenUrl;
	}


	public String getHttpsRawDataUrl() {
		return httpsRawDataUrl;
	}


	public void setHttpsRawDataUrl(String httpsRawDataUrl) {
		this.httpsRawDataUrl = httpsRawDataUrl;
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

}
