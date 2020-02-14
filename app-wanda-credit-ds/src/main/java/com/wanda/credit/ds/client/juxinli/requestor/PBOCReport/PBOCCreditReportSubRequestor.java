/**   
* @Description: 聚信立-央行个人征信报告提交采集请求
* @author xiaobin.hou  
* @date 2016年7月5日 上午9:49:09 
* @version V1.0   
*/
package com.wanda.credit.ds.client.juxinli.requestor.PBOCReport;

import java.net.SocketTimeoutException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.http.conn.ConnectTimeoutException;
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
import com.wanda.credit.base.exception.ServiceException;
import com.wanda.credit.base.util.CardNoValidator;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.client.juxinli.BasicJuXinLiDataSourceRequestor;
import com.wanda.credit.ds.client.juxinli.util.JXLConst;
import com.wanda.credit.ds.dao.domain.juxinli.PBOCReport.PBOCApplyPojo;
import com.wanda.credit.ds.dao.iface.juxinli.PBOCReport.IJXLPBOCReportApplyService;
import com.wanda.credit.ds.iface.IDataSourceRequestor;

/**
 * @author xiaobin.hou
 *
 */
@DataSourceClass(bindingDataSourceId="ds_jxl_creditReport_submit")
public class PBOCCreditReportSubRequestor extends
		BasicJuXinLiDataSourceRequestor implements IDataSourceRequestor {
	
	private final static Logger logger = LoggerFactory.getLogger(PBOCCreditReportSubRequestor.class);	
	private final static String T = "true";
	private final static String F = "false";
	
	@Autowired
	private IJXLPBOCReportApplyService applyService;
	@Autowired
	private IExecutorSecurityService synchExecutorService;

	private String creditReportUrl;
	private int timeOut;

	@SuppressWarnings("unchecked")
	public Map<String, Object> request(String trade_id, DataSource ds) {
		
		String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		
		Date nowTime = new Date();
		String requestId = StringUtil.getRequstId(40);
		//组织返回对象
		Map<String, Object> rets = new HashMap<String, Object>();
		TreeMap<String, Object> retdata = new TreeMap<String, Object>();
		//提交采集信息
		PBOCApplyPojo applyInfo = new PBOCApplyPojo();
		applyInfo.setRequestId(requestId);
		applyInfo.setSucess(F);
		applyInfo.setCreate_date(nowTime);
		applyInfo.setUpdate_date(nowTime);
		//交易日志信息数据
		DataSourceLogVO logObj = new DataSourceLogVO();
		logObj.setDs_id(ds.getId());
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));	
		logObj.setReq_url(creditReportUrl);
		logObj.setIncache("0");
		logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
		logObj.setState_msg("交易失败");
		logObj.setBiz_code1(JXLConst.FLAG_FAILED);
		
		try{
			logger.info("{} 获取并解析请求参数" , prefix);
//			name,cardNo,mobile,account,password,captcha
			String name = ParamUtil.findValue(ds.getParams_in(), paramIds[0]).toString();//姓名
			String cardNo = ParamUtil.findValue(ds.getParams_in(), paramIds[1]).toString();//身份证号
			String mobile = ParamUtil.findValue(ds.getParams_in(), paramIds[2]).toString();//手机号
			String account = ParamUtil.findValue(ds.getParams_in(), paramIds[3]).toString();//账号
			String password = ParamUtil.findValue(ds.getParams_in(), paramIds[4]).toString();//密码
			String captcha = ParamUtil.findValue(ds.getParams_in(), paramIds[5]).toString();//短信验证码
			
			//敏感信息加密
			String encCardNo = synchExecutorService.encrypt(cardNo);
			String encMobile = synchExecutorService.encrypt(mobile);
			String encAcc = synchExecutorService.encrypt(account);
			String encPas = synchExecutorService.encrypt(password);
			String encCaptcha = synchExecutorService.encrypt(captcha);
			
			//保存请求参数
			applyInfo = saveParamIn(trade_id,name,encCardNo,encMobile,encAcc,encPas,encCaptcha,logObj,applyInfo);
			//校验请求参数
			String cardNoValiRes = CardNoValidator.validate(cardNo);
			if (!StringUtil.isEmpty(cardNoValiRes)) {
				logger.info("{} 证件号码格式错误： {}" , prefix , cardNoValiRes);
				throw new Exception(JXLConst.CARDNO_INCORRECT);
			}
			if (mobile.length() != 11 || !StringUtil.isNumeric(mobile)) {
				logger.info("{} 手机号码不合法 {}" , prefix , mobile);
				throw new Exception(JXLConst.MOBILENO_INCORRECT);
			}
			
			//构建请求报文
			String reqJson = getReqJson(name,cardNo,mobile,account,password,captcha);
			//提交采集请求
			long startTime = System.currentTimeMillis();
			JsonObject subResJsonObj = postJsonData(creditReportUrl, reqJson , timeOut * 1000 ,prefix);
			logger.info("{} http_juxinli_credit_report 耗时  {} " , prefix , System.currentTimeMillis() - startTime);
			
			if (subResJsonObj == null) {
				logger.error("{} http请求聚信立提交人行报告采集请求返回内容为空，可能为网络超时 " , prefix );
				throw new Exception(JXLConst.RES_NULL);
			}
			
			logger.info("{} 提交采集请求结果为 {}" , prefix , subResJsonObj);
			
			
			Map<String,Object> resMap = (Map<String, Object>)new Gson().fromJson(subResJsonObj,Map.class);
			//解析返回报文
			if (!resMap.containsKey(JXLConst.FLAG_SUCCESS)
					|| StringUtil.isEmpty(resMap.get(JXLConst.FLAG_SUCCESS))) {

				logger.info("{} 聚信立返回报文格式异常没有success节点" , prefix);
				throw new Exception(JXLConst.RES_SUC_NULL);
			}
			
			boolean isSuccess = (Boolean)resMap.get(JXLConst.FLAG_SUCCESS);
			
			if (isSuccess) {
				if (resMap.containsKey(JXLConst.FLAG_DATA)
						&& !StringUtil.isEmpty(resMap.get(JXLConst.FLAG_DATA))) {
					
					rets.clear();
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
					rets.put(Conts.KEY_RET_MSG, "请求成功");
					
					logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
					logObj.setState_msg("交易成功");
					
					Map<String, Object> dataMap = (Map<String, Object>)resMap.get(JXLConst.FLAG_DATA);
					String type = (String)dataMap.get("type");
					String content = (String)dataMap.get("content");
					String token = (String)dataMap.get("token");
					Double processCodeDou = (Double)dataMap.get("process_code");
					int processCode = processCodeDou.intValue();
					
					//
					logObj.setBiz_code2(processCode + "");
					
					applyInfo.setContent(content);
					applyInfo.setProcess_code(processCode + "");
					applyInfo.setSucess(T);
					applyInfo.setToken(token);
					applyInfo.setType(type);
					
					switch (processCode) {
					case 10008:		
						
						retdata.put("res_code", "000");
						retdata.put("res_message", "提交采集请求成功");
						
						logObj.setBiz_code1(JXLConst.FLAG_SUCCESS);
						break;
					case 10003:
						retdata.put("res_code", "001");
						retdata.put("res_message", "密码或账号错误");
						break;
					case 30000:
						if (content.contains("验证码")) {
							retdata.put("res_code", "002");
							retdata.put("res_message", "身份验证码为空或错误");
						}else{
							retdata.put("res_code", "999");
							retdata.put("res_message", "网络或者网站异常");
						}
						break;
					default:
						retdata.put("res_code", "998");
						retdata.put("res_message", content);
						break;
					}
					retdata.put("request_id", requestId);
					rets.put(Conts.KEY_RET_DATA, retdata);
					
				}else{
					logger.info("{} 返回报文中没有data节点" , prefix);
					throw new Exception(JXLConst.RES_DATA_NULL);
				}
				
			}else{
				
				retdata.put("res_code", "997");
				retdata.put("res_message", "远程调用失败");
				
				if (resMap.containsKey("message")) {
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
					rets.put(Conts.KEY_RET_MSG, "请求成功");
					String message = resMap.get("message").toString();
					if (message.contains("身份证号码")) {
						retdata.put("res_code", "996");
						retdata.put("res_message", "身份证号码为空或身份证号码不合法");
					}else if(message.contains("手机号码")){
						retdata.put("res_code", "995");
						retdata.put("res_message", "手机号码为空或手机号码不合法");
					}else{
						rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED);
						rets.put(Conts.KEY_RET_MSG, "请求失败");
						
					}
				}
				
				rets.clear();
				rets.put(Conts.KEY_RET_DATA, retdata);
			
			}
			
		}catch(Exception e){			
			logger.error("{} 央行个人征信报告提交采集请求异常 {}" , prefix , e.getMessage());
			String errMsg = e.getMessage();	
			
			rets.clear();
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED);
			rets.put(Conts.KEY_RET_MSG, "请求失败");
			if((e instanceof ConnectTimeoutException) || (e instanceof SocketTimeoutException)){
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_TIMEOUT);
				logObj.setState_msg("请求超时");
			}else if (JXLConst.RES_NULL.equals(errMsg)) {
				logger.info("{} 聚信立返回消息为空，可能网络连接超时" , prefix);
				retdata.put("res_code", "997");
				retdata.put("res_message", "远程调用失败");
				
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_TIMEOUT);
				logObj.setState_msg("请求超时");
			}else if(JXLConst.CARDNO_INCORRECT.equals(errMsg)){
				retdata.put("res_code", "996");
				retdata.put("res_message", "身份证号码为空或身份证号码不合法");
			}else if(JXLConst.MOBILENO_INCORRECT.equals(errMsg)){
				retdata.put("res_code", "995");
				retdata.put("res_message", "手机号码为空或手机号码不合法");
			}else{
				logger.info("{} 其他异常 " , prefix);
				retdata.put("res_code", "997");
				retdata.put("res_message", "远程调用失败");
			}
			rets.put(Conts.KEY_RET_DATA, retdata);
			
		}finally{
			
			try {
				applyService.add(applyInfo);
				logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
				DataSourceLogEngineUtil.writeLog(trade_id, logObj);
			} catch (ServiceException e) {
				logger.error("{} 保存提交采集请求信息异常 {}" , prefix , e.getMessage());
			}
			
		}
		
		return rets;
	}


	/**
	 * 构建请求json报文
	 * @param name
	 * @param cardNo
	 * @param mobile
	 * @param account
	 * @param password
	 * @param captcha
	 * @return
	 */
	private String getReqJson(String name, String cardNo, String mobile,
			String account, String password, String captcha) {
		Map<String,String> basic_info = new HashMap<String, String>();
		basic_info.put("name", name );
		basic_info.put("cell_phone_num", mobile );
		basic_info.put("id_card_num", cardNo );
		Map<String, Object> reqMap = new HashMap<String, Object>();
		Map<String, Object> basicMap = new HashMap<String, Object>();
		basicMap.put("basic_info", basic_info);
		reqMap.put("apply_info", basicMap);
		reqMap.put("account", account );
		reqMap.put("password", password);
		reqMap.put("captcha", captcha );
		return JSONObject.toJSONString(reqMap);
	}


	/**
	 * 保存请求参数
	 * @param name
	 * @param encCardNo
	 * @param encMobile
	 * @param encAcc
	 * @param encPas
	 * @param encCaptcha
	 * @param logObj
	 * @param applyInfo 
	 */
	private PBOCApplyPojo saveParamIn(String trade_id,String name, String encCardNo, String encMobile,
			String encAcc, String encPas, String encCaptcha,
			DataSourceLogVO logObj, PBOCApplyPojo applyInfo) {
		
		Map<String, Object> paramIn = new HashMap<String, Object>();
		
		paramIn.put("name",name );
		paramIn.put("cardNO", encCardNo );
		paramIn.put("mobile", encMobile);
		paramIn.put("account", encAcc );
		paramIn.put("pas", encPas );
		paramIn.put("captcha", encCaptcha );
		
		applyInfo.setAccount(encAcc);
		applyInfo.setPasswd(encPas);
		applyInfo.setId_card_no(encCardNo);
		applyInfo.setCell_phone(encMobile);
		applyInfo.setCaptcha(encCaptcha);
		applyInfo.setName(name);
		try{
			DataSourceLogEngineUtil.writeParamIn(trade_id, paramIn, logObj);
		}catch(Exception e){
			logger.error("{} 保存入参信息异常 {}" , trade_id ,e.getMessage());
		}
		
		
		return applyInfo;
		
	}

	public void setCreditReportUrl(String creditReportUrl) {
		this.creditReportUrl = creditReportUrl;
	}


	public void setTimeOut(int timeOut) {
		this.timeOut = timeOut;
	}

}
