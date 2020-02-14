package com.wanda.credit.ds.client.guoztCar;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.apache.commons.lang.StringUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.api.iface.IExecutorSecurityService;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.CardNoValidator;
import com.wanda.credit.base.util.ExceptionUtil;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.log.ds.vo.LoggingEvent;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.dao.domain.Guozt_Black_Car_Result;
import com.wanda.credit.ds.dao.iface.IGuoZTBlackCarService;
import com.wanda.credit.ds.iface.IDataSourceRequestor;
@DataSourceClass(bindingDataSourceId="ds_guozt_car")
public class GuoZTDataCarRequestor extends BaseGuoZTCarSourcesRequestor
		implements IDataSourceRequestor {
	private final  Logger logger = LoggerFactory.getLogger(GuoZTDataCarRequestor.class);
	
	@Autowired
	private IGuoZTBlackCarService blackCarService;
	@Autowired
	public IPropertyEngine propertyEngine;
	@Autowired
	private IExecutorSecurityService synchExecutorService;
	@Override
	public Map<String, Object> request(String trade_id, DataSource ds){
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		String guozt_url = propertyEngine.readById("ds_guoztCar_url");
		TreeMap<String, Object> retdata = new TreeMap<String, Object>();
		Map<String, Object> rets = null;
		DataSourceLogVO logObj = new DataSourceLogVO();
		Map<String, Object> reqparam = new HashMap<String, Object>();
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));//log请求时间
		String resource_tag = Conts.TAG_TST_FAIL;
		String token = null;
		try{			
			logger.info("{}国政通租车数据调用开始...", new String[] { prefix });
			logObj.setDs_id("ds_guozt_car");
			rets = new HashMap<String, Object>();
	 		String name = ParamUtil.findValue(ds.getParams_in(), paramIds[0]).toString();   //姓名 
			String cardNo = ParamUtil.findValue(ds.getParams_in(), paramIds[1]).toString(); //身份证号码
			String enCardNo = synchExecutorService.encrypt(cardNo);
			String prodStr = "";
			String retStat2 = "";
			reqparam.put("name", name);
			reqparam.put("cardNo", cardNo);			
			logObj.setReq_url(guozt_url);
			logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
			if(StringUtils.isNotEmpty(CardNoValidator.validate(cardNo))){
				logger.warn("{}入参格式不符合要求!", new String[] { prefix });
				logObj.setIncache("1");
				logObj.setState_msg("身份证号码不符合规范");
				resource_tag = Conts.TAG_SYS_ERROR;
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_IDCARD_ERROR);
				rets.put(Conts.KEY_RET_MSG, "您输入的为无效身份证号码，请核对后重新输入!");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				return rets;
			}else{
				logObj.setIncache("0");
				token = blackCarService.getTokenSql(trade_ids1);
				logger.warn("{}国政通租车token获取成功!", new String[] { prefix });
				Gson carGson = new GsonBuilder().disableHtmlEscaping().create();
				Guozt_Black_Car_Result blackCarResult = new Guozt_Black_Car_Result();
				blackCarResult.setTrade_id(trade_id);
				blackCarResult.setCardno(enCardNo);
				blackCarResult.setName(name);

				prodStr = bookCar(name,cardNo,prefix,guozt_url,token);
				
				if(!StringUtils.isNotEmpty(prodStr)){
					logger.warn("{}租车黑名单数据获取失败!", new String[] { prefix });
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_GUOZT_CAR_FAIL);
					rets.put(Conts.KEY_RET_MSG, "租车黑名单信息查询失败!");
					rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				}else{						
					Receive receive = carGson.fromJson(prodStr, Receive.class);
					retStat2 = receive.getRet();
					
					if("10020".equals(retStat2) || "10021".equals(retStat2)){
						logger.warn("{}令牌失效，重新获取!", new String[] { prefix });
						token = getToken(prefix,guozt_url,true);
						blackCarService.updateToken(token, trade_ids1);
						prodStr = bookCar(name,cardNo,prefix,guozt_url,token);
						
						receive = carGson.fromJson(prodStr, Receive.class);
						retStat2 = receive.getRet();
					}
//					logger.warn("{}租车黑名单数据,返回数据：{}", new String[] { prefix,prodStr });
					DataSourceLogEngineUtil.writeLog2LogSys(new LoggingEvent(trade_id, prodStr, new String[] { trade_id }));
					blackCarResult.setRetstat2(retStat2);
					blackCarResult.setRetstat1("10000");
					blackCarResult.setToken(token);
					logObj.setBiz_code1(retStat2);
					logger.warn("{}租车黑名单数据解析开始...", new String[] { prefix });
					if("10000".equals(retStat2)){
						Object obj = receive.getDat();
						String json = carGson.toJson(obj);
						RowsBody rowsBody = carGson.fromJson(json, RowsBody.class);
						int total = rowsBody.getTotal();
						if(total == 1){
							resource_tag = Conts.TAG_TST_SUCCESS;
							logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
						}
						blackCarResult.setTotal(total+"");
						String rspDetail = rowsBody.getRows();
						String[] rspDetails = rspDetail.split(";");
						if(rspDetails.length <= 1){
							blackCarResult.setBlack_type(rspDetails[0]);
						}else if(rspDetails.length == 2){
							blackCarResult.setBlack_type(rspDetails[0]);
							blackCarResult.setBlack_time(rspDetails[1]);
						}else if(rspDetails.length == 3){
							blackCarResult.setBlack_type(rspDetails[0]);
							blackCarResult.setBlack_time(rspDetails[1]);
							blackCarResult.setBlack_list(rspDetails[2]);
						}else{
							blackCarResult.setBlack_list(rspDetail);
						}						
						retdata.put("blackDetail", rspDetail);
						rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
						rets.put(Conts.KEY_RET_DATA, retdata);
						rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
						rets.put(Conts.KEY_RET_MSG, "采集成功!");
					}else{
						logger.warn("{}租车黑名单数据获取失败,返回失败码为：{}", new String[] { prefix,retStat2});
						rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_GUOZT_CAR_FAIL);
						rets.put(Conts.KEY_RET_MSG, "租车黑名单信息查询失败!");
						rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
					}
				}	
				blackCarService.add(blackCarResult);
			}
		}catch(Exception ex){
			resource_tag = Conts.TAG_SYS_ERROR;
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, "数据源处理时异常! 详细信息:"+ex.getMessage());
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
