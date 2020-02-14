package com.wanda.credit.ds.client.jiean;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Resource;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.CardNoValidator;
import com.wanda.credit.base.util.ExceptionUtil;
import com.wanda.credit.base.util.RequestHelper;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.template.PropertyEngine;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.BaseDataSourceRequestor;
import com.wanda.credit.ds.client.xyan.utils.CharSet;
import com.wanda.credit.ds.dao.JieAnAuthCardServiceImpl;
import com.wanda.credit.ds.iface.IDataSourceRequestor;
import com.wanda.credit.dsconfig.commonfunc.security.MD5Util;

@DataSourceClass(bindingDataSourceId="ds_jiean_AuthenBankCard")
public class JieAnAuthenBankCardDataSourceRequestor extends BaseDataSourceRequestor
implements IDataSourceRequestor {
	private final  Logger logger = LoggerFactory.getLogger(JieAnAuthenBankCardDataSourceRequestor.class);

	@Autowired
	protected PropertyEngine propertyEngine;
	
	@Resource(name="jieAnAuthCardServiceImpl")
	protected JieAnAuthCardServiceImpl allAuthCardService;
	
	private String[] auth_pass = new String[]{"2000","认证一致"};
	private String[] auth_notpass = new String[]{"2001","认证不一致"};
	private String[] auth_notsupport = new String[]{"2003","不支持验证"};

	private String[] auth_pass_code = new String[]{"000"};
	private String[] auth_notpass_code = new String[]{"042","319","316","232"};
	private String[] auth_notsupport_code = new String[]{"305","315"};

	private static Map<String,CRSStatusEnum> errorMap = new HashMap<String,CRSStatusEnum>();
	static {
		errorMap.put("225",CRSStatusEnum.STATUS_FAILED_INVALID_CARD);
		errorMap.put("231",CRSStatusEnum.STATUS_FAILED_INVALID_CARD);
		errorMap.put("303",CRSStatusEnum.STATUS_FAILED_INVALID_CARD);
		errorMap.put("304",CRSStatusEnum.STATUS_FAILED_INVALID_CARD);

		errorMap.put("227",CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_IDCARD_ERROR);
		errorMap.put("228",CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_IDCARD_ERROR);
		errorMap.put("302",CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_IDCARD_ERROR);
		
		errorMap.put("224",CRSStatusEnum.STATUS_WARN_DS_MOBILE_NO_ERROR);
		errorMap.put("229",CRSStatusEnum.STATUS_WARN_DS_MOBILE_NO_ERROR);
		errorMap.put("230",CRSStatusEnum.STATUS_WARN_DS_MOBILE_NO_ERROR);
		errorMap.put("306",CRSStatusEnum.STATUS_WARN_DS_MOBILE_NO_ERROR);
		
		errorMap.put("226",CRSStatusEnum.STATUS_FAILED_INVALID_NAME);
		errorMap.put("301",CRSStatusEnum.STATUS_FAILED_INVALID_NAME);
		
		errorMap.put("121",CRSStatusEnum.STATUS_FAILED_INVALID_PARAM);
	}
	
	@Override
	public Map<String, Object> request(String trade_id, DataSource ds){
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		TreeMap<String, Object> retdata = new TreeMap<String, Object>();
		Map<String, Object> rets = new HashMap<String, Object>();
		DataSourceLogVO logObj = new DataSourceLogVO();
		Map<String, Object> reqparam = new HashMap<String, Object>();
		String resource_tag = Conts.TAG_SYS_ERROR;
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));
		try{			
			logObj.setDs_id(ds.getId());
//			logObj.setReq_url(propertyEngine.readById("jiean_bankcard_auth_url"));
			logObj.setReq_url("http://api.jieandata.com/vpre/ccmn/verify");
			logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
	 		String name = ParamUtil.findValue(ds.getParams_in(), "name").toString();   //姓名 
			String cardNo = ParamUtil.findValue(ds.getParams_in(), "cardNo").toString(); //身份证号码
			String cardId = ParamUtil.findValue(ds.getParams_in(), "cardId").toString(); //银行卡号
			String phone = ParamUtil.findValue(ds.getParams_in(), "phone").toString(); //手机号码

			reqparam.put("name", name);
			reqparam.put("cardNo", cardNo);
			reqparam.put("cardId", cardId);
			reqparam.put("phone", phone);

			if(StringUtils.isNotBlank(cardNo) && 
					StringUtils.isNotEmpty(CardNoValidator.validate(cardNo))){
				logObj.setIncache("1");
				logObj.setState_msg("身份证号码不符合规范");
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_IDCARD_ERROR);
				rets.put(Conts.KEY_RET_MSG, "您输入的为无效身份证号码，请核对后重新输入!");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				return rets;
			}			
			logObj.setIncache("0");
			Map<String,String> reqTypeMap = getReqType(name,cardNo,cardId,phone);
			Map<String,String> reqData = 
					buildRequestUrl(trade_id,name,cardNo,cardId,phone,reqTypeMap.get("toSend"));
			JSONObject rspDataJsn = doRequest(trade_id,reqData);
			logger.info("{} 厂商返回响应码等信息 {} {}",new Object[]{trade_id,rspDataJsn.get("respCode"),rspDataJsn.get("respDesc")});
			if(isSuccess(rspDataJsn)){
				allAuthCardService.saveAuthCard(ds.getId(), trade_id, 
						name, cardNo, cardId, phone, rspDataJsn,
						cvtRspCode(trade_id,(String)rspDataJsn.get("respCode")),reqTypeMap.get("toSave"));
				resource_tag = buildTag(trade_id,rspDataJsn);
				retdata.putAll(visitBusiData(trade_id,rspDataJsn));
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
			}else{
				resource_tag = Conts.TAG_TST_FAIL;
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
				logger.error("{} 厂商返回异常，收到响应信息 {}",trade_id,rspDataJsn.toString());
				CRSStatusEnum error;
				if((error = errorMap.get(rspDataJsn.get("respCode"))) != null){
					rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
					rets.put(Conts.KEY_RET_STATUS, error);
					rets.put(Conts.KEY_RET_MSG, error.ret_msg);			
                    return rets;
				}
				throw new Exception(rspDataJsn.getString("resTxnId")+ " code:"+rspDataJsn.getString("respCode")+
						" desc:"+rspDataJsn.getString("respDesc"));
			}			
			rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
			rets.put(Conts.KEY_RET_DATA, retdata);
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
			rets.put(Conts.KEY_RET_MSG, "采集成功!");			
		}catch(Exception ex){
			resource_tag = Conts.TAG_SYS_ERROR;
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, "数据源处理时异常! 详细信息:"+ex.getMessage());
			logger.error(prefix+" 数据源处理时异常：{}",ex);
			if (ExceptionUtil.isTimeoutException(ex)) {
				resource_tag = Conts.TAG_SYS_TIMEOUT;
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_TIMEOUT);
			} else {
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
				logObj.setState_msg("数据源处理时异常! 详细信息:" + ex.getMessage());
			}
			rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
		}finally {
			logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
			logObj.setTag(resource_tag);
			DataSourceLogEngineUtil.writeLog(trade_id, logObj);
			DataSourceLogEngineUtil.writeParamIn(trade_id, reqparam, logObj);
		}
		return rets;
	}
	
	private Map<String, String> getReqType(String name, String cardNo, String cardId, String phone) {
        Map<String,String> reqType = new HashMap<String,String>();        
		String toSend = "CARD2N"; 
//		00:三要素 01:四要素 02:二要素
		String toSave = "02";
		if(StringUtils.isNotBlank(cardNo)){
			toSend = "CARD3CN";
			toSave="00";
		}
		if(toSend.equals("CARD3CN") 
				&& StringUtils.isNotBlank(phone)){
			toSend = "CARD4";
			toSave="01";
		}
		reqType.put("toSend", toSend);
		reqType.put("toSave", toSave);
		return reqType;
	}

	private Map<? extends String, ? extends Object> visitBusiData(String trade_id, JSONObject rspDataJsn) {
		Map<String,Object> retrnMap = new HashMap<String,Object>();
		/*2000	认证一致
		2001	认证不一致*/
		String[] cvtResult = cvtRspCode(trade_id,(String)rspDataJsn.get("respCode"));
		retrnMap.put("respCode", cvtResult[0]);
		retrnMap.put("respDesc", cvtResult[1]);
		return retrnMap;
	}
    
	private String[] cvtRspCode(String trade_id,String rspCode) {
		if(ArrayUtils.contains(auth_pass_code, rspCode))return auth_pass;
		else if(ArrayUtils.contains(auth_notpass_code, rspCode))return auth_notpass;
		else if(ArrayUtils.contains(auth_notsupport_code, rspCode))return auth_notsupport;
		else{
			String message = String.format("远程响应码 %s 没有配置 数据源返回码映射", rspCode);
			logger.error("{} {}",trade_id,message);
			throw new IllegalStateException(message);
		}
	}

	private String buildTag(String trade_id, JSONObject rspDataJsn) {
		/*if("042".equals(rspDataJsn.get("respCode")) ||
				"232".equals(rspDataJsn.get("respCode")) ||
				"000".equals(rspDataJsn.get("respCode"))){
			return Conts.TAG_TST_SUCCESS;
		}		*/
		if(ArrayUtils.contains(auth_pass_code, rspDataJsn.get("respCode")) || 
		    ArrayUtils.contains(auth_notpass_code, rspDataJsn.get("respCode"))
				){
			return Conts.TAG_TST_SUCCESS;
		}
		return Conts.TAG_TST_FAIL;
	}

	private JSONObject doRequest(String trade_id, Map<String, String> reqData) 
			throws KeyManagementException, NoSuchAlgorithmException, DocumentException {
		/*String rspXml = RequestHelper.keyPost(propertyEngine.readById("jiean_bankcard_auth_url")
				, reqData, 1);*/
		String rspXml = RequestHelper.keyPost("http://api.jieandata.com/vpre/ccmn/verify"
				, reqData, 1);
/*		String rspXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><verify><versionId>01</versionId><custId>6000002342</custId><ordId>wdzx20171115112344976XJWY</ordId><transType>STD_VERI</transType><merPriv>20171115112344976XJWY</merPriv><jsonStr>{\"PROD_ID\":\"CARD4\",\"CARD_ID\":\"6212261001025817252\",\"CERT_ID\":\"362227199001302912\",\"CERT_NAME\":\"郭凯\",\"MP\":\"13061797287\"}</jsonStr><respCode>042</respCode><respDesc>不一致</respDesc><resTxnId>201711150072900895</resTxnId><macStr>7F25F829DDD3D5C59DA70225CB0EDBB8</macStr></verify>";
*/		// 解析返回报文
		logger.info("{} 收到远程返回报文 {}",trade_id,rspXml);
		Document rspDoc = DocumentHelper.parseText(filtRspBody(rspXml));
		JSONObject rtrnData = new JSONObject();
		rtrnData.put("respCode", rspDoc.selectSingleNode("//verify/respCode").getText());
		rtrnData.put("respDesc", rspDoc.selectSingleNode("//verify/respDesc").getText());
		rtrnData.put("resTxnId", rspDoc.selectSingleNode("//verify/resTxnId").getText());
		return rtrnData;
	}
	
	/**
	 * 报文格式过滤
	 * @param rspBody
	 * @return
	 */
	protected String filtRspBody(String rspBody){
		rspBody = rspBody.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?> ","");
		return rspBody;
	}
	private boolean isSuccess(JSONObject rspDataJsn) {
		/*return "042".equals(rspDataJsn.get("respCode"))
				|| "000".equals(rspDataJsn.get("respCode"));*/
		
		return ArrayUtils.contains(auth_pass_code, rspDataJsn.get("respCode")) || 
	      ArrayUtils.contains(auth_notpass_code, rspDataJsn.get("respCode")) ||
	      ArrayUtils.contains(auth_notsupport_code, rspDataJsn.get("respCode"));
	      
			
	}

	public Map<String,String> buildRequestUrl(String trade_id, String name, String cardNo,
			String cardId,String phone,String reqType) {
		Map<String,String> reqData = new TreeMap<String,String>();
		reqData.put("versionId", "01");reqData.put("chrSet", "UTF-8");
//		reqData.put("custId", propertyEngine.readById("jiean_custId")); TODO
		reqData.put("custId", "6000002342");
		reqData.put("ordId", "wdzx"+trade_id);
		reqData.put("transType", "STD_VERI");reqData.put("busiType", "STD_VERI");
		reqData.put("merPriv", trade_id);reqData.put("retUrl", "");
		reqData.put("jsonStr", buildJsnReqData(trade_id,name,cardNo,cardId,phone));
		reqData.put("macStr", generateMacStr(reqData));
//		String url = propertyEngine.readById("jiean_bankcard_auth_url");
		return reqData;
	}
    /**
     ** 
     **PROD_ID 产品代号 
     **CARD4：银行卡+姓名+身份证号+手机号 
     **CARD3CN：银行卡+姓名+身份证号 
     **CARD2N: 银行卡+姓名  
     */
	private String buildJsnReqData(String trade_id, String name, String cardNo, String cardId, String phone) {
		Map<String,Object> reqData = new TreeMap<String,Object>();
//		CARD_ID 银行卡 22  CERT_ID 身份证号 18  CERT_NAME 身份证名字 30  MP 手机号 11  PROD_ID 
		reqData.put("CARD_ID",cardId);
		reqData.put("CERT_NAME",name);
		String PROD_ID = "CARD2N"; 
		if(StringUtils.isNotBlank(cardNo)){
			reqData.put("CERT_ID",cardNo);
			PROD_ID = "CARD3CN";
		}
		if(PROD_ID.equals("CARD3CN") 
				&& StringUtils.isNotBlank(phone)){
			reqData.put("MP",phone);
			PROD_ID = "CARD4";
		}
		reqData.put("PROD_ID", PROD_ID);
		String message = null;
		switch(PROD_ID){
		case "CARD2N" :message = String.format("二要素验证 %s %s", "姓名","银行卡号");break;
		case "CARD3CN" :message = String.format("三要素验证 %s %s %s", "姓名","身份证号","银行卡号");break;
		case "CARD4" :message = String.format("四要素验证 %s %s %s %s", "姓名","身份证号","银行卡号","手机号");break;
		default : break;
		}
        logger.info("{} 正在发起 {}",trade_id,message);

		return JSON.toJSONString(reqData);
	}

	/**
	 * 顺序连接请求数据的所有 INPUT 域+MAC_KEY，
	 * 对结果取 MD5 摘要，得到长度为 32 位的 ASC 码，
	 * 并转化为大写
	 * */
	private String generateMacStr(Map<String, String> reqData) {
		StringBuffer sb = new StringBuffer();
		sb.append(reqData.get("versionId")).append(reqData.get("chrSet"))
		.append(reqData.get("custId")).append(reqData.get("ordId"))
		.append(reqData.get("transType")).append(reqData.get("busiType"))
		.append(reqData.get("merPriv")).append(reqData.get("retUrl"))
		.append(reqData.get("jsonStr")).append("0c44d6b0bbb0cac6014b6fa6e7c57cbc");
//		.append(reqData.get("jsonStr")).append(propertyEngine.readById("jiean_mackey")); TODO
		return MD5Util.MD5Encode(sb.toString(), CharSet.UTF8).toUpperCase();
	}

	public static void main(String[] args) {	
	}
}
