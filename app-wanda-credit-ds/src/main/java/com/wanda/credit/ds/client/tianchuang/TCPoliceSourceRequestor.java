package com.wanda.credit.ds.client.tianchuang;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.api.iface.IExecutorSecurityService;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.exception.ServiceException;
import com.wanda.credit.base.util.CardNoValidator;
import com.wanda.credit.base.util.ExceptionUtil;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.dao.domain.Guozt_badInfo_check_result;
import com.wanda.credit.ds.dao.iface.IGuoZTBadInfoService;
import com.wanda.credit.ds.iface.IDataSourceRequestor;
@DataSourceClass(bindingDataSourceId="ds_tianchuang_crime")
public class TCPoliceSourceRequestor extends BaseTianChSourceRequestor
		implements IDataSourceRequestor {
	private final  Logger logger = LoggerFactory.getLogger(TCPoliceSourceRequestor.class);
	private static final String BIZ_CODE_1_SUCCESS_NONE = "0";//查询成功，无记录
	private static final String BIZ_CODE_1_SUCCESS = "1";//查询成功，有记录
	@Autowired
	private IExecutorSecurityService synchExecutorService;
	@Autowired
	public IPropertyEngine propertyEngine;
	@Autowired
	private IGuoZTBadInfoService badInfoService;
	@Override
	public Map<String, Object> request(String trade_id, DataSource ds){
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		logger.info("{}天创信用数据源请求开始...", prefix);
		String url = propertyEngine.readById("ds_tianchuang_url");
		Map<String, Object> rets = null;
		Map<String, Object> retdata = null;
		DataSourceLogVO logObj = new DataSourceLogVO();
		Map<String, Object> reqparam = new HashMap<String, Object>();
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));//log请求时间
		String resource_tag = Conts.TAG_SYS_ERROR;
		try{	
			String name = ParamUtil.findValue(ds.getParams_in(), paramIds[0]).toString(); 
			String cardNo = ParamUtil.findValue(ds.getParams_in(), paramIds[1]).toString(); 			
			logObj.setDs_id(ds.getId());
			logObj.setReq_url(url);
			logObj.setTrade_id(trade_id);
			reqparam.put("name", name);
			reqparam.put("cardNo", cardNo);
			rets = new HashMap<String, Object>();	
			String enCardNo = synchExecutorService.encrypt(cardNo);
			logger.info("{} 天创信用数据源加密成功!", prefix);
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
			String cachedTradeid = badInfoService.inCached(ds.getId(), name, enCardNo);
			boolean inCache = StringUtils.isNotBlank(cachedTradeid);
			if (inCache) {
				logObj.setIncache("1");
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
				List<Guozt_badInfo_check_result> badInfoList = badInfoService.getBadInfoList(cachedTradeid);
				if (badInfoList == null || badInfoList.isEmpty()) {
					inCache = false;
				} else {
					logger.info("{}缓存数据中存在犯罪查询数据！",prefix);
					retdata = parseBadInfoListToMap(badInfoList);
					retdata.put("trade_id", cachedTradeid);
					resource_tag = Conts.TAG_INCACHE_TST_SUCCESS;
				}
			}
			if (!inCache) {
				logObj.setIncache("0");
				String res = verifyIdcard(trade_id,url,cardNo,name);
				logger.error("{} 天创信用犯罪吸毒黑名单查询返回信息:{}", prefix,res);
				if(StringUtil.isEmpty(res)){
					logger.error("{} 天创信用犯罪吸毒黑名单查询返回异常！", prefix);
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED);
					rets.put(Conts.KEY_RET_MSG, "犯罪吸毒黑名单查询失败");
					logObj.setState_msg("天创信用犯罪吸毒黑名单查询返回异常");
					return rets;
				}
				JSONObject json = (JSONObject) JSONObject.parse(res);
				if("0".equals(json.getString("status"))){
					JSONObject data = (JSONObject) JSONObject.parse(json.getString("data"));
					logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
					logObj.setBiz_code1(data.getString("result")+","+data.getString("result"));
					logObj.setBiz_code2(data.getString("seqNum"));
					if (BIZ_CODE_1_SUCCESS.equals(data.getString("result"))) {
						resource_tag = Conts.TAG_TST_SUCCESS;
						List<Guozt_badInfo_check_result> badInfoList = doSaveOperation(data, enCardNo, name, trade_id);
						retdata = parseBadInfoListToMap(badInfoList);
						retdata.put("trade_id", trade_id);						
					} else if (BIZ_CODE_1_SUCCESS_NONE.equals(data.getString("result"))) {
						rets.clear();
						resource_tag = Conts.TAG_TST_SUCCESS;
						rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_GUOZT_CRIME_NORECORD);
						rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_WARN_DS_GUOZT_CRIME_NORECORD.ret_msg);
						rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
						logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
						return rets;
					} else {
						logger.info("{} 天创信用犯罪吸毒黑名单查询失败:{}", prefix);
						rets.clear();
						rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED);
						rets.put(Conts.KEY_RET_MSG, "犯罪吸毒黑名单查询失败!");
						rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
						return rets;
					}
				}else{
					logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
					logger.info("{} 天创信用犯罪吸毒黑名单查询失败:{}", prefix,json.getString("message"));
					rets.clear();
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED);
					rets.put(Conts.KEY_RET_MSG, "犯罪吸毒黑名单查询失败!");
					rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
					return rets;
				}				
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
	private Map<String, Object> parseBadInfoListToMap(List<Guozt_badInfo_check_result> badInfoList) {
		Map<String, Object> map = new HashMap<String, Object>();
		if (badInfoList == null || badInfoList.isEmpty())
			return map;
		List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
		for (Guozt_badInfo_check_result badInfo : badInfoList) {
			map.put("checkCode", badInfo.getCheckCode());
			map.put("checkMsg", badInfo.getCheckMsg());
			Map<String, Object> item = new HashMap<String, Object>();
			item.put("caseType", badInfo.getCaseType());
			item.put("caseSource", badInfo.getCaseSource());
			item.put("caseTime", badInfo.getCaseTime());
			items.add(item);
		}
		map.put("items", items);
		return map;
	}
	private List<Guozt_badInfo_check_result> doSaveOperation(JSONObject data, 
			String cardNo, String name, String trade_id) throws ServiceException {
		List<Guozt_badInfo_check_result> list = new ArrayList<Guozt_badInfo_check_result>();
		List<String> contain_list = new ArrayList<String>();
		String checkCode = data.getString("checkCode");
		for(Object arr:data.getJSONArray("items")){
			JSONObject tmp = (JSONObject) arr;
			Guozt_badInfo_check_result guozt = new Guozt_badInfo_check_result();
			guozt.setCardNo(cardNo);
			guozt.setTrade_id(trade_id);
			guozt.setName(name);
			guozt.setCheckCode(data.getString("checkCode"));
			guozt.setCheckMsg(data.getString("checkMsg"));
			guozt.setCaseSource(tmp.getString("caseSource"));
			guozt.setCaseTime(tmp.getString("caseTime"));
			guozt.setCaseType("无");				
			if(!StringUtil.isEmpty(tmp.getString("caseSource")) && !StringUtil.isEmpty(tmp.getString("caseTime"))){
				list.add(guozt);
			}
		}
		for(String checks:checkCode.split(",")){
			for(Object arr:data.getJSONArray("items")){
				JSONObject tmp = (JSONObject) arr;
				if(getbadCheck(checks,tmp)){
					logger.info("{} item里有:{}",trade_id,checks);
					contain_list.add(checks);
				}
			}
		}
		logger.info("{} contain_list内容:{}",trade_id,JSON.toJSONString(contain_list));
		for(String checks:checkCode.split(",")){
			if(!getbadList(checks,contain_list)){
				logger.info("{} item里没有:{}",trade_id,checks);
				Guozt_badInfo_check_result guozt = new Guozt_badInfo_check_result();
				guozt.setCardNo(cardNo);
				guozt.setTrade_id(trade_id);
				guozt.setName(name);
				guozt.setCheckCode(checks);
				guozt.setCheckMsg(data.getString("checkMsg"));
				guozt.setCaseSource(badMap.get(checks));
				guozt.setCaseTime("无");
				guozt.setCaseType("无");				
				list.add(guozt);
			}
		}
		badInfoService.add(list);
		return list;
	}
	private boolean getbadCheck(String checks,JSONObject tmp){
		if(tmp.getString("caseSource").equals(badMap.get(checks))){
			return true;
		}
		return false;
	}
	private boolean getbadList(String checks,List<String> contain_list){
		for(String tmp:contain_list){
			if(checks.equals(tmp)){
				return true;
			}
		}
		return false;
	}
	private static Map<String, String> badMap = new HashMap<String, String>();
	static {
		badMap.put("1", "在逃");
		badMap.put("2", "前科");
		badMap.put("3", "涉毒");
		badMap.put("4", "吸毒");
	}
}
