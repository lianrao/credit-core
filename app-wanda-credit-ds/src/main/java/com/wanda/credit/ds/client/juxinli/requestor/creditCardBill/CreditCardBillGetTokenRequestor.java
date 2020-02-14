/**   
* @Description: 聚信立信用卡账单-获取token 
* @author xiaobin.hou  
* @date 2016年7月19日 下午3:44:11 
* @version V1.0   
*/
package com.wanda.credit.ds.client.juxinli.requestor.creditCardBill;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.JsonObject;
import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.api.iface.IExecutorSecurityService;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.CardNoValidator;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.client.juxinli.BasicJuXinLiDataSourceRequestor;
import com.wanda.credit.ds.client.juxinli.requestor.PBOCReport.PBOCCreditReportDataRequestor;
import com.wanda.credit.ds.client.juxinli.util.JXLConst;
import com.wanda.credit.ds.dao.domain.juxinli.creditCardBill.CreditCardApplyPojo;
import com.wanda.credit.ds.dao.iface.juxinli.creditCardBill.IJXLCreditCardApplyService;
import com.wanda.credit.ds.iface.IDataSourceRequestor;

/**
 * @author xiaobin.hou
 *
 */
@DataSourceClass(bindingDataSourceId="ds_jxl_creditCardBill_getToken")
public class CreditCardBillGetTokenRequestor extends
		BasicJuXinLiDataSourceRequestor implements IDataSourceRequestor {

	private final static Logger logger = LoggerFactory.getLogger(PBOCCreditReportDataRequestor.class);
	private final static String PARSE_RES_CODE = "parse_code";
	private final static String PARSE_RES_DATA = "parse_data";
	@Autowired
	private IJXLCreditCardApplyService applyService;
	@Autowired
	private IExecutorSecurityService synchExecutorService;
	
	private String billTokenUrl;
	private int billTokenTimeOut;

	
	public Map<String, Object> request(String trade_id, DataSource ds) {
		
		long startTime = System.currentTimeMillis();
		
		String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		
		//组织返回对象
		Map<String, Object> rets = new HashMap<String, Object>();
		rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED);
		rets.put(Conts.KEY_RET_MSG,"请求失败");
		
		TreeMap<String, Object> retdata = new TreeMap<String, Object>();
		CreditCardApplyPojo applyPojo = new CreditCardApplyPojo();
		applyPojo.setStatus("100");
		applyPojo.setLoad_data("0");
		
		String requestId = StringUtil.getRequstId(40);
		retdata.put("request_id", requestId);
		retdata.put(JXLConst.RES_CODE, "997");
		retdata.put(JXLConst.RES_MSG, "远程调用失败");
		rets.put(Conts.KEY_RET_DATA, retdata);
		//交易日志信息数据
		DataSourceLogVO logObj = new DataSourceLogVO();
		logObj.setDs_id(ds.getId());
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));	
		logObj.setReq_url(billTokenUrl);
		logObj.setIncache("0");
		logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
		logObj.setState_msg("交易失败");
		logObj.setBiz_code1(JXLConst.FLAG_FAILED);
		
		
		try{
			logger.info("{} 获取并解析请求参数" , prefix);
			String name = ParamUtil.findValue(ds.getParams_in(), paramIds[0]).toString();//姓名
			String cardNo = ParamUtil.findValue(ds.getParams_in(), paramIds[1]).toString();//身份证号
			String mobile = ParamUtil.findValue(ds.getParams_in(), paramIds[2]).toString();//手机号
			String email = ParamUtil.findValue(ds.getParams_in(), paramIds[3]).toString();//邮箱			
			String loginType = ParamUtil.findValue(ds.getParams_in(), paramIds[4]).toString();//登陆方式
			//保存请求参数到参数表中
			applyPojo = saveParamIn(name,cardNo,mobile,email,loginType,trade_id,logObj,applyPojo);
			
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
			if (!StringUtil.isEmailReg(email)) {
				logger.info("{} 邮箱不合法 {}" , prefix , mobile);
				throw new Exception(JXLConst.EMAIL_INCORRECT);
			}
			
			//构建请求报文
			String reqJson = buildReqJson(name,cardNo,mobile,email,loginType);
			//http请求聚信立
			JsonObject resJosnObj = postJsonData(billTokenUrl, reqJson, billTokenTimeOut * 1000, trade_id);
			
			Map<String,Object> parseMap = parseResJsonObj(resJosnObj,prefix);
			Integer parseCode = (Integer)parseMap.get(PARSE_RES_CODE);
			
			switch (parseCode) {
			case 0:
				//http请求返回内容为空
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_TIMEOUT);
				logObj.setState_msg("请求超时");
				applyPojo.setStatus("100_0");
				break;
			case 1:
				//获取到数据节点
				JsonObject dataJsonObj = (JsonObject)parseMap.get(PARSE_RES_DATA);
				String token = dataJsonObj.get(JXLConst.COLL_TOKEN).getAsString();
//				JsonObject dsJsonObj = dataJsonObj.get("datasource").getAsJsonObject();
				
				applyPojo.setToken(token);
				
				retdata.put(JXLConst.RES_CODE, "000");
				retdata.put(JXLConst.RES_MSG, "请求成功");
				rets.clear();
				rets.put(Conts.KEY_RET_DATA, retdata); 
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
				rets.put(Conts.KEY_RET_MSG,"请求成功");
				
				logObj.setBiz_code1(JXLConst.FLAG_SUCCESS);
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
				logObj.setState_msg("请求成功");
		
				break;
				
			case 2:
				//返回报文没有success节点
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED);
				rets.put(Conts.KEY_RET_MSG,"请求失败");
				
				applyPojo.setStatus("100_2");
				break;
			
			case 3:
				//返回报文success节点内容为false
				if (resJosnObj.has("message")) {
					String msg = resJosnObj.get("message").getAsString();
					retdata.put(JXLConst.RES_CODE, "998");
					retdata.put(JXLConst.RES_MSG, msg);
					rets.put(Conts.KEY_RET_DATA, retdata);
				}
				
				applyPojo.setStatus("100_3");
				break;
			case 4:
				//返回报文没有data节点	
				applyPojo.setStatus("100_4");
				break;

			default:
				applyPojo.setStatus("100_5");
				break;
			}
			
		}catch(Exception e){
			logger.error("{} 信用卡提交申请表单异常 {}" , prefix , e.getMessage());
			applyPojo.setStatus("1000_-1");
			rets.clear();
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED);
			rets.put(Conts.KEY_RET_MSG,"请求失败");	
			String errMsg = e.getMessage();
			if (JXLConst.CARDNO_INCORRECT.equals(errMsg)) {
				retdata.put(JXLConst.RES_CODE, "996");
				retdata.put(JXLConst.RES_MSG, "身份证号码为空或身份证号码不合法");
			}else if (JXLConst.MOBILENO_INCORRECT.equals(errMsg)) {
				retdata.put(JXLConst.RES_CODE, "995");
				retdata.put(JXLConst.RES_MSG, "手机号码为空或手机号码不合法");
			}else if (JXLConst.EMAIL_INCORRECT.equals(errMsg)) {
				retdata.put(JXLConst.RES_CODE, "004");
				retdata.put(JXLConst.RES_MSG, "邮箱格式不正确");
			}else{
				retdata.put(JXLConst.RES_CODE, "998");
				retdata.put(JXLConst.RES_MSG, "其他异常");
			}
			rets.put(Conts.KEY_RET_DATA, retdata);
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
			applyPojo.setRequestId(requestId);
			applyPojo.setCreate_date(new Date());
			applyPojo.setUpdate_date(new Date());
			applyService.add(applyPojo);
		}catch(Exception e){
			logger.error("{} 保存提交申请表单信息异常 {}" , prefix , e.getMessage());
			rets.clear();
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED);
			rets.put(Conts.KEY_RET_MSG,"请求失败");	
		}
		
		long tradeTime = System.currentTimeMillis() - startTime;
		logger.info("{} 聚信立-信用卡账单获取token请求总共耗时时间为（ms） {}", prefix, tradeTime);
		
		return rets;
	}


	/**
	 * @param resJosnObj
	 * @return
	 */
	private Map<String, Object> parseResJsonObj(JsonObject resJosnObj,String prefix) {
		
		Map<String, Object> result = new HashMap<String, Object>();
		result.put(PARSE_RES_CODE, -1);
		if (resJosnObj == null) {
			logger.info("{} 信用卡账单提交申请表单返回信息为空" , prefix);
			result.put(PARSE_RES_CODE, 0);
			return result;
		}
		
		if (!resJosnObj.has(JXLConst.FLAG_SUCCESS)) {
			logger.info("{} 信用卡账单提交申请表单返回报文没有success节点 {}" , prefix , resJosnObj);
			result.put(PARSE_RES_CODE, 2);
			return result;
		}
		
		boolean isSuccess = resJosnObj.get(JXLConst.FLAG_SUCCESS).getAsBoolean();
		
		if (!isSuccess) {
			//聚信立success节点返回为false
			logger.info("{} 调用信用卡提交申请表单接口返回false", prefix);
			result.put(PARSE_RES_CODE, 3);
			return result;
		}
		
		if (!resJosnObj.has(JXLConst.FLAG_DATA)) {
			logger.info("{} 信用卡提交申请表单没有data节点 {}" , prefix , resJosnObj);
			result.put(PARSE_RES_CODE, 4);
			return result;
		}
		
		JsonObject dataJsonObj = resJosnObj.get(JXLConst.FLAG_DATA).getAsJsonObject();
		result.put(PARSE_RES_CODE, 1);
		result.put(PARSE_RES_DATA, dataJsonObj);
		
		return result;
	}


	/**
	 * @param name
	 * @param cardNo
	 * @param mobile
	 * @param email
	 * @param password
	 * @param identity
	 * @param loginType
	 * @return
	 */
	private String buildReqJson(String name, String cardNo, String mobile,
			String email, String loginType) {
		Map<String, String> basicMap = new HashMap<String, String>();
		basicMap.put("name", name);
		basicMap.put("cell_phone_num", mobile);
		basicMap.put("id_card_num", cardNo);
		
		Map<String, Object> applyMap = new HashMap<String, Object>();
		applyMap.put("basic_info", basicMap);
		
		Map<String, Object> reqMap = new HashMap<String, Object>();
		reqMap.put("apply_info", applyMap);
		reqMap.put("email", email);
		reqMap.put("password", "");
		reqMap.put("identity", "");
		reqMap.put("loginType", loginType);
		
		JSONObject reqJsonObj = JSONObject.fromObject(reqMap);
		
		return reqJsonObj == null ? null:reqJsonObj.toString();
	}


	/**
	 * @param name
	 * @param cardNo
	 * @param mobile
	 * @param email
	 * @param password
	 * @param identity
	 * @param loginType
	 * @param applyPojo 
	 * @return
	 * @throws Exception 
	 */
	private CreditCardApplyPojo saveParamIn(String name, String cardNo, String mobile,
			String email, String loginType, String trade_id,
			DataSourceLogVO logObj, CreditCardApplyPojo applyPojo) throws Exception {

		Map<String, Object> paramIn = new HashMap<String, Object>();
		if (!StringUtil.isEmpty(cardNo)) {
			String encCardNo = synchExecutorService.encrypt(cardNo);
			paramIn.put("cardNO", encCardNo);
			applyPojo.setId_card_no(encCardNo);
		}
		if (!StringUtil.isEmpty(mobile)) {
			String encMobile = synchExecutorService.encrypt(mobile);
			paramIn.put("mobile", encMobile);
			applyPojo.setCell_phone(encMobile);
		}
		if (!StringUtil.isEmpty(email)) {
			String encMail = synchExecutorService.encrypt(email);
			paramIn.put("email", encMail);
			applyPojo.setEmail(encMail);
		}

		paramIn.put("name", name);
		applyPojo.setName(name);
		applyPojo.setLoginType(loginType);
		try{	
			DataSourceLogEngineUtil.writeParamIn(trade_id, paramIn, logObj);
		}catch(Exception e){
			logger.error("{} 保存参数异常 {}", trade_id , e.getMessage());
		}
		
		return applyPojo;
	}


	public void setBillTokenUrl(String billTokenUrl) {
		this.billTokenUrl = billTokenUrl;
	}


	public void setBillTokenTimeOut(int billTokenTimeOut) {
		this.billTokenTimeOut = billTokenTimeOut;
	}

}
