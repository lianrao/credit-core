/**   
* @Description: 聚信立信用卡账单-提交采集请求
* @author xiaobin.hou  
* @date 2016年7月21日 上午10:23:27 
* @version V1.0   
*/
package com.wanda.credit.ds.client.juxinli.requestor.creditCardBill;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

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
import com.wanda.credit.ds.client.juxinli.bean.creditCardBill.CreditSubRes;
import com.wanda.credit.ds.client.juxinli.bean.creditCardBill.CreditSubResData;
import com.wanda.credit.ds.client.juxinli.util.JXLConst;
import com.wanda.credit.ds.dao.domain.juxinli.creditCardBill.CreditCardApplyPojo;
import com.wanda.credit.ds.dao.iface.juxinli.creditCardBill.IJXLCreditCardApplyService;
import com.wanda.credit.ds.iface.IDataSourceRequestor;

/**
 * @author xiaobin.hou
 *
 */
@DataSourceClass(bindingDataSourceId="ds_jxl_creditCardBill_submit")
public class CreditCardBillSubmitRequestor extends
		BasicJuXinLiDataSourceRequestor implements IDataSourceRequestor {

	private final static Logger logger = LoggerFactory.getLogger(CreditCardBillSubmitRequestor.class);
	
	private final static String RES_CODE = "res_code";
	private final static String RES_MSG = "res_message";
//	private final static String READY_TO_SUB = "100";
	private final static String SUBMIT_ING = "101";
	private final static String SUBMIT_SUC = "102";
	private final static String SUBMIT_FAIL = "103";
	@Autowired
	private IJXLCreditCardApplyService applyService;
	@Autowired
	private IExecutorSecurityService synchExecutorService;
	
	private String submitUrl;
	private int submitTimeOut;
	
	public Map<String, Object> request(String trade_id, DataSource ds) {

		long startTime = System.currentTimeMillis();		
		String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		
		//组织返回对象
		Map<String, Object> rets = new HashMap<String, Object>();
		rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED);
		rets.put(Conts.KEY_RET_MSG, "提交采集请求失败");
		String requestId = "";
		CreditCardApplyPojo applyInfo = null;

		//交易日志信息数据
		DataSourceLogVO logObj = new DataSourceLogVO();
		logObj.setDs_id(ds.getId());
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));	
		logObj.setReq_url(submitUrl);
		logObj.setIncache("0");
		logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
		logObj.setState_msg("交易失败");
		logObj.setBiz_code1(JXLConst.FLAG_FAILED);
		
		try{
			logger.info("{} 获取并解析请求参数" , prefix);
			requestId = ParamUtil.findValue(ds.getParams_in(), paramIds[0]).toString();//交易序列号
			String password = ParamUtil.findValue(ds.getParams_in(), paramIds[1]).toString();//邮箱密码
//			String identity = ParamUtil.findValue(ds.getParams_in(), paramIds[2]).toString();//独立密码 identity
			Object identityObj = ParamUtil.findValue(ds.getParams_in(), "identity");//独立密码 
			String identity = "";
			if (!StringUtil.isEmpty(identityObj)) {
				identity = identityObj.toString();
			}
			logger.info("{} 开始保存请求参数" , prefix);
			saveParamIn(requestId,password,identity,trade_id,logObj);
			
			logger.info("{} 校验request_id的有效性" , prefix);
			applyInfo = applyService.queryApplyInfo(requestId);			
			TreeMap<String, Object> retData = checkRequestId(applyInfo);
			
			retData.put("request_id", requestId);
			
			logObj.setBiz_code2(retData.get(RES_CODE)+"");
			
			if ("100".equals(retData.get(RES_CODE))) {
				
				applyInfo.setStatus(SUBMIT_ING);
				
				logger.info("{} request_id合法" , prefix);
				String token = applyInfo.getToken();
				String reqJson = buildReqJson(token,password,identity);
				long postStart = System.currentTimeMillis();
				JsonObject resJosnObj = postJsonData(submitUrl, reqJson,
						submitTimeOut * 1000, trade_id);
				logger.info("{} 信用卡账单提交采集请求耗时为（ms） {}", prefix,
						System.currentTimeMillis() - postStart);
				
				
				if (resJosnObj == null) {
					logger.info("{} 信用卡账单提交采集请求结果为空 " , prefix);
					throw new Exception(JXLConst.RES_NULL);
				}
				
				CreditSubRes subRes = new Gson().fromJson(resJosnObj, CreditSubRes.class);
				
				if (!subRes.isSuccess()) {
					logger.info("{} 信用卡账单提交采集请求success节点返回不为true {}" , prefix , resJosnObj);
					throw new Exception(JXLConst.RES_SUC_NOT_TRUE);
				}

				CreditSubResData resData = subRes.getData();
				
				if (resData == null) {
					logger.info("{} 信用卡账单提交采集请求data节点不存在 {}" , prefix , resJosnObj);
					throw new Exception(JXLConst.RES_DATA_NULL);
				}
				
				int process_code = resData.getProcess_code();
				String content = resData.getContent();
				applyInfo.setProcess_code(process_code + "");
				applyInfo.setContent(content);
				applyInfo.setApply_type(resData.getType());
				
				logObj.setBiz_code2(process_code + "");
				
				switch (process_code) {
				case 10003:
					applyInfo.setStatus(SUBMIT_FAIL);
					rets.clear();
					retData.put(RES_CODE, "001");
					retData.put(RES_MSG, "账号或密码错误");	
					break;
				case 10005:
					applyInfo.setStatus(SUBMIT_FAIL);
					retData.put(RES_CODE, "003");
					retData.put(RES_MSG, "账号或独立密码错误");
					break;
				case 10008:
					
					applyInfo.setStatus(SUBMIT_SUC);
					
					rets.clear();
					retData.put(RES_CODE, "000");
					retData.put(RES_MSG, "提交采集成功");
					rets.put(Conts.KEY_RET_DATA, resData);
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
					rets.put(Conts.KEY_RET_MSG, "提交采集请求成功");
					
					logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
					logObj.setState_msg("提交采集请求成功");
					logObj.setBiz_code1(JXLConst.FLAG_SUCCESS);
					break;
				case 30000:
					if (!StringUtil.isEmpty(content)
							&& (content.contains("账号")
									|| content.contains("密码") || content
										.contains("用户名"))) {
						applyInfo.setStatus(SUBMIT_FAIL);
						retData.put(RES_CODE, "001");
						retData.put(RES_MSG, "账号或密码错误");
					}else{
						applyInfo.setStatus(SUBMIT_FAIL);
						retData.put(RES_CODE, "999");
						retData.put(RES_MSG, "网站或网络异常,建议结束流程");
					}
					break;					
				default:
					applyInfo.setStatus(SUBMIT_FAIL);
					retData.put(RES_CODE, "999");
					retData.put(RES_MSG, "网站或网络异常,建议结束流程");

					break;
				}

				rets.put(Conts.KEY_RET_DATA, retData);
			}else{
				if ("104".equals(retData.get(RES_CODE))) {
					logger.info("{} 当前令牌超时" , prefix);
					applyInfo.setStatus(SUBMIT_FAIL);
				}
				logger.info("{} request_id不合法 {}" , prefix , retData.get(RES_MSG));
				rets.put(Conts.KEY_RET_DATA, retData);
			}
		}catch(Exception e){
			logger.error("{} 提交采集请求失败 {}" , prefix , e.getMessage());
			applyInfo.setStatus(SUBMIT_FAIL);
		}finally{
			try {
				/** 记录响应状态信息 */
				logObj.setBiz_code3(requestId);
				logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
				DataSourceLogEngineUtil.writeLog(trade_id, logObj);
			} catch (Exception e) {
				logger.error("{} 日志表数据保存异常 {}", prefix, e.getMessage());
			}
		}
		
		try{
			if (applyInfo != null) {
				applyInfo.setUpdate_date(new Date());
				applyService.updateApplyInfo(applyInfo);
			}
		}catch(Exception e){
			logger.error("{} 更新提交采集信息失败" , prefix);
			rets.clear();
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED);
			rets.put(Conts.KEY_RET_MSG, "提交采集请求失败");
		}
		
		long tradeTime = System.currentTimeMillis() - startTime;
		logger.info("{} 聚信立-信用卡账单提交采集请求总共耗时时间为（ms） {}", prefix, tradeTime);
		
		return rets;
	}

	/**
	 * @param token
	 * @param password
	 * @param identity
	 * @return
	 */
	private String buildReqJson(String token, String password, String identity) {
		Map<String, String> reqMap = new HashMap<String, String>();
		
		reqMap.put("token", token);
		reqMap.put("password", password);
		reqMap.put("identity", identity);
		
		return JSONObject.toJSONString(reqMap);
	}

	/**
	 * @param applyInfo
	 * @return
	 */
	private TreeMap<String, Object> checkRequestId(CreditCardApplyPojo applyInfo) {
		
		TreeMap<String, Object> retData = new TreeMap<String, Object>();
		if (applyInfo == null) {
			retData.put(RES_CODE, "099");
			retData.put(RES_MSG, "错误的request_id");
			return retData;
		}
		
		String status = applyInfo.getStatus();
		int statusInt = Integer.parseInt(status);
		switch (statusInt) {
		case 100:
			//提交申请表单成功可提交采集请求
			Date updDate = applyInfo.getUpdate_date();
			long timedif = System.currentTimeMillis() - updDate.getTime();
			if (timedif > 10 * 60 * 1000) {
				retData.put(RES_CODE, "104");
				retData.put(RES_MSG, "request_id已过期,有效期为10分钟");
			}else{
				retData.put(RES_CODE, "100");
				retData.put(RES_MSG, "已完成申请表单");
			}
			break;
		case 101:
			//提交采集中
			retData.put(RES_CODE, "101");
			retData.put(RES_MSG, "提交采集请求中,请稍后重试");
			break;
		case 102:
			//提交采集成功
			retData.put(RES_CODE, "102");
			retData.put(RES_MSG, "已完成采集请求,请勿重复提交");
			break;
		case 103:
			//提交采集失败
			retData.put(RES_CODE, "103");
			retData.put(RES_MSG, "采集请求失败,请重新发起流程");
			break;
		default:
			retData.put(RES_CODE, "099");
			retData.put(RES_MSG, "错误的交易序列号");
			break;
		}
		
		return retData;
	}

	/**
	 * @param requestId
	 * @param password
	 * @param identity
	 * @param trade_id
	 * @param applyPojo
	 * @param logObj 
	 * @return
	 * @throws Exception 
	 */
	private void saveParamIn(String requestId, String password,
			String identity, String trade_id, DataSourceLogVO logObj)
			throws Exception {
		
		
		Map<String, Object> paramIn = new HashMap<String, Object>();
		if (!StringUtil.isEmpty(password)) {
			String encPas = synchExecutorService.encrypt(password);
			paramIn.put("pas", encPas);
		}
		if (!StringUtil.isEmpty(identity)) {
			String encIdentity = synchExecutorService.encrypt(identity);
			paramIn.put("identity", encIdentity);
		}

		paramIn.put("request_id", requestId);
		try{
			DataSourceLogEngineUtil.writeParamIn(trade_id, paramIn, logObj);
		}catch(Exception e){
			logger.error("{} 保存参数异常 {}", trade_id , e.getMessage());
		}
		
	}

	public void setSubmitUrl(String submitUrl) {
		this.submitUrl = submitUrl;
	}

	public void setSubmitTimeOut(int submitTimeOut) {
		this.submitTimeOut = submitTimeOut;
	}

}
