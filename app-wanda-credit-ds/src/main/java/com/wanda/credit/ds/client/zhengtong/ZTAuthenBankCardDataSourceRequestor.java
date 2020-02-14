package com.wanda.credit.ds.client.zhengtong;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.thinkive.base.util.Base64;
import com.thinkive.base.util.security.AES;
import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.api.iface.IExecutorSecurityService;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.CardNoValidator;
import com.wanda.credit.base.util.CommonUtil;
import com.wanda.credit.base.util.RequestHelper;
import com.wanda.credit.base.util.SignatureUtil;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceBizCodeVO;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.log.ds.vo.LoggingEvent;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.dao.iface.IAuthenBankCardService;
import com.wanda.credit.ds.iface.IDataSourceRequestor;

/**
 * @description  证通银行卡鉴权
 * @author wuchsh 
 * @version 1.0
 * @createdate 2016年4月11日 下午2:29:32 
 *  
 */
@DataSourceClass(bindingDataSourceId="ds_zhengtong_AuthenBankCard")
public class ZTAuthenBankCardDataSourceRequestor extends BaseZTDataSourceRequestor
              implements IDataSourceRequestor {
	private final  Logger logger = LoggerFactory.getLogger(ZTAuthenBankCardDataSourceRequestor.class);
	
	@Autowired
	private IAuthenBankCardService service;
	@Autowired
	public IPropertyEngine propertyEngine;
	@Autowired
	private IExecutorSecurityService synchExecutorService;

	@Override
	public Map<String, Object> request(String trade_id, DataSource ds){
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		TreeMap<String, Object> retdata = new TreeMap<String, Object>();
		/**设置编目*/
		retdata.put("server_idx", "zt_authen");
		Set<String> tags = new HashSet<String>();
		String initTag = Conts.TAG_SYS_ERROR;
		logger.info("{} 银行卡鉴权交易开始",prefix);
		CRSStatusEnum retStatus = CRSStatusEnum.STATUS_FAILED_DS_ZT_BANKCARD_AUTHEN_EXCEPTION;
		String retMsg = "银行卡鉴权失败";
		DataSourceLogVO logObj = new DataSourceLogVO();
		logObj.setTrade_id(trade_id);
		logObj.setDs_id("ds_zhengtong_AuthenBankCard");
		String zhengtong_url =  propertyEngine.readById("zhengtong_address01");
		logObj.setReq_url(zhengtong_url);
		
		Map<String, Object> rets = new HashMap<String, Object>();;
 		Map<String,Object> paramForLog = new HashMap<String,Object>();
		try{			
			/**姓名*/
	 		String name = ParamUtil.findValue(ds.getParams_in(), "name").toString();
            /**身份证号码*/  
	 		String cardNo = ParamUtil.findValue(ds.getParams_in(), "cardNo").toString(); 
			String crptedCardNo = synchExecutorService.encrypt(cardNo);

			/**手机号码 如果为空 置为空字符串*/
	 		String phone = (String)ParamUtil.findValue(ds.getParams_in(), "phone");
	 		if(phone == null )phone = "";

	 		String crptedPhone = synchExecutorService.encrypt(phone);

			/**銀行卡號*/
	 		String cardId = ParamUtil.findValue(ds.getParams_in(), "cardId").toString();
			String crptedCardId = synchExecutorService.encrypt(cardId);

	 		/**请求参数记录到日志*/
	 		paramForLog.put("name", name);
	 		paramForLog.put("cardNo", cardNo);
	 		paramForLog.put("phone",phone);
	 		paramForLog.put("cardId", cardId);

	 		if(StringUtils.isNotEmpty(CardNoValidator.validate(cardNo))){
				logger.warn("{}入参格式不符合要求!", new String[] { prefix });
				logObj.setIncache("1");
				logObj.setState_msg("身份证号码不符合规范");
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_IDCARD_ERROR);
				rets.put(Conts.KEY_RET_MSG, "您输入的为无效身份证号码，请核对后重新输入!");
				rets.put(Conts.KEY_RET_TAG, new String[]{Conts.TAG_SYS_ERROR});
				return rets;
			}
	 		/**组织上下文数据*/
	 		Map<String,Object> contxt = new HashMap<String,Object>();
	 		contxt.put("trade_id", trade_id);
	 		contxt.putAll(paramForLog);
	 		
	 		/**组织请求数据*/
	 		Map<String, String> paramMap = buildRequestData(contxt);
			/**发送请求*/
	 		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));
			logObj.setIncache("0");
	 		DataSourceLogEngineUtil.writeLog2LogSys(new LoggingEvent(trade_id,JSONObject.fromObject(paramMap).toString(),new String[]{trade_id}));
			String retrnStr = RequestHelper.keyPost(zhengtong_url, paramMap,2);
	 		DataSourceLogEngineUtil.writeLog2LogSys(new LoggingEvent(trade_id,retrnStr,new String[]{trade_id}));
	 		logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
	 		/**解析响应数据*/
			JSONObject retrnJsn = JSONObject.fromObject(retrnStr);
			/**对方返回成功码 0*/
			if(retrnJsn !=null && "0".equals(retrnJsn.get("error_no"))){
				retStatus = CRSStatusEnum.STATUS_SUCCESS;
				retMsg = "银行卡鉴权成功";
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
				if(retrnJsn.get("results")!=null){
					retdata.putAll(visitBusiData(retrnJsn));
					JSONArray jsonArray = retrnJsn.getJSONArray("results");
					if(jsonArray != null && jsonArray.size()>0){
						/**保存业务数据*/
						contxt.put("sourcechnl", sourceChannel);
						contxt.put("cardId",crptedCardId);
						contxt.put("cardNo",crptedCardNo);
						contxt.put("phone",crptedPhone);
						service.addAuthenBackCard(retrnJsn,contxt);
						contxt.put("cardId",cardId);
						contxt.put("cardNo",cardNo);
						contxt.put("phone",phone);
						
						/**处理biz_code*/
						handleBizcode(logObj,jsonArray.getJSONObject(0));	 
						initTag = handleTag(trade_id,jsonArray.getJSONObject(0));
					}
				  }
				}
			else{
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
				logObj.setState_msg((String)retrnJsn.get("error_info"));
				logger.error(prefix+" 银行卡鉴权失败 :{}", retrnJsn.get("error_info"));

			}
				    
			rets.put(Conts.KEY_RET_STATUS, retStatus);
			rets.put(Conts.KEY_RET_DATA, retdata);
			rets.put(Conts.KEY_RET_MSG, retMsg);
		}catch(Exception ex){
			initTag = Conts.TAG_SYS_ERROR;
			logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
			if (CommonUtil.isTimeoutException(ex)) {
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_TIMEOUT);
				initTag = Conts.TAG_SYS_TIMEOUT;
			} else {
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
				logObj.setState_msg("数据源处理时异常! 详细信息:" + ex.getMessage());
			}
			rets.put(Conts.KEY_RET_STATUS,
					CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, "数据源处理时异常! 详细信息:" + ex.getMessage());
			logger.error(prefix+" 数据源处理时异常", ex);
		}finally{
			tags.add(initTag);
			rets.put(Conts.KEY_RET_TAG,tags.toArray(new String[0]));
			logObj.setTag(StringUtils.join(tags, ";"));
			/**记录请求*/
	 		if(MapUtils.isNotEmpty(paramForLog)){
	 			DataSourceLogEngineUtil.writeParamIn(trade_id, paramForLog,logObj);
	 		}
		    DataSourceLogEngineUtil.writeLog(trade_id,logObj);
		}	
		return rets;
	}


	private String handleTag(String trade_id, JSONObject busiJsn) {
		if(busiJsn != null && busiJsn.get("status") != null){
			String status = (String)busiJsn.get("status");
//			01：正在处理中  00：成功 03：失败；
			if("00".equals(status))return Conts.TAG_TST_SUCCESS;
			if("01".equals(status))return Conts.TAG_TST_PROCESS;
			if("03".equals(status))return Conts.TAG_TST_FAIL;
		}
		return null;
	}


	/**
	 * 处理返回数据
	 * 剥离出result的第一条[也只有一条]记录到retdata里面
	 * */
	@SuppressWarnings("unchecked")
	private Map<? extends String, ? extends Object> visitBusiData(
			JSONObject retrnJsn) {
		HashMap<String, Object> retMap = new HashMap<String, Object>();
		List<Map<String, Object>> recrdls = (List<Map<String, Object>>) retrnJsn
				.get("results");
		/**理论上只会存在一条记录*/
		if(recrdls != null && recrdls.size() >0 ){
			retMap.putAll(recrdls.get(0));			
		}
		return retMap;
	}


	/**
	 * 构建请求体
	 * @param contxt 
	 *  一定要含有 cardId cardNo phone usernm
	 *  四个key 的值
	 * */
	private Map<String, String> buildRequestData(Map<String, Object> contxt) throws UnsupportedEncodingException {
		Map<String,String> paramMap = new HashMap<String,String>();
		AES aes = new AES(encrykey);
		/**加密卡号*/
		String AESAcctno = aes.encrypt((String)contxt.get("cardId"), "utf-8");  
		/**加密身份证号*/
		String AESCertseq = aes.encrypt((String)contxt.get("cardNo"), "utf-8"); 
		 
        /**Base64*/
		String base64Acctno=  new String(Base64.encodeBytes((URLEncoder.encode(AESAcctno, "utf-8")).getBytes(), Base64.DONT_BREAK_LINES));
		String base64Certseq=  new String(Base64.encodeBytes((URLEncoder.encode(AESCertseq, "utf-8")).getBytes(), Base64.DONT_BREAK_LINES));

		/**卡号*/
		paramMap.put("acctno", AESAcctno);
		paramMap.put("biztyp", biztyp);//对照接口文档查看
		paramMap.put("biztypdesc", biztypDesc);//服务描述
		/**身份证号*/
		paramMap.put("certseq", AESCertseq);
		paramMap.put("code", "");//短信验证码 .如不调用短信，这里可以传空字符
        /**手机号*/
		paramMap.put("phoneno", (String)contxt.get("phone"));
		
		paramMap.put("sysseqnb", "");//调用生成短信接口返回的业务流水号 .如不调用短信，这里可以传空字符
		
		paramMap.put("placeid", placeId);//业务发生地
		paramMap.put("ptyacct", accessId);//机构帐号
		paramMap.put("ptycd", ptycd);//机构号
		paramMap.put("sourcechnl", sourceChannel);//来源渠道，pc端传0
		
		String sign = SignatureUtil.signature(paramMap,encrykey);
		paramMap.put("sign", sign);//防篡改密钥
		paramMap.put("funcNo", ZHENGTONG_AUTHENBANKCARD_API);//单笔请求业务BUS功能号
		/**身份证号*/
		paramMap.put("acctno", base64Acctno);
		/**身份证号*/
		paramMap.put("certseq", base64Certseq);
		/**姓名*/
		paramMap.put("usernm", (String)contxt.get("name"));
		
		return paramMap;
	}

	/**
	 * 处理log的bizcode1 和 bizcode2值 
	 * */
	private void handleBizcode(DataSourceLogVO logObj, JSONObject busiJsn){
		if(busiJsn != null && busiJsn.get("status") != null){
			 DataSourceBizCodeVO bizcodeVO = DataSourceLogEngineUtil.
					 fetchBizCodeByRetCode("ds_zhengtong_AuthenBankCard", (String)busiJsn.get("status"));
			if(bizcodeVO != null)
				logObj.setBiz_code1(bizcodeVO.getBizCode());
		}
		
		if(busiJsn != null && busiJsn.get("respcd") != null){
			 DataSourceBizCodeVO bizcodeVO = DataSourceLogEngineUtil.
					 fetchBizCodeByRetCode("ds_zhengtong_AuthenBankCard", (String)busiJsn.get("respcd"));
			if(bizcodeVO != null)
				logObj.setBiz_code2(bizcodeVO.getBizCode());
		}
	}
	
	@Override
	public Map<String, Object> valid(String trade_id, DataSource ds){
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		Map<String, Object> rets = null;
		
			rets = new HashMap<String, Object>();
			if(ds!=null && ds.getParams_in()!=null){
				for(String paramId : paramIds){
					/**忽略phone 字段 非空校验*/
					if("phone".equals(paramId)){
						continue;
					}
					if(null==ParamUtil.findValue(ds.getParams_in(),paramId)){
						rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_PARAM_INVALID);
						rets.put(Conts.KEY_RET_MSG, "数据源参数校验不通过!");
						return rets;
					}
				}
			}
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
			rets.put(Conts.KEY_RET_MSG, "数据源参数校验通过!");
			return rets;
	}		
	public static void main(String[] args) {}
}
