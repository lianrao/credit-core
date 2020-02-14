package com.wanda.credit.ds.client.juxinli;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.api.iface.IExecutorSecurityService;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.client.juxinli.bean.ebusi.MobileEBusiDataSource;
import com.wanda.credit.ds.client.juxinli.bean.mobile.SubmitCollReq;
import com.wanda.credit.ds.client.juxinli.bean.mobile.SubmitCollRes;
import com.wanda.credit.ds.client.juxinli.service.IJXLEBusiMobileSubmitService;
import com.wanda.credit.ds.client.juxinli.util.JXLConst;
import com.wanda.credit.ds.dao.domain.juxinli.apply.ApplyAccountPojo;
import com.wanda.credit.ds.dao.domain.juxinli.apply.ApplyBasicInfoPojo;
import com.wanda.credit.ds.dao.domain.juxinli.apply.ApplyNextDataSourcePojo;
import com.wanda.credit.ds.dao.domain.juxinli.trade.JXLPublicLoadLogPojo;
import com.wanda.credit.ds.dao.iface.juxinli.apply.IJXLNextDatasourceService;
import com.wanda.credit.ds.dao.iface.juxinli.trade.IJXLPublicLoadLogService;
import com.wanda.credit.ds.iface.IDataSourceRequestor;
/**
 * 聚信立提交采集请求并获取回执信息
 * @author xiaobin.hou
 *
 */
@DataSourceClass(bindingDataSourceId="ds_jxlEbusiSubmitReq")
public class JXLMobileEBusiSubmitRequestor extends
		BasicJuXinLiDataSourceRequestor implements IDataSourceRequestor {
	
	private final static Logger logger = LoggerFactory.getLogger(JXLMobileEBusiSubmitRequestor.class);
		
	private String httpsPostUrl;
	private int timeOut;
	@Autowired
	private IJXLNextDatasourceService jxlNextDatasourceService;
	@Autowired
	private IJXLEBusiMobileSubmitService jxlEBusiMobileSubmitService;
	@Autowired
	private IJXLPublicLoadLogService jxlPublicLoadLogService;
	@Autowired
	private IExecutorSecurityService synchExecutorService;
	
	

	@Override
	public Map<String, Object> request(String trade_id, DataSource ds) {
		long startTime = System.currentTimeMillis();
		String remark = "collecting";
		JsonObject resJsonObj = null;
		String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		//组织返回对象
		Map<String, Object> rets = new HashMap<String, Object>();
		TreeMap<String, Object> retdata = new TreeMap<String, Object>();
		//初始化标签
//		Set<String> tagsSet = new HashSet<String>();
		List<String> tagsSet = new ArrayList<String>();
		tagsSet.add(Conts.TAG_SYS_ERROR);
		ApplyAccountPojo accountPojo = new ApplyAccountPojo();
		//交易日志信息数据
		DataSourceLogVO logObj = new DataSourceLogVO();
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));
		logObj.setDs_id(ds.getId());
		logObj.setReq_url(httpsPostUrl);
		logObj.setIncache("0");
		logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
		logObj.setState_msg("交易失败");
		
		//TODO
//		String jxlContent = "";
		Date nowTime = new Date();
		
		logger.info("{} 聚信立提交采集请求开始tradeId=" + trade_id);
		//获取请求参数
		String requestId = ParamUtil.findValue(ds.getParams_in(), paramIds[0]).toString();
		String account = ParamUtil.findValue(ds.getParams_in(), paramIds[1]).toString();
		String password = ParamUtil.findValue(ds.getParams_in(), paramIds[2]).toString();
		String website = ParamUtil.findValue(ds.getParams_in(), paramIds[3]).toString();
		String captcha = "";
		String type = "";
		String queryPwd = "";		
		Object captchaObj = ParamUtil.findValue(ds.getParams_in(), "captcha");
		Object typeObj = ParamUtil.findValue(ds.getParams_in(), "captcha_type");	
		Object queryPwdObj = ParamUtil.findValue(ds.getParams_in(), "query_pwd");
		if (!StringUtil.isEmpty(captchaObj)) {
			captcha = captchaObj.toString();
		}		
		if (!StringUtil.isEmpty(typeObj)) {
			type = typeObj.toString().toUpperCase();
		}
		if (!StringUtil.isEmpty(queryPwdObj)) {
			queryPwd = queryPwdObj.toString().toUpperCase();
		}
		try {
			// 敏感数据加密处理
			String encAccount = "";
			String encCaptha = "";
			String encPassword = "";
			String encQueryPwd = "";

			if (!StringUtil.isEmpty(account)) {
				encAccount = synchExecutorService.encrypt(account);
			}
			if (!StringUtil.isEmpty(captcha)) {
				encCaptha = synchExecutorService.encrypt(captcha);
			}
			if (!StringUtil.isEmpty(password)) {
				encPassword = synchExecutorService.encrypt(password);
			}
			if (!StringUtil.isEmpty(queryPwd)) {
				encQueryPwd = synchExecutorService.encrypt(queryPwd);
			}
			/* 保存请求参数 */
			saveParamIn(encAccount, encPassword, website, encCaptha, type,encQueryPwd, trade_id, logObj, requestId);

			// 拼接账户信息对象，方便数据落地
			accountPojo.setRequestId(requestId);
			accountPojo.setCrt_time(nowTime);
			accountPojo.setUpd_time(nowTime);
			accountPojo.setAccount(encAccount);
			accountPojo.setPassword(encPassword);
			accountPojo.setCaptcha(encCaptha);
			accountPojo.setCaptcha_type(type);
			accountPojo.setWebsite(website);
			// 校验参数
			boolean valiRes = validateCapthcaInfo(captcha, type, queryPwd,prefix);
			if (!valiRes) {
				logger.error("{} 参数格式错误 {}", prefix);
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS,
						CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_CAPTCHATYPE_ERROR);
				rets.put(Conts.KEY_RET_MSG,
						CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_CAPTCHATYPE_ERROR.getRet_msg());
				logObj.setState_msg("参数校验失败");
				return rets;
			}

			if (StringUtil.isEmpty(type)) {
				type = "";
			} else if (JXLConst.MOBILE_RESEND_CAPTCHA.equals(type)) {
				type = JXLConst.JXL_RESEND_CAPTCHA;
			} else if (JXLConst.MOBILE_SUBMIT_QUERY_PWD.equals(type)) {
				type = JXLConst.JXL_SUBMIT_QUERY_PWD;
			} else if (JXLConst.MOBILE_SUBMIT_CAPTCHA.equals(type)) {
				type = JXLConst.JXL_SUBMIT_CAPTCHA;
			}

			// 通过requestId获取对应的Token
			ApplyBasicInfoPojo applyBasicInfo = requestId2Token(requestId);
			// 判断该requestId是否存在
			if (applyBasicInfo == null) {
				logger.info("{} 聚信立提交采集请求传入的requestId不存在" + requestId, prefix);
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS,
						CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_REQUESTID_NOTEXSIT);
				rets.put(Conts.KEY_RET_MSG, "request_id不存在");
				
				logObj.setState_msg("交易失败：request_id不存在");
				return rets;
			}
			// 判断该requestId是否有效
			Date tokenUpdTime = applyBasicInfo.getUpd_time();
			long timeDif = System.currentTimeMillis() - tokenUpdTime.getTime();
			if (timeDif >= 10 * 60 * 1000) {
				logger.info("{} 聚信立提交采集请求传入的requestId超过有效期(10 min) {}" , prefix, requestId);

				rets.clear();
				rets.put(Conts.KEY_RET_STATUS,
						CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_REQUESTID_NOUSE);
				rets.put(Conts.KEY_RET_MSG,
						CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_REQUESTID_NOUSE
								.getRet_msg());
				logObj.setState_msg("交易失败-request_id失效");
				return rets;
			}
			try {
				// 判断请求的数据源是否合法
				ApplyNextDataSourcePojo queryNextDs = jxlEBusiMobileSubmitService.queryNextDs(requestId);
				if (queryNextDs == null) {
					logger.info("{} 聚信立提交采集请求该数据源[" + website
							+ "]已成功提交采集请求,不能重复提交" + requestId, prefix);
					rets.clear();
					rets.put(Conts.KEY_RET_STATUS,
							CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_REPEAT_SUBMIT);
					rets.put(Conts.KEY_RET_MSG, "重复的采集请求");
					
					logObj.setState_msg("交易失败-重复的采集请求");
					return rets;
				} else {
					String submitingWebsite = queryNextDs.getWebsite();
					if (submitingWebsite != null
							&& submitingWebsite.equals(website)) {
						logger.info("{} 合法的采集请求", prefix);
					} else {
						logger.info("{} 该采集请求不合法，需要采集的是" + submitingWebsite
								+ ";用户提交的是" + website, prefix);
						rets.clear();
						rets.put(Conts.KEY_RET_STATUS,
								CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_ILLEGAL_SUBMIT);
						rets.put(Conts.KEY_RET_MSG, "非法的采集请求");
						
						logObj.setState_msg("交易失败-采集请求不合法");
						return rets;
					}
				}

				// 判断是否为重复提交
				boolean repeatSubmit = jxlEBusiMobileSubmitService
						.isRepeatSubmit(requestId, website);
				if (repeatSubmit) {
					logger.info("{} 聚信立提交采集请求该数据源[" + website
							+ "]已成功提交采集请求,不能重复提交" + requestId, prefix);
					rets.clear();
					rets.put(Conts.KEY_RET_STATUS,
							CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_REPEAT_SUBMIT);
					rets.put(Conts.KEY_RET_MSG, "重复的采集请求");
					
					logObj.setState_msg("交易失败-重复的采集请求");
					return rets;
				}
			} catch (Exception e) {
				e.printStackTrace();
				logger.info("{} 连接本地数据库判断当前采集请求异常，直接连接聚信立提交采集请求 {}",prefix ,e.getMessage());
			}
			String token = applyBasicInfo.getToken();
			
			logger.info("{} 聚信立提交采集请求remark初始值为：{}", prefix, remark);
			// 拼装请求参数并转化成JSON
			String reqJsonString = buildReqJson(token, account, password,
					captcha, type, queryPwd, website, encAccount, encPassword,
					encQueryPwd, prefix);
			// http请求聚信立提交采集请求
			long postStartTime = System.currentTimeMillis();
			resJsonObj = submitCollReq(httpsPostUrl, reqJsonString,timeOut * 1000, prefix);
			long PostTime = System.currentTimeMillis() - postStartTime;
			logger.info("{} 向聚信立发送提交采集请求耗时为（ms）" + PostTime, prefix);
			logger.info("{} 向聚信立提交采集请求，EBUSISUBMITRES201604聚信立返回结果为:{}", prefix ,resJsonObj);

			/*
			 * //TODO 测试用，后续删除 开始 String result =
			 * "{\"success\":true,\"data\":{\"type\":\"CONTROL\",\"content\":\"开始采集行为数据\",\"process_code\":10008,\"finish\":true}}"
			 * ; resJsonObj = (JsonObject) new JsonParser().parse(result); //结束
			 */
			tagsSet.clear();
			tagsSet.add(Conts.TAG_TST_FAIL);
			
			if (resJsonObj != null && resJsonObj.get(JXLConst.FLAG_SUCCESS) != null) {
				
				String isSuccess = resJsonObj.get(JXLConst.FLAG_SUCCESS).getAsString();
				logger.info("{} 聚信立提交采集请求返回success对应的值为:{}" , prefix , isSuccess);
				if ("true".equals(isSuccess)) {
					JsonElement dataElement = resJsonObj.get(JXLConst.FLAG_DATA);
					if (dataElement != null) {
						logger.info("{} 聚信立提交采集请求成功，开始解析data节点数据", prefix);
						Gson gson = new Gson();
						SubmitCollRes res = gson.fromJson(dataElement,SubmitCollRes.class);

						String resposeType = res.getType();
						int process_code = res.getProcess_code();
						String jxlContent = res.getContent();
						
						logger.info("{} 聚信立提交采集请求返回响应类型为 :{}" , prefix ,resposeType);
						logger.info("{} 聚信立提交采集请求返回流程码为:{}" ,prefix , process_code);

						// 将征信返回码转化成内部对应码
						logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
						logObj.setState_msg("交易失败");
						logObj.setBiz_code2(resposeType);
						logObj.setBiz_code1(process_code + "");
						// 拼装账户数据用于后续保存
						accountPojo.setContent(jxlContent);
						accountPojo.setProcess_code(process_code + "");
						accountPojo.setResponse_type(res.getType());
						accountPojo.setFinish(res.getFinish() + "");

						if ("CONTROL".equals(resposeType)) {
							tagsSet.clear();
							tagsSet.add(Conts.TAG_TST_PROCESS);
							logger.info("{} 聚信立提交采集请求响应结果为控制类型，可继续交互", prefix);
							retdata.put(JXLConst.SUBMIT_RESPONSE_TYPE,JXLConst.SUBMIT_RESPONSE_NORMAL);
							//根据不同返回码处理不同的业务
							switch (process_code) {
							case JXLConst.PROCESS_CODE_SUCC:
								// 将biz_code1置为success，用于计费
								logObj.setBiz_code1("success");
								
								logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
								logObj.setState_msg("交易成功");

								logger.info("{} 聚信立提交采集请求成功", prefix);
								Map<String, String> jsonMap = new HashMap<String, String>();
								if (res.getFinish()) {
									
									tagsSet.clear();
									tagsSet.add(Conts.TAG_TST_SUCCESS);
									
									logger.info("{} 所有的采集请求完成，稍后可以获取报告信息,remark值为:{}",prefix,remark);
									retdata.put(JXLConst.SUBMIT_FINISHED,JXLConst.SUBMIT_FINISHED_YES);
									// 完成采集请求，将T_DS_JXL_ORIG_APPLY_INFO表中的REMARK置为finished
									remark = "finished";
									// 更新T_DS_JXL_ORIG_RESP_RESULT表中响应记录的success标识
									jxlEBusiMobileSubmitService.updateNextDSByReqIdAndWebSite(
													requestId, website, "true");

									logger.info("{} 采集流程结束，将相关数据插入数据轮询记录表",prefix);
									JXLPublicLoadLogPojo loadLog = new JXLPublicLoadLogPojo();
									loadLog.setRequestId(requestId);
									loadLog.setReqid_type("1");
									loadLog.setLoad_result("0");
									Date finishDate = new Date();
									loadLog.setCrt_time(finishDate);
									loadLog.setUpd_time(finishDate);
									jxlPublicLoadLogService.save(loadLog);

								} else {
									MobileEBusiDataSource nextDS = res.getNext_datasource();
									logger.info("{} 还有数据源信息需要提交,下一个数据源信息为:{}", prefix , nextDS);

									/* 保存下一个需要提交的dataSource BEGIN */
									ApplyNextDataSourcePojo nextDsPojo = dataSourceBean2Pojo(nextDS);
									if (nextDsPojo != null) {
										nextDsPojo.setRequestId(requestId);
										nextDsPojo.setSuccess("false");
										jxlNextDatasourceService.add(nextDsPojo);
									}
									/* 保存下一个需要提交的dataSource END */

									if (nextDS != null) {
										jsonMap.put(JXLConst.WEBSITE_EN_NAME,
												nextDS.getWebsite());
										jsonMap.put(JXLConst.WEBSITE_CN_NAME,
												nextDS.getName());
										jsonMap.put(JXLConst.CATEGORY_EN_NAME,
												nextDS.getCategory());
										jsonMap.put(JXLConst.CATEGORY_CN_NAME,
												nextDS.getCategory_name());
									}
									retdata.put(JXLConst.SUBMIT_FINISHED,JXLConst.SUBMIT_FINISHED_NO);

								}

								retdata.put(JXLConst.EBUSI_NEXT_DATASOURCE,JSONObject.toJSONString(jsonMap));
								rets.put(Conts.KEY_RET_DATA, retdata);
								rets.put(Conts.KEY_RET_STATUS,CRSStatusEnum.STATUS_SUCCESS);
								rets.put(Conts.KEY_RET_MSG, "提交采集请求成功");
								break;
							case 10001:
								logger.info("{} 四川移动用户需要再次提交短信验证码,remark值为:{}", prefix , remark);
								rets.put(Conts.KEY_RET_STATUS,
										CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_NEED_CAPTCHA_AGAIN);
								rets.put(Conts.KEY_RET_MSG,
										CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_NEED_CAPTCHA_AGAIN.getRet_msg());
								break;
							case 10002:
								logger.info("{} 聚信立提交采集请求需要输入动态验证码,remark值为:{}", prefix ,remark);
								rets.put(
										Conts.KEY_RET_STATUS,
										CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_NEED_CAPTCHA);
								rets.put(Conts.KEY_RET_MSG, "请输入动态验证码");
								break;
							case 10003:
								logger.info("{} 聚信立提交采集请求密码错误,remark值为:{}", prefix ,remark);
								rets.put(Conts.KEY_RET_STATUS,
										CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_PAW_ERROR);
								rets.put(Conts.KEY_RET_MSG, "密码错误,请重新输入");
								break;
							case 10004:
								logger.info("{} 聚信立提交采集请求动态密码错误,remark值为:{}", prefix ,remark);
								rets.put(Conts.KEY_RET_STATUS,
										CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_PAW_ERROR);
								rets.put(Conts.KEY_RET_MSG, "动态验证码错误,请重新输入");
								break;
							case 10006:
								// TODO
								logger.info("{} 北京移动-短信验证码失效系统已自动重新下发,remark值为:{}", prefix ,remark);
								rets.put(Conts.KEY_RET_STATUS,
										CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_CAPTCHA_NOUSE);
								rets.put(
										Conts.KEY_RET_MSG,
										CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_CAPTCHA_NOUSE.getRet_msg());
								break;
							case 10007:
								logger.info("{} 聚信立提交采集请求简单或初始密码无法登录,remark值为:{}", prefix ,remark);
								rets.put(Conts.KEY_RET_STATUS,
										CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_PAW_ERROR);
								rets.put(Conts.KEY_RET_MSG, "简单密码或初始密码无法登录");
								break;
							case 10009:
								logger.info("{} 聚信立提交采集请求联系人填写不符合需求或手机号码未实名认证,remark值为:{}", prefix ,remark);
								rets.put(Conts.KEY_RET_STATUS,
										CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_PAW_SIMPLE);
								rets.put(Conts.KEY_RET_MSG, "验证信息错误");
								break;
							case 30000:
								logger.info("{} 聚信立提交采集网络或运营商异常无法下发短信验证码从而无法登录,remark值为:{}", prefix ,remark);
								remark = "failed";
								rets.put(Conts.KEY_RET_STATUS,
										CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_WEBSITE_ERROR);
								rets.put(Conts.KEY_RET_MSG, "待查询网站异常，无法获取数据");
								break;
							case 0:
								logger.info("{} 聚信立提交采集请求运营商网站异常或者服务更新升级导致不可用,remark值为:{}", prefix ,remark);
								rets.put(Conts.KEY_RET_STATUS,
										CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_WEBSITE_ERROR);
								rets.put(Conts.KEY_RET_MSG, "待查询网站异常，无法获取数据");
								break;
							case 10017:
								logger.info("{} 聚信立提交采集吉林电信需要用户自己发送指令获取验证码,remark值为:{}", prefix ,remark);
								rets.put(Conts.KEY_RET_STATUS,
										CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_JILIN_GET_CAPTCHA);
								rets.put(Conts.KEY_RET_MSG,
										CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_JILIN_GET_CAPTCHA.getRet_msg());
								break;
							case 10018:
								logger.info(
										"{} 聚信立提交采集请求吉林电信用户验证码失效重新获取,remark值为:{}", prefix ,remark);
								rets.put(Conts.KEY_RET_STATUS,
										CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_JILIN_GET_CAPTCHA_AGAIN);
								rets.put(Conts.KEY_RET_MSG,
										CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_JILIN_GET_CAPTCHA_AGAIN.getRet_msg());
								break;
							case 10022:
								logger.info("{} 北京移动-请输入查询密码,remark值为:{}" ,prefix ,remark);
								rets.put(Conts.KEY_RET_STATUS,
										CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_NEED_QUERY_PWD);
								rets.put(Conts.KEY_RET_MSG,
										CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_NEED_QUERY_PWD.getRet_msg());
								break;
							case 10023:
								logger.info("{} 北京移动-查询密码错误请重新输入,remark值为:{}", prefix ,remark);
								rets.put(Conts.KEY_RET_STATUS,
										CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_QUERY_PWD_ERROR);
								rets.put(Conts.KEY_RET_MSG,
										CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_QUERY_PWD_ERROR.getRet_msg());
								break;
							default:
								rets.put(Conts.KEY_RET_STATUS,CRSStatusEnum.STATUS_FAILED);
								rets.put(Conts.KEY_RET_MSG, "提交采集请求失败,请稍后重试");
								break;
							}

						} else if ("ERROR".equals(resposeType)) {
							tagsSet.clear();
							tagsSet.add(Conts.TAG_TST_FAIL);
							logger.info("{} 聚信立提交采集请求响应结果为错误类型，建议重新开始流程,remark值为:{}", prefix ,remark);
							retdata.put(JXLConst.SUBMIT_RESPONSE_TYPE,
									JXLConst.SUBMIT_RESPONSE_ERROR);
							rets.put(Conts.KEY_RET_STATUS,
									CRSStatusEnum.STATUS_FAILED);

							if (jxlContent != null && jxlContent.length() > 0) {
								rets.put(Conts.KEY_RET_MSG, jxlContent);
							} else {
								rets.put(Conts.KEY_RET_MSG, "提交请求失败");
							}
						} else if ("RUNNING".equals(resposeType)) {
							
							
							
							logger.info(
									"{} 聚信立提交采集请求响应结果为正在运行，建议多尝试几次或联系厂商分析问题原因,remark值为:{}", prefix ,remark);
							retdata.put(JXLConst.SUBMIT_RESPONSE_TYPE,
									JXLConst.SUBMIT_RESPONSE_RETRY);
							rets.put(Conts.KEY_RET_STATUS,
									CRSStatusEnum.STATUS_FAILED);

							if (jxlContent != null && jxlContent.length() > 0) {
								rets.put(Conts.KEY_RET_MSG, jxlContent);
							} else {
								rets.put(Conts.KEY_RET_MSG, "提交请求失败");
							}
						} else {
							
							logger.info("{} 聚信立提交采集请求响应结果type对应值为:{}", prefix,resposeType);
							retdata.put(JXLConst.SUBMIT_RESPONSE_TYPE,
									JXLConst.SUBMIT_RESPONSE_ERROR);
							rets.put(Conts.KEY_RET_STATUS,
									CRSStatusEnum.STATUS_FAILED);

							if (jxlContent != null && jxlContent.length() > 0) {
								rets.put(Conts.KEY_RET_MSG, jxlContent);
							} else {
								rets.put(Conts.KEY_RET_MSG, "提交请求失败");
							}
						}

					} else {
						logger.info("{} 提交采集请求返回data节点为空" ,prefix);
						rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED);
						rets.put(Conts.KEY_RET_MSG, "提交请求失败");
						
						logObj.setState_msg("交易失败-data节点为空");
						
						return rets;
					}

				} else {
					//TODO
					tagsSet.clear();
					tagsSet.add(Conts.TAG_SYS_TIMEOUT);
					
					logger.info("{} 聚信立提交采集请求返回结果中success对应结果为:{}" , prefix ,isSuccess);
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED);
					rets.put(Conts.KEY_RET_MSG, "提交请求失败");
					
					logObj.setState_msg("交易失败-success节点不为true");
					
					//
					JsonElement msgEle = resJsonObj.get("message");
					if(msgEle != null){
						String msgString = msgEle.getAsString();
						if(msgString.contains("token")){
							msgString = msgString.replaceAll("token", "request_id");
						}
						
						rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED);
						rets.put(Conts.KEY_RET_MSG, msgString);
					}
					//
					return rets;
				}
			} else {
				tagsSet.clear();
				tagsSet.add(Conts.TAG_SYS_TIMEOUT);
				
				logger.info("{} 聚信立提交采集请求返回结果为" + resJsonObj, prefix);
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_TIMEOUT);
				rets.put(Conts.KEY_RET_MSG, "服务请求超时");
				
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_TIMEOUT);	//初始值-失败
				logObj.setState_msg("交易超时");
				return rets;
			}

		}catch (Exception e) { 
			tagsSet.clear();
			tagsSet.add(Conts.TAG_SYS_ERROR);
			
			logger.info("{} 提交采集请求交易处理异常 {}" , prefix , e.getMessage());

		}finally{
			
			logger.info("{} 聚信立提交采集请求结束，开始保存数据并更新当前状态" ,prefix);
			try {
				jxlEBusiMobileSubmitService.saveSubmitAccount(accountPojo,requestId,remark);
			} catch (Exception e) {
				logger.error("{} 聚信立提交采集请求将账户信息和返回结果保存到数据库中异常:{}" ,prefix ,e.getMessage());
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED);
				rets.put(Conts.KEY_RET_MSG, "提交采集请求失败");
				tagsSet.clear();
				tagsSet.add(Conts.TAG_SYS_ERROR);
			}
			rets.put(Conts.KEY_RET_TAG,tagsSet.toArray(new String[tagsSet.size()]));
			//保存日志信息
			logObj.setTag(StringUtils.join(tagsSet, ";"));
			logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
			DataSourceLogEngineUtil.writeLog(trade_id,logObj);
			//保存交易记录
			String retCode = "";
			if (rets.containsKey(Conts.KEY_RET_STATUS)) {
				CRSStatusEnum retstatus = CRSStatusEnum.valueOf(rets.get(Conts.KEY_RET_STATUS).toString());
				retCode = retstatus.getRet_sub_code();
			}
			saveTradeInfo(trade_id, JXLConst.TF_SUB_ACCOUT, retCode, website, null, null, requestId);
			
		}
		
		long tradeTime = System.currentTimeMillis() - startTime;
		logger.info("{} 提交采集请求总共耗时时间为（ms）：{}",prefix , tradeTime );
		
		return rets;
	}

	
	/**
	 * @param token
	 * @param account
	 * @param password
	 * @param captcha
	 * @param type
	 * @param queryPwd
	 * @return
	 */
	private String buildReqJson(String token, String account, String password,
			String captcha, String type, String queryPwd, String website,
			String encAccount, String encPassword, String encQueryPwd,String prefix) {
		SubmitCollReq collReq = new SubmitCollReq();
		collReq.setToken(token);
		collReq.setAccount(encAccount);
		collReq.setPassword(encPassword);
		collReq.setCaptcha(captcha);
		collReq.setType(type);
		collReq.setWebsite(website);
		collReq.setQueryPwd(encQueryPwd);
		logger.info("{} 准备向向聚信立提交采集，请求的json报文数据为:{} ", prefix,
				JSONObject.toJSONString(collReq));
		collReq.setPassword(password);
		collReq.setQueryPwd(queryPwd);
		collReq.setAccount(account);
		
		String jsonString = JSONObject.toJSONString(collReq);
		return jsonString;
	}


	/**
	 * @param captcha
	 * @param type
	 * @param queryPwd
	 * @return
	 */
	private boolean validateCapthcaInfo(String captcha, String type,
			String queryPwd,String prefix) {
		boolean validate = true;
		try{
			//captcha和queryPwd不能同时有值
			if (!StringUtil.isEmpty(captcha) && !StringUtil.isEmpty(queryPwd)) {
				logger.info("{} 短信验证码和查询密码同时有值" , prefix);
				return false;
			}
			if (!StringUtil.isEmpty(queryPwd)) {
				if (!JXLConst.MOBILE_SUBMIT_QUERY_PWD.equals(type)) {
					logger.info("{} 传入参数query_pwd不为空，captcha_type必须为SUBMIT_QUERY_PWD",prefix);
					return false;
				}
			}
			if (StringUtil.isEmpty(captcha)) {
				//短信验证码为空，则type可以为空或者为resend
				if (StringUtil.isEmpty(type)) {
					type = "";
				}else if(JXLConst.MOBILE_RESEND_CAPTCHA.equals(type)){
					type = JXLConst.JXL_RESEND_CAPTCHA;
				}else if(JXLConst.MOBILE_SUBMIT_QUERY_PWD.equals(type)){
					type = JXLConst.JXL_SUBMIT_QUERY_PWD;
				}else{
					logger.info("{} 传入参数captcha为空，captcha_type为空或者RESEND",prefix);
					return false;
				}
			}else{
				//短信验证码不为空，则type必须有值
				if (JXLConst.MOBILE_SUBMIT_CAPTCHA.equals(type)) {
					type = JXLConst.JXL_SUBMIT_CAPTCHA;
				}else if(JXLConst.MOBILE_RESEND_CAPTCHA.equals(type)){
					type = JXLConst.JXL_RESEND_CAPTCHA;
				}else {
					logger.info("{} 传入参数captcha不为空，captcha_type为SUBMIT或者RESEND",prefix);
					return false;
				}
			}
		}catch(Exception e){
			logger.error("{} 参数格式错误 {}" , prefix , e.getMessage());	
			return false;
		}
		return validate;
	}


	/**
	 * 保存入参信息
	 * @param encAccount
	 * @param encPassword
	 * @param website
	 * @param encCaptha
	 * @param type
	 * @param encQueryPwd
	 * @param trade_id
	 * @param logObj
	 * @param requestId
	 */
	private void saveParamIn(String encAccount, String encPassword,
			String website, String encCaptha, String type, String encQueryPwd,
			String trade_id, DataSourceLogVO logObj, String requestId) {
		try {
			StringBuffer paramBf = new StringBuffer();
			paramBf.append("request_id=").append(requestId)
					.append(";account=").append(encAccount)
					.append(";password=").append(encPassword)
					.append(";website_en_name=").append(website)
					.append(";captcha=").append(encCaptha)
					.append("queryPwd=").append(encQueryPwd)
					.append(";captcha_type=").append(type);
			logger.info("{} 聚信立提交采集请求传入参数为" + paramBf.toString(), trade_id);

			Map<String, Object> paramIn = new HashMap<String, Object>();
			paramIn.put("account", encAccount);
			paramIn.put("website", website);
			paramIn.put("captcha", encCaptha);
			paramIn.put("captcha_type", type);
			paramIn.put("query_pwd", encQueryPwd);
			DataSourceLogEngineUtil.writeParamIn(trade_id, paramIn, logObj);
		} catch (Exception e) {
			logger.error("{} 将输入参数保存到数据库异常，跳过该步骤", trade_id);
		}

	}


	public void saveLog(String trade_id,DataSourceLogVO logObj , String bizCode1 ,String bizCode2,String bizCode3){
		try {				
			logObj.setRsp_time(new Timestamp(System.currentTimeMillis()));
			logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
			logObj.setState_msg("交易失败");
			logObj.setBiz_code1(bizCode1);
			logObj.setBiz_code2(bizCode2);
			logObj.setBiz_code3(bizCode3);
			DataSourceLogEngineUtil.writeLog(trade_id,logObj);
		} catch (Exception ex) {
			logger.error("{} 交易数据落地失败 " + ex.getMessage() ,trade_id);
		}
	}
	
	public String getHttpsPostUrl() {
		return httpsPostUrl;
	}

	public void setHttpsPostUrl(String httpsPostUrl) {
		this.httpsPostUrl = httpsPostUrl;
	}

	public int getTimeOut() {
		return timeOut;
	}

	public void setTimeOut(int timeOut) {
		this.timeOut = timeOut;
	}

}
