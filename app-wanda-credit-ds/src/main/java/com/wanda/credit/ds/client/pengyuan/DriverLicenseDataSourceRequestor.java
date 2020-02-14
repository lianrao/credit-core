package com.wanda.credit.ds.client.pengyuan;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.api.dto.Param;
import com.wanda.credit.api.iface.IExecutorSecurityService;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.CardNoValidator;
import com.wanda.credit.base.util.CommonUtil;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceBizCodeVO;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.log.ds.vo.LoggingEvent;
import com.wanda.credit.common.props.DynamicConfigLoader;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.dao.domain.pengyuan.Py_driver_license;
import com.wanda.credit.ds.dao.iface.pengyuan.IPyDriverLicenseService;
import com.wanda.credit.ds.iface.IDataSourceRequestor;

/**
 * @description 鹏元驾驶证查询
 * @author guohao.ou
 * @date 2016-04-21 13:56:00
 * */
@DataSourceClass(bindingDataSourceId="ds_py_driverlicense")
public class DriverLicenseDataSourceRequestor extends BasePengYuanDataSourceRequestor implements IDataSourceRequestor {
	private static final String CheckResult_Match = "一致";

	private final Logger logger = LoggerFactory.getLogger(DriverLicenseDataSourceRequestor.class);

	@Autowired
	private IPyDriverLicenseService driverLicenseService;
	@Autowired
	private IExecutorSecurityService synchExecutorService;

	private final String DS_ID = "ds_py_driverlicense";

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
			/**是否只查姓名身份证*/
			boolean simplest = true;
			// 姓名
			String name = ParamUtil.findValue(ds.getParams_in(), "name").toString();
			// 身份证号码
			String cardNo = ParamUtil.findValue(ds.getParams_in(), "cardNo").toString();
			String crptedCardNo = synchExecutorService.encrypt(cardNo);
			// 准驾车辆类型
			String carModels = (String) ParamUtil.findValue(ds.getParams_in(), "carModels");
			if(StringUtils.isNotBlank(carModels))simplest = false;
			// 初次领证日期
			String firstGetDocDate = (String) ParamUtil.findValue(ds.getParams_in(), "firstGetDocDate");
			if(StringUtils.isNotBlank(firstGetDocDate))simplest = false;
			// 档案编号
			String archviesNo = (String) ParamUtil.findValue(ds.getParams_in(), "archviesNo");
			if(StringUtils.isNotBlank(archviesNo))simplest = false;
			// 驾驶证状态
			String dlStatus = (String) ParamUtil.findValue(ds.getParams_in(), "status");
			if(StringUtils.isNotBlank(dlStatus) && dlStatus.equals("Y"))simplest = false;

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
			logObj.setReq_time(new Timestamp(System.currentTimeMillis()));
			params = CommonUtil.sliceMap(ParamUtil.convertParams(ds.getParams_in()), new String[] { "name", "cardNo", "carModels", "firstGetDocDate", "archviesNo", "status" });
            logger.info("{} simplest>>{}",trade_id,simplest);
			if(simplest){
    			logger.info("{} 驾驶证信息开始查询缓存", new String[] { prefix });
            	Py_driver_license cachedResult = driverLicenseService.queryCacheResult(name,crptedCardNo);
            	if(cachedResult != null && cachedResult.getCardNoCheckResult() != null &&
            			CheckResult_Match.equals(cachedResult.getCardNoCheckResult())){
            		logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
        			logObj.setIncache("1");
        			tags.add(Conts.TAG_BASIC_INCACHE_FOUND);
        			Map<String,Object> retdata = new HashMap<String,Object>();
        			retdata.put("nameCheckResult", cachedResult.getNameCheckResult());
        			retdata.put("cardNoCheckResult", cachedResult.getCardNoCheckResult());
    				rets.put(Conts.KEY_RET_DATA, retdata);
        			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
        			rets.put(Conts.KEY_RET_MSG, "采集成功!");
        			logger.info("{} 驾驶证信息查询缓存成功", new String[] { prefix });
        			return rets;
            	}
            }
			logObj.setIncache("0");
			logger.info("{} 驾驶证查询数据源采集开始......", new String[] { prefix });
			Map<String, String> reqData = buildRequestBody(name, cardNo, carModels, firstGetDocDate, archviesNo, dlStatus);
			String reqXML = reqData.get("conditionXML");
			DataSourceLogEngineUtil.writeLog2LogSys(new LoggingEvent(trade_id, reqXML, new String[] { trade_id }));
			String respXML = oldStub.queryReport(userId, userPwd, reqXML);
			logger.info("{} 驾驶证返回数据 {}",trade_id,respXML);
			DataSourceLogEngineUtil.writeLog2LogSys(new LoggingEvent(trade_id, respXML, new String[] { trade_id }));
			logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
			// 解析返回报文
			Document rspDoc = DocumentHelper.parseText(respXML);
			Node status = rspDoc.selectSingleNode("//result/status");
			/** 数据源返回失败 */
			if (status != null && !"1".equals(status.getStringValue())) {
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_PY_DRIVERLICENSE_EXCEPTION);
				rets.put(Conts.KEY_RET_MSG, "驾驶证查询数据源返回失败!");
				Node errMsg = rspDoc.selectSingleNode("//result/errorMessage");
				if (errMsg != null) {
					logObj.setState_msg(errMsg.getStringValue());
					logger.error("{} 驾驶证查询数据源返回失败：{}", prefix, errMsg.getStringValue());
				}
				tags.add(Conts.TAG_SYS_ERROR);
				return rets;
			}
			logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
			rspDoc = DocumentHelper.parseText(filtRspBody(respXML));
			Node cisReport = rspDoc.selectSingleNode("//*/cisReport");
			if (null != cisReport) {
				/** 记录响应状态信息 */
				handleBizcode(cisReport, logObj);
				/**处理tag信息 依赖于bizcode 所以上面的 handleBizcode method 必须执行在前*/
				if(StringUtils.isNotBlank(logObj.getBiz_code1())){
				    handleTag(trade_id,logObj,tags);
				}
				Py_driver_license driverLicence = createDriverLicence((Element) cisReport);
				driverLicence.setTradeId(trade_id);
				driverLicence.setSubreportIds(reqData.get("subreportIDs"));
				driverLicence.setName(name);
				driverLicence.setCardNo(crptedCardNo);
				driverLicence.setCarModels(carModels);
				driverLicence.setFirstGetDocDate(firstGetDocDate);
				driverLicence.setArchviesNo(archviesNo);
				driverLicenseService.add(driverLicence);
				driverLicence.setCardNo(cardNo);
				rets.put(Conts.KEY_RET_DATA, visitBusiData(driverLicence, params));
			}

			logger.info("{} 驾驶证查询数据源采集成功", new String[] { prefix });
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
			rets.put(Conts.KEY_RET_MSG, "采集成功!");
		} catch (Exception e) {
			/** 如果是超时异常 记录超时信息 */
			tags.clear();
			tags.add(Conts.TAG_SYS_ERROR);
			if (CommonUtil.isTimeoutException(e)) {
				tags.add(Conts.TAG_SYS_TIMEOUT);
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_TIMEOUT);
			} else {
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
				logObj.setState_msg("驾驶证查询数据源处理时异常! 详细信息:" + e.getMessage());
			}
			logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, "驾驶证查询数据源处理时异常! 详细信息:" + e.getMessage());
			logger.error(prefix + " 驾驶证查询数据源处理时异常", e);
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
        if("$PYJZ_009".equalsIgnoreCase(bizcode2[1])){tags.add(Conts.TAG_BASIC_FOUND);}		
        if("$PYJZ_010".equalsIgnoreCase(bizcode2[1])){tags.add(Conts.TAG_BASIC_UNFOUND);}	
        if("$PYJZ_011".equalsIgnoreCase(bizcode2[1])){tags.add(Conts.TAG_BASIC_UNFOUND_OTHERS);}
        
		/**驾驶证状态查得()*/
        if("$PYJZ_009".equalsIgnoreCase(bizcode2[2])){tags.add(Conts.TAG_STATUS_FOUND);}		
        if("$PYJZ_010".equalsIgnoreCase(bizcode2[2])){tags.add(Conts.TAG_STATUS_UNFOUND);}	
        if("$PYJZ_011".equalsIgnoreCase(bizcode2[2])){tags.add(Conts.TAG_STATUS_UNFOUND_OTHERS);}        

		/**个人驾驶证准驾车型核查()*/
        if("$PYJZ_006".equalsIgnoreCase(bizcode3[3])){tags.add(Conts.TAG_CAR_MODELS_MATCH);}		
        if("$PYJZ_007".equalsIgnoreCase(bizcode3[3])){tags.add(Conts.TAG_CAR_MODELS_UNMATCH);}	
        if("$PYJZ_008".equalsIgnoreCase(bizcode3[3])){tags.add(Conts.TAG_CAR_MODELS_UNFOUND);}  //无法核查
        if("$PYJZ_011".equalsIgnoreCase(bizcode2[3])){tags.add(Conts.TAG_CAR_MODELS_UNFOUND_OTHERS);}//其他原因未查得          

        
		/**初次领证日期核查()*/
        if("$PYJZ_006".equalsIgnoreCase(bizcode3[4])){tags.add(Conts.TAG_INITIAL_DATE_MATCH);}		
        if("$PYJZ_007".equalsIgnoreCase(bizcode3[4])){tags.add(Conts.TAG_INITIAL_DATE_UNMATCH);}	
        if("$PYJZ_008".equalsIgnoreCase(bizcode3[4])){tags.add(Conts.TAG_INITIAL_DATE_UNFOUND);}      //无法核查 ?? 未查得基本不存在 因为是提交收费 至于一致 不一致 其他不一致??    
        if("$PYJZ_011".equalsIgnoreCase(bizcode2[4])){tags.add(Conts.TAG_INITIAL_DATE_UNFOUND_OTHERS);}  //其他原因未查得        
        
        /**档案编号核查()*/
        if("$PYJZ_006".equalsIgnoreCase(bizcode3[5])){tags.add(Conts.TAG_ARCH_NUMBER_MATCH);}		
        if("$PYJZ_007".equalsIgnoreCase(bizcode3[5])){tags.add(Conts.TAG_ARCH_NUMBER_UNMATCH);}	
        if("$PYJZ_008".equalsIgnoreCase(bizcode3[5])){tags.add(Conts.TAG_ARCH_NUMBER_UNFOUND);}      //无法核查 ??TODO 未查得呢??    
        if("$PYJZ_011".equalsIgnoreCase(bizcode2[5])){tags.add(Conts.TAG_ARCH_NUMBER_UNFOUND_OTHERS);}  //其他原因未查得        
        
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
	private void handleBizcode(Node cisReport, DataSourceLogVO logObj) {
		StringBuffer reportIds = new StringBuffer();// 拼接请求子报告Id
		StringBuffer treatResults = new StringBuffer();// 拼接交易结果:1.查得 2.未查得
														// 3.其他原因未查得
		StringBuffer checkResults = new StringBuffer();// 拼接核查结果：一致、不一致、无法核查
		DataSourceBizCodeVO bizCodeVo;
		for (DriverLicenseResult rs : DriverLicenseResult.values()) {
			String reportId = null, treatResult = null, checkResult = null;
			Element node = (Element) cisReport.selectSingleNode(rs.getNodeName());
			if (node != null) {
				bizCodeVo = DataSourceLogEngineUtil.fetchBizCodeByRetCode(DS_ID, rs.getReportId());
				reportId = bizCodeVo == null ? null : bizCodeVo.getBizCode();
				treatResult = node.attributeValue("treatResult");
				if ("1".equals(treatResult)) {// 1查得数据的情况下，查询核查结果状态码
					Node checkResultNode = node.selectSingleNode("checkResult");
					Node driverLicenseStatusDesc = node.selectSingleNode("driverLicenseStatusDesc");
					if (checkResultNode != null) {// 初次领证日期firstGetDocDate,
													// 准驾车型carModels,
													// 档案编号archviesNo 的查询结果
						checkResult = checkResultNode.getText();
					} else if (driverLicenseStatusDesc != null) {// 驾照状态的查询结果
						checkResult = driverLicenseStatusDesc.getText();
					} else {// 姓名name，身份证 cardNo的查询结果
						Node item = node.selectSingleNode("item");
						if (rs == DriverLicenseResult.NAME) {
							checkResult = item.selectSingleNode("nameCheckResult").getText();
						} else if (rs == DriverLicenseResult.DOCUMENTNO) {
							checkResult = item.selectSingleNode("documentNoCheckResult").getText();
						}
					}
					if (StringUtils.isNotBlank(checkResult)) {
						bizCodeVo = DataSourceLogEngineUtil.fetchBizCodeByRetCode(DS_ID, checkResult.trim());
						checkResult = bizCodeVo == null ? null : bizCodeVo.getBizCode();
					}

				}
				if (StringUtils.isNotBlank(treatResult)) {
					// 查询交易结果状态码
					bizCodeVo = DataSourceLogEngineUtil.fetchBizCodeByRetCode(DS_ID, treatResult);
					treatResult = bizCodeVo == null ? null : bizCodeVo.getBizCode();
				}

			}
			reportIds.append(StringUtils.defaultString(reportId)).append(",");
			treatResults.append(StringUtils.defaultString(treatResult)).append(",");
			checkResults.append(StringUtils.defaultString(checkResult)).append(",");
		}
		// 设置拼接好的状态码
		logObj.setBiz_code1(reportIds.toString());
		logObj.setBiz_code2(treatResults.toString());
		logObj.setBiz_code3(checkResults.toString());
	}

	/**
	 * 构建鹏元请求信息
	 * 
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
	private Map<String, String> buildRequestBody(String name, String cardNo, String carModels, String firstGetDocDate, String archviesNo, String dlStatus) {
		Map<String, String> retrnMap = new HashMap<String, String>();
		StringBuffer conditionXML = new StringBuffer(), subreportIDs = new StringBuffer("14901");
		conditionXML.append("<?xml version=\"1.0\" encoding=\"GBK\"?>" + "<conditions><condition queryType=\"" + queryType + "\">");

		conditionXML.append("<item><name>name</name><value>");
		conditionXML.append(name);
		conditionXML.append("</value></item>");

		conditionXML.append("<item><name>documentNo</name><value>");
		conditionXML.append(cardNo);
		conditionXML.append("</value></item>");

		if ("Y".equals(dlStatus)) {//TODO //y
			subreportIDs.append(",14902");// 14902：驾驶证状态查询
		}

		if (StringUtils.isNotBlank(carModels)) {
			conditionXML.append("<item><name>carModels</name><value>");
			conditionXML.append(carModels);
			conditionXML.append("</value></item>");
			subreportIDs.append(",14903");// 14903：驾驶证准驾车型核查
		}

		if (StringUtils.isNotBlank(firstGetDocDate)) {
			conditionXML.append("<item><name>firstGetDocDate</name><value>");
			conditionXML.append(firstGetDocDate);
			conditionXML.append("</value></item>");
			subreportIDs.append(",14904");// 14904：初次领证日期核查
		}

		if (StringUtils.isNotBlank(archviesNo)) {
			conditionXML.append("<item><name>archviesNo</name><value>");
			conditionXML.append(archviesNo);
			conditionXML.append("</value></item>");
			subreportIDs.append(",14905");// 14905:档案编号核查
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
	 * 把返回结果XML节点转为Py_driver_license对象
	 * 
	 * @date 2016年4月26日 下午4:00:01
	 * @author ou.guohao
	 * @param cisReport XML节点
	 * @return
	 */
	private Py_driver_license createDriverLicence(Element cisReport) {
		Py_driver_license driverLicence = new Py_driver_license();
		String treatResult, errorMessage;

		Element driverBaseCheck = (Element) cisReport.selectSingleNode(DriverLicenseResult.NAME.getNodeName());
		driverLicence.setTreatResult(cisReport.attributeValue("treatResult"));
		driverLicence.setSubReportTypes(cisReport.attributeValue("subReportTypes"));
		driverLicence.setSubreportIds(cisReport.attributeValue("subreportIds"));
		if (driverBaseCheck != null) {// 基本信息 姓名、身份证查询 结果
			treatResult = driverBaseCheck.attributeValue("treatResult");
			driverLicence.setBasicTreatResult(treatResult);
			if (StringUtils.isNotBlank(treatResult) && "1".equals(treatResult)) {
				Node nameCheckResult = driverBaseCheck.selectSingleNode("//*/nameCheckResult");
				Node documentNoCheckResult = driverBaseCheck.selectSingleNode("//*/documentNoCheckResult");
				driverLicence.setNameCheckResult(nameCheckResult == null ? null : nameCheckResult.getText());
				driverLicence.setCardNoCheckResult(documentNoCheckResult == null ? null : documentNoCheckResult.getText());
			} else {
				driverLicence.setNameErrorMessage(driverBaseCheck.attributeValue("errorMessage"));
				driverLicence.setCardnoErrorMessage(driverBaseCheck.attributeValue("errorMessage"));
			}
		}

		Element driverLicenseStatusInfo = (Element) cisReport.selectSingleNode(DriverLicenseResult.STATUS.getNodeName());
		if (driverLicenseStatusInfo != null) {// 驾照状态查询结果
			treatResult = driverLicenseStatusInfo.attributeValue("treatResult");
			driverLicence.setStatusTreatResult(treatResult);
			if (StringUtils.isNotBlank(treatResult) && "1".equals(treatResult)) {
				Node driverLicenseStatusDesc = driverLicenseStatusInfo.selectSingleNode("driverLicenseStatusDesc");
				driverLicence.setDriverLicenseStatusDesc(driverLicenseStatusDesc == null ? null : driverLicenseStatusDesc.getText());
			} else {
				driverLicence.setStatusErrorMessage(driverLicenseStatusInfo.attributeValue("errorMessage"));
			}
		}

		Element driverCarModelsCheck = (Element) cisReport.selectSingleNode(DriverLicenseResult.CARMODELS.getNodeName());
		if (driverCarModelsCheck != null) {// 准驾车型查询结果
			treatResult = driverCarModelsCheck.attributeValue("treatResult");
			driverLicence.setCarModelsTreatResult(treatResult);
			if (StringUtils.isNotBlank(treatResult) && "1".equals(treatResult)) {
				Node checkResult = driverCarModelsCheck.selectSingleNode("checkResult");
				driverLicence.setCarModelsCheckResult(checkResult == null ? null : checkResult.getText());
			} else {
				driverLicence.setCarModelsErrorMessage(driverCarModelsCheck.attributeValue("errorMessage"));
			}
		}

		Element driverFirstGetDocNoDateCheck = (Element) cisReport.selectSingleNode(DriverLicenseResult.FIRSTGETDOCDATE.getNodeName());
		if (driverFirstGetDocNoDateCheck != null) {// 初次领证日期查询结果
			treatResult = driverFirstGetDocNoDateCheck.attributeValue("treatResult");
			driverLicence.setFirstGetDocDateTreatResult(treatResult);
			if (StringUtils.isNotBlank(treatResult) && "1".equals(treatResult)) {
				Node checkResult = driverFirstGetDocNoDateCheck.selectSingleNode("checkResult");
				driverLicence.setFirstGetDocDateCheckResult(checkResult == null ? null : checkResult.getText());
			} else {
				driverLicence.setFirstGetDocDateErrorMessage(driverFirstGetDocNoDateCheck.attributeValue("errorMessage"));
			}
		}

		Element driverArchviesNoCheck = (Element) cisReport.selectSingleNode(DriverLicenseResult.ARCHVIESNO.getNodeName());
		if (driverArchviesNoCheck != null) {// 档案编号查询结果
			treatResult = driverArchviesNoCheck.attributeValue("treatResult");
			driverLicence.setArchviesNoTreatResult(treatResult);
			if (StringUtils.isNotBlank(treatResult) && "1".equals(treatResult)) {
				Node checkResult = driverArchviesNoCheck.selectSingleNode("checkResult");
				driverLicence.setArchviesNoCheckResult(checkResult == null ? null : checkResult.getText());
			} else {
				driverLicence.setArchviesNoErrorMessage(driverArchviesNoCheck.attributeValue("errorMessage"));
			}
		}
		return driverLicence;
	}

	/**
	 * 处理返回的结果
	 * 
	 * @date 2016年4月27日 下午5:43:44
	 * @author ou.guohao
	 * @param driverLicence
	 * @param params
	 * @return
	 * @throws Exception
	 */
	private Map<String, Object> visitBusiData(Py_driver_license driverLicence, Map<String, Object> params) throws Exception {
		Map<String, Object> retMap = BeanUtils.describe(driverLicence);
		// name,cardNo,carModels,firstGetDocDate,archviesNo,status

		Map<String, String> statusMap = new HashMap<String, String>();
		String statusText;
		statusMap.put("2", "未查得");
		statusMap.put("3", "其他原因未查得");
		for (String key : params.keySet()) {
			if (StringUtils.isBlank((String) params.get(key))) {
				continue;
			}
			statusText = statusMap.get(driverLicence.getBasicTreatResult());
			if ("name".equals(key)) {
				retMap.put("nameCheckResult", StringUtils.isBlank(driverLicence.getNameCheckResult()) ? statusText : driverLicence.getNameCheckResult());
			} else if ("cardNo".equals(key)) {
				retMap.put("cardNoCheckResult", StringUtils.isBlank(driverLicence.getCardNoCheckResult()) ? statusText : driverLicence.getCardNoCheckResult());
			} else if ("carModels".equals(key)) {
				statusText = statusMap.get(driverLicence.getCarModelsTreatResult());
				retMap.put("carModelsCheckResult", StringUtils.isBlank(driverLicence.getCarModelsCheckResult()) ? statusText : driverLicence.getCarModelsCheckResult());
			} else if ("firstGetDocDate".equals(key)) {
				statusText = statusMap.get(driverLicence.getFirstGetDocDateTreatResult());
				retMap.put("firstGetDocDateCheckResult", StringUtils.isBlank(driverLicence.getFirstGetDocDateCheckResult()) ? statusText : driverLicence.getFirstGetDocDateCheckResult());
			} else if ("archviesNo".equals(key)) {
				statusText = statusMap.get(driverLicence.getArchviesNoTreatResult());
				retMap.put("archviesNoCheckResult", StringUtils.isBlank(driverLicence.getArchviesNoCheckResult()) ? statusText : driverLicence.getArchviesNoCheckResult());
			} else if ("status".equals(key)) {
				statusText = statusMap.get(driverLicence.getStatusTreatResult());
				retMap.put("driverLicenseStatusDesc", StringUtils.isBlank(driverLicence.getDriverLicenseStatusDesc()) ? statusText : driverLicence.getDriverLicenseStatusDesc());
			}

		}
		retMap.remove("class");
		return retMap;
	}

	/**
	 * 查询结果枚举 date 2016年4月26日 下午4:10:21
	 * 
	 * @author ou.guohao
	 *
	 */
	private enum DriverLicenseResult {
		NAME("driverBaseCheck", "14900"), DOCUMENTNO("driverBaseCheck", "14901"), STATUS("driverLicenseStatusInfo", "14902"), CARMODELS("driverCarModelsCheck", "14903"), FIRSTGETDOCDATE("driverFirstGetDocNoDateCheck", "14904"), ARCHVIESNO("driverArchviesNoCheck", "14905");
		private DriverLicenseResult(String nodeName, String reportId) {
			this.nodeName = nodeName;
			this.reportId = reportId;
		}

		private String nodeName; // 返回结果节点名次
		private String reportId; // 返回结果节点对应的子报

		public String getNodeName() {
			return nodeName;
		}

		public String getReportId() {
			return reportId;
		}
	}

	/**
	 * 只验证姓名和身份证号码非空
	 * */
	public Map<String, Object> valid(String trade_id, DataSource ds) {
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		Map<String, Object> rets = null;
		try {
			rets = new HashMap<String, Object>();
			if (ds != null && ds.getParams_in() != null) {
				for (String paramId : paramIds) {
					if (!"name".equals(paramId) && !"cardNo".equals(paramId))
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
			logger.error(prefix +"驾驶证查询异常" , ex);
		}
		return rets;
	}

	protected String doHttpsPost(String trade_id, String aijin_address, String reqData) {
		return null;
	}

	public static void main(String[] args) {
		System.out.println(">>"+ StringUtils.isNotBlank(""));
		System.out.println(">>"+ StringUtils.isNotBlank(null));
		System.out.println(">>"+ StringUtils.isNotEmpty(null));
		System.out.println(">>"+ StringUtils.isNotEmpty(" "));


	}
}