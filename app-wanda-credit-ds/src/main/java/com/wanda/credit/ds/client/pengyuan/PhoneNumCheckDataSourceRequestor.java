package com.wanda.credit.ds.client.pengyuan;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.ArrayUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

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
import com.wanda.credit.common.log.ds.vo.LoggingEvent;
import com.wanda.credit.common.props.DynamicConfigLoader;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.dao.domain.pengyuan.Py_pho_check;
import com.wanda.credit.ds.dao.domain.pengyuan.Py_pho_status;
import com.wanda.credit.ds.dao.iface.pengyuan.IPYPhoCheckService;
import com.wanda.credit.ds.dao.iface.pengyuan.IPYPhoStatusService;
import com.wanda.credit.ds.iface.IDataSourceRequestor;

@DataSourceClass(bindingDataSourceId="ds_phoneQuery")
public class PhoneNumCheckDataSourceRequestor extends BasePengYuanDataSourceRequestor implements IDataSourceRequestor {
	private final Logger logger = LoggerFactory.getLogger(PhoneNumCheckDataSourceRequestor.class);
	@Autowired
	private IPYPhoCheckService pYPhoCheckService;
	@Autowired
	private IPYPhoStatusService pYPhoStatusService;
	@Autowired
	private IExecutorSecurityService synchExecutorService;

	@Override
	public Map<String, Object> request(String trade_id, DataSource ds) {
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		Map<String, Object> rets = new HashMap<String, Object>();
		DataSourceLogVO logObj = new DataSourceLogVO();
		Map<String, Object> reqparam = new HashMap<String, Object>();
		try {
			TreeMap<String, Object> retdata = new TreeMap<String, Object>();
			String name = (String)ParamUtil.findValue(ds.getParams_in(), paramIds[0]);
			String cardNo = (String)ParamUtil.findValue(ds.getParams_in(), paramIds[1]);
			String phone = (String)ParamUtil.findValue(ds.getParams_in(), paramIds[2]);
			String crptedCardNo = synchExecutorService.encrypt(cardNo);
			String crptedPhone = synchExecutorService.encrypt(phone);
			
			reqparam.put("name", name);
			reqparam.put("cardNo", cardNo);
			reqparam.put("phone", phone);
			logObj.setDs_id(ds.getId());
			logObj.setReq_url(DynamicConfigLoader.get("sys.credit.client.pengyuan.old.url"));
			logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
			Py_pho_status phoneStatus = null;
			Py_pho_check phoneCheck = null;
			boolean inCached = pYPhoCheckService.inCached(name, crptedCardNo, crptedPhone);
			if (!inCached) {
				logObj.setIncache("0");
				logger.info("{}手机核查与状态查询数据源采集开始......", new String[] { prefix });
				String reqXML = buildRequestBody(queryType, reportIds, name.toString(), cardNo, phone);
				DataSourceLogEngineUtil.writeLog2LogSys(new LoggingEvent(trade_id, reqXML, new String[] { trade_id }));
				logObj.setReq_time(new Timestamp(System.currentTimeMillis()));
				String respXML = oldStub.queryReport(userId, userPwd, reqXML);
				logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
				DataSourceLogEngineUtil.writeLog2LogSys(new LoggingEvent(trade_id, respXML, new String[] { trade_id }));
				// 解析返回报文
				Document rspDoc = DocumentHelper.parseText(filtRspBody(respXML));
				Node status = rspDoc.selectSingleNode("//result/status");
				if (status != null && !"1".equals(status.getStringValue())) {
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_PHONE_EXCEPTION);
					rets.put(Conts.KEY_RET_MSG, "远程数据源返回失败!");
					logger.error("{} 远程数据源返回失败：{}", prefix);
					logObj.setState_msg("远程数据源返回失败!");
					return rets;
				} else {
					XStream stream = new XStream(new DomDriver());
					stream.registerConverter(new WsDateConverter("yyyy-MM-dd", new String[] { "yyyyMMdd", "yyyy" }));
					stream.registerConverter(new WsIntConverter());
					stream.registerConverter(new WsDoubleConverter());
					Node node = rspDoc.selectSingleNode("//*/mobileCheckInfo");
					if (node != null) {
						Element element = (Element) node;
						if (ArrayUtils.contains(new String[] { "2", "3" }, element.attributeValue("treatResult"))) {
							rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_PHONE_NOTFOUND_EXCEPTION);
							rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_FAILED_DS_PHONE_NOTFOUND_EXCEPTION.ret_msg);
							logger.warn("{} 没有查询到任何手机号码信息", prefix);
							return rets;
						}
						stream.alias("mobileCheckInfo", Py_pho_check.class);
						phoneCheck = (Py_pho_check) stream.fromXML(element.asXML().replace("<item>", "").replace("</item>", "").trim().replaceAll("	", "").replaceAll("\r|\n", ""));
						if (phoneCheck != null) {
							phoneCheck.setTrade_id(trade_id);
							phoneCheck.setName(name);
							phoneCheck.setDocumentNo(crptedCardNo);
							phoneCheck.setPhone(crptedPhone);
							pYPhoCheckService.add(phoneCheck);
							phoneCheck.setDocumentNo(cardNo);
							phoneCheck.setPhone(phone);							
						}
					}
					node = rspDoc.selectSingleNode("//*/mobileStatusInfo");
					if (node != null) {
						Element element = (Element) node;
						if (ArrayUtils.contains(new String[] { "2", "3" }, element.attributeValue("treatResult"))) {
							rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_PHONE_NOTFOUND_EXCEPTION);
							rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_FAILED_DS_PHONE_NOTFOUND_EXCEPTION.ret_msg);
							logger.warn("{} 没有查询到任何手机号码信息", prefix);
							return rets;
						}
						stream.alias("mobileStatusInfo", Py_pho_status.class);
						phoneStatus = (Py_pho_status) stream.fromXML(element.asXML().replace("<item>", "").replace("</item>", "").trim().replaceAll("	", "").replaceAll("\r|\n", ""));
						if (phoneStatus != null) {
							phoneStatus.setTrade_id(trade_id);
							pYPhoStatusService.add(phoneStatus);
						}
					}
				}
				logger.info("{}手机核查与状态查询数据源采集成功", new String[] { prefix });
			} else {
				logObj.setIncache("1");
				logObj.setReq_time(new Timestamp(System.currentTimeMillis()));
				phoneCheck = pYPhoCheckService.queryPhoneCheck(name, crptedCardNo, crptedPhone);
				phoneStatus = pYPhoCheckService.queryPhoneStatus(name, crptedCardNo, crptedPhone);
				logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
				logger.warn("{}缓存数据中存在此手机核查与状态查询数据!", new String[] { prefix });
			}
			logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
			retdata.put("phoneStatus", phoneStatus);
			phoneCheck.setDocumentNo(cardNo);		
			phoneCheck.setPhone(phone);
			retdata.put("phoneCheck", phoneCheck);
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
			rets.put(Conts.KEY_RET_DATA, retdata);
			rets.put(Conts.KEY_RET_MSG, "采集成功!");
		} catch (Exception e) {
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_PHONE_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, "数据源处理时异常! 详细信息:" + e.getMessage());
			logger.error(prefix +" 数据源处理时异常：{}", e);
			if (CommonUtil.isTimeoutException(e)) {
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_TIMEOUT);
			} else {
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
				logObj.setState_msg("数据源处理时异常! 详细信息:" + e.getMessage());
			}
			logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
		} finally {
			DataSourceLogEngineUtil.writeLog(trade_id, logObj);
			DataSourceLogEngineUtil.writeParamIn(trade_id, reqparam, logObj);
		}
		return rets;
	}

	/**
	 * 构建鹏元请求信息
	 * 
	 * @param queryType
	 * @param reportIds
	 * @param name
	 * @param cardNo
	 * @return
	 */
	private String buildRequestBody(String queryType, String reportIds, String name, String cardNo, String phone) {
		StringBuffer conditionXML = new StringBuffer();
		conditionXML.append("<?xml version=\"1.0\" encoding=\"GBK\"?><conditions><condition queryType=\"" + queryType + "\">");
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
