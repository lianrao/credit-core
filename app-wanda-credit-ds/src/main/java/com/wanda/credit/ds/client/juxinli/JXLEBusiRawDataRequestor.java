package com.wanda.credit.ds.client.juxinli;

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
import net.sf.json.JsonConfig;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.client.juxinli.bean.IgnoreFieldProcessor;
import com.wanda.credit.ds.client.juxinli.bean.ebusi.origin.Address;
import com.wanda.credit.ds.client.juxinli.bean.ebusi.origin.Basic;
import com.wanda.credit.ds.client.juxinli.bean.ebusi.origin.Items;
import com.wanda.credit.ds.client.juxinli.bean.ebusi.origin.Members;
import com.wanda.credit.ds.client.juxinli.bean.ebusi.origin.Transaction;
import com.wanda.credit.ds.client.juxinli.bean.ebusi.origin.Transactions;
import com.wanda.credit.ds.client.juxinli.util.JXLConst;
import com.wanda.credit.ds.dao.domain.juxinli.apply.ApplyBasicInfoPojo;
import com.wanda.credit.ds.dao.domain.juxinli.ebusi.EBusiAddrPojo;
import com.wanda.credit.ds.dao.domain.juxinli.ebusi.EbusiBasicPojo;
import com.wanda.credit.ds.dao.domain.juxinli.ebusi.EbusiItemPojo;
import com.wanda.credit.ds.dao.domain.juxinli.ebusi.EbusiRawDataResPojo;
import com.wanda.credit.ds.dao.domain.juxinli.ebusi.EbusiTransPojo;
import com.wanda.credit.ds.dao.iface.juxinli.ebusi.IJXLEBusiBasicService;
import com.wanda.credit.ds.dao.iface.juxinli.ebusi.IJXLEBusiRawDataResService;
import com.wanda.credit.ds.iface.IDataSourceRequestor;
/**
 * 获取电商的原始数据
 * @author xiaobin.hou
 *
 */
@DataSourceClass(bindingDataSourceId="ds_jxlEBusiRawData")
public class JXLEBusiRawDataRequestor extends BasicJuXinLiDataSourceRequestor
		implements IDataSourceRequestor {
	
	
	private final static Logger logger = LoggerFactory.getLogger(JXLEBusiRawDataRequestor.class);
	@Autowired
	private IJXLEBusiBasicService jxlEBusiBasicService;
	@Autowired
	private IJXLEBusiRawDataResService jxlEBusiRawDataResService;
	
	/*通过配置文件注入的参数  begin*/
	private String accessTokenUrl;
	private String eBusiRawDataUrl;	
	private String orgAccount;
	private String clientSecret;
	private String hours;
	private int timeOut;
	/*通过配置文件注入的参数  end*/
	


	public Map<String, Object> request(String trade_id, DataSource ds) {
		
		String acceptToken;
		String token;
		
		long startTime = System.currentTimeMillis();
		String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		
		//组织返回对象
		Map<String, Object> rets = new HashMap<String, Object>();
		TreeMap<String, Object> retdata = new TreeMap<String, Object>();
		EbusiRawDataResPojo rawDataResPojo = new EbusiRawDataResPojo();
		
		// 交易日志信息数据
		DataSourceLogVO logObj = new DataSourceLogVO();
		logObj.setDs_id(ds.getId());
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));
		logObj.setReq_url(eBusiRawDataUrl);
		logObj.setIncache("0");

		
		logger.info("{} 聚信立根据Token获取电商原始数据" + trade_id);
		//获取请求参数
		String requestId = ParamUtil.findValue(ds.getParams_in(), paramIds[0]).toString();
		String resource_tag = Conts.TAG_SYS_ERROR;
		try {
			//
			rawDataResPojo.setRequestId(requestId);
			rawDataResPojo.setTrade_id(trade_id);
			//保存请求参数到参数表中
			try {
				Map<String, Object> paramIn = new HashMap<String, Object>();
				paramIn.put("request_id", requestId);
				DataSourceLogEngineUtil.writeParamIn(trade_id, paramIn, logObj);
			} catch (Exception e) {
				logger.error("{} 保存参数异常 {}", trade_id, e.getMessage());
			}
			//通过requestId获取对应的Token
			ApplyBasicInfoPojo applyBasicInfo = requestId2Token(requestId);
			
			if (applyBasicInfo == null || StringUtil.isEmpty(applyBasicInfo.getToken())) {
				logger.info("{} 该序列号无效，没有提交过采集请求",prefix);
				throw new Exception(JXLConst.REQUESTID_NO_EXIST);
			}
			
			//判断缓存中是否有数据
			boolean inCache = jxlEBusiBasicService.isInCache(requestId);
			
			if(inCache){
				logObj.setIncache("1");
				logger.info("{} 本地缓存有该序列号对应的数据 {}" , prefix ,requestId);
				try{
					List<EbusiBasicPojo> basicPojoList = jxlEBusiBasicService.getCacheData(requestId);				
					List<Transaction> transactionList = parseToTransaction(basicPojoList);				
					TreeMap<String, Object> parseData2Res = parseData2Res(transactionList);
					resource_tag = Conts.TAG_TST_SUCCESS;
					rets.put(Conts.KEY_RET_DATA, parseData2Res);
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
					rets.put(Conts.KEY_RET_MSG, "获取电商原始数据成功");
				}catch(Exception e){
					logger.error("{} 获取本地缓存数据异常，直接连接聚信立获取电商原始数据", prefix);
					inCache = false;					
				}
		
			}
			
			//缓存中没有相应数据
			if(!inCache){
				
				token = applyBasicInfo.getToken();
				//获取安全凭证码
				acceptToken = getAcceptToken(accessTokenUrl, orgAccount,
						clientSecret, hours, timeOut, prefix);
				
				logger.info("{} 准备连接聚信立获取电商原始数据" , prefix);
				long postStartTime = System.currentTimeMillis();
				JsonObject ebusiRawDataJsonObj = getEbusiRawData(eBusiRawDataUrl,clientSecret,acceptToken,token,timeOut * 1000,prefix);				
				long PostTime = (System.currentTimeMillis() - postStartTime);
				logger.info("{} https请求聚信立获取电商原始数据耗时为（ms）" + PostTime);
				
				if (ebusiRawDataJsonObj == null) {
					logger.info("{} 连接聚信立获取电商原始数据返回结果为空，可能超时或者将返回结果转化成对象失败" , prefix);
					throw new Exception(JXLConst.RES_NULL);
				}
				
				JsonElement successEle = ebusiRawDataJsonObj.get("success");
				
				if (successEle == null) {
					logger.info("{} 调用聚信立获取原始数据success节点为空" , prefix);
					throw new Exception(JXLConst.RES_SUC_NULL);
				}
				
				rawDataResPojo.setStatus(successEle.getAsString());
				
				if (!"true".equals(successEle.getAsString())) {
					logger.info("{} 调用聚信立获取原始数据success节点不为true" , prefix);
					throw new Exception(JXLConst.RES_SUC_NOT_TRUE);
				}
				
				
				
				JsonElement membersEle = ebusiRawDataJsonObj.get("raw_data").getAsJsonObject().get("members");
				
				Members members = new Gson().fromJson(membersEle, Members.class);
				
				int errorCode = members.getError_code();
				String errorMsg = members.getError_msg();
				
				rawDataResPojo.setError_code(errorCode + ""); 
				rawDataResPojo.setError_msg(errorMsg);
				
				switch (errorCode) {
				case 31200:
					
					logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
					logObj.setState_msg("获取电商数据成功");
					resource_tag = Conts.TAG_TST_SUCCESS;
					List<Transaction> transactions = members.getTransactions();
					
					List<EbusiBasicPojo> basicPojoList = parseData2Pojo(transactions,requestId);
					
					if (basicPojoList != null && basicPojoList.size() > 0) {
						try{
							boolean isExist = jxlEBusiBasicService.isInCache(requestId);
							if(!isExist){
								jxlEBusiBasicService.add(basicPojoList);
							}
						}catch(Exception e){
							logger.info("{} 保存电商数据到数据库异常 {}" , prefix , e.getMessage());
						}
						
						
					}
					
					//解析数据并拼装返回结果
					retdata = parseData2Res(transactions);
					rets.put(Conts.KEY_RET_DATA, retdata);
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
					rets.put(Conts.KEY_RET_MSG, "获取电商原始数据成功");
					
					break;

				default:
					
					rets.clear();
					if (!StringUtil.isEmpty(errorMsg)) {
						errorMsg = errorMsg.replace("token", "request_id");
						rets.put(Conts.KEY_RET_MSG, errorMsg);
					}else{
						rets.put(Conts.KEY_RET_MSG, "获取电商原始数据失败");
					}
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED);
					
					
					break;
				}
			
				
				
				
			}
			
			
		} catch (Exception e) {
			String errorMsg = e.getMessage();
			//返回信息
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED);
			rets.put(Conts.KEY_RET_MSG, "获取电商原始数据失败");
			//交易日志信息
			logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
			logObj.setState_msg("交易失败");
			
			if(JXLConst.REQUESTID_NO_EXIST.equals(errorMsg)){
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_REQUESTID_NOTEXSIT);
				rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_REQUESTID_NOTEXSIT.getRet_msg());
			}else if (JXLConst.ACCEPT_TOKEN_RES_ACCESS_TOKEN_NULL.equals(errorMsg)
					|| JXLConst.ACCEPT_TOKEN_SUC_FALSE.equals(errorMsg)) {
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_ACCESS_TOKEN_ERROR);
				rets.put(Conts.KEY_RET_MSG, "生成安全码失败，请稍后再试");
			}else if(JXLConst.ACCEPT_TOKEN_RES_NULL.equals(errorMsg)){
				logger.info("{} 获取安全凭证码网络超时" , prefix);
				//交易日志信息
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_TIMEOUT);
				logObj.setState_msg("连接获取安全凭证码超时");
			}else if(JXLConst.RES_NULL.equals(errorMsg)){
				//交易日志信息
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_TIMEOUT);
				logObj.setState_msg("连接获取电商原始数据超时");
			}
			
		}
		
		logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
		/** 记录响应状态信息 */
		try {
			Date nowTime = new Date();
			rawDataResPojo.setCrt_time(nowTime);
			rawDataResPojo.setUpd_time(nowTime);
			jxlEBusiRawDataResService.add(rawDataResPojo);
			DataSourceLogEngineUtil.writeLog(trade_id, logObj);
		} catch (Exception e) {
			logger.error("{} 日志表数据保存异常 {}", prefix, e.getMessage());
		}
		rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
		long tradeTime = System.currentTimeMillis() - startTime;
		logger.info("{} 聚信立-获取电商原始数据总共耗时时间为（ms） {}", prefix, tradeTime);
		return rets;
		


}

	/**
	 * @param basicPojoList
	 * @return
	 * @throws Exception 
	 * @throws  
	 */
	private List<Transaction> parseToTransaction(List<EbusiBasicPojo> basicPojoList) throws Exception {

		List<Transaction> transList = new ArrayList<Transaction>();
		
		if (basicPojoList != null && basicPojoList.size() > 0) {
			for (EbusiBasicPojo basicPojo : basicPojoList) {
				Transaction trans = new Transaction();
				
				trans.setDatasource(basicPojo.getDatasource());
				trans.setVersion(basicPojo.getVersion());
				//转化成Basic
				Basic basic = new Basic();
				BeanUtils.copyProperties(basic, basicPojo);
				trans.setBasic(basic);
				//转化成Address
				Set<EBusiAddrPojo> addrPojoSet = basicPojo.getAddress();
				if (addrPojoSet != null && addrPojoSet.size() > 0) {
					List<Address> addrList = new ArrayList<Address>();
					for (EBusiAddrPojo addrPojo : addrPojoSet) {
						Address addr = new Address();
						BeanUtils.copyProperties(addr, addrPojo);
						addrList.add(addr);
					}
					trans.setAddress(addrList);
				}
				
				//转化成transactions
				Set<EbusiTransPojo> transPojoSet = basicPojo.getTransactions();
				if (transPojoSet != null && transPojoSet.size() > 0) {
					List<Transactions> transactionsList = new ArrayList<Transactions>();
					for (EbusiTransPojo transPojo : transPojoSet) {
						Transactions transactions = new Transactions();
						Set<EbusiItemPojo> itemsPojoSet = transPojo.getItems();
						if (itemsPojoSet != null && itemsPojoSet.size() > 0) {
							List<Items> itemsList = new ArrayList<Items>();
							for (EbusiItemPojo itemPojo : itemsPojoSet) {
								Items items = new Items();
								BeanUtils.copyProperties(items, itemPojo);
								items.setTrans_time(basicPojo.getUpdate_time());
								itemsList.add(items);
							}
							transactions.setItems(itemsList);
						}
						
						
						transactions.setBill_type(transPojo.getBill_type());
						transactions.setDelivery_fee(transPojo.getDeliver_fee());
						transactions.setDelivery_type(transPojo.getDeliver_type());
						if ("true".equals(transPojo.getIs_success().toLowerCase())) {
							transactions.setIs_success(true);
						}else{
							transactions.setIs_success(false);
						}
					
						transactions.setOrder_id(transPojo.getOrder_id());
						transactions.setPayment_type(transPojo.getPayment_type());
						transactions.setReceiver_addr(transPojo.getReceiver_addr());
						transactions.setReceiver_cell_phone(transPojo.getReceiver_cell_phone());
						transactions.setReceiver_name(transPojo.getReceiver_name());
						transactions.setReceiver_phone(transPojo.getReveiver_phone());
						transactions.setReceiver_title(transPojo.getReveiver_title());
						transactions.setTotal_price(transPojo.getTotal_price());
						transactions.setTrans_time(transPojo.getTrans_time());
						transactions.setZipcode(transPojo.getZipcode());
						
						
						transactionsList.add(transactions);
					}
					trans.setTransactions(transactionsList);
				}
				
				transList.add(trans);
			}
			
		}

		return transList;
	}

	/**
	 * 将聚信立报文转化成POJO类用于保存
	 * 
	 * 
	 * @param requestId 
	 * @param transactions
	 * @return
	 * @throws Exception 
	 * @throws  
	 */
	private List<EbusiBasicPojo> parseData2Pojo(List<Transaction> transactions, String requestId) throws Exception {
		
		if (transactions == null || transactions.size() <1) {
			return null;
		}
		
		Date nowTime = new Date();
		
		List<EbusiBasicPojo> basicPojoList = new ArrayList<EbusiBasicPojo>();
		
		for (Transaction tr : transactions) {
			
			Basic basic = tr.getBasic();
			String cell_phone = basic.getCell_phone();
			String email = basic.getEmail();
			
			EbusiBasicPojo basicPojo = new EbusiBasicPojo();
			BeanUtils.copyProperties(basicPojo, basic);
			
			basicPojo.setToken(tr.getToken());
			basicPojo.setDatasource(tr.getDatasource());
			basicPojo.setVersion(tr.getVersion());
			
			basicPojo.setRequestId(requestId);
			basicPojo.setCrt_time(nowTime);
			basicPojo.setUpd_time(nowTime);
			
			
			List<Address> addressList = tr.getAddress();
			Set<EBusiAddrPojo> addrPojoSet = new HashSet<EBusiAddrPojo>();
			if (addressList != null && addressList.size() > 0) {
				for (Address address : addressList) {
					EBusiAddrPojo addrPojo = new EBusiAddrPojo();
					BeanUtils.copyProperties(addrPojo, address);
					
					addrPojo.setCell_phone(cell_phone);
					addrPojo.setEmail(email);
					addrPojo.setRequestId(requestId);
					addrPojo.setCrt_time(nowTime);
					addrPojo.setUpd_time(nowTime);
					addrPojoSet.add(addrPojo);
				}
			}
			
			basicPojo.setAddress(addrPojoSet);
			
			List<Transactions> transactionsList = tr.getTransactions();
			
			Set<EbusiTransPojo> transPojoSet = new HashSet<EbusiTransPojo>();
			
			if (transactionsList != null && transactionsList.size() > 0) {
				
				for (Transactions transaction : transactionsList) {
					EbusiTransPojo transPojo = new EbusiTransPojo();
					
					transPojo.setBill_type(transaction.getBill_type());
					transPojo.setCell_phone(cell_phone);
					transPojo.setDeliver_fee(transaction.getDelivery_fee());
					transPojo.setDeliver_type(transaction.getDelivery_type());
					transPojo.setEmail(email);
					transPojo.setIs_success(transaction.isIs_success() + "");
					transPojo.setOrder_id(transaction.getOrder_id());
					transPojo.setPayment_type(transaction.getPayment_type());
					transPojo.setReceiver_addr(transaction.getReceiver_addr());
					transPojo.setReceiver_cell_phone(transaction.getReceiver_cell_phone());
					transPojo.setReceiver_name(transaction.getReceiver_name());
					transPojo.setReveiver_phone(transaction.getReceiver_phone());
					transPojo.setReveiver_title(transaction.getReceiver_title());
					transPojo.setTotal_price(transaction.getTotal_price());
					transPojo.setTrans_time(transaction.getTrans_time());
					transPojo.setZipcode(transaction.getZipcode());
					
					
					transPojo.setRequestId(requestId);
					transPojo.setCrt_time(nowTime);
					transPojo.setUpd_time(nowTime);
					Set<EbusiItemPojo> itemPojoSet = transPojo.getItems();
					List<Items> itemsList = transaction.getItems();
					if (itemsList != null && itemsList.size() > 0) {
						for (Items items : itemsList) {
							EbusiItemPojo itemPojo = new EbusiItemPojo();
							BeanUtils.copyProperties(itemPojo, items);
							itemPojo.setRequestId(requestId);
							itemPojo.setCrt_time(nowTime);
							itemPojo.setUpd_time(nowTime);
							itemPojoSet.add(itemPojo);
						}
						transPojo.setItems(itemPojoSet);
					}					
				
					transPojoSet.add(transPojo);
				}
				
				basicPojo.setTransactions(transPojoSet);
			}
			
			basicPojoList.add(basicPojo);
			
		}
		
		return basicPojoList;
	}

	private TreeMap<String, Object> parseData2Res(List<Transaction> transactions) {
		
		JsonConfig config = new JsonConfig();
        config.setJsonPropertyFilter(new IgnoreFieldProcessor(false, new String[]{"seqId","requestId"}));
		
		TreeMap<String, Object> ret = new TreeMap<String, Object>();
//		List<EBusiRawDataRes> resList = new ArrayList<EBusiRawDataRes>();
		
		List<Map<String, String>> retList = new ArrayList<Map<String,String>>();
		
		
		if(transactions != null && transactions.size() > 0){		
			
			for (Transaction tr : transactions) {
				
				List<Address> address = tr.getAddress();
				Basic basic = tr.getBasic();
				String datasource = tr.getDatasource();
				List<Transactions> transactions2 = tr.getTransactions();
				
				Map<String, String> dataMap = new HashMap<String, String>();
				
				dataMap.put("dataSource", datasource);
//				dataMap.put("basic_info", JSONObject.toJSONString(basic));
//				dataMap.put("addresses_info", JSONArray.toJSONString(address));
//				dataMap.put("transactions_info", JSONArray.toJSONString(transactions2));
				
				dataMap.put("basic_info", net.sf.json.JSONObject.fromObject(basic, config).toString());
				dataMap.put("addresses_info", JSONArray.fromObject(address, config).toString());
				dataMap.put("transactions_info", JSONArray.fromObject(transactions2,config).toString());
				
				retList.add(dataMap);		
				
			}
		}
		ret.put("transactions", retList);
		return ret;
	}

	public void setAccessTokenUrl(String accessTokenUrl) {
		this.accessTokenUrl = accessTokenUrl;
	}

	public void seteBusiRawDataUrl(String eBusiRawDataUrl) {
		this.eBusiRawDataUrl = eBusiRawDataUrl;
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
