package com.wanda.credit.ds.client.dianwei;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wanda.credit.api.dto.DataSource;
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
import com.wanda.credit.common.util.GladDESUtils;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.client.dianwei.beans.DianWei_bad_Msg;
import com.wanda.credit.ds.client.dianwei.beans.DianWei_bad_badDetail;
import com.wanda.credit.ds.client.dianwei.beans.DianWei_bad_detail;
import com.wanda.credit.ds.dao.domain.Guozt_badInfo_check_result;
import com.wanda.credit.ds.dao.iface.IGuoZTBadInfoService;
import com.wanda.credit.ds.iface.IDataSourceRequestor;
@DataSourceClass(bindingDataSourceId="ds_dianwei_badMsg")
public class DWeiBadMsgSourceRequestor extends BaseDWeiRequestor
		implements IDataSourceRequestor {
	private final  Logger logger = LoggerFactory.getLogger(DWeiBadMsgSourceRequestor.class);
	@Autowired
	public IPropertyEngine propertyEngine;
	@Autowired
	private IGuoZTBadInfoService badInfoService;
	@Override
	public Map<String, Object> request(String trade_id, DataSource ds){
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		logger.info("{} 点微不良信息数据源调用开始...", prefix);
		Map<String, Object> retdata = new TreeMap<String, Object>();
		Map<String, Object> rets = null;
		DataSourceLogVO logObj = new DataSourceLogVO(trade_id);
		Map<String, Object> reqparam = new HashMap<String, Object>();
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));//log请求时间
		String dianwei_url = propertyEngine.readById("ds_dianwei_url");//远鉴调用连接
		String channelId = propertyEngine.readById("ds_dianwei_channelId");//channelId
		String enkey = propertyEngine.readById("ds_dianwei_enkey");//enkey
		
		String enCardNo = "";
		String resource_tag = Conts.TAG_SYS_ERROR;
		try{	
			String cardNo = ParamUtil.findValue(ds.getParams_in(), paramIds[0]).toString();   //姓名 
			String name = ParamUtil.findValue(ds.getParams_in(), paramIds[1]).toString(); //身份证号码

			logObj.setDs_id(ds.getId());
			rets = new HashMap<String, Object>();	 		
			enCardNo = GladDESUtils.encrypt(cardNo);
			logger.info("{} 点微不良信息数据源加密成功!", prefix);
			reqparam.put("name", name);
			reqparam.put("cardNo", cardNo);			
			logObj.setReq_url(dianwei_url);
			logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);

			if(StringUtils.isNotEmpty(CardNoValidator.validate(cardNo))){
				logger.warn("{}入参格式不符合要求", prefix);
				logObj.setIncache("1");
				logObj.setState_msg("身份证号码不符合规范");
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_IDCARD_ERROR);
				rets.put(Conts.KEY_RET_MSG, "您输入的为无效身份证号码，请核对后重新输入!");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				return rets;
			}		

			logObj.setIncache("0");
			String cachedTradeid = badInfoService.inCached(ds.getId(), name, enCardNo);
			boolean inCache = StringUtils.isNotBlank(cachedTradeid);
			if (inCache) {
				logObj.setIncache("1");
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
				List<Guozt_badInfo_check_result> badInfoList = badInfoService.getBadInfoList(cachedTradeid);
				if (badInfoList == null || badInfoList.isEmpty()) {
					inCache = false;
				} else {
					logger.info("{} 查询不良数据开始了",prefix);
					retdata = parseBadInfoListToMap(badInfoList);
					retdata.put("trade_id", cachedTradeid);
					resource_tag = Conts.TAG_INCACHE_TST_SUCCESS;
				}
			}else{
				Map<String, String> predata = new HashMap<String, String>();
				predata.put("productCode", "8202");
			    predata.put("cid", cardNo);
			    predata.put("name", name);
			    predata.put("subChannelName", "格兰德信息技术有限公司");
			    predata.put("channelOrderId", trade_id);
				String res = getDWeiResp(trade_id,predata,enkey,channelId,dianwei_url);
				if(StringUtil.isEmpty(res)){
					logger.error("{} 点微不良信息查询返回异常！", prefix);
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
					rets.put(Conts.KEY_RET_MSG, "犯罪吸毒黑名单查询失败");
					rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
					logObj.setState_msg("点微不良信息查询返回异常");
					return rets;
				}
				DianWei_bad_Msg yj_face = com.alibaba.fastjson.JSONObject.parseObject(res,DianWei_bad_Msg.class);				
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);			
				if("C0".equals(yj_face.getCode())){
					if("0".equals(yj_face.getData().getRespCode())){
		        		if(containBadDetail(yj_face.getData().getDetail(),"REVOKE")){
		        			resource_tag = Conts.TAG_TST_SUCCESS;
							rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_GUOZT_CRIME_NORECORD);
							rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_WARN_DS_GUOZT_CRIME_NORECORD.ret_msg);
							rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
							logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
							return rets;
		        		}else{
		        			resource_tag = Conts.TAG_TST_SUCCESS;
							List<Guozt_badInfo_check_result> badInfoList = 
									doSaveOperation(getBadDetail(yj_face.getData().getDetail(),
											yj_face.getData().getDetail().getNewestDate()), enCardNo, name, trade_id);
							retdata = parseBadInfoListToMap(badInfoList);
							retdata.put("trade_id", trade_id);
		        		}
					}else if("1".equals(yj_face.getData().getRespCode())){
						resource_tag = Conts.TAG_TST_SUCCESS;
						rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_POLICE_INVALID);
						rets.put(Conts.KEY_RET_MSG, "身份证号码，姓名校验不一致!");
						rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
						logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
						return rets;
					}else if("25".equals(yj_face.getData().getRespCode())){
						resource_tag = Conts.TAG_TST_SUCCESS;
						rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_GUOZT_CRIME_NORECORD);
						rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_WARN_DS_GUOZT_CRIME_NORECORD.ret_msg);
						rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
						logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
						return rets;
					}else{
						logger.info("{}不良信息查询失败", prefix);		        	
			        	rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
						rets.put(Conts.KEY_RET_MSG, "犯罪吸毒黑名单查询失败");
						rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
						logObj.setState_msg("爱金犯罪吸毒黑名单查询返回异常");
						return rets;
					}
				}else{
					logger.info("{}不良信息查询失败", prefix);		        	
		        	rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
					rets.put(Conts.KEY_RET_MSG, "犯罪吸毒黑名单查询失败");
					rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
					logObj.setState_msg("爱金犯罪吸毒黑名单查询返回异常");
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
			rets.put(Conts.KEY_RET_MSG, "数据源查询异常!");
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

			logger.info("{} 保存ds Log开始..." ,prefix);
			executorDtoService.writeDsLog(trade_id,logObj,true);
			executorDtoService.writeDsParamIn(trade_id, reqparam, logObj,true);
			logger.info("{} 保存ds Log结束" ,prefix);
		}
		return rets;
	}
	private boolean containBadDetail(DianWei_bad_detail badMsg,String badmsg){
		for(DianWei_bad_badDetail tmp:badMsg.getBadDetail()){
			if(badmsg.equals(tmp.getBadCode())){
				return true;
			}
		}
		return false;
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
	public JSONObject getBadDetail(DianWei_bad_detail badMsg,String newestDate){
		JSONArray items = new JSONArray();	
		JSONObject result = new JSONObject();
		String bad = "";
		for(DianWei_bad_badDetail tmp:badMsg.getBadDetail()){
			if("AT_LARGE".equals(tmp.getBadCode())){
				bad=bad+"1";
			}else if("DRUG".equals(tmp.getBadCode())){
				bad=bad+"4";
			}else if("DRUG_RELATED".equals(tmp.getBadCode())){
				bad=bad+"3";
			}else if("ILLEGAL_A".equals(tmp.getBadCode())){
				bad=bad+"2";
			}else if("ILLEGAL_B".equals(tmp.getBadCode())){
				bad=bad+"2";
			}else if("ILLEGAL_C".equals(tmp.getBadCode())){
				bad=bad+"2";
			}else if("ILLEGAL_E".equals(tmp.getBadCode())){
				bad=bad+"2";
			}else if("ILLEGAL_F".equals(tmp.getBadCode())){
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
			json.put("caseTime", newestDate);
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
}
