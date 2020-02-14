package com.wanda.credit.ds.client.aijin;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONArray;
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
import com.wanda.credit.ds.client.aijin.beans.YuanJin_bad_Msg;
import com.wanda.credit.ds.dao.domain.Guozt_badInfo_check_result;
import com.wanda.credit.ds.dao.iface.IGuoZTBadInfoService;
import com.wanda.credit.ds.iface.IDataSourceRequestor;

/**
 * 爰金不良信息 101400
 * */
@DataSourceClass(bindingDataSourceId="ds_aijin_badMsg")
public class AiJinBadDetailRequestor extends BaseAijinDataSourceRequestor
		implements IDataSourceRequestor {
	private final  Logger logger = LoggerFactory.getLogger(AiJinBadDetailRequestor.class);
	@Autowired
	private IExecutorSecurityService synchExecutorService;
	@Autowired
	public IPropertyEngine propertyEngine;
	@Autowired
	private IGuoZTBadInfoService badInfoService;
	@Override
	public Map<String, Object> request(String trade_id, DataSource ds){
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		logger.info("{}爱金不良信息数据源(重点人口筛查)请求开始...", prefix);
		Map<String, Object> rets = null;
		Map<String, Object> retdata = null;
		DataSourceLogVO logObj = new DataSourceLogVO();
		Map<String, Object> reqparam = new HashMap<String, Object>();
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));//log请求时间
		String yuanjin_face_url = propertyEngine.readById("ds_yuanjin_badmsg_url");//爰金调用连接
		String yuanjin_face_account = propertyEngine.readById("ds_yuanjin_badmsg_account");//爰金账号
		String yuanjin_face_acode = propertyEngine.readById("ds_yuanjin_badmsg_acode");//服务代码
		String yuanjin_face_key = propertyEngine.readById("ds_yuanjin_badmsg_key");//服务代码
		
		String enCardNo = "";
		String resource_tag = Conts.TAG_SYS_ERROR;
		try{	
			String name = ParamUtil.findValue(ds.getParams_in(), paramIds[0]).toString(); //身份证号码
			String cardNo = ParamUtil.findValue(ds.getParams_in(), paramIds[1]).toString();   //姓名 			
			logObj.setDs_id(ds.getId());
			rets = new HashMap<String, Object>();	 		
			enCardNo = synchExecutorService.encrypt(cardNo);
			logger.info("{}爱金不良信息数据源(重点人口筛查)加密成功!", prefix);
			reqparam.put("name", name);
			reqparam.put("cardNo", cardNo);			
			logObj.setReq_url(yuanjin_face_url);
			logObj.setIncache("0");
			logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
			
			if(StringUtils.isNotEmpty(CardNoValidator.validate(cardNo))){
				logger.warn("{}入参格式不符合要求!", prefix);
				logObj.setIncache("1");
				logObj.setState_msg("身份证号码不符合规范");
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_IDCARD_ERROR);
				rets.put(Conts.KEY_RET_MSG, "您输入的为无效身份证号码，请核对后重新输入!");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
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
					logger.info("{} 查询不良信息开始了",prefix);
					retdata = parseBadInfoListToMap(badInfoList);
					retdata.put("trade_id", cachedTradeid);
					resource_tag = Conts.TAG_INCACHE_TST_SUCCESS;
				}
			}
			if (!inCache) {
				int EncType = 0;//加密方式（0：明文；1：MD5 2：Sha256）
				String clientNo = "";//客户自己定义，可忽略
				String param = "name=" + name + "&idnumber=" + cardNo + "&enctype=" + EncType + "&clientno=" + clientNo;
		        String sign = md5(yuanjin_face_acode + param + yuanjin_face_account + md5(yuanjin_face_key));//生成签名

		        String post_data = null;
		        try {
		            post_data = "acode=" + yuanjin_face_acode + "&param=" + URLEncoder.encode(param, "UTF-8") + "&account="
		                    + yuanjin_face_account + "&sign=" + sign;
		        } catch (UnsupportedEncodingException e) {
		            e.printStackTrace();
		        }
		        logger.info("{}爱金不良信息请求开始...", prefix);
		        String json = postHtml(yuanjin_face_url, post_data);
		        logger.info("{}爱金不良信息请求完成:{}", prefix,json);
		        if(StringUtil.isEmpty(json)){
					logger.error("{} 爱金查询返回异常！", prefix);
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
					rets.put(Conts.KEY_RET_MSG, "数据源处理时异常!");
					rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
					logObj.setState_msg("爱金查询返回异常");
					return rets;
				}
		        YuanJin_bad_Msg bad_msg = com.alibaba.fastjson.JSONObject.parseObject(json,YuanJin_bad_Msg.class);
		        logObj.setBiz_code1(bad_msg.getResponseCode());
		        logObj.setBiz_code2(bad_msg.getSerialNo());		        
	        	logObj.setState_msg(bad_msg.getResponseText());

		        if("100".equals(bad_msg.getResponseCode())){
		        	logger.info("{}爱金不良信息查询成功", prefix);		        	
		        	if(!"1".equals(bad_msg.getResult()) || StringUtil.isEmpty(bad_msg.getResultData())){
		        		resource_tag = Conts.TAG_TST_SUCCESS;
		        		if("2".equals(bad_msg.getResult())){
		        			resource_tag = Conts.TAG_UNMATCH;
		        			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_POLICE_INVALID);
		    				rets.put(Conts.KEY_RET_MSG, "身份证号码，姓名校验不一致!");
							rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
							logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
							return rets;
		        		}else{
		        			resource_tag = Conts.TAG_NOMATCH;
		        			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_GUOZT_CRIME_NORECORD);
							rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_WARN_DS_GUOZT_CRIME_NORECORD.ret_msg);
							rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
							logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
							return rets;
		        		}
		        	}else{		        		
		        		String badMsg = bad_msg.getResultData().getBadDetail();
		        		if(badMsg.contains("REVOKE")){
		        			logger.info("{}爱金不良信息修正信息", prefix);
		        			resource_tag = Conts.TAG_TST_SUCCESS;
							rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_GUOZT_CRIME_NORECORD);
							rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_WARN_DS_GUOZT_CRIME_NORECORD.ret_msg);
							rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
							logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
							return rets;
		        		}else{
		        			resource_tag = Conts.TAG_TST_SUCCESS;
							List<Guozt_badInfo_check_result> badInfoList = 
									doSaveOperation(getBadDetail(badMsg,bad_msg.getResultData().getNewestDate()), enCardNo, name, trade_id);
							retdata = parseBadInfoListToMap(badInfoList);
							retdata.put("trade_id", trade_id);
		        		}
		        	}
		        }else{
		        	logger.info("{}爱金不良信息查询失败:{}", prefix,bad_msg.getResponseText());		        	
		        	rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
					rets.put(Conts.KEY_RET_MSG, "数据源处理时异常!");
					rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
					logObj.setState_msg("爱金黑名单查询返回异常");
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
			list.add(guozt);
		}
		badInfoService.add(list);
		return list;
	}
	public JSONObject getBadDetail(String badMsg,String newestDate){
		JSONArray items = new JSONArray();	
		JSONObject result = new JSONObject();
		String bad = "";
		for(String tmp:badMsg.split(",")){
			if("AT_LARGE".equals(tmp)){
				bad=bad+"1";
			}else if("DRUG".equals(tmp)){
				bad=bad+"4";
			}else if("DRUG_RELATED".equals(tmp)){
				bad=bad+"3";
			}else if("ILLEGAL_A".equals(tmp)){
				bad=bad+"2";
			}else if("ILLEGAL_B".equals(tmp)){
				bad=bad+"2";
			}else if("ILLEGAL_C".equals(tmp)){
				bad=bad+"2";
			}else if("ILLEGAL_E".equals(tmp)){
				bad=bad+"2";
			}else if("ILLEGAL_F".equals(tmp)){
				bad=bad+"2";
			}
		}
		String badlist = "";
		String badMsgs = "";
		if(bad.contains("1")){
			badlist="1";
			badMsgs="比中在逃";
			JSONObject json = new JSONObject();
			json.put("caseSource", "在逃");
			items.add(json);
		}
		if(bad.contains("2")){
			if(!StringUtil.isEmpty(badlist)){
				badlist=badlist+",2";
				badMsgs=badMsgs+"、前科";
			}else{
				badlist="2";
				badMsgs="前科";
			}			
			JSONObject json = new JSONObject();
			json.put("caseSource", "前科");
			json.put("caseTime", getBadTime(newestDate));
			items.add(json);
		}
		if(bad.contains("3")){
			if(!StringUtil.isEmpty(badlist)){
				badlist=badlist+",3";
				badMsgs=badMsgs+"、涉毒";
			}else{
				badlist="3";
				badMsgs="涉毒";
			}
			JSONObject json = new JSONObject();
			json.put("caseSource", "涉毒");
			items.add(json);
		}
		if(bad.contains("4")){
			if(!StringUtil.isEmpty(badlist)){
				badlist=badlist+",4";
				badMsgs=badMsgs+"、吸毒";
			}else{
				badlist="4";
				badMsgs="吸毒";
			}
			JSONObject json = new JSONObject();
			json.put("caseSource", "吸毒");
			items.add(json);
		}
		result.put("checkCode", badlist);
		result.put("checkMsg", badMsgs);
		result.put("items", items);
		return result;
	}
	public String getBadTime(String newestDate){
		switch (newestDate){
			case "1":
				return "[0,0.25)";
			case "2":
				return "[0.25,0.5)";
			case "3":
				return "[0.5,1)";
			case "4":
				return "[1,2)";
			case "5":
				return "[2,5)";
			case "6":
				return "[5,10)";
			case "7":
				return "[10,+)";
			default:
				return "";
		}
	}
}