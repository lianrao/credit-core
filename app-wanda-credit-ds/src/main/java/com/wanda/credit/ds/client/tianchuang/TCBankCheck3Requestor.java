package com.wanda.credit.ds.client.tianchuang;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.CardNoValidator;
import com.wanda.credit.base.util.ExceptionUtil;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.common.util.GladDESUtils;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.dao.iface.IAllAuthCardService;
import com.wanda.credit.ds.iface.IDataSourceRequestor;
@DataSourceClass(bindingDataSourceId="ds_tianchuang_bank3")
public class TCBankCheck3Requestor extends BaseTianChSourceRequestor
		implements IDataSourceRequestor {
	private final  Logger logger = LoggerFactory.getLogger(TCBankCheck3Requestor.class);
	@Autowired
	public IPropertyEngine propertyEngine;
	@Autowired
    protected IAllAuthCardService allAuthCardService;
	@Override
	public Map<String, Object> request(String trade_id, DataSource ds){
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		logger.info("{}天创信用数据源请求开始...", prefix);
		String url = propertyEngine.readById("ds_tianchuang_bank3_url");
		Map<String, Object> rets = null;
		Map<String, Object> retdata = null;
		DataSourceLogVO logObj = new DataSourceLogVO();
		Map<String, Object> reqparam = new HashMap<String, Object>();
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));//log请求时间
		String resource_tag = Conts.TAG_SYS_ERROR;
		try{	
			String name = String.valueOf(ParamUtil.findValue(ds.getParams_in(), paramIds[0])); // 身份证号码
			String cardNo = String.valueOf(ParamUtil.findValue(ds.getParams_in(), paramIds[1])); // 姓名
			String cardId = String.valueOf(ParamUtil.findValue(ds.getParams_in(), paramIds[2])); // 照片数据包 			
			logObj.setDs_id(ds.getId());
			logObj.setReq_url(url);
			logObj.setTrade_id(trade_id);
			reqparam.put("name", name);
			reqparam.put("cardNo", cardNo);
			reqparam.put("cardId", cardId);
			rets = new HashMap<String, Object>();
			retdata = new HashMap<String, Object>();
			//参数校验 - 身份证号码
			String validate = CardNoValidator.validate(cardNo);
			if (!StringUtil.isEmpty(validate)) {
				logObj.setIncache("1");
				logger.info("{} 身份证格式校验错误： {}" , prefix , validate);
				logObj.setState_msg("身份证格式校验错误");
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_IDCARD_ERROR);
				rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_IDCARD_ERROR.getRet_msg());
				return rets;
			}
			logObj.setIncache("0");
			Map<String, String> param = new HashMap<String,String>();
			param.put("name", name);
			param.put("idcard", cardNo);
			param.put("bankcard", cardId);
			String res = verifyDriverLicensce(trade_id,url,param);
			if(StringUtil.isEmpty(res)){
				logger.error("{} 天创信用银行卡三要素查询返回异常！", prefix);
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_ZT_BANKCARD_AUTHEN_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "银行卡鉴权失败");
				rets.put(Conts.KEY_RET_TAG, new String[] { resource_tag });
				logger.error("{} 银行卡鉴权失败,返回结果为空", trade_id);
				return rets;
			}
			logger.error("{} 天创信用银行卡三要素查询结束:{}", prefix,res);
			JSONObject json = (JSONObject) JSONObject.parse(res);
			if("0".equals(json.getString("status"))){
				JSONObject data = (JSONObject) JSONObject.parse(json.getString("data"));
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
				logObj.setBiz_code1(data.getString("result")+","+data.getString("result"));
				logObj.setBiz_code2(json.getString("seqNum"));
				
				retdata.put("name", name);
				retdata.put("cardNo", cardNo);
				retdata.put("cardId", cardId);
				resource_tag = bulidResp(trade_id,data.getString("result"),retdata,rets);
				String req_values = cardNo+"_"+cardId;
				allAuthCardService.saveAuthCard(ds.getId(), trade_id, name, GladDESUtils.encrypt(cardNo), GladDESUtils.encrypt(cardId), "",
						retdata, req_values);
			}else if("2".equals(json.getString("status"))){
				logger.info("{} 天创信用银行卡三要素传入参数有误", prefix);
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
				resource_tag = Conts.TAG_SYS_ERROR;
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_PARAM_INVALID);
				rets.put(Conts.KEY_RET_MSG, "传入参数格式有误");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				return rets;
			}else{
				logger.info("{} 天创信用银行卡三要素验证失败1", prefix);
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_ZT_BANKCARD_AUTHEN_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "银行卡鉴权失败");
				rets.put(Conts.KEY_RET_TAG, new String[] { resource_tag });
				logger.error("{} 银行卡鉴权失败,返回结果为空", trade_id);
				return rets;
			}
			rets.put(Conts.KEY_RET_DATA, retdata);
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
			rets.put(Conts.KEY_RET_MSG, "采集成功!");
			rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
		}catch(Exception ex){
			resource_tag = Conts.TAG_SYS_ERROR;
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, "数据源处理时异常!");
			logger.error("{} 数据源处理时异常：{}",prefix,ExceptionUtil.getTrace(ex));
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
}
