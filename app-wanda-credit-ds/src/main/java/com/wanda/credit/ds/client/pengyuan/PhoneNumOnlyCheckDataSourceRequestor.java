package com.wanda.credit.ds.client.pengyuan;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.api.iface.IExecutorSecurityService;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.converter.WsDateConverter;
import com.wanda.credit.base.converter.WsDoubleConverter;
import com.wanda.credit.base.converter.WsIntConverter;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.CommonUtil;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.props.DynamicConfigLoader;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.dao.domain.pengyuan.Py_pho_only_check;
import com.wanda.credit.ds.dao.iface.pengyuan.IPYPhoOnlyCheckService;
import com.wanda.credit.ds.iface.IDataSourceRequestor;

@DataSourceClass(bindingDataSourceId="ds_phoneonlycheck")
public class PhoneNumOnlyCheckDataSourceRequestor extends BasePengYuanDataSourceRequestor implements IDataSourceRequestor {
	private final  Logger logger = LoggerFactory.getLogger(PhoneNumOnlyCheckDataSourceRequestor.class);
	@Autowired
	private IPYPhoOnlyCheckService iPYPhoOnlyCheckService;
	@Autowired
	private IExecutorSecurityService synchExecutorService;

	@Override
	public Map<String, Object> request(String trade_id, DataSource ds) {
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		Map<String, Object> rets = new HashMap<String, Object>();
		DataSourceLogVO logObj = new DataSourceLogVO();
		logObj.setDs_id(ds.getId());
		logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
		logObj.setReq_url(DynamicConfigLoader.get("sys.credit.client.pengyuan.old.url"));
		logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));
		try {
			TreeMap<String, Object> retdata = new TreeMap<String, Object>();
			String name = (String) ParamUtil.findValue(ds.getParams_in(), paramIds[0]);
			String cardNo = (String) ParamUtil.findValue(ds.getParams_in(), paramIds[1]);
			String phone = (String) ParamUtil.findValue(ds.getParams_in(), paramIds[2]);
			
			String crptedCardNo = synchExecutorService.encrypt(cardNo);
			String crptedPhone = synchExecutorService.encrypt(phone);
			Map<String, Object> paramsIn = new HashMap<String, Object>();
			paramsIn.put("name", name);
			paramsIn.put("cardNo", crptedCardNo);
			paramsIn.put("phone", crptedPhone);
			DataSourceLogEngineUtil.writeParamIn(trade_id, paramsIn, logObj);
			Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
			Py_pho_only_check phoneOnlyCheck = new Py_pho_only_check();
			boolean inCached  = iPYPhoOnlyCheckService.inCached(name,crptedCardNo,crptedPhone);
			if(!inCached){
				logObj.setIncache("0");
				logger.info("手机核查数据源采集开始 {}...", new String[] {prefix});
				String reqXML  = buildRequestBody(queryType,reportIds,name,cardNo,phone);
				String respXML = oldStub.queryReport(userId, userPwd, reqXML);
				//解析返回报文
				Document rspDoc = DocumentHelper.parseText(filtRspBody(respXML));
				Node status = rspDoc.selectSingleNode("//result/status");
				if(status!=null && !"1".equals(status.getStringValue())){
					String errorCode = rspDoc.selectSingleNode("//result/errorCode").getStringValue();
					String errorMessage = rspDoc.selectSingleNode("//result/errorMessage").getStringValue();
					phoneOnlyCheck.setTrade_id(trade_id);
					phoneOnlyCheck.setName(name);
					phoneOnlyCheck.setDocumentNo(crptedCardNo);
					phoneOnlyCheck.setPhone(crptedPhone);
					phoneOnlyCheck.setStatus("2");
					phoneOnlyCheck.setErrorCode(errorCode);
					phoneOnlyCheck.setErrorMessage(errorMessage);
					if(phoneOnlyCheck!=null){
						iPYPhoOnlyCheckService.add(phoneOnlyCheck);
					}
					phoneOnlyCheck.setDocumentNo(cardNo);
					phoneOnlyCheck.setPhone(phone);
					retdata.put("phoneCheck",gson.toJson(phoneOnlyCheck));
					rets.put(Conts.KEY_RET_DATA, retdata);
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED);
					rets.put(Conts.KEY_RET_MSG, "远程数据源返回失败! 错误代码:"+errorCode+",错误描述:"+errorMessage);
					logger.info("{} 远程数据源返回失败：错去代码：{}，错误描述：{}",new String[]{prefix,errorCode,errorMessage});
					logObj.setState_msg("远程数据源返回失败! 错误代码:"+errorCode+",错误描述:"+errorMessage);
					return rets;
				}else{
					XStream stream = new XStream(new DomDriver());
					stream.registerConverter(new WsDateConverter("yyyy-MM-dd",
							new String[] { "yyyyMMdd", "yyyy" }));
					stream.registerConverter(new WsIntConverter());
					stream.registerConverter(new WsDoubleConverter());
					Node node = rspDoc.selectSingleNode("//*/mobileCheckInfo");
					if(node!=null){
						Element element = (Element) node;
						if("3".equals(element.attributeValue("treatResult"))){
							String treatErrorCode = element.attributeValue("treatErrorCode");
							String treatErrorMessage = element.attributeValue("errorMessage");
							phoneOnlyCheck.setTrade_id(trade_id);
							phoneOnlyCheck.setName(name);
							phoneOnlyCheck.setDocumentNo(crptedCardNo);
							phoneOnlyCheck.setPhone(crptedPhone);
							phoneOnlyCheck.setStatus("1");
							phoneOnlyCheck.setTreatResult("3");
							phoneOnlyCheck.setErrorCode(treatErrorCode);
							phoneOnlyCheck.setErrorMessage(treatErrorMessage);
							if(phoneOnlyCheck!=null){
								iPYPhoOnlyCheckService.add(phoneOnlyCheck);
							}
							phoneOnlyCheck.setDocumentNo(cardNo);
							phoneOnlyCheck.setPhone(phone);
							retdata.put("phoneCheck", gson.toJson(phoneOnlyCheck));
							rets.put(Conts.KEY_RET_DATA, retdata);
							rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED);
							rets.put(Conts.KEY_RET_MSG, "远程数据源返回失败! 错误代码:"+treatErrorCode+",错误描述:"+treatErrorMessage);
							logger.info("{} 远程数据源返回失败：错去代码：{}，错误描述：{}",new String[]{prefix,treatErrorCode,treatErrorMessage});
							logObj.setState_msg("远程数据源返回失败! 错误代码:"+treatErrorCode+",错误描述:"+treatErrorMessage);
							return rets;
						}else if("2".equals(element.attributeValue("treatResult"))){
							phoneOnlyCheck.setTrade_id(trade_id);
							phoneOnlyCheck.setName(name);
							phoneOnlyCheck.setDocumentNo(crptedCardNo);
							phoneOnlyCheck.setPhone(crptedPhone);
							phoneOnlyCheck.setStatus("1");
							phoneOnlyCheck.setTreatResult("2");
							if(phoneOnlyCheck!=null){
								iPYPhoOnlyCheckService.add(phoneOnlyCheck);
							}
							phoneOnlyCheck.setDocumentNo(cardNo);
							phoneOnlyCheck.setPhone(phone);
							retdata.put("phoneCheck", gson.toJson(phoneOnlyCheck));
							rets.put(Conts.KEY_RET_DATA, retdata);
							rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED);
							rets.put(Conts.KEY_RET_MSG, "远程数据源返回失败,未查得报告");
							logger.info("远程数据源返回失败","未查得报告");
							logObj.setState_msg("远程数据源返回失败,未查得报告");
							return rets;
						}else{
							stream.alias("mobileCheckInfo", Py_pho_only_check.class);
							phoneOnlyCheck = (Py_pho_only_check) stream.fromXML(element.asXML().replace("<item>", "").replace("</item>", "").trim().replaceAll("	", "").replaceAll("\r|\n", ""));
							phoneOnlyCheck.setTrade_id(trade_id);
							phoneOnlyCheck.setName(name);
							phoneOnlyCheck.setDocumentNo(crptedCardNo);
							phoneOnlyCheck.setPhone(crptedPhone);
							phoneOnlyCheck.setStatus("1");
							phoneOnlyCheck.setTreatResult("1");
							if(phoneOnlyCheck!=null){
								iPYPhoOnlyCheckService.add(phoneOnlyCheck);
							}
							phoneOnlyCheck.setDocumentNo(cardNo);
							phoneOnlyCheck.setPhone(phone);
							logger.info("{}手机核查数据源采集成功", new String[] { prefix});
							retdata.put("phoneCheck", gson.toJson(phoneOnlyCheck));
							rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
							rets.put(Conts.KEY_RET_DATA, retdata);
							rets.put(Conts.KEY_RET_MSG, "交易成功!");
							logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
						}
					}
				}
			}else{
				logObj.setIncache("1");
				phoneOnlyCheck = iPYPhoOnlyCheckService.queryPhoneCheck(name, crptedCardNo, crptedPhone);
				phoneOnlyCheck.setDocumentNo(cardNo);phoneOnlyCheck.setPhone(phone);
				logger.info("{}缓存数据中存在此手机核查数据!", new String[] { prefix});
				if("1".equals(phoneOnlyCheck.getStatus())&&"1".equals(phoneOnlyCheck.getTreatResult())){
					retdata.put("phoneCheck", gson.toJson(phoneOnlyCheck));
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
					rets.put(Conts.KEY_RET_DATA, retdata);
					rets.put(Conts.KEY_RET_MSG, "交易成功!");
					logger.info("{}手机核查数据源采集成功", new String[] { prefix});
					logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
				}else if("2".equals(phoneOnlyCheck.getStatus())){
					retdata.put("phoneCheck",gson.toJson(phoneOnlyCheck));
					rets.put(Conts.KEY_RET_DATA, retdata);
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED);
					rets.put(Conts.KEY_RET_MSG, "远程数据源返回失败! 错误代码:"+phoneOnlyCheck.getErrorCode()+",错误描述:"+phoneOnlyCheck.getErrorMessage());
					logger.info("{} 远程数据源返回失败：错去代码：{}，错误描述：{}",new String[]{prefix,phoneOnlyCheck.getErrorCode(),phoneOnlyCheck.getErrorMessage()});
					logObj.setState_msg("远程数据源返回失败! 错误代码:"+phoneOnlyCheck.getErrorCode()+",错误描述:"+phoneOnlyCheck.getErrorMessage());
					return rets;
				}else if("2".equals(phoneOnlyCheck.getTreatResult())){
					retdata.put("phoneCheck", gson.toJson(phoneOnlyCheck));
					rets.put(Conts.KEY_RET_DATA, retdata);
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED);
					rets.put(Conts.KEY_RET_MSG, "远程数据源返回失败,未查得报告");
					logger.info("远程数据源返回失败,未查得报告");
					logObj.setState_msg("远程数据源返回失败,未查得报告");
				}else if("3".equals(phoneOnlyCheck.getTreatResult())){
					retdata.put("phoneCheck", gson.toJson(phoneOnlyCheck));
					rets.put(Conts.KEY_RET_DATA, retdata);
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED);
					rets.put(Conts.KEY_RET_MSG, "远程数据源返回失败! 错误代码:"+phoneOnlyCheck.getTreatErrorCode()+",错误描述:"+phoneOnlyCheck.getTreatErrorMessage());
					logger.info("{} 远程数据源返回失败：错去代码：{}，错误描述：{}",new String[]{prefix,phoneOnlyCheck.getTreatErrorCode(),phoneOnlyCheck.getTreatErrorMessage()});
					logObj.setState_msg("远程数据源返回失败! 错误代码:"+phoneOnlyCheck.getTreatErrorCode()+",错误描述:"+phoneOnlyCheck.getTreatErrorMessage());
					return rets;
				}
			}
		} catch (Exception e) {
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_PHONE_ONLY_CHECK_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, "数据源处理时异常! 详细信息:"+e.getMessage());
			logger.info(prefix +" 数据源处理时异常",e);
			if (CommonUtil.isTimeoutException(e)) {
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_TIMEOUT);
			} else {
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
				logObj.setState_msg("数据源处理时异常! 详细信息:" + e.getMessage());
			}
		}finally{
			logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
			DataSourceLogEngineUtil.writeLog(trade_id, logObj);
		}
		return rets;
	}
	/**
	 * 构建鹏元请求信息
	 * @param queryType
	 * @param reportIds
	 * @param name
	 * @param cardNo
	 * @return
	 */
	private String buildRequestBody(String queryType,String reportIds, String name,String cardNo,String phone){
		StringBuffer conditionXML = new StringBuffer();
		conditionXML.append("<?xml version=\"1.0\" encoding=\"GBK\"?><conditions><condition queryType=\""+queryType+"\">");
		conditionXML.append("<item><name>name</name><value>");
		conditionXML.append(name);
		conditionXML.append("</value></item>");
		conditionXML.append("<item><name>documentNo</name><value>");
		conditionXML.append(cardNo);
		conditionXML.append("</value></item>");
		conditionXML.append("<item><name>phone</name><value>");
		conditionXML.append(phone);
		conditionXML.append("</value></item>");
		conditionXML.append("<item><name>subreportIDs</name><value>");
		conditionXML.append(reportIds);
		conditionXML.append("</value></item>");
		conditionXML.append("</condition></conditions>");
		return conditionXML.toString();
	}
}
