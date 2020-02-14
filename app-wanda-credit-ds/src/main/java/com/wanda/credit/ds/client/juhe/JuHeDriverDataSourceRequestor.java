package com.wanda.credit.ds.client.juhe;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

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
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.common.util.GladDESUtils;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.client.zhengtong.BaseZTDataSourceRequestor;
import com.wanda.credit.ds.dao.iface.juhe.IJuHeCarSearchService;
import com.wanda.credit.ds.iface.IDataSourceRequestor;

/**
 * 350 驾驶证信息查询姓名校验版
 * @author liunan
 */
@DataSourceClass(bindingDataSourceId="ds_juhe_driverSearch")
public class JuHeDriverDataSourceRequestor extends BaseJuheDSRequestor
implements IDataSourceRequestor {
	private final  Logger logger = LoggerFactory.getLogger(JuHeDriverDataSourceRequestor.class);
	@Autowired
	private IPropertyEngine propertyEngine;
	@Autowired
	private IJuHeCarSearchService juheSaveCarService;
	@Override
	public Map<String, Object> request(String trade_id, DataSource ds){
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		logger.info("{} 聚合车辆信息查询开始...",trade_id);
		TreeMap<String, Object> retdata = new TreeMap<String, Object>();
		Map<String, Object> rets = new HashMap<String, Object>();
		DataSourceLogVO logObj = new DataSourceLogVO(trade_id);
		Map<String, Object> reqparam = new HashMap<String, Object>();
		Map<String, String> mapStr = new HashMap<String, String>();
		String resource_tag = Conts.TAG_SYS_ERROR;
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));
		try{
			String url = propertyEngine.readById("ds_juhe_verifyDriverSearch_url");
			int incache_days = Integer.valueOf(propertyEngine.readById("ds_juhe_carSearch_incacheTime"));//车辆数据缓存时间(天)
			logObj.setDs_id(ds.getId());
			logObj.setReq_url(url);
			logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
			logObj.setIncache("1");
			
	 		String name = ParamUtil.findValue(ds.getParams_in(), "name").toString();   //姓名 
			String cardNo = ParamUtil.findValue(ds.getParams_in(), "cardNo").toString().toUpperCase(); //车牌号
 
			reqparam.put("name", name);
			reqparam.put("cardNo", cardNo);

			//加密敏感信息
			String encardNo = GladDESUtils.encrypt(cardNo);
			
			//参数校验 - 身份证号码
			String validate = CardNoValidator.validate(cardNo);
			if (!StringUtil.isEmpty(validate)) {
				logger.info("{} 身份证格式校验错误： {}" , prefix , validate);
				logObj.setState_msg("身份证格式校验错误");
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_IDCARD_ERROR);
				rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_IDCARD_ERROR.getRet_msg());
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				return rets;
			}
			if(!BaseZTDataSourceRequestor.isChineseWord(name)){				
				logger.warn("{} 姓名入参格式不符合要求:{}", prefix,name);
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_JIAO_NAME_ERROR);
				rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_WARN_DS_JIAO_NAME_ERROR.getRet_msg());
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				return rets;
			}
			logger.info("{} 聚合车辆信息数据库查询开始...",trade_id);
			if(juheSaveCarService.inCachedCount(name, encardNo,ds.getId(), incache_days)){
				logger.info("{} 聚合车辆信息数据库查询匹配到数据",trade_id);
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
				String incache_map = juheSaveCarService.findCarDetail(ds.getId(), name, encardNo, incache_days);
				JSONObject incache_json = JSON.parseObject(incache_map);
				retdata.putAll(incache_json);
				
				resource_tag = Conts.TAG_INCACHE_TST_SUCCESS;
				
				rets.put(Conts.KEY_RET_DATA, retdata);
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
				rets.put(Conts.KEY_RET_MSG, "采集成功!");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				return rets;
			}
			mapStr.put("key", propertyEngine.readById("ds_juhe_verifyDriverSearch_key"));
			mapStr.put("name", name);
			mapStr.put("IDCardNo", cardNo);
			logObj.setIncache("0");
			
			Map<String,Object> rspDataMap = 
					RequestHelper.doGetRetFull(url, mapStr, 
							new HashMap<String, String>(), true, null, "UTF-8");
			logger.info("{} 返回数据 {}",trade_id, rspDataMap.get("res_body_str"));
			
			JSONObject rspData = JSONObject.parseObject(String.valueOf(rspDataMap.get("res_body_str")));
			
			if (StringUtil.isEmpty(rspData) || "null".equals(rspData)) {
				logger.info("{} http请求返回结果为空" , prefix);
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_TIMEOUT);
				logObj.setState_msg("请求超时");
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_PY_CARINFO_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "车辆信息查询失败!");
				return rets;
			}
			
			logObj.setBiz_code1(rspData.getString("error_code") + "-" + rspData.getString("reason"));		
			if("0".equals(rspData.getString("error_code"))){
				JSONObject result = new JSONObject();
				resource_tag = Conts.TAG_TST_SUCCESS;
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
				if("一致".equals(rspData.getString("reason"))){
					result = rspData.getJSONObject("result");
					result.put("found", "1");//姓名和车牌号匹配
				}else{
					result.put("found", "0");//姓名和车牌号不匹配
				}				
				retdata.putAll(result);
				rets.put(Conts.KEY_RET_DATA, retdata);
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
				rets.put(Conts.KEY_RET_MSG, "采集成功!");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				juheSaveCarService.saveCarDetail(trade_id, ds.getId(), name, encardNo, JSON.toJSONString(result));
				logger.info("处理成功" , prefix);
			}else if("235001".equals(rspData.getString("error_code")) || "235008".equals(rspData.getString("error_code"))){
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
				logger.info("{} 传入参数有误:{}" , prefix,rspData.getString("reason"));
				resource_tag = Conts.TAG_SYS_ERROR;
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_IDCARD_ERROR);
				rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_IDCARD_ERROR.getRet_msg());
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				return rets;
			}else if("235005".equals(rspData.getString("error_code"))){
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
				logger.info("{} 查无车辆信息:{}" , prefix,rspData.getString("reason"));
				resource_tag = Conts.TAG_SYS_ERROR;
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_CARDRIVER_EXCEPTION01);
				rets.put(Conts.KEY_RET_MSG, "查无记录");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				return rets;
			}else if("235002".equals(rspData.getString("error_code"))){
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
				logger.info("{} 姓名格式不对" , prefix);
				resource_tag = Conts.TAG_SYS_ERROR;
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_JIAO_NAME_ERROR);
				rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_WARN_DS_JIAO_NAME_ERROR.getRet_msg());
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				return rets;
			}else{
				resource_tag = Conts.TAG_SYS_ERROR;
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_PY_CARINFO_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "车辆信息查询失败!");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				return rets;
			}			
		}catch(Exception ex){
			resource_tag = Conts.TAG_SYS_ERROR;
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_PY_CARINFO_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, "车辆信息查询失败!");
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
			executorDtoService.writeDsLog(trade_id,logObj,true);
			executorDtoService.writeDsParamIn(trade_id, reqparam, logObj,true);
		}
		return rets;
	}
}
