package com.wanda.credit.ds.client.guoztCredit;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

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
import com.wanda.credit.common.log.ds.vo.LoggingEvent;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.client.dsconfig.commonfunc.CryptUtil;
import com.wanda.credit.ds.dao.domain.Guozt_badInfo_check_result;
import com.wanda.credit.ds.dao.iface.IGuoZTBadInfoService;
import com.wanda.credit.ds.iface.IDataSourceRequestor;

@DataSourceClass(bindingDataSourceId="ds_guozt_crime")
public class GuoZTCrimeDataSourceRequestor extends BaseGuoZTDataSourcesRequestor implements IDataSourceRequestor {
	private final Logger logger = LoggerFactory.getLogger(GuoZTCrimeDataSourceRequestor.class);
	@Autowired
	private IGuoZTBadInfoService badInfoService;
	@Autowired
	public IPropertyEngine propertyEngine;
	private static final String TRADE_ID = "trade_id";
	private static final String BIZ_CODE_1 = "biz_code1";
	private static final String STATUS_SUCCESS = "0";//接口调用成功
	private static final String BIZ_CODE_1_SUCCESS = "1";//查询成功，有记录
	private static final String BIZ_CODE_1_SUCCESS_NONE = "0";//查询成功，无记录
	private static final String CHECK_SUCCESS = "一致";//默认true 查询结果一致
	private static final String CHECK_FAIL = "不一致";//默认true 查询结果不一致

	@Override
	public Map<String, Object> request(String trade_id, DataSource ds) {
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		Map<String, Object> retdata = null;
		Map<String, Object> rets = new HashMap<String, Object>();
		DataSourceLogVO logObj = new DataSourceLogVO();
		String crime_url = propertyEngine.readById("ds_guozt_crime_url");
		Map<String, Object> reqparam = new HashMap<String, Object>();
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));//log请求时间
		logObj.setDs_id(ds.getId());
		logObj.setReq_url(crime_url);
		logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
		String resource_tag = Conts.TAG_SYS_ERROR;
		try {
			String name = ParamUtil.findValue(ds.getParams_in(), paramIds[0]).toString(); //姓名 
			String cardNo = ParamUtil.findValue(ds.getParams_in(), paramIds[1]).toString(); //身份证号码
			String params = name + "," + cardNo + "," + trade_id +"," + "true" + "," + "true" + "," + "true"+ "," + "true" ;
			String enCardNo = CryptUtil.encrypt(cardNo);
			reqparam.put("name", name);
			reqparam.put("cardNo", cardNo);
			
			/*add 身份证规则校验 20160905	Begin*/
			String valiRes = CardNoValidator.validate(cardNo);
			if (!StringUtil.isEmpty(valiRes)) {				
				logger.error("{} 身份证号码不符合规范： {}" , prefix , valiRes);
				logObj.setIncache("1");
				logObj.setState_msg("身份证号不合法");
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
					logger.info("{}数据中存在犯罪查询数据！", new String[] { prefix });
					retdata = parseBadInfoListToMap(badInfoList);
					retdata.put("trade_id", cachedTradeid);
					resource_tag = Conts.TAG_INCACHE_TST_SUCCESS;
				}
			}
			if (!inCache) {
				logObj.setIncache("0");
				logger.info("{}国政通犯罪吸毒黑名单数据源源采集开始......", new String[] { prefix });
				logger.info("{}请求参数params信息！", new String[] { JSONObject.toJSONString(params) });
				String respXml =singQurey(params, prefix,crime_url);
				//"<?xml version=\"1.0\" encoding=\"UTF-8\"?><data>  <message>    <status>0</status>    <value>处理成功</value>  </message>  <badInfoDs>    <badInfoD inputXm=\"周元\" inputZjhm=\"512222197808120017\">      <wybs desc=\"唯一标识\">402889405577a1ce01559106b1093250</wybs>      <inputZjhm18 desc=\"18位证件号码\">512222197808120017</inputZjhm18>      <code desc=\"返回代码\">1</code>      <message desc=\"调用结果描述\">查询成功_有数据</message>      <checkCode desc=\"核验代码\">2,3,4</checkCode>      <checkMsg desc=\"核验描述\">比中前科、涉毒、吸毒</checkMsg>      <item>        <caseType desc=\"案件类别\" />        <caseSource desc=\"案件来源\">前科</caseSource>        <caseTime desc=\"发案时间\">2012-04-16 09:32:41</caseTime>      </item>      <item>        <caseType desc=\"案件类别\">贩卖毒品案</caseType>        <caseSource desc=\"案件来源\">前科</caseSource>        <caseTime desc=\"发案时间\">2009-03-09 00:00:00</caseTime>      </item>      <item>        <caseType desc=\"案件类别\">无</caseType>        <caseSource desc=\"案件来源\">涉毒</caseSource>        <caseTime desc=\"发案时间\">未设置时间</caseTime>      </item>      <item>        <caseType desc=\"案件类别\">无</caseType>        <caseSource desc=\"案件来源\">吸毒</caseSource>        <caseTime desc=\"发案时间\">未设置时间</caseTime>      </item>    </badInfoD>  </badInfoDs></data>";
				logger.info("{}厂商返回xml信息！", new String[] { respXml });
				DataSourceLogEngineUtil.writeLog2LogSys(new LoggingEvent(trade_id, respXml, new String[] { trade_id }));
				if (respXml == null || respXml.length() == 0) {
					logger.error("{} 国政通犯罪吸毒黑名单查询返回异常！", prefix);
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED);
					rets.put(Conts.KEY_RET_MSG, "犯罪吸毒黑名单查询失败");
					logObj.setState_msg("国政通犯罪吸毒黑名单查询返回异常");
					return rets;
				}

				//解析返回报文
				Document rspDoc = DocumentHelper.parseText(filtRspBody(respXml));
				logger.info("{}解析厂商返回xml信息！", new String[] { rspDoc.toString() });
				Node repDocMsg = rspDoc.selectSingleNode("//data/message");
				Node messStat = repDocMsg.selectSingleNode("status");
				Node messStatDesc = repDocMsg.selectSingleNode("value");
				if (STATUS_SUCCESS.equals(messStat.getStringValue())) {
					Node badInfoNode = rspDoc.selectSingleNode("//data/badInfoDs/badInfoD");
					Node codeNode = badInfoNode.selectSingleNode("code");
					Node messageNode = badInfoNode.selectSingleNode("message");
					Node wybsNode=badInfoNode.selectSingleNode("wybs");//厂商返回流水号
					String checkCode = codeNode.getStringValue();
					String checkMessage = messageNode.getStringValue();
					String checkWybs = wybsNode.getStringValue();
					if (BIZ_CODE_1_SUCCESS.equals(checkCode)) {
						resource_tag = Conts.TAG_TST_SUCCESS;
						List<Guozt_badInfo_check_result> badInfoList = doSaveOperation(badInfoNode, enCardNo, name, trade_id);
						retdata = parseBadInfoListToMap(badInfoList);
						retdata.put("trade_id", trade_id);
						logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
						logObj.setBiz_code1(checkCode+","+checkMessage);
						logObj.setBiz_code3(checkWybs);
					} else if (BIZ_CODE_1_SUCCESS_NONE.equals(checkCode)) {
						rets.clear();
						resource_tag = Conts.TAG_TST_SUCCESS;
						rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_GUOZT_CRIME_NORECORD);
						rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_WARN_DS_GUOZT_CRIME_NORECORD.ret_msg);
						rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
						logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
						logObj.setBiz_code1(checkCode+","+checkMessage);
						logObj.setBiz_code3(checkWybs);
						return rets;
					} else {
						logger.info("{} 国政通犯罪吸毒黑名单查询失败:{}", new String[] { prefix, messageNode.getStringValue() });
						rets.clear();
						rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED);
						rets.put(Conts.KEY_RET_MSG, "犯罪吸毒黑名单查询失败!");
						logObj.setBiz_code1(checkCode+","+checkMessage);
						logObj.setBiz_code3(checkWybs);
						return rets;
					}
				} else {
					logger.info("{} 国政通犯罪吸毒黑名单数据源调用失败:{}", new String[] { prefix, messStatDesc.getStringValue() });
					rets.clear();
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED);
					rets.put(Conts.KEY_RET_MSG, "犯罪吸毒黑名单查询失败!");
					logObj.setState_msg(messStatDesc.getStringValue());
					logObj.setBiz_code1(messStat.getStringValue()+","+messStatDesc.getStringValue());
					return rets;
				}
			} 
			rets.put(Conts.KEY_RET_DATA, retdata);
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
			rets.put(Conts.KEY_RET_MSG, "采集成功!");
			rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
		} catch (Exception ex) {
			resource_tag = Conts.TAG_SYS_ERROR;
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, "数据源处理时异常! 详细信息:" + ex.getMessage());
			logger.error("{} 数据源处理时异常：{}", prefix, ExceptionUtil.getTrace(ex));
			if (ExceptionUtil.isTimeoutException(ex)) {
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_TIMEOUT);
				resource_tag = Conts.TAG_SYS_TIMEOUT;
			} else {
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
				logObj.setState_msg("数据源处理时异常! 详细信息:" + ex.getMessage());
			}
		} finally {
			logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
			logObj.setTag(resource_tag);
			DataSourceLogEngineUtil.writeLog(trade_id, logObj);
			DataSourceLogEngineUtil.writeParamIn(trade_id, reqparam, logObj);
		}
		return rets;
	}

	
	/**
	 * 保存犯罪记录信息
	 * 
	 * @date 2017年12月12日 下午2:49:27
	 * @author mafei-add（根据数据源厂商变更信息更改返回逻辑）
	 * @param badInfoNode
	 * @param cardNo
	 * @param name
	 * @param trade_id
	 * @return
	 * @throws ServiceException
	 */
	private List<Guozt_badInfo_check_result> doSaveOperation(Node badInfoNode, String cardNo, String name, String trade_id) throws ServiceException {
		// TODO Auto-generated method stub
		String ztCheckresultNode = badInfoNode.selectSingleNode("ztCheckresult").getStringValue();
		String wfxwCheckresultNode = badInfoNode.selectSingleNode("wfxwCheckresult").getStringValue();
		String sdCheckresultNode = badInfoNode.selectSingleNode("sdCheckresult").getStringValue();
		String xdCheckresultNode = badInfoNode.selectSingleNode("xdCheckresult").getStringValue();
		logger.info("{} 国政通犯罪吸毒黑名单数据源调用返回数据一致情况:{}", new String[] { ztCheckresultNode+","+wfxwCheckresultNode+","+sdCheckresultNode+","+xdCheckresultNode});
		Map<String,String> dataCode=new HashMap<String,String>();
		Map<String,String> data=new HashMap<String,String>();
		StringBuffer s1 = new StringBuffer();
		StringBuffer s11 = new StringBuffer();
		if (ztCheckresultNode!=null&&ztCheckresultNode.equals("一致")){
			dataCode.put("zt", "1");
			data.put("zt", "在逃");
			s1.append("1,");
			s11.append("在逃、");
		}
		if (wfxwCheckresultNode!=null&&wfxwCheckresultNode.equals("一致")){
			dataCode.put("wf", "2");
			data.put("wf", "前科");
			s1.append("2,");
			s11.append("前科、");
		}
		if (sdCheckresultNode!=null&&sdCheckresultNode.equals("一致")){
			dataCode.put("sd", "3");
			data.put("sd", "涉毒");
			s1.append("3,");
			s11.append("涉毒、");
		}
		if (xdCheckresultNode!=null&&xdCheckresultNode.equals("一致")){
			dataCode.put("xd", "4");
			data.put("xd", "吸毒");
			s1.append("4,");
			s11.append("吸毒、");
		}
		String ss = "";
		String ss11 = "";
		if(StringUtils.isNotBlank(s1.toString())){
			ss = s1.deleteCharAt(s1.length()-1).toString();
		}
		if(StringUtils.isNotBlank(s11.toString())){
			ss11 = s11.deleteCharAt(s11.length()-1).toString();
		}
		
		//item字段只有 checkCount2Node（QK） 事件数量大于 0 的情况下才会输出且只输出最近违法行为，如 QK 事件数量为 0 则只输出 <item>空节点
		Node  checkCount2Node =badInfoNode.selectSingleNode("checkCount2");
		Node item =  badInfoNode.selectSingleNode("item");
		List<Guozt_badInfo_check_result> badInfoList = null;
		Guozt_badInfo_check_result badInfo=null;
		badInfoList = new ArrayList<Guozt_badInfo_check_result>();
		for (String no :dataCode.keySet()){
			badInfo= new Guozt_badInfo_check_result();
			String checkCode = dataCode.get(no);
			if (no !=null && !"".equals(no)){
				logger.info("{} 国政通犯罪吸毒黑名单数据源调用返回数据类型:{}", new String[] { no});
				
				if (checkCode.equals("1")){
					badInfo.setTrade_id(trade_id);
					badInfo.setCardNo(cardNo);
					badInfo.setName(name);
					badInfo.setCaseType("无");
					badInfo.setCaseSource(data.get("zt"));
					badInfo.setCaseTime(null);
					badInfo.setCheckCode(ss);
					badInfo.setCheckMsg("比中"+ ss11);
					badInfoList.add(badInfo);
				}
				if (checkCode.equals("2")){
					badInfo.setTrade_id(trade_id);
					badInfo.setCardNo(cardNo);
					badInfo.setName(name);
					badInfo.setCaseType("无");
					badInfo.setCaseSource(data.get("wf"));
					if(checkCount2Node.getStringValue()!="0"&& item != null && !"".equals(item)){
					Node caseTimeNode = item.selectSingleNode("caseTime");
					badInfo.setCaseTime(caseTimeNode == null ? null : caseTimeNode.getStringValue());
					badInfo.setCheckCode(ss);
					badInfo.setCheckMsg("比中"+ ss11);
					badInfoList.add(badInfo);
					}
				}
				if (checkCode.equals("3")){
					badInfo.setTrade_id(trade_id);
					badInfo.setCardNo(cardNo);
					badInfo.setName(name);
					badInfo.setCaseType("无");
					badInfo.setCaseSource(data.get("sd"));
					badInfo.setCaseTime(null);
					badInfo.setCheckCode(ss);
					badInfo.setCheckMsg("比中"+ ss11);
					badInfoList.add(badInfo);
				}
				if (checkCode.equals("4")){
					badInfo.setTrade_id(trade_id);
					badInfo.setCardNo(cardNo);
					badInfo.setName(name);
					badInfo.setCaseType("无");
					badInfo.setCaseSource(data.get("xd"));
					badInfo.setCaseTime(null);
					badInfo.setCheckCode(ss);
					badInfo.setCheckMsg("比中"+ ss11);
					badInfoList.add(badInfo);
				}
			}
		}
			badInfoService.add(badInfoList);
		    return badInfoList;
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

	/**
	 * 转换字符串结果为Map
	 * 
	 * @date 2016年6月29日 下午2:43:15
	 * @author ou.guohao
	 * @param inCacheString
	 * @return
	 */
	private Map<String, Object> getInCacheObject(String inCacheString) {
		//trade_id=20160629094605460JOKO;biz_code1=0;biz_code2=1;biz_code3=
		Map<String, Object> map = new HashMap<String, Object>();
		boolean inCache = false;
		if (inCacheString != null && inCacheString.split(";").length == 4) {
			inCacheString = inCacheString.trim().replace(" ", "");
			String[] fields = inCacheString.split(";");
			for (String field : fields) {
				String[] f = field.split("=");
				if (f.length == 2)
					map.put(f[0], f[1]);
			}
			if (StringUtils.isNotBlank((String) map.get(TRADE_ID)) && (BIZ_CODE_1_SUCCESS).equals(map.get(BIZ_CODE_1))) {
				inCache = true;
			}
		}
		map.put("inCache", inCache);
		return map;
	}

	private static Map<String, String> checkCode = new HashMap<String, String>();
	static {
		checkCode.put("GZTFZ_001", "1");//查询有数据
		checkCode.put("GZTFZ_002", "0");//查询无数据
		checkCode.put("GZTFZ_003", "-990");//数据源接口调用失败
		checkCode.put("GZTFZ_004", "-999");//数据格式错误或其他错误
	}

	private String getCheckCodeKey(String code) {
		for (String key : checkCode.keySet()) {
			if (checkCode.get(key).equals(code))
				return key;
		}
		return null;
	}
}
