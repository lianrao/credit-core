package com.wanda.credit.ds.client.yiwei;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.api.iface.IExecutorSecurityService;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.CardNoValidator;
import com.wanda.credit.base.util.CommonUtil;
import com.wanda.credit.base.util.HttpsHelper;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.log.ds.vo.LoggingEvent;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.dao.iface.IYWAuthenBankCardService;
import com.wanda.credit.ds.iface.IDataSourceRequestor;

/**
 * @description 亿微银行卡鉴权(3要素)
 * @author wuchsh
 * @version 1.0
 * @createdate 2016年9月29日
 *  
 */
@DataSourceClass(bindingDataSourceId="ds_yiwei_AuthenBankCard3")
public class YWAuthenBankCard3DataSourceRequestor  extends BaseYWAuthenBankCardDataSourceRequestor
              implements IDataSourceRequestor {
	private final  Logger logger = LoggerFactory.getLogger(YWAuthenBankCard3DataSourceRequestor.class);
	
	
	@Autowired
	public IPropertyEngine propertyEngine;

	@Autowired
	public IYWAuthenBankCardService ywAuthenBankCardService;
	
	@Autowired
	private IExecutorSecurityService synchExecutorService;

	@Override
	public Map<String, Object> request(String trade_id, DataSource ds){
		final String prefix = trade_id;
		TreeMap<String, Object> retdata = new TreeMap<String, Object>();
		/**设置编目*/
		retdata.put("server_idx", "yw_authen3");
		Set<String> tags = new HashSet<String>();
		String initTag = Conts.TAG_SYS_ERROR;
		logger.info("{} 银行卡鉴权交易开始",prefix);
		CRSStatusEnum retStatus = CRSStatusEnum.STATUS_FAILED_DS_ZT_BANKCARD_AUTHEN_EXCEPTION;
		String retMsg = "银行卡鉴权失败";
		DataSourceLogVO logObj = new DataSourceLogVO();
		logObj.setTrade_id(trade_id);
		logObj.setDs_id(ds.getId());
		logObj.setReq_url(propertyEngine.readById("sys.credit.client.yiwei.authenbankcard3.url"));
 		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));

		Map<String, Object> rets = new HashMap<String, Object>();;
 		Map<String,Object> paramForLog = new HashMap<String,Object>();
		try{			
			/**姓名*/
	 		String name = (String)ParamUtil.findValue(ds.getParams_in(), "name");
            /**身份证号码*/  
	 		String cardNo = (String)ParamUtil.findValue(ds.getParams_in(), "cardNo"); 

			if(StringUtils.isNotEmpty(CardNoValidator.validate(cardNo))){
				logObj.setIncache("0");
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
				logObj.setState_msg("身份证号码不符合规范");
				logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
				logger.error("{} {}",prefix,logObj.getState_msg());
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_IDCARD_ERROR);
				rets.put(Conts.KEY_RET_MSG, "您输入的为无效身份证号码，请核对后重新输入!");
				return rets;
			}
		
			
			String crptedCardNo = synchExecutorService.encrypt(cardNo);

			/**銀行卡號*/
	 		String cardId = (String)ParamUtil.findValue(ds.getParams_in(), "cardId");
			String crptedCardId = synchExecutorService.encrypt(cardId);

	 		/**请求参数记录到日志*/
	 		paramForLog.put("name", name);
	 		paramForLog.put("cardNo", cardNo);
	 		paramForLog.put("cardId", cardId);	 		

	 		/**组织上下文数据*/
	 		Map<String,Object> contxt = new HashMap<String,Object>();
	 		contxt.put("trade_id", trade_id);
	 		contxt.put("crptedCardId",crptedCardId);
	 		contxt.put("crptedCardNo",crptedCardNo);
	 		contxt.put("typeNo","00");
	 		contxt.putAll(paramForLog);
	 		
			/**发送请求*/
			logObj.setIncache("0");
			String retrnStr = sendRequest(contxt);

	 		DataSourceLogEngineUtil.writeLog2LogSys(new LoggingEvent(trade_id,retrnStr,new String[]{trade_id}));
	 		logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
	 		/**解析响应数据*/
			JSONObject retrnJsn = JSONObject.parseObject(retrnStr);
			JSONObject busiData = retrnJsn != null ? retrnJsn.getJSONObject("data"):null;
			/**对方返回成功码 0*/
			if(isSuccess(contxt,retrnJsn)){
                if(busiDataIsValid(contxt,busiData)){
    				retStatus = CRSStatusEnum.STATUS_SUCCESS;
    				retMsg = "采集成功";
    				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
    				logger.info("{} 开始保存返回数据 ",prefix);
					doSaveOper(contxt,busiData);
					retdata.putAll(visitBusiData(contxt,busiData));
					logger.info("{} 保存返回数据完成 ",prefix);
					initTag = handleTag(contxt,busiData.getString("respCode"));
                }
			}
			else{
				String errMsg = null;
				if(!"0".equals(retrnJsn.getString("code"))){
//					errMsg = retrnJsn.getString("msg");
					errMsg = busiData!=null?busiData.getString("errorMsg"):""; 
					logger.error("{} 厂商返回信息: {}",trade_id,retrnStr);
				}else{
					if(busiDataIsValid(contxt,busiData)){
						errMsg = busiData.getString("respDesc"); 
						/**不支持改银行卡验证*/
						if("9924".equals(busiData.getString("respCode"))){
							retStatus= CRSStatusEnum.STATUS_FAILED_DS_BANKCARD_AUTHEN_NOTSUPPORT;
							retMsg = retStatus.getRet_msg();
						}
	                }
				}	
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
				logObj.setState_msg(errMsg);
				logger.error(prefix+" 银行卡鉴权调用失败 :{}",errMsg);
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


	private Map<? extends String, ? extends Object> visitBusiData(
			Map<String, Object> contxt, JSONObject busiData) {
		Map<String, Object> map = new HashMap<String,Object>();
		map = CommonUtil.sliceMapIfNotBlank(busiData,new String[]{"respDesc","respCode"});
		map.put("name", contxt.get("name"));
		map.put("cardNo", contxt.get("cardNo"));
		map.put("cardId", contxt.get("cardId"));
		return map;
	}


	private boolean busiDataIsValid(Map<String, Object> contxt,
			JSONObject busiData) {
		if(busiData == null){
			logger.warn("{} 业务数据为null ",contxt.get("trade_id"));
			return false;
		}
		return true;
	}


	private void doSaveOper(Map<String, Object> contxt, JSONObject busiData) {
		ywAuthenBankCardService.addAuthenBackCard(contxt,busiData);
	}

    /**交易是否成功*/
	private boolean isSuccess(Map<String, Object> contxt, JSONObject retrnJsn) {
		if(retrnJsn != null){
			if("0".equals(retrnJsn.getString("code"))){
				JSONObject busiData = retrnJsn.getJSONObject("data");
	            if(busiData != null && isSuccCode(busiData.getString("respCode")))
	            	return true;
			}
		}
		return false;
	}

    
	private boolean isSuccCode(String string) {
		return ArrayUtils.indexOf(succCode, string) > -1;
	}


	private String sendRequest(Map<String, Object> contxt) throws UnsupportedEncodingException {
		
		StringBuffer reqUrl_SB = new StringBuffer(formatUrl(propertyEngine.readById("sys.credit.client.yiwei.authenbankcard3.url"))).append(
        		URLEncoder.encode((String)contxt.get("name"),"UTF-8"))
        		.append(urlSplit).append(contxt.get("cardNo")).append(urlSplit).
        		append(contxt.get("cardId")).append(urlSplit).append(userName).
        		append(urlSplit).append(password).append(urlSplit);
	    String reqUrl = reqUrl_SB.toString();
        String rspData = HttpsHelper.doGet(reqUrl);
        
        DataSourceLogEngineUtil.writeLog2LogSys(new LoggingEvent((String)contxt.get("trade_id"),
        		reqUrl,new String[]{(String)contxt.get("trade_id")}));

		return rspData;
	}

   
	private String handleTag(Map<String, Object> contxt, String string) {
	    if(ArrayUtils.indexOf(succCode, string) > -1){
	    	return Conts.TAG_TST_SUCCESS;
	    }
		return Conts.TAG_TST_FAIL;
	}
		
	
	public static void main(String[] args) {}
}
