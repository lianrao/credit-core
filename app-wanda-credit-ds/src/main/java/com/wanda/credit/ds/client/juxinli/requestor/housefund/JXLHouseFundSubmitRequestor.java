/**   
* @Description: 聚信立_公积金_提交采集请求 
* @author xiaobin.hou  
* @date 2016年5月24日 下午2:49:57 
* @version V1.0   
*/
package com.wanda.credit.ds.client.juxinli.requestor.housefund;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
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
import com.wanda.credit.ds.client.juxinli.bean.housefund.HouseSubmitRes;
import com.wanda.credit.ds.client.juxinli.bean.housefund.SubmitResData;
import com.wanda.credit.ds.client.juxinli.util.JXLConst;
import com.wanda.credit.ds.dao.domain.juxinli.housefund.HouseApplyInfoPojo;
import com.wanda.credit.ds.dao.domain.juxinli.housefund.HouseFormDetailPojo;
import com.wanda.credit.ds.dao.domain.juxinli.housefund.HouseFormPojo;
import com.wanda.credit.ds.dao.domain.juxinli.trade.JXLPublicLoadLogPojo;
import com.wanda.credit.ds.dao.iface.juxinli.housefund.IJXLHouseFormService;
import com.wanda.credit.ds.dao.iface.juxinli.housefund.IJXLHouseFundApplyService;
import com.wanda.credit.ds.dao.iface.juxinli.trade.IJXLPublicLoadLogService;
import com.wanda.credit.ds.iface.IDataSourceRequestor;

/**
 * @author xiaobin.hou
 *
 */
@DataSourceClass(bindingDataSourceId="ds_jxlHouseFund_submit")
public class JXLHouseFundSubmitRequestor extends
		BasicJuXinLiDataSourceRequestor implements IDataSourceRequestor {
	
	private final static Logger logger = LoggerFactory.getLogger(JXLHouseFundSubmitRequestor.class);
	private final static String SUBMIT_RESULT_ING = "002";
	private final static String SUBMIT_RESULT_SUC = "000";
	private final static String SUBMIT_RESULT_FAIL = "999";
	private final static String SUBMIT_RESULT_AGAIN = "003";
	@Autowired
	private IJXLHouseFundApplyService jxlHouseFundApplyService;
	@Autowired
	private IJXLPublicLoadLogService jxlPublicLoadLogService;
	@Autowired
	private IJXLHouseFormService jxlHouseFormService;
	@Autowired
	private IExecutorSecurityService synchExecutorService;
	
	private String houseFundSubmitUrl;
	private int timeOut;

	public Map<String, Object> request(String trade_id, DataSource ds) {		
		
		long startTime = System.currentTimeMillis();
		Date nowTime = new Date();
		String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		
		String requestId = StringUtil.getRequstId(40);
		//初始化计费标签
		Set<String> tagsSet = new HashSet<String>();
		tagsSet.add(Conts.TAG_SYS_ERROR);
		//组织返回对象
		Map<String, Object> rets = new HashMap<String, Object>();
		TreeMap<String, Object> retdata = new TreeMap<String, Object>();
		retdata.put(JXLConst.EBUSI_REQUEST_ID, requestId);
		// 用于标识
		StringBuffer errorParamBf = new StringBuffer();
		
		HouseApplyInfoPojo applyInfo = new HouseApplyInfoPojo();
		applyInfo.setTrade_id(trade_id);
		applyInfo.setRequestId(requestId);
		applyInfo.setCreate_time(nowTime);
		applyInfo.setUpdate_time(nowTime);
		applyInfo.setSubmit_result(SUBMIT_RESULT_ING);//提交采集请求中
		
		
		//交易日志信息数据
		DataSourceLogVO logObj = new DataSourceLogVO();
		logObj.setDs_id(ds.getId());
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));	
		logObj.setReq_url(houseFundSubmitUrl);
		logObj.setIncache("0");
		logObj.setBiz_code1(JXLConst.FLAG_FAILED);
		logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
		logObj.setState_msg("交易失败");
		
		logger.info("{} 公积金提交采集请求开始" , prefix);	
		
		try{
			logger.info("{} 开始解析传入的参数" , prefix);
			String websiteEn = ParamUtil.findValue(ds.getParams_in(), paramIds[0]).toString();
			String websiteSort = ParamUtil.findValue(ds.getParams_in(), paramIds[1]).toString();
			String submitType = ParamUtil.findValue(ds.getParams_in(), paramIds[2]).toString().toLowerCase();
			String name = ParamUtil.findValue(ds.getParams_in(), paramIds[3]).toString();
			String idCardNum = ParamUtil.findValue(ds.getParams_in(), paramIds[4]).toString();
			String cellPhoneNum = ParamUtil.findValue(ds.getParams_in(), paramIds[5]).toString();
			logger.info("{} 解析传入的参数成功" , prefix);
			
			String encIdCard = synchExecutorService.encrypt(idCardNum);
			String encCellPhone = synchExecutorService.encrypt(cellPhoneNum);
			// 保存该次交易请求
			applyInfo.setWebsite(websiteEn);
			applyInfo.setSort(websiteSort);
			applyInfo.setSubmit_type(submitType);
			applyInfo.setName(name);
			applyInfo.setId_card(encIdCard);
			applyInfo.setCell_phone(encCellPhone);
			jxlHouseFundApplyService.add(applyInfo);

			//判断是否为必要参数
			HouseFormPojo param = new HouseFormPojo();
			param.setWebsite(websiteEn);
			param.setSortId(websiteSort);
			List<HouseFormPojo> queryList = jxlHouseFormService.query(param);
			if (queryList == null || queryList.size() < 1) {
				logger.info("{} 当前sortId没有匹配到提交表单需要的参数，可能没有调用获取表单信息接口" , prefix);
				//TODO
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED);
				rets.put(Conts.KEY_RET_MSG, "请先调用获取表单数据接口");
				
				logObj.setState_msg("交易失败-没有调用获取表单信息接口");
				return rets;
			}
			HouseFormPojo houseFormPojo = queryList.get(0);
			if (!submitType.equals(houseFormPojo.getLoginType())) {
				logger.info("{} 提交方式错误：{}" , prefix , submitType);
				//TODO
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_PARAM_ERROR);
				rets.put(Conts.KEY_RET_MSG, "采集方式参数错误");
				
				logObj.setState_msg("交易失败-参数采集方式错误");
				return rets;
			}
			Set<HouseFormDetailPojo> formDetailsSet = houseFormPojo.getHouseFormDetails();
			if (formDetailsSet == null || formDetailsSet.size() < 1) {
				logger.info("{} 当前sortId没有匹配到提交表单需要的参数，可能没有调用获取表单信息接口" , prefix);
				//TODO
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED);
				rets.put(Conts.KEY_RET_MSG, "请先调用获取表单数据接口");
				
				logObj.setState_msg("交易失败-没有调用获取表单信息接口");
				return rets;
			}
			
			//拼接并验证请求参数
			Map<String, Object> reqJxlMap = new HashMap<String, Object>();
			for (HouseFormDetailPojo detailPojo : formDetailsSet) {
				String parameterCode = detailPojo.getParameterCode();
				Object parameterValue = ParamUtil.findValue(ds.getParams_in(), parameterCode);
				if (StringUtil.isEmpty(parameterValue)) {
					logger.info("{} 必要参数"+ parameterCode + "为空" , prefix);
					errorParamBf.append(parameterCode).append(",");
				}else{
					reqJxlMap.put(parameterCode, parameterValue);
				}
			}
			reqJxlMap.put("website", websiteEn);
			reqJxlMap.put("sort", websiteSort);
			reqJxlMap.put("type", submitType);
			
			applyInfo = saveParamIn(trade_id,logObj,reqJxlMap,applyInfo);			
			
			
			if (errorParamBf.length() > 0) {
				logger.info("{} 缺少必要参数： {}" , prefix ,errorParamBf.toString());
				//TODO
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_NO_NECESSARY_PARAMTER);
				rets.put(Conts.KEY_RET_MSG, "缺少必要参数：" + errorParamBf.toString());
				
				logObj.setState_msg("交易失败-缺少必要参数");
				return rets;
			}
			
			String params = JSONObject.toJSONString(reqJxlMap);
			
			JsonObject submitResJsonObj = postJsonData(houseFundSubmitUrl, params, timeOut * 1000 ,prefix);		
			
			applyInfo.setSubmit_result(SUBMIT_RESULT_FAIL);//提交采集请求失败
			tagsSet.clear();
			tagsSet.add(Conts.TAG_TST_FAIL);
			/*	
		  	String readFile = CommonUtil.readFile("d:\\xiaobin.hou\\桌面\\temp\\temp5.txt", "gbk");
			JsonElement parse = new JsonParser().parse(readFile);
			JsonObject submitResJsonObj = parse.getAsJsonObject();
			*/
			
			if (submitResJsonObj == null) {
				logger.error("{} http请求结果为空 " , prefix );
				//TODO
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_TIMEOUT);
				rets.put(Conts.KEY_RET_MSG, "服务请求超时");
				
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_TIMEOUT);
				logObj.setState_msg("请求超时");
				
				return rets;
			}
			
			logger.info("{} 公积金提交采集请求http请求结果为 {}" , prefix , submitResJsonObj);
			
			HouseSubmitRes submitRes = new Gson().fromJson(submitResJsonObj, HouseSubmitRes.class);
			
			if (!submitRes.isSuccess()) {
				logger.info("{} 聚信立返回接口调用失败" , prefix);
				//TODO
				tagsSet.clear();
				tagsSet.add(Conts.TAG_TST_FAIL);
				
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "数据源查询异常");
				
				logObj.setState_msg("交易失败-success节点不为true");
				
				return rets;
			}
			
			SubmitResData resData = submitRes.getData();
			
			if (resData == null) {
				logger.info("{} 提交公积金采集请求返回data节点为空 " , prefix);
				//TODO
				tagsSet.clear();
				tagsSet.add(Conts.TAG_TST_FAIL);
				
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "数据源查询异常");
				
				logObj.setState_msg("交易失败-data节点为空");
				
				return rets;
			}
			
			int proccessCode = resData.getProcess_code();
			String token = resData.getToken();
			String content = resData.getContent();
			
			/**/
			logObj.setBiz_code2(proccessCode + "");
			applyInfo.setProcess_code(proccessCode + "");
			applyInfo.setRes_type(resData.getType());
			applyInfo.setSucess(submitRes.isSuccess()+"");
			applyInfo.setToken(token);
			applyInfo.setContent(content);
			
			/**/
			
			//请求结果状态保存到日志表中
			if (proccessCode == JXLConst.PROCESS_CODE_SUCC) {
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
				logObj.setState_msg((String) DataSourceLogEngineUtil.ERRMAP.get(DataSourceLogEngineUtil.TRADE_STATE_SUCC));
			}else{
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
				logObj.setState_msg((String) DataSourceLogEngineUtil.ERRMAP.get(DataSourceLogEngineUtil.TRADE_STATE_FAIL));
			}
			logObj.setBiz_code1(proccessCode + "");
			logObj.setState_msg(content);
			
			
			switch (proccessCode) {
			case 10008:
				logger.info("{} 公积金提交采集请求成功开始采集数据" , prefix);
				logObj.setBiz_code1(JXLConst.FLAG_SUCCESS);
				
				//提交成功将数据保存到跑批表中
				saveDataToBatch(requestId);
				logger.info("{} 保存requestId到跑批表成功" , prefix);
				//修改提交采集请求结果为成功
				applyInfo.setSubmit_result(SUBMIT_RESULT_SUC);//成功
				//组装返回结果
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
				rets.put(Conts.KEY_RET_MSG, "提交采集请求成功");	
				//组装标签
				tagsSet.clear();
				tagsSet.add(Conts.TAG_TST_SUCCESS);
				
				break;
			case 10015:
				//解析content
				if (StringUtil.isEmpty(content)) {
					throw new Exception("data_content_null");
				}
				
				applyInfo.setSubmit_result(SUBMIT_RESULT_AGAIN);//需要再次提交
				
				JSONObject contentJsonObj = JSONObject.parseObject(content);
				
				JSONArray jsonArray = contentJsonObj.getJSONArray("area");
				
				Iterator<Object> iterator = jsonArray.iterator();
				
				List<Map<String, Object>> idList = new ArrayList<Map<String,Object>>();
				while(iterator.hasNext()) {
					JSONObject next = (JSONObject)iterator.next();
					Map<String, Object> id = new HashMap<String, Object>();
					id.put("id", next.get("id"));
					id.put("name", next.get("name"));					
					idList.add(id);
				}
				
				retdata.put("next_sumit_info", idList);
				rets.put(Conts.KEY_RET_DATA, retdata);
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_SUB_AGAIN);
				rets.put(Conts.KEY_RET_MSG, "请将grab_type为对应的参数选择对应的id值再次提交采集请求");
				
				break;
			case 30000:
				if (StringUtil.isEmpty(content)) {
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_WEBSITE_ERROR);
					rets.put(Conts.KEY_RET_MSG, "无法获取公积金网站数据");
				}else if (content.contains("密码不正确") || content.contains("密码错误")) {
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_PAW_ERROR);
					rets.put(Conts.KEY_RET_MSG, "账号或密码不正确");
				}else {
					if (content.contains("token")) {
						content.replace("token", "request_id");
					}
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED);
					rets.put(Conts.KEY_RET_MSG, content);
				}
				break;
			default:
				logger.info("{} 聚信立公积金接口返回其他异常信息" , prefix);
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED);
				rets.put(Conts.KEY_RET_MSG, "获取公积金数据失败");
				break;
			}
			
		}catch(Exception e){
			applyInfo.setSubmit_result(SUBMIT_RESULT_FAIL);
			
			logger.error("{} 获取公积金原始数据异常 {}", prefix , e.getMessage());
			rets.clear();
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED);
			rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_FAILED.getRet_msg());
			
		}finally{
			
			rets.put(Conts.KEY_RET_TAG,tagsSet.toArray(new String[tagsSet.size()]));
			
			try{
				/**记录响应状态信息*/
				logObj.setTag(StringUtils.join(tagsSet, ";"));
				logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
				DataSourceLogEngineUtil.writeLog(trade_id,logObj);
				
				applyInfo.setUpdate_time(new Date());
//				jxlHouseFundApplyService.add(applyInfo);
				jxlHouseFundApplyService.merge(applyInfo);
//				jxlHouseFundApplyService.updateApplyInfo(applyInfo);
							
			}catch(Exception e){
				logger.error("{} 日志表数据保存异常 {}" , prefix , e.getMessage());
			}
			
			long tradeTime = System.currentTimeMillis() - startTime;
			logger.info("{} 聚信立-公积金提交采集请求总共耗时时间为（ms） {}" , prefix , tradeTime );
		}
		rets.put(Conts.KEY_RET_DATA, retdata);
		return rets;
	}
	
	
	
	
	












	/**
	 * @param requestId
	 * @throws Exception 
	 */
	private void saveDataToBatch(String requestId) throws Exception {
		Date nowTime = new Date();
		JXLPublicLoadLogPojo loadPojo = new JXLPublicLoadLogPojo();
		loadPojo.setRequestId(requestId);
		loadPojo.setLoad_result("0");
		loadPojo.setLoad_times(0);
		loadPojo.setReqid_type("2");
		loadPojo.setCrt_time(nowTime);
		loadPojo.setUpd_time(nowTime);
		
		jxlPublicLoadLogService.add(loadPojo);
		
	}





	/**
	 * @param trade_id
	 * @param logObj
	 * @param reqJxlMap
	 * @return
	 */
	private HouseApplyInfoPojo saveParamIn(String trade_id,
			DataSourceLogVO logObj, Map<String, Object> reqJxlMap,HouseApplyInfoPojo applyInfo) {
		
		if (reqJxlMap == null) {
			return null;
		}
		Map<String, Object> paramIn = new HashMap<String, Object>();
		Set<String> keySet = reqJxlMap.keySet();
		
		try{
			for (String key : keySet) {
				String value = (String)reqJxlMap.get(key);
				if ("id_card_num".equals(key) && !StringUtil.isEmpty(value)) {
//					String encValue = synchExecutorService.encrypt(value);
//					applyInfo.setId_card(encValue);
					paramIn.put(key, applyInfo.getId_card());
				}else if("cell_phone_num".equals(key)){
//					String encValue = synchExecutorService.encrypt(value);
//					applyInfo.setCell_phone(encValue);
					paramIn.put(key, applyInfo.getCell_phone());
				}else if("account".equals(key)){
					String encValue = synchExecutorService.encrypt(value);
					applyInfo.setAcct(encValue);
					paramIn.put(key, encValue);
				}else if("password".equals(key)){
					//不保存用户的密码信息
				}else if("name".equals(key)){
					applyInfo.setName(value);
					paramIn.put(key, value);
				}else{
					paramIn.put(key, value);
				}
			}
			DataSourceLogEngineUtil.writeParamIn(trade_id, paramIn, logObj);
		}catch(Exception e){
			logger.error("{} 加密入参或保存失败" , trade_id);
		}
				
		return applyInfo;
	}



	/**
	 * @param trade_id
	 * @param logObj
	 * @param websiteEn
	 * @param websiteSort
	 * @param submitType
	 * @param name
	 * @param cardNo
	 * @param mobileNo
	 * @param account
	 * @return 
	 */
	@SuppressWarnings("unused")
	private HouseApplyInfoPojo saveParamIn(String trade_id, DataSourceLogVO logObj,
			String websiteEn, String websiteSort, String submitType,
			String name, String cardNo, String mobileNo, Object account) {
		
		HouseApplyInfoPojo applyInfo = new HouseApplyInfoPojo();
		Date nowTime = new Date();
		
		applyInfo.setId_card(cardNo);
		applyInfo.setCell_phone(mobileNo);
		applyInfo.setId_card(cardNo);
		applyInfo.setName(name);
		applyInfo.setSort(websiteSort);
		applyInfo.setSubmit_type(submitType);
		applyInfo.setWebsite(websiteEn);
		applyInfo.setCreate_time(nowTime);
		applyInfo.setUpdate_time(nowTime);
		

		Map<String, Object> paramIn = new HashMap<String, Object>();
		paramIn.put("website_en", websiteEn);
		paramIn.put("website_sort", websiteSort);
		paramIn.put("submit_type", submitType);
		paramIn.put("name", name);
		paramIn.put("cardNo", cardNo);
		paramIn.put("mobileNo", mobileNo);
		try {
			String encAccount = null;
			if (!StringUtil.isEmpty(account)) {
				encAccount = synchExecutorService.encrypt(account.toString());
				paramIn.put("account", encAccount);
				applyInfo.setAcct(encAccount);
			}
			DataSourceLogEngineUtil.writeParamIn(trade_id, paramIn, logObj);
		} catch (Exception e) {
			logger.error("{} 保存参数异常 {}", trade_id, e.getMessage());
		}
		
		return applyInfo;
		
	}








	public void setTimeOut(int timeOut) {
		this.timeOut = timeOut;
	}
	public void setHouseFundSubmitUrl(String houseFundSubmitUrl) {
		this.houseFundSubmitUrl = houseFundSubmitUrl;
	}
	
	

}
