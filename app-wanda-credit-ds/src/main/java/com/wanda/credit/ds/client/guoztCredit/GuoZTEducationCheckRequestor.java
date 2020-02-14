package com.wanda.credit.ds.client.guoztCredit;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.CardNoValidator;
import com.wanda.credit.base.util.ExceptionUtil;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.log.ds.vo.LoggingEvent;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.common.util.ModelUtils;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.dao.domain.Guozt_degrees_check_result;
import com.wanda.credit.ds.dao.iface.IGuoZCollageService;
import com.wanda.credit.ds.dao.iface.IGuoZTDegreesService;
import com.wanda.credit.ds.dao.iface.pengyuan.IPYEduPersonBaseService;
import com.wanda.credit.ds.iface.IDataSourceRequestor;
import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.api.iface.IExecutorFileService;
import com.wanda.credit.api.iface.IExecutorSecurityService;

@DataSourceClass(bindingDataSourceId = "ds_guozt_eduNew03")
public class GuoZTEducationCheckRequestor extends BaseGuoZTDataSourcesRequestor implements IDataSourceRequestor {
	private final Logger logger = LoggerFactory.getLogger(GuoZTEducationCheckRequestor.class);
	@Autowired
	private BaseGuoZTIncacheRequestor baseIncaheGZTService;
	@Autowired
	private IGuoZTDegreesService degreesService;
	@Autowired
	private IExecutorSecurityService synchExecutorService;
	private static final String STATUS_SUCCESS = "0";// 接口调用成功
	private static final String BIZ_CODE_1_SUCCESS = "1";// 查询成功，有记录
	private static final String BIZ_CODE_1_SUCCESS_NONE = "0";// 查询成功，无记录
	@Autowired
	private IPropertyEngine propertyEngine;

	@Override
	public Map<String, Object> request(String trade_id, DataSource ds) {
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		Map<String, Object> retdata = new HashMap<String, Object>();
		Map<String, Object> rets = new HashMap<String, Object>();
		DataSourceLogVO logObj = new DataSourceLogVO();
		Map<String, Object> reqparam = new HashMap<String, Object>();
		Map<String, Object> model_param = new HashMap<String, Object>();
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));// log请求时间
		logObj.setDs_id(ds.getId());
		logObj.setBiz_code3(trade_id);
		logObj.setReq_url(guoztUrl);
		logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
		logObj.setIncache("0");
		String initTag = Conts.TAG_SYS_ERROR;
		String college = null;
		String degree = null;
		String graduateYear = null;
		String studyStyle = null;
		String bjyjl = null;
		try {
			String name = (String) ParamUtil.findValue(ds.getParams_in(), "name"); // 姓名
			String cardNo = (String) ParamUtil.findValue(ds.getParams_in(), "cardNo"); // 身份证号码
			String reqcollege = (String) ParamUtil.findValue(ds.getParams_in(), "college"); // 院校名称（可不填）
			String reqdegree = (String) ParamUtil.findValue(ds.getParams_in(), "degree"); // 学历层次（可不填）
			String reqgraduateYear = (String) ParamUtil.findValue(ds.getParams_in(), "graduateYear"); // 毕业年份（可不填）
			String reqstudyStyle = (String) ParamUtil.findValue(ds.getParams_in(), "studyStyle"); // 学习形式（可不填）
			if (StringUtils.isNotBlank(reqcollege)) {
				college = reqcollege;
			} else {
				college = "";
			}
			if (StringUtils.isNotBlank(reqdegree)) {
				degree = reqdegree;
			} else {
				degree = "";
			}
			if (StringUtils.isNotBlank(reqgraduateYear)) {
				graduateYear = reqgraduateYear;
			} else {
				graduateYear = "";
			}
			if (StringUtils.isNotBlank(reqstudyStyle)) {
				studyStyle = reqstudyStyle;
			} else {
				studyStyle = "";
			}
			if (StringUtils.isNotEmpty(CardNoValidator.validate(cardNo))) {
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
				logObj.setState_msg("身份证号码不符合规范");
				logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
				reqparam.put("name", name);
				reqparam.put("cardNo", cardNo);
				logger.error("{} {}", prefix, logObj.getState_msg());
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_IDCARD_ERROR);
				rets.put(Conts.KEY_RET_MSG, "您输入的为无效身份证号码，请核对后重新输入!");
				rets.put(Conts.KEY_RET_TAG, new String[] { initTag });
				return rets;
			}
			String birthDays = "";
			Map<String, String> mapresult = baseIncaheGZTService.IdNOToSex(cardNo, prefix);
			birthDays = mapresult.get("birthDays");
			String params = trade_id + "," + name + "," + cardNo + "," + college + "," + degree + "," + graduateYear
					+ "," + studyStyle;
			reqparam.put("name", name);
			reqparam.put("cardNo", cardNo);
			String enCardNo = synchExecutorService.encrypt(cardNo);
			// 基本信息设置
			boolean inCache = false;
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
					logger.info("{} 查询模型获得数据:{} {}", new String[] { prefix, incache_flag,
							extractValueFromResult("INCACHE_NUM", model_param).toString() });
				}
			}
			if (inCache) {
				logger.info("{} 查询高学历核查数据......", new String[] { prefix });
				logObj.setIncache("1");
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
				Map<String, String> mapStr = new HashMap<String, String>();
				mapStr.put("trade_id", trade_id);
				mapStr.put("incache_flag", incache_flag);
				mapStr.put("name", name);
				mapStr.put("enCardNo", enCardNo);
				mapStr.put("cardNo", cardNo);
				mapStr.put("college", college);
				mapStr.put("degree", degree);
				mapStr.put("graduateYear", graduateYear);
				mapStr.put("studyStyle", studyStyle);
				mapStr.put("birthDays", birthDays);
				logger.info("{}请求开始{}",
						new String[] { college + "," + degree + "," + graduateYear + "," + studyStyle });
				rets = searchEduIncacheGuoZT(mapStr, date_num, logObj);
				if (rets == null) {
					inCache = false;
				} else {
					String[] initTags = (String[]) rets.get(Conts.KEY_RET_TAG);
					initTag = initTags[0];
					return rets;
				}
			}
			if (!inCache) {
				logObj.setIncache("0");
				logger.info("{}国政通高学历核查数据源采集开始......", new String[] { prefix });
				String respXml = singQurey(params, prefix,guoztUrl);
				logger.info("{}厂商返回xml信息！", new String[] { respXml });
				DataSourceLogEngineUtil.writeLog2LogSys(new LoggingEvent(trade_id, respXml, new String[] { trade_id }));
				if (respXml == null || respXml.length() == 0) {
					logger.error("{} 国政通高学历核查返回异常！", prefix);
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_EDU_CHECK_FAIL);
					rets.put(Conts.KEY_RET_MSG, "最高学历核查失败");
					rets.put(Conts.KEY_RET_TAG, new String[] { initTag });
					logObj.setState_msg("国政通高学历核查返回异常");
					return rets;
				}
				// 解析返回报文
				Document rspDoc = DocumentHelper.parseText(filtRspBody(respXml));
				DataSourceLogEngineUtil
						.writeLog2LogSys(new LoggingEvent(trade_id, rspDoc.toString(), new String[] { trade_id }));
				Node repDocMsg = rspDoc.selectSingleNode("//data/message");
				Node messStat = repDocMsg.selectSingleNode("status");
				Node messStatDesc = repDocMsg.selectSingleNode("value");
				if (STATUS_SUCCESS.equals(messStat.getStringValue())) {
					Node eduInfoNode = rspDoc.selectSingleNode("//data/xlCompareS/xlCompare");
					Node messageNode = eduInfoNode.selectSingleNode("message");
					Node codeNode = eduInfoNode.selectSingleNode("code");
					Node wybsNode = eduInfoNode.selectSingleNode("wybs");// 厂商返回流水号
					String status = codeNode.getStringValue();
					String checkMessage = messageNode.getStringValue();
					String checkWybs = wybsNode.getStringValue();// 厂商返回流水号
					logger.info("返回数据状态 {}", status + "++++" + checkMessage + "+++" + checkWybs);
					if (BIZ_CODE_1_SUCCESS.equals(status)) {
						Map<String, Object> degrees = makeDegreesBean(eduInfoNode);
						String graduate_chk_rst = (String) degrees.get("graduate_chk_rst");// 院校名称核查结果
						String educationdegree_chk_rst = (String) degrees.get("educationdegree_chk_rst");// 学历层次核查结果
						String graduatetime_chk_rst = (String) degrees.get("graduatetime_chk_rst");// 毕业年份核查结果
						String educationapproach_chk_rst = (String) degrees.get("educationapproach_chk_rst");// 学习形式核查结果
						String studyResult = (String) degrees.get("studyResult");// 毕业结论
						String studyType = (String) degrees.get("studyType");// 学历类别
						logger.info(" 返回参数:{} {} {} {} {} {} ",
								new String[] { graduate_chk_rst, educationdegree_chk_rst, educationapproach_chk_rst,
										graduatetime_chk_rst, studyResult, studyType });
						// 拼装放入数据库的数据
						Guozt_degrees_check_result newdegrees = new Guozt_degrees_check_result();
						newdegrees.setCardNo(enCardNo);
						newdegrees.setTrade_id(trade_id);
						newdegrees.setUserName(name);
						newdegrees.setBirthday(birthDays);
						if (StringUtils.isNotBlank(graduate_chk_rst)) {
							if ("1".equals(graduate_chk_rst)) {
								newdegrees.setGraduate(college);// 院校名称信息
								newdegrees.setGraduate_chk_rst(graduate_chk_rst);// 院校名称核查结果
							}
						}
						if (StringUtils.isNotBlank(educationdegree_chk_rst)) {
							if ("1".equals(educationdegree_chk_rst)) {
								newdegrees.setEducationDegree(degree);// 学历层次
								newdegrees.setEducationdegree_chk_rst(educationdegree_chk_rst);// 学历层次核查结果
							}
						}
						if (StringUtils.isNotBlank(graduatetime_chk_rst)) {
							if ("1".equals(graduatetime_chk_rst)) {
								newdegrees.setGraduateTime(graduateYear);// 毕业年份
								newdegrees.setGraduatetime_chk_rst(graduatetime_chk_rst);// 毕业年份核查结果
							}
						}
						if (StringUtils.isNotBlank(educationapproach_chk_rst)) {
							if ("1".equals(educationapproach_chk_rst)) {
								newdegrees.setDstudyStyle(studyStyle);// 学习形式
								newdegrees.setEducationapproach_chk_rst(educationapproach_chk_rst);// 学习形式核查结果
							}
						}
						if (StringUtils.isNotBlank(educationdegree_chk_rst)
								&& StringUtils.isNotBlank(educationapproach_chk_rst)) {
							if ("1".equals(educationdegree_chk_rst) && "1".equals(educationapproach_chk_rst)) {
								if (degree.equals("专科") || degree.equals("本科") || degree.equals("第二学士学位")) {
									newdegrees.setStudyStyle("普通");
								} else if (degree.equals("硕士") || degree.equals("博士")) {// 符合逻辑存放学历类别
									newdegrees.setStudyStyle("研究生");
								} else {
									newdegrees.setStudyStyle("其他");
								}
							}
						}
						if (StringUtils.isNotBlank(studyResult)) {
							newdegrees.setStudyResult(studyResult);// 符合逻辑存放毕业结论
						}
						newdegrees.setStatus1("0");
						newdegrees.setSourceid("03");
						newdegrees.setCreate_date(new Date());
						newdegrees.setCheckResult("1");
						logObj.setBiz_code1(status + "," + checkMessage);
						logger.info("{} 查询成功返回的学院信息{}", (String) newdegrees.getEducationapproach_chk_rst());
						degreesService.add(newdegrees);
						// status为1 查得结果有数据
						retdata.put("checkResult", "1");
						retdata.put("trade_id", trade_id);
						if (StringUtils.isNotBlank(college)) {
							if (StringUtils.isNotBlank(graduate_chk_rst)) {
								retdata.put("college", graduate_chk_rst);
							} else {
								retdata.put("college", "未查得");
							}
						}
						if (StringUtils.isNotBlank(degree)) {
							if (StringUtils.isNotBlank(educationdegree_chk_rst)) {
								retdata.put("degree", educationdegree_chk_rst);
							} else {
								retdata.put("degree", "未查得");
							}
						}
						if (StringUtils.isNotBlank(graduateYear)) {
							if (StringUtils.isNotBlank(graduatetime_chk_rst)) {
								retdata.put("graduateYear", graduatetime_chk_rst);
							} else {
								retdata.put("graduateYear", "未查得");
							}
						}

						if (StringUtils.isNotBlank(studyStyle)) {
							if (StringUtils.isNotBlank(educationapproach_chk_rst)) {
								retdata.put("studyStyle", educationapproach_chk_rst);
							} else {
								retdata.put("studyStyle", "未查得");
							}
						}

						if (StringUtils.isNotBlank(educationdegree_chk_rst)
								&& StringUtils.isNotBlank(educationapproach_chk_rst)) {
							if (educationdegree_chk_rst.equals("1") && educationapproach_chk_rst.equals("1")) {
								if (degree.equals("专科") || degree.equals("本科") || degree.equals("第二学士学位")) {
									retdata.put("studyType", "普通");
								} else if (degree.equals("硕士") || degree.equals("博士")) {
									retdata.put("studyType", "研究生");
								} else {
									retdata.put("studyType", "其他");
								}
							}
						}

						if (StringUtils.isNotBlank(studyResult)) {
							retdata.put("bjyjl", studyResult);
						}
						initTag = Conts.TAG_FOUND;
						logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
					} else if (BIZ_CODE_1_SUCCESS_NONE.equals(status)) {
						initTag = Conts.TAG_UNFOUND;
						logger.warn("{} 查询成功但未查询到高学历核查信息", trade_id);
						retdata.put("checkResult", "0");
						retdata.put("trade_id", trade_id);
						logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
						logObj.setBiz_code1(status + "," + checkMessage);
					} else {
						initTag = Conts.TAG_UNFOUND_OTHERS;
						logger.info(" 国政通高学历核查失败:{}", new String[] { prefix, messageNode.getStringValue() });
						rets.clear();
						rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_EDU_CHECK_FAIL);
						rets.put(Conts.KEY_RET_MSG, "高学历核查失败!");
						rets.put(Conts.KEY_RET_TAG, new String[] { initTag });
						logObj.setBiz_code1(messStat.getStringValue() + "," + messStatDesc.getStringValue());
						return rets;
					}
				} else {
					logger.info(" 国政通高学历核查数据源调用失败:{}", new String[] { prefix, messStatDesc.getStringValue() });
					rets.clear();
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_EDU_CHECK_FAIL);
					rets.put(Conts.KEY_RET_MSG, "高学历核查失败!");
					rets.put(Conts.KEY_RET_TAG, new String[] { initTag });
					logObj.setState_msg(messStatDesc.getStringValue());
					logObj.setBiz_code1(messStat.getStringValue() + "," + messStatDesc.getStringValue());
					return rets;
				}
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

	// 用于高学历核查（mafei-add 2018/1/10）
	public Map<String, Object> searchEduIncacheGuoZT(Map<String, String> mapStr, int date_num, DataSourceLogVO logObj) {
		final String prefix = mapStr.get("trade_id") + " " + Conts.KEY_SYS_AGENT_HEADER;
		Map<String, Object> rets = new HashMap<String, Object>();
		String initTag = Conts.TAG_SYS_ERROR;
		String trade_id = mapStr.get("trade_id");
		String name = mapStr.get("name");
		String cardNo = mapStr.get("cardNo");
		String college = null;
		String degree = null;
		String graduateYear = null;
		String studyStyle = null;
		Map<String, Object> retdatas = new HashMap<String, Object>();
		Map<String, Object> retdata = new HashMap<String, Object>();
		Map<String, Object> reqparam = new HashMap<String, Object>();
		reqparam.put("name", name);
		reqparam.put("cardNo", cardNo);
		Guozt_degrees_check_result degrees = new Guozt_degrees_check_result();
		if ("2".equals(mapStr.get("incache_flag"))) {
			degrees = degreesService.getDegreesByTradeIdEver(mapStr.get("name"), mapStr.get("enCardNo"));
		} else {
			degrees = degreesService.getDegreesByTradeIdDate(mapStr.get("name"), mapStr.get("enCardNo"), date_num * 30);
		}
		if (degrees == null) {
			return null;
		}
		try {
			if ("0".equals(degrees.getStatus1())) {
				logger.info("{}高学历核查数据！", prefix);
				initTag = Conts.TAG_INCACHE_FOUND;
				// 四个可选入参
				college = (String) mapStr.get("college");
				degree = (String) mapStr.get("degree");
				graduateYear = (String) mapStr.get("graduateYear");
				studyStyle = (String) mapStr.get("studyStyle");
				// 数据库中存在的对应可选入参值
				Long id = (Long) degrees.getId();// id
				String graduate = (String) degrees.getGraduate();// 院校名称信息
				String educationdegree = (String) degrees.getEducationDegree();// 学历层次信息
				String graduatetime = (String) degrees.getGraduateTime();// 毕业时间信息
				String dsstudystyle = (String) degrees.getDstudyStyle();// 学习形式
				logger.info("{}存在的可选入参数据{}",
						graduate + "++" + educationdegree + "++" + graduatetime + "++" + dsstudystyle);
				// 数据库对应的各项核查结果
				String checkResult = (String) degrees.getCheckResult();// 核查结果
				String graduate_chk_rst = (String) degrees.getGraduate_chk_rst();// 院校名称核查结果
				String educationdegree_chk_rst = (String) degrees.getEducationdegree_chk_rst();// 学历层次核查结果
				String graduatetime_chk_rst = (String) degrees.getGraduatetime_chk_rst();// 毕业年份核查结果
				String educationapproach_chk_rst = (String) degrees.getEducationapproach_chk_rst();// 学习形式核查结果
				String studyResult = (String) degrees.getStudyResult();// 毕业结论
				String studyType = (String) degrees.getStudyStyle();// 学历类别
				// 1.判断是否有值
				Map<String, Object> reqeuest = new HashMap<String, Object>();
				if (StringUtils.isNotBlank(studyResult)) {
					retdatas.put("bjyjl", studyResult);
				}
				if (StringUtils.isNotBlank(studyType)) {
					retdatas.put("studyType", studyType);
				}
				if (StringUtils.isNotBlank(checkResult)) {
					retdatas.put("checkResult", checkResult);
				}
				if (StringUtils.isNotBlank(college)) {
					if (StringUtils.isNotBlank(graduate)) {
						if (college.equals(graduate)) {
							retdatas.put("college", graduate_chk_rst);// 学院名称匹配
						} else {
							retdatas.put("college", "0");// 学院名称不匹配
						}
					} else {
						reqeuest.put("college", college);// 不存在学院名称请求数据源
					}
				}
				if (StringUtils.isNotBlank(degree)) {
					if (StringUtils.isNotBlank(educationdegree)) {
						if (degree.equals(educationdegree)) {
							retdatas.put("degree", educationdegree_chk_rst);// 学历层次匹配
						} else {
							retdatas.put("degree", "0");// 学历层次不匹配
						}
					} else {
						reqeuest.put("degree", degree);// 不存在学历层次请求数据源
					}
				}
				if (StringUtils.isNotBlank(graduateYear)) {
					if (StringUtils.isNotBlank(graduatetime)) {
						if (graduateYear.equals(graduatetime)) {
							retdatas.put("graduateYear", graduatetime_chk_rst);// 毕业年份匹配
						} else {
							retdatas.put("graduateYear", "0");// 毕业年份不匹配
						}
					} else {
						reqeuest.put("graduateYear", graduateYear);// 不存在毕业年份请求数据源
					}
				}
				if (StringUtils.isNotBlank(studyStyle)) {
					if (StringUtils.isNotBlank(dsstudystyle)) {
						if (studyStyle.equals(dsstudystyle)) {
							retdatas.put("studyStyle", educationapproach_chk_rst);// 学习形式匹配
						} else {
							retdatas.put("studyStyle", "0");// 学习形式匹配
						}
					} else {
						reqeuest.put("studyStyle", studyStyle);// 不存在学习形式请求数据源
					}
				}
				// 2.根据map去数据源请求
				Map<String, Object> ret = new HashMap<String, Object>();
				Map<String, Object> ret_da = new HashMap<String, Object>();
				if (reqeuest != null && reqeuest.size() > 0) {
					String college1 = null;
					String degree1 = null;
					String graduateYear1 = null;
					String studyStyle1 = null;
					String reqcollege = (String) reqeuest.get("college"); // 院校名称（可不填）
					String reqdegree = (String) reqeuest.get("degree"); // 学历层次（可不填）
					String reqgraduateYear = (String) reqeuest.get("graduateYear"); // 毕业年份（可不填）
					String reqstudyStyle = (String) reqeuest.get("studyStyle"); // 学习形式（可不填）
					if (StringUtils.isNotBlank(reqcollege)) {
						college1 = reqcollege;
					} else {
						college1 = "";
					}
					if (StringUtils.isNotBlank(reqdegree)) {
						degree1 = reqdegree;
					} else {
						degree1 = "";
					}
					if (StringUtils.isNotBlank(reqgraduateYear)) {
						graduateYear1 = reqgraduateYear;
					} else {
						graduateYear1 = "";
					}
					if (StringUtils.isNotBlank(reqstudyStyle)) {
						studyStyle1 = reqstudyStyle;
					} else {
						studyStyle1 = "";
					}
					String params = trade_id + "," + name + "," + cardNo + "," + college1 + "," + degree1 + ","
							+ graduateYear1 + "," + studyStyle1;
					logger.info("{}不存在的数据需再次请求数据源========={}", new String[] { trade_id + "+++" + params });
					String respXml = singQurey(params, prefix,guoztUrl);
					logger.info("{}再次请求厂商返回xml信息！", new String[] { respXml });
					if (respXml == null || respXml.length() == 0) {
						initTag = Conts.TAG_SYS_ERROR;
						logger.error("{} 国政通最高学历核查返回异常！", prefix);
						ret.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_EDU_CHECK_FAIL);
						ret.put("retmsg", "最高学历核查失败");
						ret.put(Conts.KEY_RET_TAG, new String[] { initTag });
						logObj.setState_msg("国政通最高学历核查返回异常");
						logObj.setIncache("0");
						// return ret;
					}
					// 解析返回报文
					Document rspDoc = DocumentHelper.parseText(filtRspBody(respXml));
					DataSourceLogEngineUtil
							.writeLog2LogSys(new LoggingEvent(trade_id, rspDoc.toString(), new String[] { trade_id }));
					Node repDocMsg = rspDoc.selectSingleNode("//data/message");
					Node messStat = repDocMsg.selectSingleNode("status");
					Node messStatDesc = repDocMsg.selectSingleNode("value");
					if ("0".equals(messStat.getStringValue())) {
						Node eduInfoNode = rspDoc.selectSingleNode("//data/xlCompareS/xlCompare");
						Node messageNode = eduInfoNode.selectSingleNode("message");
						Node codeNode = eduInfoNode.selectSingleNode("code");
						Node wybsNode = eduInfoNode.selectSingleNode("wybs");// 厂商返回流水号
						String status = codeNode.getStringValue();
						String checkMessage = messageNode.getStringValue();
						String checkWybs = wybsNode.getStringValue();// 厂商返回流水号
						logger.info("查询没有相关值再次查询数据源返回数据状态 {}", status + "++++" + checkMessage + "+++" + checkWybs);
						if ("1".equals(status)) {
							logObj.setBiz_code1(status + "," + checkMessage);
							Map<String, Object> newdegrees = makeDegreesBean(eduInfoNode);
							String rspgraduate_chk_rst = (String) newdegrees.get("graduate_chk_rst");// 院校名称核查结果
							String rspeducationdegree_chk_rst = (String) newdegrees.get("educationdegree_chk_rst");// 学历层次核查结果
							String rspgraduatetime_chk_rst = (String) newdegrees.get("graduatetime_chk_rst");// 毕业年份核查结果
							String rspeducationapproach_chk_rst = (String) newdegrees.get("educationapproach_chk_rst");// 学习形式核查结果
							String rspstudyResult = (String) newdegrees.get("studyResult");// 毕业结论
							String rspstudyType = (String) newdegrees.get("studyType");// 学历类别
							logger.info(" 返回参数:{} {} {} {} {} {} ",
									new String[] { graduate_chk_rst, educationdegree_chk_rst, educationapproach_chk_rst,
											graduatetime_chk_rst, studyResult, studyType });
							Guozt_degrees_check_result redegrees = new Guozt_degrees_check_result();
							if (StringUtils.isNotBlank(rspgraduate_chk_rst)) {
								if ("1".equals(rspgraduate_chk_rst)) {
									redegrees.setGraduate(college1);// 院校名称信息
									redegrees.setGraduate_chk_rst(rspgraduate_chk_rst);// 院校名称核查结果
								}
							}
							if (StringUtils.isNotBlank(rspeducationdegree_chk_rst)) {
								if ("1".equals(rspeducationdegree_chk_rst)) {
									redegrees.setEducationDegree(degree1);// 学历层次
									redegrees.setEducationdegree_chk_rst(rspeducationdegree_chk_rst);// 学历层次核查结果
								}
							}
							if (StringUtils.isNotBlank(rspgraduatetime_chk_rst)) {
								if ("1".equals(rspgraduatetime_chk_rst)) {
									redegrees.setGraduateTime(graduateYear1);// 毕业年份
									redegrees.setGraduatetime_chk_rst(rspgraduatetime_chk_rst);// 毕业年份核查结果
								}
							}
							if (StringUtils.isNotBlank(rspeducationapproach_chk_rst)) {
								if ("1".equals(rspeducationapproach_chk_rst)) {
									redegrees.setDstudyStyle(studyStyle1);// 学习形式
									redegrees.setEducationapproach_chk_rst(rspeducationapproach_chk_rst);// 学习形式核查结果
								}
							}

							if (StringUtils.isNotBlank(degree) && StringUtils.isNotBlank(studyStyle)) {
								if (StringUtils.isNotBlank(rspstudyType)) {
									redegrees.setStudyStyle(rspstudyType);// 符合逻辑存放学历类别
								} else {
									if (StringUtils.isNotBlank((String) retdatas.get("degree"))
											&& StringUtils.isNotBlank(rspeducationapproach_chk_rst)) {
										if (retdatas.get("degree").equals("1")
												&& rspeducationapproach_chk_rst.equals("1")) {
											if (degree.equals("专科") || degree.equals("本科") || degree.equals("第二学士学位")) {
												ret_da.put("studyType", "普通");
												redegrees.setStudyStyle("普通");
											} else if (degree.equals("硕士") || degree.equals("博士")) {
												ret_da.put("studyType", "研究生");
												redegrees.setStudyStyle("研究生");
											} else {
												ret_da.put("studyType", "其他");
												redegrees.setStudyStyle("其他");
											}
										}
									} else if (StringUtils.isNotBlank((String) retdatas.get("studyStyle"))
											&& StringUtils.isNotBlank(rspeducationdegree_chk_rst)) {
										if (retdatas.get("studyStyle").equals("1")
												&& rspeducationdegree_chk_rst.equals("1")) {
											if (degree.equals("专科") || degree.equals("本科") || degree.equals("第二学士学位")) {
												ret_da.put("studyType", "普通");
												redegrees.setStudyStyle("普通");
											} else if (degree.equals("硕士") || degree.equals("博士")) {
												ret_da.put("studyType", "研究生");
												redegrees.setStudyStyle("研究生");
											} else {
												ret_da.put("studyType", "其他");
												redegrees.setStudyStyle("其他");
											}
										}
									}
								}
							}

//							if (StringUtils.isNotBlank(rspstudyType)) {
//								redegrees.setStudyStyle(rspstudyType);// 符合逻辑存放学历类别
//							}

							if (StringUtils.isNotBlank(rspstudyResult)) {
								redegrees.setStudyResult(rspstudyResult);// 符合逻辑存放毕业结论
							}
							// 3.更新数据库编写update方法
							redegrees.setId(id);
							degreesService.update(name, cardNo, redegrees);
							if (StringUtils.isNotBlank(college1)) {
								if (StringUtils.isNotBlank(rspgraduate_chk_rst)) {
									ret_da.put("college", rspgraduate_chk_rst);
								} else {
									ret_da.put("college", "未查得");
								}
							}
							if (StringUtils.isNotBlank(degree1)) {
								if (StringUtils.isNotBlank(rspeducationdegree_chk_rst)) {
									ret_da.put("degree", rspeducationdegree_chk_rst);
								} else {
									ret_da.put("degree", "未查得");
								}
							}
							if (StringUtils.isNotBlank(graduateYear1)) {
								if (StringUtils.isNotBlank(rspgraduatetime_chk_rst)) {
									ret_da.put("graduateYear", rspgraduatetime_chk_rst);
								} else {
									ret_da.put("graduateYear", "未查得");
								}
							}
							if (StringUtils.isNotBlank(studyStyle1)) {
								if (StringUtils.isNotBlank(rspeducationapproach_chk_rst)) {
									ret_da.put("studyStyle", rspeducationapproach_chk_rst);
								} else {
									ret_da.put("studyStyle", "未查得");
								}
							}
//							if (StringUtils.isNotBlank(rspeducationdegree_chk_rst)
//									&& StringUtils.isNotBlank(rspeducationapproach_chk_rst)) {
//								if (rspeducationdegree_chk_rst.equals("1")
//										&& rspeducationapproach_chk_rst.equals("1")) {
//									if (StringUtils.isNotBlank(rspstudyType)) {
//										ret_da.put("studyType", rspstudyType);
//									}
//								}
//							}
							if (StringUtils.isNotBlank(rspstudyResult)) {
								ret_da.put("bjyjl", rspstudyResult);
							}
							if (ret_da != null && ret_da.size() > 0) {
								ret_da.put("checkResult", "1");
							}
							ret_da.put("trade_id", trade_id);
							initTag = Conts.TAG_FOUND;
							logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
							logObj.setIncache("0");
						} else if ("0".equals(status)) {
							initTag = Conts.TAG_UNFOUND;
							logger.warn("{} 再次查询成功但未查询到最高学历核查信息", trade_id);
							ret_da.put("checkResult", "0");
							ret_da.put("trade_id", trade_id);
							logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
							logObj.setBiz_code1(status + "," + checkMessage);
							logObj.setIncache("0");
						} else {
							initTag = Conts.TAG_UNFOUND_OTHERS;
							logger.info(" 国政通学历核查失败:{}", new String[] { prefix, messageNode.getStringValue() });
							ret.clear();
							ret.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_EDU_CHECK_FAIL);
							ret.put("retmsg", "高学历核查失败!");
							ret.put(Conts.KEY_RET_TAG, new String[] { initTag });
							logObj.setBiz_code1(messStat.getStringValue() + "," + messStatDesc.getStringValue());
							logObj.setIncache("0");
							// return ret;
						}
					} else {
						logger.info(" 国政通高学历核查数据源调用失败:{}", new String[] { prefix, messStatDesc.getStringValue() });
						ret.clear();
						initTag = Conts.TAG_SYS_ERROR;
						ret.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_EDU_CHECK_FAIL);
						ret.put("retmsg", "高学历核查失败!");
						ret.put(Conts.KEY_RET_TAG, new String[] { initTag });
						logObj.setState_msg(messStatDesc.getStringValue());
						logObj.setBiz_code1(messStat.getStringValue() + "," + messStatDesc.getStringValue());
						// return ret;
					}
					ret.put(Conts.KEY_RET_TAG, new String[] { initTag });
					ret.put("retdata", ret_da);
					logger.info(" {}国政通高学历核查数据源学院信息:{}", new String[] { (String) ret_da.get("college") });
					ret.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
					ret.put("retmsg", "采集成功");
					// return ret;
				}
				if (StringUtils.isNotBlank((String) ret.get("retmsg"))) {
					if (!ret.get("retmsg").equals("采集成功")) {
						rets.clear();
						rets.putAll(ret);
						return rets;
					}
				}
				Map<String, Object> lastretdata = (Map<String, Object>) ret.get("retdata");
				if (lastretdata != null && lastretdata.size() > 0) {
					initTag = Conts.TAG_FOUND;
					retdatas.putAll(lastretdata);
				}
				logger.info("{} 查看是否拼装成功&&&&&&&&：{}",
						retdatas.get("college") + "%%%" + retdatas.get("degree") + "%%%" + retdatas.get("graduateYear")
								+ "%%%" + retdatas.get("studyStyle") + "%%%" + retdatas.get("checkResult") + "%%%"
								+ retdatas.get("bjyjl"));
				// 4.判断毕业结论和学历类别？5.返回数据
				if (StringUtils.isNotBlank((String) retdatas.get("checkResult"))) {
					retdata.put("checkResult", (String) retdatas.get("checkResult"));
				}
				if (StringUtils.isNotBlank(college)) {// 返回学院信息
					if (StringUtils.isNotBlank((String) retdatas.get("college"))) {
						retdata.put("college", (String) retdatas.get("college"));
					} else {
						retdata.put("college", "未查得");
					}
				}
				if (StringUtils.isNotBlank(degree)) {// 返回学历层次信息
					if (StringUtils.isNotBlank((String) retdatas.get("degree"))) {
						retdata.put("degree", (String) retdatas.get("degree"));
					} else {
						retdata.put("degree", "未查得");
					}
				}
				if (StringUtils.isNotBlank(graduateYear)) {// 返回毕业时间信息
					if (StringUtils.isNotBlank((String) retdatas.get("graduateYear"))) {
						retdata.put("graduateYear", (String) retdatas.get("graduateYear"));
					} else {
						retdata.put("graduateYear", "未查得");
					}
				}
				if (StringUtils.isNotBlank(studyStyle)) {// 返回学习形式信息
					if (StringUtils.isNotBlank((String) retdatas.get("studyStyle"))) {
						retdata.put("studyStyle", (String) retdatas.get("studyStyle"));
					} else {
						retdata.put("studyStyle", "未查得");
					}
				}
				if (StringUtils.isNotBlank(degree) && StringUtils.isNotBlank(studyStyle)) {
					if ((StringUtils.isNotBlank((String) retdatas.get("degree")))
							&& (StringUtils.isNotBlank((String) retdatas.get("studyStyle")))) {
						if (("1".equals((String) retdatas.get("degree")))
								&& ("1".equals((String) retdatas.get("studyStyle")))) {
							if (degree.equals("专科") || degree.equals("本科") || degree.equals("第二学士学位")) {
								retdata.put("studyType", "普通");
							} else if (degree.equals("硕士") || degree.equals("博士")) {
								retdata.put("studyType", "研究生");
							} else {
								retdata.put("studyType", "其他");
							}
						}
					}
				}
				if (StringUtils.isBlank(college) && StringUtils.isBlank(degree) && StringUtils.isBlank(graduateYear)
						&& StringUtils.isBlank(studyStyle)) {
					if (StringUtils.isBlank((String) retdatas.get("bjyjl"))) {
						retdata.put("bjyjl", (String) retdatas.get("bjyjl"));
					}
				}
				String chkcollege = (String) retdata.get("college");
				String chkdegree = (String) retdata.get("degree");
				String chkgraduateYear = (String) retdata.get("graduateYear");
				String chkstudyStyle = (String) retdata.get("studyStyle");
				String chkbjyjl = (String) retdatas.get("bjyjl");
				// 四个可选参数college degree graduateYear studyStyle
				Boolean result = checkResult(college, degree, graduateYear, studyStyle, chkcollege, chkdegree,
						chkgraduateYear, chkstudyStyle);
				if (result) {
					if (StringUtils.isNotBlank(chkbjyjl))
						retdata.put("bjyjl", chkbjyjl);
					logger.info("{} 国政通学历核查毕业结论!", chkbjyjl);
				}
				retdata.put("trade_id", trade_id);
				if (retdata != null && retdata.size() > 0) {
					rets.put(Conts.KEY_RET_TAG, new String[] { initTag });
					rets.put(Conts.KEY_RET_DATA, retdata);
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
					rets.put(Conts.KEY_RET_MSG, "采集成功");
					return rets;
				}
			} else {
				logger.info("{} 国政通学历核查失败!", prefix);
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_EDU_CHECK_FAIL);
				rets.put(Conts.KEY_RET_MSG, "学历核查失败!");
				rets.put(Conts.KEY_RET_TAG, new String[] { initTag });
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
				return rets;
			}
		} catch (Exception ex) {
			initTag = Conts.TAG_SYS_ERROR;
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, "查询时出现异常! 详细信息:" + ex.getMessage());
			logger.error("{} 查询时出现异常：{}", prefix, ExceptionUtil.getTrace(ex));
			if (ExceptionUtil.isTimeoutException(ex)) {
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_TIMEOUT);
				initTag = Conts.TAG_SYS_TIMEOUT;
			} else {
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
				logObj.setState_msg("查询时出现异常! 详细信息:" + ex.getMessage());
			}
			rets.put(Conts.KEY_RET_TAG, new String[] { initTag });
		}
		return rets;
	}

	private Boolean checkResult(String reqco, String reqde, String reqgr, String reqst, String rspco, String rspde,
			String rspgr, String rspst) {
		if (StringUtils.isNotBlank(reqco)) {
			if (!"1".equals(rspco)) {
				return false;
			}
		}
		if (StringUtils.isNotBlank(reqde)) {
			if (!"1".equals(rspde)) {
				return false;
			}
		}
		if (StringUtils.isNotBlank(reqgr)) {
			if (!"1".equals(rspgr)) {
				return false;
			}
		}
		if (StringUtils.isNotBlank(reqst)) {
			if (!"1".equals(rspst)) {
				return false;
			}
		}
		return true;
	}

	private Map<String, Object> makeDegreesBean(Node Node) {
		Map<String, Object> degrees = new HashMap<String, Object>();
		String graduate_chk_rst = (String) Node.selectSingleNode("yxmcCheckrs").getStringValue();// 院校名称
		String educationdegree_chk_rst = (String) Node.selectSingleNode("ccCheckrs").getStringValue();// 学历层次
		String graduatetime_chk_rst = (String) Node.selectSingleNode("byrqCheckrs").getStringValue();// 毕业年份
		String educationapproach_chk_rst = (String) Node.selectSingleNode("xxxsCheckrs").getStringValue();// 学习形式
		String studyResult = (String) Node.selectSingleNode("bjyjl").getStringValue();// 毕结业结论
		String studyType = (String) Node.selectSingleNode("xllb").getStringValue();// 学历类别
		logger.info(" 国政通学历核查厂商返回参数:{} {} {} {} {} {} ", new String[] { graduate_chk_rst, educationdegree_chk_rst,
				educationapproach_chk_rst, graduatetime_chk_rst, studyResult, studyType });
		if (StringUtils.isNotBlank(graduate_chk_rst)) {
			degrees.put("graduate_chk_rst", graduate_chk_rst);
		}
		if (StringUtils.isNotBlank(educationdegree_chk_rst)) {
			degrees.put("educationdegree_chk_rst", educationdegree_chk_rst);
		}
		if (StringUtils.isNotBlank(graduatetime_chk_rst)) {
			degrees.put("graduatetime_chk_rst", graduatetime_chk_rst);
		}
		if (StringUtils.isNotBlank(educationapproach_chk_rst)) {
			degrees.put("educationapproach_chk_rst", educationapproach_chk_rst);
		}
		if (StringUtils.isNotBlank(studyResult)) {
			degrees.put("studyResult", studyResult);
		}
		if (StringUtils.isNotBlank(studyType)) {
			degrees.put("studyType", studyType);
		}
		return degrees;

	}
}
