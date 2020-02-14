package com.wanda.credit.ds.client.huifu;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
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
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.common.util.GladDESUtils;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.client.zhengtong.BaseZTDataSourceRequestor;
import com.wanda.credit.ds.dao.iface.IAllAuthCardService;
import com.wanda.credit.ds.iface.IDataSourceRequestor;
@DataSourceClass(bindingDataSourceId="ds_huifu_bank4")
public class HuiFuBankCard4DataSourceRequestor extends BaseHuifuDSRequestor
implements IDataSourceRequestor {
	private final  Logger logger = LoggerFactory.getLogger(HuiFuBankCard4DataSourceRequestor.class);
	@Autowired
	private IPropertyEngine propertyEngine;
	@Autowired
    protected IAllAuthCardService allAuthCardService;
	@Override
	public Map<String, Object> request(String trade_id, DataSource ds){
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		TreeMap<String, Object> retdata = new TreeMap<String, Object>();
		Map<String, Object> rets = new HashMap<String, Object>();
		DataSourceLogVO logObj = new DataSourceLogVO(trade_id);
		Map<String, Object> reqparam = new HashMap<String, Object>();
		String resource_tag = Conts.TAG_SYS_ERROR;
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));
		try{
			String request_url = propertyEngine.readById("ds_huifu_verifybank_url");
			String callback_url = propertyEngine.readById("ds_huifu_verifybank_call_url");
			String app_token = propertyEngine.readById("ds_huifu_verifybank_apptoken");
			String appkey = propertyEngine.readById("ds_huifu_verifybank_appkey");
			int huifu_times = Integer.valueOf(propertyEngine.readById("ds_huifu_verifybank_times"));
			logObj.setDs_id(ds.getId());
			logObj.setReq_url(request_url);
			logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
	 		String name = ParamUtil.findValue(ds.getParams_in(), "name").toString();   //姓名 
			String cardNo = ParamUtil.findValue(ds.getParams_in(), "cardNo").toString(); //身份证号码
			String cardId = ParamUtil.findValue(ds.getParams_in(), "cardId").toString(); //银行卡号
			String phone = ParamUtil.findValue(ds.getParams_in(), "phone").toString(); //手机号
			reqparam.put("name", name);
			reqparam.put("cardNo", cardNo);
			reqparam.put("cardId", cardId);
			reqparam.put("phone", phone);
			if(StringUtils.isNotEmpty(CardNoValidator.validate(cardNo))){
				logObj.setIncache("1");
				logObj.setState_msg("身份证号码不符合规范");
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_IDCARD_ERROR);
				rets.put(Conts.KEY_RET_MSG, "您输入的为无效身份证号码，请核对后重新输入!");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				return rets;
			}		
			if(!BaseZTDataSourceRequestor.isChineseWord(name)){
				logObj.setIncache("1");
				logger.warn("{} 姓名入参格式不符合要求:{}", prefix,name);
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_INVALID_NAME);
				rets.put(Conts.KEY_RET_MSG, "您输入的姓名无效，请核对后重新输入!");
				return rets;
			}
			logObj.setIncache("0");

			JSONObject result = getBankResult(trade_id,request_url,callback_url,app_token,appkey,
					name,cardNo,cardId,phone,5000,huifu_times);
			
			if (result==null) {
				logger.info("{} http请求返回结果为空" , prefix);
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_TIMEOUT);
				logObj.setState_msg("请求超时");					
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "数据源调用失败!");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				return rets;
			}
			logObj.setBiz_code2(result.getString("order_id"));
			String return_code = result.getString("return_code");
			if("90000".equals(return_code)){
				String resp_code = result.getString("resp_code");
				if("00".equals(resp_code)){
					resource_tag = Conts.TAG_TST_SUCCESS;
					retdata.put("respCode", "2000");
					retdata.put("respDesc", "认证一致");
					retdata.put("detailRespCode", "");
					retdata.put("respDetail", "");
					logger.info("{} 认证一致",trade_id);
					String req_values = cardNo+"_"+cardId+ "_" + phone;
					allAuthCardService.saveAuthCard(ds.getId(), trade_id, name, GladDESUtils.encrypt(cardNo), 
							GladDESUtils.encrypt(cardId), GladDESUtils.encrypt(phone),retdata, req_values);
				}else if("01".equals(resp_code)){
					resource_tag = Conts.TAG_TST_SUCCESS;
					retdata.put("respCode", "2001");
					retdata.put("respDesc", result.getString("resp_info"));
					retdata.put("detailRespCode", "");
					retdata.put("respDetail", "");
					logger.info("{} 认证不一致",trade_id);
				}else{
					if(result.getString("resp_info").contains("请求频繁") || result.getString("resp_info").contains("交易次数超限")){
						rets.put(Conts.KEY_RET_MSG, "银行卡鉴权失败:请求频繁,验证次数超限");
					}else{
						rets.put(Conts.KEY_RET_MSG, "银行卡鉴权失败:"+result.getString("resp_info"));
					}
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_ZT_BANKCARD_AUTHEN_EXCEPTION);
					rets.put(Conts.KEY_RET_TAG, new String[] { Conts.TAG_UNFOUND });
					logger.error("{} 外部返回识别失败,返回结果为空", trade_id);
					return rets;
				}
			}else{
				resource_tag = Conts.TAG_SYS_ERROR;
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "数据源调用失败!");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				return rets;
			}			
			retdata.put("name", name);
            retdata.put("cardNo", cardNo);
            retdata.put("cardId", cardId);
            
            
			rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
			rets.put(Conts.KEY_RET_DATA, retdata);
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
			rets.put(Conts.KEY_RET_MSG, "采集成功!");			
		}catch(Exception ex){
			resource_tag = Conts.TAG_SYS_ERROR;
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, "数据源查询异常!");
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
			logger.info("{} 保存ds Log开始..." ,prefix);
			executorDtoService.writeDsLog(trade_id,logObj,true);
			executorDtoService.writeDsParamIn(trade_id, reqparam, logObj,true);
			logger.info("{} 保存ds Log结束" ,prefix);
		}
		return rets;
	}
}
