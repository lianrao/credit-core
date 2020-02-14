package com.wanda.credit.ds.client.pengyuan;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.wanda.credit.api.iface.IExecutorSecurityService;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.converter.WsDateConverter;
import com.wanda.credit.base.converter.WsDoubleConverter;
import com.wanda.credit.base.converter.WsIntConverter;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.CardNoValidator;
import com.wanda.credit.base.util.CommonUtil;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.log.ds.vo.LoggingEvent;
import com.wanda.credit.common.props.DynamicConfigLoader;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.dao.domain.pengyuan.PY_car_info;
import com.wanda.credit.ds.dao.domain.pengyuan.PY_car_info_detail;
import com.wanda.credit.ds.dao.iface.pengyuan.IPYCarInfoDetailService;
import com.wanda.credit.ds.dao.iface.pengyuan.IPYCarInfoService;
import com.wanda.credit.ds.iface.IDataSourceRequestor;

/**
 * @description 鹏元车辆信息核查
 * @author guohao.ou
 * @date 2016-05-15 13:56:00
 * */
@DataSourceClass(bindingDataSourceId="ds_py_carInfo")
public class CarInfoCheckDataSourceRequestor extends BasePengYuanDataSourceRequestor implements IDataSourceRequestor {
	private final Logger logger = LoggerFactory.getLogger(CarInfoCheckDataSourceRequestor.class);

	@Autowired
	private IPYCarInfoService carInfoService;

	@Autowired
	private IPYCarInfoDetailService carInfoDetailService;
	@Autowired
	private IExecutorSecurityService synchExecutorService;

	private final String DS_ID = "ds_py_carInfo";

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
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));
		Map<String, Object> params = null;
		try {
			// 姓名
			String name = ParamUtil.findValue(ds.getParams_in(), "name").toString();
			// 身份证号码
			String cardNo = ParamUtil.findValue(ds.getParams_in(), "cardNo").toString();
			String crptedCardNo = synchExecutorService.encrypt(cardNo);

			// 车牌号
			String licenseNo = ParamUtil.findValue(ds.getParams_in(), "licenseNo").toString();
			// 号牌类型
			String carType = ParamUtil.findValue(ds.getParams_in(), "carType").toString();
			// 车架号
			String vin = (String) ParamUtil.findValue(ds.getParams_in(), "vin");
			// 初次登记日期
			String registTime = (String) ParamUtil.findValue(ds.getParams_in(), "registTime");
			// 是否查询汽车状态
			String carStatus = (String) ParamUtil.findValue(ds.getParams_in(), "carStatus");
			// 是否查询汽车信息
			String carDetail = (String) ParamUtil.findValue(ds.getParams_in(), "carDetail");

			params = CommonUtil.sliceMap(ParamUtil.convertParams(ds.getParams_in()), new String[] { "name", "cardNo", "licenseNo", "carType", "vin", "registTime", "carStatus", "carDetail" });
			/*add 身份证规则校验 20160905	Begin*/
			String valiRes = CardNoValidator.validate(cardNo);
			if (!StringUtil.isEmpty(valiRes)) {				
				logger.error("{} 身份证号码不符合规范： {}" , prefix , valiRes);
				tags.add(Conts.TAG_SYS_ERROR);
				logObj.setIncache("1");
				logObj.setState_msg("身份证号不合法");
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_IDCARD_ERROR);
        		rets.put(Conts.KEY_RET_MSG, CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_IDCARD_ERROR.getRet_msg());
                return rets;
			}
			/*add 身份证规则校验 20160905	End*/
			logObj.setIncache("0");
			logger.info("{} 汽车信息查询数据源采集开始......", new String[] { prefix });
			Map<String, String> reqData = buildRequestBody(name, cardNo, licenseNo, carType, vin, registTime, carStatus, carDetail);
			String reqXML = reqData.get("conditionXML");
			DataSourceLogEngineUtil.writeLog2LogSys(new LoggingEvent(trade_id, reqXML, new String[] { trade_id }));
			String respXML = oldStub.queryReport(userId, userPwd, reqXML);
			DataSourceLogEngineUtil.writeLog2LogSys(new LoggingEvent(trade_id, respXML, new String[] { trade_id }));
			logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
			// 解析返回报文
			Document rspDoc = DocumentHelper.parseText(respXML);
			Node status = rspDoc.selectSingleNode("//result/status");
			/** 数据源返回失败 */
			if (status != null && !"1".equals(status.getStringValue())) {
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_PY_CARINFO_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "汽车信息查询数据源返回失败!");
				Node errMsg = rspDoc.selectSingleNode("//result/errorMessage");
				if (errMsg != null) {
					logObj.setState_msg(errMsg.getStringValue());
					logger.error("{} 汽车信息查询数据源返回失败：{}", prefix, errMsg.getStringValue());
				}
				tags.add(Conts.TAG_SYS_ERROR);
				return rets;
			}
			logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
			rspDoc=DocumentHelper.parseText(filtRspBody(respXML));
			Element cisReport = (Element) rspDoc.selectSingleNode("//*/cisReport");
			if (null != cisReport) {
				/** 记录响应状态信息 */
				handleBizcode(cisReport, logObj);
				/**处理tag信息 依赖于bizcode 所以上面的 handleBizcode method 必须执行在前*/
				if(StringUtils.isNotBlank(logObj.getBiz_code1())){
				    handleTag(trade_id,logObj,tags);
				}
				PY_car_info carInfo = createCarInfo(cisReport);
				carInfo.setTradeId(trade_id);
				carInfo.setSubreportIDs(reqData.get("subreportIDs"));
				carInfo.setName(name);
				carInfo.setCardNo(crptedCardNo);
				carInfo.setLicenseNo(licenseNo);
				carInfo.setCarType(carType);
				carInfo.setVin(vin);
				carInfo.setRegistTime(registTime);
				carInfoService.add(carInfo);
				carInfo.setCardNo(cardNo);
				PY_car_info_detail carInfoDetail = createCarInfoDetail(cisReport);
				if (carInfoDetail != null) {
					carInfoDetail.setTradeId(trade_id);
					carInfoDetailService.add(carInfoDetail);
				}
				rets.put(Conts.KEY_RET_DATA, visitBusiData(carInfo, carInfoDetail));
			}

			logger.info("{} 汽车信息查询数据源采集成功", new String[] { prefix });
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);

			rets.put(Conts.KEY_RET_MSG, "采集成功!");
		} catch (Exception e) {
			tags.clear();
			tags.add(Conts.TAG_SYS_ERROR);
			/** 如果是超时异常 记录超时信息 */
			if (CommonUtil.isTimeoutException(e)) {
				tags.add(Conts.TAG_SYS_TIMEOUT);
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_TIMEOUT);
			} else {
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
				logObj.setState_msg("汽车信息查询数据源处理时异常! 详细信息:" + e.getMessage());
			}
			logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, "汽车信息查询数据源处理时异常! 详细信息:" + e.getMessage());
			logger.error(prefix + " 汽车信息查询数据源处理时异常", e);
		} finally {
			rets.put(Conts.KEY_RET_TAG,tags.toArray(new String[0]));
			logObj.setTag(StringUtils.join(tags, ";"));
			DataSourceLogEngineUtil.writeLog(trade_id, logObj);
			if (null != params) {
				DataSourceLogEngineUtil.writeParamIn(trade_id, params, logObj);
			}
		}
		return rets;
	}

	
	private static Map<String, String> checkResultMap = new HashMap<String, String>();
	static {
		checkResultMap.put("13813", "PYXSZ_002");
		checkResultMap.put("13814", "PYXSZ_003");
		checkResultMap.put("13817", "PYXSZ_004");
		checkResultMap.put("13818", "PYXSZ_005");
		checkResultMap.put("1", "PYXSZ_006");//查得
		checkResultMap.put("2", "PYXSZ_007");//未查得
		checkResultMap.put("3", "PYXSZ_008");//其他原因未查得
		checkResultMap.put("一致", "PYXSZ_009");
		checkResultMap.put("不一致", "PYXSZ_010");
		checkResultMap.put("无法核查", "PYXSZ_011");
	}

	/**
	 * note:根据 bizcode 处理tag信息 必须先执行下面的这个方法 
	 * @see #handleBizcode 
	 * */
	private void handleTag(String trade_id, DataSourceLogVO logObj,
			List<String> tags) {		
		String[] bizcode1 = format(logObj.getBiz_code1()).split(",");
		String[] bizcode2 = format(logObj.getBiz_code2()).split(",");
		String[] bizcode3 = format(logObj.getBiz_code3()).split(",");
		/**基本信息查得(身份证核查)*/
        if("$PYXSZ_006".equalsIgnoreCase(bizcode2[1])){tags.add(Conts.TAG_BASIC_FOUND);}		
        if("$PYXSZ_007".equalsIgnoreCase(bizcode2[1])){tags.add(Conts.TAG_BASIC_UNFOUND);}	
        if("$PYXSZ_008".equalsIgnoreCase(bizcode2[1])){tags.add(Conts.TAG_BASIC_UNFOUND_OTHERS);}
        
		/**车辆状态状态查得()*/
        if("$PYXSZ_006".equalsIgnoreCase(bizcode2[4])){tags.add(Conts.TAG_STATUS_FOUND);}		
        if("$PYXSZ_007".equalsIgnoreCase(bizcode2[4])){tags.add(Conts.TAG_STATUS_UNFOUND);}	
        if("$PYXSZ_008".equalsIgnoreCase(bizcode2[4])){tags.add(Conts.TAG_STATUS_UNFOUND_OTHERS);}        
        
 		/**机动车信息核查()*/
        if("$PYXSZ_006".equalsIgnoreCase(bizcode2[5])){tags.add(Conts.TAG_CAR_DETAIL_FOUND);}		
        if("$PYXSZ_007".equalsIgnoreCase(bizcode2[5])){tags.add(Conts.TAG_CAR_DETAIL_UNFOUND);}	
        if("$PYXSZ_008".equalsIgnoreCase(bizcode2[5])){tags.add(Conts.TAG_CAR_DETAIL_UNFOUND_OTHERS);}  
        
   		/**车架号核查()*/
        if("$PYXSZ_009".equalsIgnoreCase(bizcode3[6])){tags.add(Conts.TAG_VIN_MATCH);}		
        if("$PYXSZ_010".equalsIgnoreCase(bizcode3[6])){tags.add(Conts.TAG_VIN_UNMATCH);}	
        if("$PYXSZ_011".equalsIgnoreCase(bizcode3[6])){tags.add(Conts.TAG_VIN_UNFOUND);}//无法核查       
        if("$PYXSZ_008".equalsIgnoreCase(bizcode2[6])){tags.add(Conts.TAG_VIN_UNFOUND_OTHERS);} //其他原因未查得     
        
        /**个人车辆初次登记日期()*/
        if("$PYXSZ_009".equalsIgnoreCase(bizcode3[7])){tags.add(Conts.TAG_REGIST_TIME_MATCH);}		
        if("$PYXSZ_010".equalsIgnoreCase(bizcode3[7])){tags.add(Conts.TAG_REGIST_TIME_UNMATCH);}	
        if("$PYXSZ_011".equalsIgnoreCase(bizcode3[7])){tags.add(Conts.TAG_REGIST_TIME_UNFOUND);}	//无法核查
        if("$PYXSZ_008".equalsIgnoreCase(bizcode2[7])){tags.add(Conts.TAG_REGIST_TIME_UNFOUND_OTHERS);} //其他原因未查得
	}
	
	private static String format(String bizcode) {
	    bizcode = bizcode.replace(",", ",$");
	    bizcode = "$"+bizcode;
		return bizcode;
	}
	/**
	 * 处理业务状态码信息,拼接biz_code
	 * 
	 * @date 2016年4月25日 下午5:54:56
	 * @author ou.guohao
	 * @param cisReport
	 * @param logObj
	 */
	private void handleBizcode(Element cisReport, DataSourceLogVO logObj) {
		StringBuffer reportIds = new StringBuffer();// 拼接请求子报告Id
		StringBuffer treatResults = new StringBuffer();// 拼接交易结果:1.查得 2.未查得 3.其他原因未查得
		StringBuffer checkResults = new StringBuffer();// 拼接核查结果：一致、不一致、无法核查
		String reportId, treatResult;
		Element element = cisReport.element("carCheckInfo");
		if (element != null) {
			reportIds.append("PYXSZ_001_1,PYXSZ_001_2,PYXSZ_001_3,PYXSZ_001_4,");
			treatResult = element.attributeValue("treatResult");
			for (int i = 0; i < 4; i++) {
				treatResults.append(checkResultMap.get(treatResult)).append(",");
			}
			element = (Element) element.element("item");
			if (element != null && "1".equals(treatResult)) {
				for (Element e : (List<Element>) element.elements()) {
					checkResults.append(checkResultMap.get(e.getText())).append(",");
				}
			} else {
				checkResults.append(",,,,");
			}
		}
		element = (Element) cisReport.element("carStatusInfo");
		if (element != null) {
			reportId = element.attributeValue("subReportType");
			treatResult = element.attributeValue("treatResult");
			reportIds.append(checkResultMap.get(reportId));
			treatResults.append(checkResultMap.get(treatResult));
		}
		reportIds.append(",");
		treatResults.append(",");
		checkResults.append(",");
		element = (Element) cisReport.element("carInfo");
		if (element != null) {
			reportId = element.attributeValue("subReportType");
			treatResult = element.attributeValue("treatResult");
			reportIds.append(checkResultMap.get(reportId));
			treatResults.append(checkResultMap.get(treatResult));
		}
		reportIds.append(",");
		treatResults.append(",");
		checkResults.append(",");
		element = (Element) cisReport.element("carCodeCheck");
		if (element != null) {
			reportId = element.attributeValue("subReportType");
			treatResult = element.attributeValue("treatResult");
			reportIds.append(checkResultMap.get(reportId));
			treatResults.append(checkResultMap.get(treatResult));
			element = element.element("carCodeCheckResult");
			if (element != null) {
				checkResults.append(checkResultMap.get(element.getText()));
			}
		}
		reportIds.append(",");
		treatResults.append(",");
		checkResults.append(",");
		element = (Element) cisReport.element("carRegistTimeCheck");
		if (element != null) {
			reportId = element.attributeValue("subReportType");
			treatResult = element.attributeValue("treatResult");
			reportIds.append(checkResultMap.get(reportId));
			treatResults.append(checkResultMap.get(treatResult));
			element = element.element("registTimeCheckResult");
			if (element != null) {
				checkResults.append(checkResultMap.get(element.getText()));
			}
		}
		reportIds.append(",");
		treatResults.append(",");
		checkResults.append(",");
		// 设置拼接好的状态码
		logObj.setBiz_code1(reportIds.toString());
		logObj.setBiz_code2(treatResults.toString());
		logObj.setBiz_code3(checkResults.toString());
	}

	/**
	 * 构建鹏元请求信息
	 * @date 2016年4月22日 上午11:09:42
	 * @author ou.guohao
	 * @param name 姓名
	 * @param cardNo 身份证号码
	 * @param carModels 准驾车型
	 * @param firstGetDocDate 初次领证日期
	 * @param archviesNo 档案编号
	 * @param dlStatus 驾照状态
	 * @return
	 */
	private Map<String, String> buildRequestBody(String name, String cardNo, String licenseNo, String carType, String vin, String registTime, String carStatus, String carDetail) {
		Map<String, String> retrnMap = new HashMap<String, String>();
		StringBuffer conditionXML = new StringBuffer(), subreportIDs = new StringBuffer("13812");
		conditionXML.append("<?xml version=\"1.0\" encoding=\"GBK\"?>" + "<conditions><condition queryType=\"" + queryType + "\">");

		conditionXML.append("<item><name>name</name><value>");
		conditionXML.append(name);
		conditionXML.append("</value></item>");

		conditionXML.append("<item><name>documentNo</name><value>");
		conditionXML.append(cardNo);
		conditionXML.append("</value></item>");

		conditionXML.append("<item><name>licenseNo</name><value>");
		conditionXML.append(licenseNo);
		conditionXML.append("</value></item>");

		conditionXML.append("<item><name>carType</name><value>");
		conditionXML.append(carType);
		conditionXML.append("</value></item>");

		if ("Y".equals(carStatus)) {
			subreportIDs.append(",13813");// 13813 ：个人车辆状态查询
		}

		if ("Y".equals(carDetail)) {
			subreportIDs.append(",13814");// 13814 ：个人车辆信息查询
		}

		if (StringUtils.isNotBlank(vin)) {
			conditionXML.append("<item><name>VIN</name><value>");
			conditionXML.append(vin);
			conditionXML.append("</value></item>");
			subreportIDs.append(",13817");// 13817 ：车架号查询
		}

		if (StringUtils.isNotBlank(registTime)) {
			conditionXML.append("<item><name>registTime</name><value>");
			conditionXML.append(registTime);
			conditionXML.append("</value></item>");
			subreportIDs.append(",13818");// 14903：初次登记日期核查
		}
		conditionXML.append("<item><name>subreportIDs</name><value>");
		conditionXML.append(subreportIDs);
		conditionXML.append("</value></item>");
		conditionXML.append("</condition></conditions>");
		retrnMap.put("subreportIDs", subreportIDs.toString());
		retrnMap.put("conditionXML", conditionXML.toString());
		return retrnMap;
	}

	/**
	 * 把返回XML转PY_car_info对象
	 * 
	 * @date 2016年5月17日 下午6:12:39
	 * @author ou.guohao
	 * @param cisReport xml节点
	 * @return
	 */
	private PY_car_info createCarInfo(Element cisReport) {
		PY_car_info carInfo = new PY_car_info();
		String treatResult;
		carInfo.setReportId(cisReport.attributeValue("reportID"));
		treatResult = cisReport.attributeValue("treatResult");
		carInfo.setTreatResult(treatResult);
		Element carCheckInfo = (Element) cisReport.selectSingleNode("//cisReport/carCheckInfo");
		if (carCheckInfo != null) {
			treatResult = carCheckInfo.attributeValue("treatResult");
			carInfo.setBasicTreatResult(treatResult);
			if (StringUtils.isNotBlank(treatResult) && "1".equals(treatResult)) {
				Node nameCheckResult = carCheckInfo.selectSingleNode("//*/nameCheckResult");
				Node documentNoCheckResult = carCheckInfo.selectSingleNode("//*/documentNoCheckResult");
				Node licenseNoCheckResult = carCheckInfo.selectSingleNode("//*/licenseNoCheckResult");
				Node carTypeCheckResult = carCheckInfo.selectSingleNode("//*/carTypeCheckResult");
				carInfo.setNameCheckResult(nameCheckResult == null ? null : nameCheckResult.getText());
				carInfo.setCardNoCheckResult(documentNoCheckResult == null ? null : documentNoCheckResult.getText());
				carInfo.setLicenseNoCheckResult(licenseNoCheckResult == null ? null : licenseNoCheckResult.getText());
				carInfo.setCarTypeCheckResult(carTypeCheckResult == null ? null : carTypeCheckResult.getText());
			} else {
				carInfo.setBasicErrorMessage(carCheckInfo.attributeValue("errorMessage"));
			}
		}

		Element carStatusInfo = (Element) cisReport.selectSingleNode("//cisReport/carStatusInfo");
		if (carStatusInfo != null) {// 
			treatResult = carStatusInfo.attributeValue("treatResult");
			carInfo.setCarStatusTreatResult(treatResult);
			if (StringUtils.isNotBlank(treatResult) && "1".equals(treatResult)) {
				Node carStatusDesc = carStatusInfo.selectSingleNode("carStatusDesc");
				carInfo.setCarStatusDesc(carStatusDesc == null ? null : carStatusDesc.getText());
			} else {
				carInfo.setCarStatusErrorMessage(carStatusInfo.attributeValue("errorMessage"));
			}
		}
		Element carInfoDetail = (Element) cisReport.selectSingleNode("//cisReport/carInfo");
		if (carInfoDetail != null) {// 
			treatResult = carInfoDetail.attributeValue("treatResult");
			carInfo.setCarInfoTreatResult(treatResult);
			if (!"1".equals(treatResult)) {
				carInfo.setCarStatusErrorMessage(carInfoDetail.attributeValue("errorMessage"));
			}
		}

		Element carCodeCheck = (Element) cisReport.selectSingleNode("//cisReport/carCodeCheck");
		if (carCodeCheck != null) {// 准驾车型查询结果
			treatResult = carCodeCheck.attributeValue("treatResult");
			carInfo.setCarCodeTreatResult(treatResult);
			if (StringUtils.isNotBlank(treatResult) && "1".equals(treatResult)) {
				Node carCodeCheckResult = carCodeCheck.selectSingleNode("carCodeCheckResult");
				carInfo.setCarCodeCheckResult(carCodeCheckResult == null ? null : carCodeCheckResult.getText());
			} else {
				carInfo.setCarCodeErrorMessage(carCodeCheck.attributeValue("errorMessage"));
			}
		}

		Element carRegistTimeCheck = (Element) cisReport.selectSingleNode("//cisReport/carRegistTimeCheck");
		if (carRegistTimeCheck != null) {// 初次领证日期查询结果
			treatResult = carRegistTimeCheck.attributeValue("treatResult");
			carInfo.setRegistTimeTreatResult(treatResult);
			if (StringUtils.isNotBlank(treatResult) && "1".equals(treatResult)) {
				Node registTimeCheckResult = carRegistTimeCheck.selectSingleNode("registTimeCheckResult");
				carInfo.setRegistTimeCheckResult(registTimeCheckResult == null ? null : registTimeCheckResult.getText());
			} else {
				carInfo.setRegistTimeErrorMessage(carRegistTimeCheck.attributeValue("errorMessage"));
			}
		}
		return carInfo;
	}

	/**
	 * 把返回的xml转PY_car_info_detail
	 * 
	 * @date 2016年5月17日 下午6:31:08
	 * @author ou.guohao
	 * @param cisReport
	 * @return
	 */
	private PY_car_info_detail createCarInfoDetail(Element cisReport) {
		Element item = (Element) cisReport.selectSingleNode("//cisReport/carInfo/item");
		if (item != null) {
			XStream stream = new XStream(new DomDriver());
			stream.registerConverter(new WsDateConverter("yyyy-MM-dd", new String[] { "yyyyMMdd", "yyyy" }));
			stream.registerConverter(new WsIntConverter());
			stream.registerConverter(new WsDoubleConverter());
			stream.alias("item", PY_car_info_detail.class);
			PY_car_info_detail carInfoDetail = (PY_car_info_detail) stream.fromXML(item.asXML().trim().replaceAll(" ", "").replaceAll("\r|\n", ""));
			return carInfoDetail;
		}
		return null;
	}

	/**
	 * 验证姓名、身份证、车牌号、车辆类型
	 */
	public Map<String, Object> valid(String trade_id, DataSource ds) {
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		Map<String, Object> rets = null;
		try {
			rets = new HashMap<String, Object>();
			if (ds != null && ds.getParams_in() != null) {
				for (String paramId : paramIds) {
					if (!"name".equals(paramId) && !"cardNo".equals(paramId) && !"licenseNo".equals(paramId) && !"carType".equals(paramId))
						continue;
					if (null == ParamUtil.findValue(ds.getParams_in(), paramId)) {
						rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_PARAM_INVALID);
						rets.put(Conts.KEY_RET_MSG, "数据源参数校验不通过!");
						return rets;
					}
				}
			}
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
			rets.put(Conts.KEY_RET_MSG, "数据源参数校验通过!");
		} catch (Exception ex) {
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, "数据源处理时异常! 详细信息:" + ex.getMessage());
			logger.error("{} 数据源处理时异常：{}", prefix, ex.getMessage());
			ex.printStackTrace();
		}
		return rets;
	}

	private Map<String, Object> visitBusiData(PY_car_info carInfo, PY_car_info_detail carInfoDetail) throws Exception {
		Map<String, String> statusMap = new HashMap<String, String>();
		statusMap.put("2", "未查得");
		statusMap.put("3", "其他原因未查得");
		Map<String, Object> retMap = new HashMap<String, Object>();
		retMap.put("reportID", carInfo.getReportId());
		retMap.put("subReportType", carInfo.getSubreportIDs());
		if ("1".equals(carInfo.getBasicTreatResult())) {
			retMap.put("nameCheckResult", carInfo.getNameCheckResult());
			retMap.put("documentNoCheckResult", carInfo.getCardNoCheckResult());
			retMap.put("licenseNoCheckResult", carInfo.getLicenseNoCheckResult());
			retMap.put("carTypeCheckResult", carInfo.getCarTypeCheckResult());
		}

		retMap.put("carCodeCheckResult", carInfo.getCarCodeCheckResult());
		if (statusMap.get(carInfo.getCarCodeTreatResult()) != null) {
			retMap.put("carCodeCheckResult", statusMap.get(carInfo.getCarCodeTreatResult()));
		}

		retMap.put("registTimeCheckResult", carInfo.getRegistTimeCheckResult());
		if (statusMap.get(carInfo.getRegistTimeTreatResult()) != null) {
			retMap.put("registTimeCheckResult", statusMap.get(carInfo.getRegistTimeTreatResult()));
		}

		retMap.put("carStatusDesc", carInfo.getCarStatusDesc());
		if (statusMap.get(carInfo.getCarStatusTreatResult()) != null) {
			retMap.put("carStatusDesc", statusMap.get(carInfo.getCarStatusTreatResult()));
		}

		retMap.put("carInfo", carInfoDetail);
		if (statusMap.get(carInfo.getCarInfoTreatResult()) != null) {
			retMap.put("carInfo", statusMap.get(carInfo.getCarInfoTreatResult()));
		}

		return retMap;
	}
}
