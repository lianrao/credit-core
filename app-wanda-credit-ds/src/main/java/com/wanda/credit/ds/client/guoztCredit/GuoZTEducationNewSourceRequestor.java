package com.wanda.credit.ds.client.guoztCredit;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.converter.WsDateConverter;
import com.wanda.credit.base.converter.WsDoubleConverter;
import com.wanda.credit.base.converter.WsIntConverter;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.CardNoValidator;
import com.wanda.credit.base.util.ExceptionUtil;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.log.ds.vo.LoggingEvent;
import com.wanda.credit.common.props.DynamicConfigLoader;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.common.util.ModelUtils;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.dao.domain.Guozt_degrees_check_result;
import com.wanda.credit.ds.dao.domain.Py_edu_college;
import com.wanda.credit.ds.dao.domain.Py_edu_degree;
import com.wanda.credit.ds.dao.domain.Py_edu_degreeNew;
import com.wanda.credit.ds.dao.domain.Py_edu_personBase;
import com.wanda.credit.ds.dao.iface.IGuoZCollageService;
import com.wanda.credit.ds.dao.iface.IGuoZTDegreesService;
import com.wanda.credit.ds.dao.iface.pengyuan.IPYEduPersonBaseService;
import com.wanda.credit.ds.iface.IDataSourceRequestor;
import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.api.enums.FileArea;
import com.wanda.credit.api.enums.FileType;
import com.wanda.credit.api.iface.IExecutorFileService;
import com.wanda.credit.api.iface.IExecutorSecurityService;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

@DataSourceClass(bindingDataSourceId = "ds_guozt_eduNew")
public class GuoZTEducationNewSourceRequestor extends BaseGuoZTDataSourcesRequestor implements IDataSourceRequestor {
	private final Logger logger = LoggerFactory.getLogger(GuoZTEducationNewSourceRequestor.class);
	@Autowired
	private BaseGuoZTIncacheRequestor baseIncaheGZTService;
	@Autowired
	private IGuoZTDegreesService degreesService;
	@Autowired
	private IGuoZCollageService collageService;
	@Autowired
	private IPYEduPersonBaseService pyEduPersonBaseService;
	@Autowired
	private IExecutorFileService fileService;
	@Autowired
	private IExecutorSecurityService synchExecutorService;
	private static final String STATUS_SUCCESS = "0";// 接口调用成功
	private static final String BIZ_CODE_1_SUCCESS = "0";// 查询成功，有记录
	private static final String BIZ_CODE_1_SUCCESS_NONE = "1";// 查询成功，无记录

	@Autowired
	private IPropertyEngine propertyEngine;

	@Override
	public Map<String, Object> request(String trade_id, DataSource ds) {
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		String edu_acctId = propertyEngine.readById("ds_edu_acctId");
		String eduUrl = propertyEngine.readById("ds_eduGztUrl");
		Map<String, Object> retdata = new HashMap<String, Object>();
		Map<String, String> photoData = new HashMap<String, String>();
		Map<String, Object> rets = new HashMap<String, Object>();
		DataSourceLogVO logObj = new DataSourceLogVO();
		Map<String, Object> reqparam = new HashMap<String, Object>();
		Map<String, Object> model_param = new HashMap<String, Object>();
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));// log请求时间
		logObj.setDs_id(ds.getId());
		logObj.setReq_url(eduUrl);
		logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
		logObj.setIncache("0");

		String initTag = Conts.TAG_SYS_ERROR;
		int topDegreeCode = 0;
		String topDegree = "";
		String riskInfo = "";
		try {
			String name = ParamUtil.findValue(ds.getParams_in(), "name").toString(); // 姓名
			String cardNo = ParamUtil.findValue(ds.getParams_in(), "cardNo").toString(); // 身份证号码
			String degreeLevel = (String) ParamUtil.findValue(ds.getParams_in(), "degreeLevel");
			String acct_id = (String) ParamUtil.findValue(ds.getParams_in(), "acct_id");
			if (StringUtils.isNotEmpty(CardNoValidator.validate(cardNo))) {
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
				logObj.setState_msg("身份证号码不符合规范");
				logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
				logger.error("{} {}", prefix, logObj.getState_msg());
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_IDCARD_ERROR);
				rets.put(Conts.KEY_RET_MSG, "您输入的为无效身份证号码，请核对后重新输入!");
				rets.put(Conts.KEY_RET_TAG, new String[] { initTag });
				return rets;
			}
			boolean is_eduAcct = isStartTimeReturn(edu_acctId, acct_id);
			int gender = 0;
			String birthDays = "";
			Map<String, String> mapresult = baseIncaheGZTService.IdNOToSex(cardNo, prefix);
			gender = Integer.valueOf(mapresult.get("gender"));
			birthDays = mapresult.get("birthDays");
			String params = name + "," + cardNo + "," + trade_id;
			String enCardNo = synchExecutorService.encrypt(cardNo);
			reqparam.put("name", name);
			reqparam.put("cardNo", cardNo);
			Py_edu_personBase personBase = null;
			Py_edu_degree degree = null;
			Py_edu_degreeNew degreeNew = null;
			Py_edu_college college = null;
			// 基本信息设置
			personBase = new Py_edu_personBase();
			degree = new Py_edu_degree();
			degreeNew = new Py_edu_degreeNew();
			college = new Py_edu_college();
			boolean inCache = false;
			boolean inCachPY = false;
			int date_num = 0;
			String incache_flag = "0";
			// 调用模型判断时间
			model_param.put("AGES", baseIncaheGZTService.IdNOToAge(cardNo));
			model_param = ModelUtils.calculate("M_credit_eduModel", ParamUtil.convertParams(model_param), false);
			if (model_param != null) {
				date_num = (int) Double.parseDouble(extractValueFromResult("INCACHE_NUM", model_param).toString());
				incache_flag = extractValueFromResult("INCACHE_FLAG", model_param).toString();
				if ("1".equals(incache_flag) || "2".equals(incache_flag)) {
					inCache = degreesService.inCachedDate(name, enCardNo, date_num * 30);

					if (!inCache) {
						inCache = pyEduPersonBaseService.inCachedMonth(name, enCardNo, date_num * 30);
						inCachPY = true;
					}
					logger.info("{} 查询模型获得数据:{} {}", new String[] { prefix, incache_flag,
							extractValueFromResult("INCACHE_NUM", model_param).toString() });
				}
			}
			if (inCache) {
				logger.info("{} 查询学历查询数据......", new String[] { prefix });
				logObj.setIncache("1");
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
				if (inCachPY) {
					// 查询鹏元数据表
					logObj.setBiz_code1("pengyuan_data");
					logger.info("{}查询鹏元学历数据", new String[] { prefix });
					personBase = pyEduPersonBaseService.queryPersonBase(name, enCardNo);
					initTag = Conts.TAG_INCACHE_UNFOUND;
					if (!"0".equals(personBase.getCollege())) {
						initTag = Conts.TAG_INCACHE_FOUND;
						degree = pyEduPersonBaseService.queryPersonDegree(personBase.getTrade_id());
						String photoId = "";
						if (degree != null) {
							String checkdegree = degree.getDegree();
							String checkstudyStyle = degree.getStudyStyle();
							if (checkdegree != null && checkstudyStyle != null) {
								String studyType = checkType(checkdegree, checkstudyStyle);
								degreeNew.setStudyType(studyType);
							} else {
								degreeNew.setStudyType("其他");
							}
							photoId = degree.getPhoto_id();
						}
						photoData.put("fileId", photoId);
						college = pyEduPersonBaseService.queryPersonCollege(personBase.getTrade_id());
					}
					logger.info("{}数据中存在此人学历数据!", prefix);
					logger.info("{} 鹏元学历信息开始封装!", prefix);
					personBase.setDocumentNo(cardNo);

					degreeNew.setCollege(degree.getCollege());
					degreeNew.setSpecialty("");
					degreeNew.setStudyResult(degree.getStudyResult());
					degreeNew.setPhoto_id("");
					degreeNew.setPhoto("");
					degreeNew.setPhotoStyle("");
					degreeNew.setDegree(degree.getDegree());
					degreeNew.setLevelNo(degree.getLevelNo());
					degreeNew.setTrade_id(trade_id);
					degreeNew.setStartTime("");
					degreeNew.setStudyStyle(degree.getStudyStyle());
					degreeNew.setGraduateTime(degree.getGraduateTime());
					degreeNew.setIsKeySubject(degree.getIsKeySubject());
					degreeNew.setId(trade_id);

					/* 如果从中获取的结果是未查得-直接返回数据 */
					if ("0".equals(personBase.getCollege())) {
						retdata.put("topDegreeCode", topDegreeCode);
						retdata.put("topDegree", topDegree);
						retdata.put("edu_result", "1");// 未查到
					} else {
						// -------------计算学历评估模型--------------------------
						riskInfo = baseIncaheGZTService.EduRiskInfo(personBase.getGraduateYears(), gender + "",
								college.getIs211(), degree.getStudyType(), personBase.getDegree());
						if ("100".equals(riskInfo)) {
							riskInfo = "";
						}
						personBase.setRiskAndAdviceInfo("");
						personBase.setSpecialty("");
						retdata.put("personBase", personBase);
						retdata.put("degree", degreeNew);
						retdata.put("college", college);
						// add by wangjing 2015-12-15 用于快易花风控准入规则学历校验
						if (personBase != null) {
							topDegreeCode = BaseGuoZTIncacheRequestor.degreeMap.get(personBase.getDegree());
							topDegree = personBase.getDegree();
						}
						retdata.put("topDegreeCode", topDegreeCode);
						retdata.put("topDegree", topDegree);
						// end
						retdata.put("edu_result", "0");// 查到
						if (StringUtils.isNotBlank(degreeLevel)) {
							degreeLevel = degreeLevel.trim();
							retdata.put("degreeLevelCheck", baseIncaheGZTService.check(degreeLevel, topDegree));
						}
					}
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
					rets.put(Conts.KEY_RET_DATA, retdata);
					rets.put(Conts.KEY_RET_MSG, "采集成功!");
					rets.put("photoData", photoData);
					rets.put(Conts.KEY_RET_TAG, new String[] { initTag });
					return rets;
				} else {
					logger.info("{}查询国政通学历数据", new String[] { prefix });
					Map<String, String> mapStr = new HashMap<String, String>();
					mapStr.put("trade_id", trade_id);
					mapStr.put("incache_flag", incache_flag);
					mapStr.put("name", name);
					mapStr.put("enCardNo", enCardNo);
					mapStr.put("cardNo", cardNo);
					mapStr.put("gender", gender + "");
					mapStr.put("birthDays", birthDays);
					mapStr.put("degreeLevel", degreeLevel);
					rets = baseIncaheGZTService.getEduIncacheGuoZT(mapStr, date_num, is_eduAcct);
					if (rets == null) {
						inCache = false;
					} else {
						String[] initTags = (String[]) rets.get(Conts.KEY_RET_TAG);
						initTag = initTags[0];
						return rets;
					}
				}
			}
			if (!inCache) {
				logObj.setIncache("0");
				logger.info("{}国政通学历数据源源采集开始......", new String[] { prefix });
				String respXml = singQureyEdu(params, prefix, eduUrl);
				// logger.info("{}国政通学历数据源返回数据：{}", new String[] {
				// prefix,respXml });
				// String respXml = IOUtils.toString(new
				// FileInputStream("e:/temp/gztxl1.xml"));
				DataSourceLogEngineUtil.writeLog2LogSys(new LoggingEvent(trade_id, respXml, new String[] { trade_id }));
				if (respXml == null || respXml.length() == 0) {
					logger.error("{} 国政通学历查询返回异常！", prefix);
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_EDU_EXCEPTION);
					rets.put(Conts.KEY_RET_MSG, "学历查询失败");
					rets.put(Conts.KEY_RET_TAG, new String[] { initTag });
					logObj.setState_msg("国政通学历查询返回异常");
					logObj.setBiz_code3(trade_id);
					return rets;
				}
				// 解析返回报文
				Document rspDoc = DocumentHelper.parseText(filtRspBody(respXml));
				Node repDocMsg = rspDoc.selectSingleNode("//data/message");
				Node messStat = repDocMsg.selectSingleNode("status");
				Node messStatDesc = repDocMsg.selectSingleNode("value");
				if (STATUS_SUCCESS.equals(messStat.getStringValue())) {
					Node eduInfoNode = rspDoc.selectSingleNode("//data/eduInfos/eduInfo");
					Node messageNode = eduInfoNode.selectSingleNode("message");
					String status = messageNode.selectSingleNode("status").getStringValue();
					if (BIZ_CODE_1_SUCCESS.equals(status)) {
						((Element) eduInfoNode).remove(messageNode);
						Guozt_degrees_check_result degrees = getDegreesBean(eduInfoNode);
						degrees.setCardNo(enCardNo);
						degrees.setTrade_id(trade_id);
						degrees.setStatus1("0");
						degrees.setSourceid("01");
						degrees.setBirthday(birthDays);
						degrees.setCreate_date(new Date());
						String photoContent = degrees.getPhoto();
						if (StringUtils.isNotBlank(photoContent)) {
							logger.info("{} 照片上传征信存储开始...", prefix);
							String fileId = fileService.upload(photoContent, FileType.JPG, FileArea.DS, trade_id);
							logger.info("{}照片上传存储成功,照片id为：{}", new String[] { prefix, fileId });
							degrees.setImage_file(fileId);
							photoData.put("fileId", fileId);
						}
						degreesService.add(degrees);
						// degrees.setCardNo(enCardNo);

						// -------------计算学历评估模型--------------------------
						String gradeYears = baseIncaheGZTService.EndTime(degrees.getGraduateTime());// 毕业年限

						personBase.setDocumentNo(cardNo);
						personBase.setName(degrees.getUserName());
						personBase.setTrade_id(trade_id);
						personBase.setId(trade_id);
						personBase.setReportId(trade_id);
						personBase.setDegree(degrees.getEducationDegree());
						personBase.setSpecialty("");
						personBase.setCollege(degrees.getGraduate());
						personBase.setGraduateTime(degrees.getGraduateTime());
						personBase.setOriginalAddress("");
						personBase.setVerifyResult(null);
						personBase.setBirthday("");
						personBase.setGender(gender);
						personBase.setAge(baseIncaheGZTService.IdNOToAge(cardNo));

						personBase.setGraduateYears(gradeYears);
						personBase.setBirthday(birthDays);
						// 学历信息
						degreeNew.setCollege(degrees.getGraduate());
						degreeNew.setId(trade_id);
						degreeNew.setStartTime("");
						degreeNew.setGraduateTime(degrees.getGraduateTime());
						degreeNew.setStudyStyle(degrees.getDstudyStyle());
						degreeNew.setStudyType(degrees.getStudyStyle());
						degreeNew.setSpecialty("");
						degreeNew.setDegree(degrees.getEducationDegree());
						degreeNew.setStudyResult(degrees.getStudyResult());
						if (StringUtils.isNotEmpty(degrees.getPhoto())) {
							degreeNew.setPhoto(degrees.getPhoto());
						} else {
							degreeNew.setPhoto("");
						}
						if (StringUtils.isNotEmpty(degrees.getImage_file())) {
							degreeNew.setPhoto_id(degrees.getImage_file());
						} else {
							degreeNew.setPhoto_id("");
						}
						degreeNew.setPhotoStyle("");
						degreeNew.setTrade_id(trade_id);
						degreeNew.setLevelNo("");
						degreeNew.setIsKeySubject("");

						// 学院信息
						college = baseIncaheGZTService.getColleage(degrees, prefix);
						college.setId(trade_id);
						college.setTrade_id(trade_id);
						if (BaseGuoZTIncacheRequestor.degreeMap.get(personBase.getDegree()) != null) {
							topDegreeCode = BaseGuoZTIncacheRequestor.degreeMap.get(personBase.getDegree());
							topDegree = personBase.getDegree();
						}
						riskInfo = baseIncaheGZTService.EduRiskInfo(gradeYears, gender + "", college.getIs211(),
								degrees.getStudyStyle(), degrees.getEducationDegree());
						if ("100".equals(riskInfo)) {
							riskInfo = "";
						}
						personBase.setRiskAndAdviceInfo("");
						initTag = Conts.TAG_FOUND;
						retdata.put("topDegreeCode", topDegreeCode);
						retdata.put("topDegree", topDegree);
						retdata.put("personBase", personBase);
						retdata.put("degree", degreeNew);
						retdata.put("college", college);
						rets.put("photoData", photoData);
						retdata.put("edu_result", "0");
						logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
						logObj.setBiz_code1(BaseGuoZTIncacheRequestor.checkCode.get(status));
						logObj.setBiz_code3(trade_id);
					} else if (BIZ_CODE_1_SUCCESS_NONE.equals(status)) {
						Guozt_degrees_check_result degrees = new Guozt_degrees_check_result();
						degrees.setCardNo(enCardNo);
						degrees.setUserName(name);
						degrees.setBirthday(birthDays);
						degrees.setTrade_id(trade_id);
						degrees.setStatus1("1");
						degrees.setSourceid("01");
						degrees.setCreate_date(new Date());
						degreesService.add(degrees);
						initTag = Conts.TAG_UNFOUND;
						logger.warn("{} 未查询到学历信息", trade_id);
						retdata.put("topDegreeCode", topDegreeCode);
						retdata.put("topDegree", topDegree);
						retdata.put("edu_result", "1");
						logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
						logObj.setBiz_code1(BaseGuoZTIncacheRequestor.checkCode.get(status));
						logObj.setBiz_code3(trade_id);
					} else {
						initTag = Conts.TAG_UNFOUND_OTHERS;
						logger.info("{} 国政通学历查询失败:{}", new String[] { prefix, messageNode.getStringValue() });
						rets.clear();
						rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_EDU_EXCEPTION);
						rets.put(Conts.KEY_RET_MSG, "学历查询失败!");
						rets.put(Conts.KEY_RET_TAG, new String[] { initTag });
						logObj.setBiz_code1(BaseGuoZTIncacheRequestor.checkCode.get(status));
						logObj.setBiz_code3(trade_id);
						return rets;
					}
				} else {
					logger.info("{} 国政通学历数据源调用失败:{}", new String[] { prefix, messStatDesc.getStringValue() });
					rets.clear();
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_EDU_EXCEPTION);
					rets.put(Conts.KEY_RET_MSG, "学历查询失败!");
					rets.put(Conts.KEY_RET_TAG, new String[] { initTag });
					logObj.setState_msg(messStatDesc.getStringValue());
					logObj.setBiz_code1(messStat.getStringValue());
					logObj.setBiz_code3(trade_id);
					return rets;
				}
			}
			if (StringUtils.isNotBlank(degreeLevel)) {
				degreeLevel = degreeLevel.trim();
				retdata.put("degreeLevelCheck", baseIncaheGZTService.check(degreeLevel, topDegree));
			}
			rets.put(Conts.KEY_RET_TAG, new String[] { initTag });
			rets.put(Conts.KEY_RET_DATA, retdata);
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
			rets.put(Conts.KEY_RET_MSG, "采集成功!");
		} catch (Exception ex) {
			initTag = Conts.TAG_SYS_ERROR;
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, "数据源处理时异常! 详细信息:" + ex.getMessage());
			logger.error("{} 数据源处理时异常：{}", prefix, ExceptionUtil.getTrace(ex));
			if (ExceptionUtil.isTimeoutException(ex)) {
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_TIMEOUT);
				initTag = Conts.TAG_SYS_TIMEOUT;
			} else {
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
				logObj.setState_msg("数据源处理时异常! 详细信息:" + ex.getMessage());
			}
			rets.put(Conts.KEY_RET_TAG, new String[] { initTag });
		} finally {
			logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
			logObj.setTag(initTag);
			DataSourceLogEngineUtil.writeLog(trade_id, logObj);
			DataSourceLogEngineUtil.writeParamIn(trade_id, reqparam, logObj);
		}
		return rets;
	}

	/**
	 * 保存犯罪记录信息
	 * 
	 * @date 2016年6月29日 下午1:49:27
	 * @author ou.guohao
	 * @param badInfoNode
	 * @param cardNo
	 * @param name
	 * @param trade_id
	 * @return
	 * @throws ServiceException
	 */
	private Guozt_degrees_check_result getDegreesBean(Node eduInfoNode) {
		// TODO Auto-generated method stub
		XStream stream = new XStream(new DomDriver());
		stream.registerConverter(new WsDateConverter("yyyy-MM-dd", new String[] { "yyyyMMdd", "yyyy" }));
		stream.registerConverter(new WsIntConverter());
		stream.registerConverter(new WsDoubleConverter());
		stream.alias("eduInfo", Guozt_degrees_check_result.class);
		Guozt_degrees_check_result degrees = (Guozt_degrees_check_result) stream.fromXML(eduInfoNode.asXML());
		return degrees;
	}

	/** 学历一致性核查 */
	private String check(String degreeLevel, String topDegree) {

		/*
		 * 11=博士研究生;10=硕士研究生,研究生班;6=本科,第二学士学位,高升本,专升本,第二本科,研究生班,夜大电大函大普通班;
		 * 4=专科,专科(高职);other=不详
		 */
		String ruleStr = propertyEngine.readById("py_edu_degreelvl_map");
		if (StringUtils.isNotBlank(ruleStr)) {
			String[] rules = ruleStr.split(";");
			for (int i = 0; i < rules.length; i++) {
				String[] rule = rules[i].split("=");
				if (degreeLevel.equals(rule[0])) {
					if (rule[1].indexOf(topDegree) > -1) {
						return "一致";
					}
				} else if ("other".equals(rule[0])) {
					if (rule[1].indexOf(topDegree) > -1) {
						return "其他原因不一致";
					}
				}
			}
		}
		return "不一致";
	}

	private static Map<String, String> checkCode = new HashMap<String, String>();
	static {
		checkCode.put("0", "found");// 查询有数据
		checkCode.put("1", "notfound");// 查询无数据
		checkCode.put("2", "other");// 数据源接口调用失败
	}
	private static Map<String, Integer> degreeMap = new HashMap<String, Integer>();
	static {
		// 专科以下
		degreeMap.put("不详", 5);
		degreeMap.put("夜大电大函大普通班", 10);// 6分
		// 专科
		degreeMap.put("第二专科", 15);// 6
		degreeMap.put("专科(高职)", 20);// 6 大专
		degreeMap.put("专科", 25);// 6 大专
		// 本科
		degreeMap.put("专升本", 30);// 13
		degreeMap.put("本科", 35);// 13
		degreeMap.put("高升本", 40);
		degreeMap.put("第二本科", 45);// 14
		degreeMap.put("第二学士学位", 50);// 14
		// 研究生
		degreeMap.put("研究生班", 55);// 14
		degreeMap.put("硕士研究生", 60);// 15
		// 博士
		degreeMap.put("博士研究生", 65);// 17
	}

	// 根据身份证号输出年龄
	public static int IdNOToAge(String IdNO) {
		int leh = IdNO.length();
		String dates = "";
		SimpleDateFormat df = new SimpleDateFormat("yyyy");
		String year = df.format(new Date());
		if (leh == 18) {
			dates = IdNO.substring(6, 10);
			int u = Integer.parseInt(year) - Integer.parseInt(dates);
			return u;
		} else {
			dates = "19" + IdNO.substring(6, 8);
			return Integer.parseInt(year) - Integer.parseInt(dates);
		}

	}

	// 根据身份证号输出年龄
	public static String EndTime(String dates) {
		if (dates == null)
			return "";
		SimpleDateFormat df = new SimpleDateFormat("yyyy");
		String year = df.format(new Date());
		int u = Integer.parseInt(year) - Integer.parseInt(dates) - 1;
		return String.valueOf(u);

	}

	// 添加拼装学历类别的方法mafei-add
	protected String checkType(String degree, String studystyle) {
		String retstyle = null;
		if ((degree.equals("专科") || degree.contains("专科") || degree.equals("高升本") || degree.equals("专升本")
				|| degree.equals("第二本科") || degree.equals("第二学士学位") || degree.equals("第二专科") || degree.equals("本科"))
				&& (studystyle.equals("全日制") || studystyle.equals("普通全日制"))) {
			retstyle = "普通";
		} else if ((degree.equals("硕士") || degree.equals("硕士研究生") || degree.equals("研究生班") || degree.equals("博士研究生")
				|| degree.equals("博士"))
				&& (studystyle.equals("全日制") || studystyle.equals("非全日制") || studystyle.equals("普通全日制")
						|| studystyle.equals("在职"))) {
			retstyle = "研究生";
		} else if (studystyle.equals("业余") || studystyle.equals("夜大学") || studystyle.equals("脱产")
				|| studystyle.equals("函授")) {
			retstyle = "成人";
		} else if (studystyle.equals("开放教育") || studystyle.equals("电视教育")) {
			retstyle = "开放教育";
		} else if (studystyle.equals("远程教育")) {
			retstyle = "远程教育";
		} else if (studystyle.equals("网络教育")) {
			retstyle = "网络教育";
		} else if (studystyle.equals("") || studystyle.equals("不详") || studystyle.equals("*")) {
			retstyle = "自考";
		} else {
			retstyle = "其他";
		}
		return retstyle;
	}
}
