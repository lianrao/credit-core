
package com.wanda.credit.ds.client.pengyuan;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
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
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.converter.WsDateConverter;
import com.wanda.credit.base.converter.WsDoubleConverter;
import com.wanda.credit.base.converter.WsIntConverter;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceBizCodeVO;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.props.DynamicConfigLoader;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.dao.domain.pengyuan.Py_car_illegal;
import com.wanda.credit.ds.dao.iface.pengyuan.IPyCarIllegalService;
import com.wanda.credit.ds.iface.IDataSourceRequestor;

/**
 * @description 鹏元车辆违章查询
 * @author guohao.ou
 * 
 * */
@DataSourceClass(bindingDataSourceId="ds_py_carillegal")
public class CarIllegalDataSourceRequestor extends BasePengYuanDataSourceRequestor implements IDataSourceRequestor {
	private final Logger logger = LoggerFactory.getLogger(CarIllegalDataSourceRequestor.class);

	@Autowired
	private IPyCarIllegalService pyCarIllegalService;

	private static final String KEY_RESULT = "treatResult";
	private static final String QUERY_REASON_ID = "101";
	private static final String DS_ID = "ds_py_carillegal";

	@Override
	public Map<String, Object> request(String trade_id, DataSource ds) {
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		Map<String, Object> rets = new HashMap<String, Object>();
		List<String> tags = new ArrayList<String>();
		DataSourceLogVO logObj = new DataSourceLogVO();
		logObj.setDs_id(DS_ID);
		logObj.setReq_url(DynamicConfigLoader.get("sys.credit.client.pengyuan.old.url"));
		// 默认交易失败
		logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
		Map<String, Object> params = null;
		try {
			TreeMap<String, Object> retdata = new TreeMap<String, Object>();
			// 车牌号
			String carNumber = ParamUtil.findValue(ds.getParams_in(), "carNumber").toString();
			// 车辆类型（大型车 小型车）
			String carType = ParamUtil.findValue(ds.getParams_in(), "carType").toString();
			// 车架号
			String carCode = ParamUtil.findValue(ds.getParams_in(), "carCode").toString();
			
			String carEngine = ParamUtil.findValue(ds.getParams_in(), "carEngine").toString();

			/** 记录请求状态信息 */
			params = new HashMap<String, Object>();
			params.put("carNumber", carNumber);
			params.put("carType", carType);
			params.put("carCode", carCode);
			params.put("carEngine", carEngine);
//			DataSourceLogEngineUtil.writeParamIn(trade_id, params,logObj);

			logObj.setIncache("0");
			logger.info("{} 车辆违章查询数据源采集开始......", new String[] { prefix });
			String reqXML = buildRequestBody(carNumber, carType, carCode, carEngine);
			logObj.setReq_time(new Timestamp(System.currentTimeMillis()));
			String respXML = oldStub.queryReport(userId, userPwd, reqXML);
			logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
			// 解析返回报文
			Document rspDoc = DocumentHelper.parseText(filtRspBody(respXML));
			Node status = rspDoc.selectSingleNode("//result/status");
			/** 数据源返回失败 */
			if (status != null && !"1".equals(status.getStringValue())) {
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_PY_CARILLEGAL_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "车辆违章查询数据源返回失败!");
				Node errMsg = rspDoc.selectSingleNode("//result/errorMessage");
				if (errMsg != null) {
					logObj.setState_msg(errMsg.getStringValue());
					logger.error("{} 车辆违章数据源返回失败：{}", prefix, errMsg.getStringValue());
				}
				tags.add(Conts.TAG_SYS_ERROR);
				return rets;
			}
			List<Py_car_illegal> carIllegals = null;
			logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
			Node node = rspDoc.selectSingleNode("//*/vehicleViolation");
			if (node != null) {
				Element element = (Element) node;
				/** 记录响应状态信息 */
				handleBizcode(element, logObj);
				String treatResult = element.attributeValue("treatResult");
				/** 未查得报告 */
				if ("3".equals(treatResult)) {
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_PY_CARILLEGAL_EXCEPTION);//
					rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_FAILED_DS_PY_CARILLEGAL_EXCEPTION.ret_msg);//
					logger.warn("{} 没有查询到车辆违章信息 {}", prefix, element.attributeValue("errorMessage"));
					tags.add(Conts.TAG_UNFOUND_OTHERS);
					return rets;
				}else if("2".equals(treatResult)){
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_PY_CARILLEGAL_NORECORD);//
					rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_FAILED_DS_PY_CARILLEGAL_NORECORD.ret_msg);//
					logger.warn("{} 没有查询到车辆违章记录", prefix);
					tags.add(Conts.TAG_UNFOUND);
				}else{
					List<Element> items = element.selectNodes("item");
					if (items != null && !items.isEmpty()) {
						tags.add(Conts.TAG_FOUND);
						carIllegals = new ArrayList<Py_car_illegal>();
						Element cisReport = (Element) rspDoc.selectSingleNode("//*/cisReport");
						XStream stream = new XStream(new DomDriver());
						stream.registerConverter(new WsDateConverter("yyyy-MM-dd", new String[] { "yyyyMMdd", "yyyy" }));
						stream.registerConverter(new WsIntConverter());
						stream.registerConverter(new WsDoubleConverter());
						stream.alias("item", Py_car_illegal.class);
						for (Element item : items) {
							Py_car_illegal carIllegal = (Py_car_illegal) stream.fromXML(item.asXML().trim().replaceAll(" ", "").replaceAll("\r|\n", ""));
							if (carIllegal != null) {
								carIllegal.setTradeId(trade_id);
								carIllegal.setCarNumber(carNumber);
								carIllegal.setCarType(carType);
								carIllegal.setCarCode(carCode);
								carIllegal.setCarEngine(carEngine);
								carIllegal.setReportID(cisReport.attributeValue("reportID"));
								carIllegal.setSubReportTypes(cisReport.attributeValue("subReportTypes"));
								carIllegal.setTreatResult(treatResult);
								pyCarIllegalService.add(carIllegal);
								carIllegals.add(carIllegal);
							}
						}
						retdata.put("carIllegals", carIllegals);
					}
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
					rets.put(Conts.KEY_RET_MSG, "采集成功!");
				}
				retdata.put("status", treatResult);
			}
			logger.info("{} 车辆违章查询数据源采集成功", new String[] { prefix });
			retdata.putAll(params);
			rets.put(Conts.KEY_RET_DATA, retdata);
		} catch (Exception e) {
			tags.clear();
			tags.add(Conts.TAG_SYS_ERROR);
			/** 如果是超时异常 记录超时信息 */
			if (isTimeoutException(e)) {
				logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_TIMEOUT);
				tags.add(Conts.TAG_SYS_TIMEOUT);
			}
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, "车辆违章查询数据源处理时异常! 详细信息:" + e.getMessage());
			logger.error(prefix + " 车辆违章查询数据源处理时异常", e);
		}finally {
			rets.put(Conts.KEY_RET_TAG,tags.toArray(new String[0]));
			logObj.setTag(StringUtils.join(tags, ";"));
			DataSourceLogEngineUtil.writeLog(trade_id, logObj);
			if (null != params) {
				DataSourceLogEngineUtil.writeParamIn(trade_id, params, logObj);
			}
		}
		return rets;
	}

	/** 处理业务状态码信息 */
	private void handleBizcode(Element node, DataSourceLogVO logObj) {
		String treatResult = (node.attributeValue(KEY_RESULT));
		if (StringUtils.isBlank(treatResult))
			return;
		DataSourceBizCodeVO bizCodeVo = null;
		if ("3".equals(treatResult)) {
			String errcode = node.attributeValue("treatErrorCode");
			if (StringUtils.isNotBlank(errcode)) {
				bizCodeVo = DataSourceLogEngineUtil.fetchBizCodeByRetCode(DS_ID, errcode);
			}
		} else {
			// 1 查得 2未查得
			bizCodeVo = DataSourceLogEngineUtil.fetchBizCodeByRetCode(DS_ID, treatResult);
		}
		if (bizCodeVo != null) {
			logObj.setBiz_code1(bizCodeVo.getBizCode());
		}
	}

	/**
	 * 构建鹏元请求信息
	 * 
	 * @param carNumber
	 * @param carType
	 * @param carCode车架号
	 * @return
	 */
	private String buildRequestBody(String carNumber, String carType, String carCode, String carEngine) {
		StringBuffer conditionXML = new StringBuffer();
		conditionXML.append("<?xml version=\"1.0\" encoding=\"GBK\"?>" + "<conditions><condition queryType=\"" + queryType + "\">");

		conditionXML.append("<item><name>carNumber</name><value>");
		conditionXML.append(carNumber);
		conditionXML.append("</value></item>");

		conditionXML.append("<item><name>carType</name><value>");
		conditionXML.append(carType);
		conditionXML.append("</value></item>");

		conditionXML.append("<item><name>carCode</name><value>");
		conditionXML.append(carCode);
		conditionXML.append("</value></item>");

		conditionXML.append("<item><name>carEngine</name><value>");
		conditionXML.append(carEngine);
		conditionXML.append("</value></item>");

		conditionXML.append("<item><name>queryReasonID</name><value>");
		conditionXML.append(QUERY_REASON_ID);
		conditionXML.append("</value></item>");

		conditionXML.append("<item><name>subreportIDs</name><value>");
		conditionXML.append(reportIds);
		conditionXML.append("</value></item>");
		conditionXML.append("</condition></conditions>");

		return conditionXML.toString();
	}

	/**
	 * 判断ex异常是否是超时异常：SocketTimeoutException
	 * */
	private boolean isTimeoutException(Exception ex) {
		if (ex == null)
			return false;
		String exeMsg = ex.getMessage();
		if (exeMsg != null && exeMsg.toLowerCase().indexOf("sockettimeout") > -1) {
			return true;
		}
		exeMsg = ex.toString();
		if (exeMsg != null && exeMsg.toLowerCase().indexOf("sockettimeout") > -1) {
			return true;
		}
		return false;
	}

}
