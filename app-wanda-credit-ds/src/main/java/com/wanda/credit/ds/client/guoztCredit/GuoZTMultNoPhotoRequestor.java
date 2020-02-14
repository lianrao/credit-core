package com.wanda.credit.ds.client.guoztCredit;

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

import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.CardNoValidator;
import com.wanda.credit.base.util.ExceptionUtil;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.log.ds.vo.LoggingEvent;
import com.wanda.credit.common.props.DynamicConfigLoader;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.dao.domain.Guozt_Mult_Check_Result;
import com.wanda.credit.ds.dao.iface.IGuoZTMultCheckService;
import com.wanda.credit.ds.iface.IDataSourceRequestor;
import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.api.enums.FileArea;
import com.wanda.credit.api.enums.FileType;
import com.wanda.credit.api.iface.IExecutorFileService;
import com.wanda.credit.api.iface.IExecutorSecurityService;
@DataSourceClass(bindingDataSourceId="ds_guozt_mult")
public class GuoZTMultNoPhotoRequestor extends BaseGuoZTDataSourcesRequestor
		implements IDataSourceRequestor {
	private final  Logger logger = LoggerFactory.getLogger(GuoZTMultNoPhotoRequestor.class);
	private final  String CHANNEL_NO = "01";
	private final  String POLICE_PHOTO_NO = "POLICE_PHOTO_NOTEXISTS";
	private final  String POLICE_STATUS_SUSSES = "一致";
	private final  String POLICE_STATUS_FAIL = "不一致";
	private final  String STATUS_CHECK_EQUAL = "00";
	private final  String STATUS_CHECK_NO = "01";
	private final  String STATUS_CHECK_NULL = "02";
	@Autowired
	private IGuoZTMultCheckService guoZTMultCheckService;
	@Autowired
	private IExecutorFileService fileService;
	@Autowired
	private IExecutorSecurityService synchExecutorService;
	@Autowired
	public IPropertyEngine propertyEngine;
	@Override
	public Map<String, Object> request(String trade_id, DataSource ds){
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		String police_url = propertyEngine.readById("ds_guozt_police_url");
		TreeMap<String, Object> retdata = new TreeMap<String, Object>();
		Map<String, Object> rets = null;
		DataSourceLogVO logObj = new DataSourceLogVO();
		Map<String, Object> reqparam = new HashMap<String, Object>();
		String respXml = "";
		String resource_tag = Conts.TAG_SYS_ERROR;
		logObj.setReq_time(new Timestamp(System.currentTimeMillis()));//log请求时间
		try{	
			int incache_days = Integer.valueOf(propertyEngine.readById("ds_police_incacheTime"));//公安数据时间(天)
			logObj.setDs_id("ds_guozt_mult");
			if ("1".equals(DynamicConfigLoader.get("ds_guozt_request_method"))) {//1走最新client调用方式
				logObj.setReq_url(police_url);
			}else {
				logObj.setReq_url(guoztUrl);
			}
			logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
			rets = new HashMap<String, Object>();
	 		String name = ParamUtil.findValue(ds.getParams_in(), paramIds[0]).toString();   //姓名 
			String cardNo = ParamUtil.findValue(ds.getParams_in(), paramIds[1]).toString(); //身份证号码
			String flag = ParamUtil.findValue(ds.getParams_in(), paramIds[2]).toString(); //是否带照片,01带照片,02不带照片
			String acct_id = ParamUtil.findValue(ds.getParams_in(), paramIds[3]).toString();//账户号
			String params = name+","+cardNo+","+trade_id;
			String enCardNo = synchExecutorService.encrypt(cardNo);
			reqparam.put("name", name);
			reqparam.put("cardNo", cardNo);
			if(StringUtils.isNotEmpty(CardNoValidator.validate(cardNo)) || StringUtil.isEmpty(name)){
				logger.warn("{}入参格式不符合要求!", new String[] { prefix });
				logObj.setIncache("1");
				logObj.setBiz_code1(DXCODE_NOCHECK);
				logObj.setState_msg("身份证号码不符合规范");
				rets.clear();
				rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_JUXINLI_IDCARD_ERROR);
				rets.put(Conts.KEY_RET_MSG, "您输入的为无效身份证号码，请核对后重新输入!");
				rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
				return rets;
			}
			retdata.put("PersonId", cardNo);
			retdata.put("PersonName", name);
			retdata.put("server_idx", "03");
			
			String cardNo_check = null;
			String name_check = null;
			String image = null;
			String fileId = null;
			
			boolean ds_incache = dsIncache(acct_id,ds.getId());
			if(!guoZTMultCheckService.inCachedMult(name, enCardNo,incache_days) || ds_incache){
				logObj.setIncache("0");
				logger.info("{}国账通多项数据源采集开始......", new String[] { prefix});
				if ("1".equals(DynamicConfigLoader.get("ds_guozt_request_method"))) {//1走最新client调用方式
					respXml = httpClientQuery(params,prefix,police_url);
				}else {
					respXml = singQurey(params,prefix,police_url);
				}
//				logger.info("{}国账通多项数据源返回数据：{}", new String[] { prefix,respXml});
				DataSourceLogEngineUtil.writeLog2LogSys(new LoggingEvent(trade_id, respXml, new String[] { trade_id }));
				if(respXml == null || respXml.length()==0){
					logger.error("{} 国账通查询失败！",prefix);
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_POLICE_EXCEPTION);
					rets.put(Conts.KEY_RET_MSG, "公安数据源厂商返回异常!");	
					rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
					return rets;
				}else{
					logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
					Guozt_Mult_Check_Result guozt_multcheck = new Guozt_Mult_Check_Result();
					guozt_multcheck.setTrade_id(trade_id);
					guozt_multcheck.setCardno(enCardNo);
					guozt_multcheck.setName(name);
					guozt_multcheck.setStatus(STATUS_CHECK_NULL);
					//解析返回报文
					Document rspDoc = DocumentHelper.parseText(filtRspBody(respXml));
					Node repDocMsg = rspDoc.selectSingleNode("//data/message");
					Element repDocMsgEle = (Element) repDocMsg;
					Element messStat = repDocMsgEle.element("status");
					Element messStatDesc = repDocMsgEle.element("value");					
					logObj.setBiz_code2(messStat.getStringValue());	
					logObj.setState_msg(messStatDesc.getStringValue());
					logger.info("{}国政通返回信息码:{},返回信息:{}",prefix,
							messStat.getStringValue(),messStatDesc.getStringValue() );				
					if("0".equals(messStat.getStringValue())){
						//开始异步插入照片准备
						List<Map<String,String>> file_list = new ArrayList<Map<String,String>>();
						Map<String,String> fileMap = new HashMap<String,String>();
						fileMap.put("table_name", "CPDB_DS.T_DS_GUOZT_MULT_RESULT");
						fileMap.put("table_col", "image_file");
						file_list.add(fileMap);
						
						Node nameIdInfos = rspDoc.selectSingleNode("//data/nameIdInfos");
						Node nameIdInfo = nameIdInfos.selectSingleNode("nameIdInfo");
						String wybs = nameIdInfo.selectSingleNode("wybs").getStringValue();
						String code = nameIdInfo.selectSingleNode("code").getStringValue();
						String message = nameIdInfo.selectSingleNode("message").getStringValue();
						logObj.setBiz_code3(code);
						//查询成功
						if("00".equals(code)){//一致							
							logObj.setBiz_code1(DXCODE_EQUAL);
							resource_tag = Conts.TAG_MATCH;
							String xb = nameIdInfo.selectSingleNode("xb").getStringValue();//性別
							String mz = nameIdInfo.selectSingleNode("mz").getStringValue();//民族
							String csrq = nameIdInfo.selectSingleNode("csrq").getStringValue();//出生日期
							String ssssxq = nameIdInfo.selectSingleNode("ssssxq").getStringValue();//所属省市县区
							String zz = nameIdInfo.selectSingleNode("zz").getStringValue();//住所
							image = nameIdInfo.selectSingleNode("xp").getStringValue();//相片
							if(StringUtils.isNotEmpty(xb)){
								retdata.put("xb", xb);
								guozt_multcheck.setSex(xb);
							}else{
								retdata.put("xb", "");
							}
							if(StringUtils.isNotEmpty(csrq)){
								retdata.put("csrq", csrq);
								guozt_multcheck.setBirth_day(csrq);
							}else{
								retdata.put("csrq", "");
							}
							if(StringUtils.isNotEmpty(zz)){
								retdata.put("zz", zz);
								guozt_multcheck.setAddress(zz);
							}else{
								retdata.put("zz", "");
							}
							if(StringUtils.isNotEmpty(ssssxq)){
								retdata.put("city", ssssxq);
								guozt_multcheck.setCity(ssssxq);
							}else{
								retdata.put("city", "");
							}
							if(StringUtils.isNotEmpty(mz)){
								retdata.put("mz", mz);
								guozt_multcheck.setNations(mz);
							}else{
								retdata.put("mz", "");
							}
							retdata.put("whcd", "");
							retdata.put("resultGmsfhm", POLICE_STATUS_SUSSES);
							retdata.put("resultXm", POLICE_STATUS_SUSSES);
							if(StringUtils.isNotEmpty(image)){
								logger.info("{}照片异步上传征信存储开始...", new String[] { prefix});
								fileService.uploadAsync(image,trade_id, FileType.JPG, FileArea.DS, file_list);
								logger.info("{}照片异步上传征信存储结束!", new String[] { prefix});	
								
								retdata.put("xp", image);
								retdata.put("xp_id", fileId);
								rets.clear();
								rets.put(Conts.KEY_RET_DATA, retdata);
								rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
								rets.put(Conts.KEY_RET_MSG, "采集成功!");
							}else{
								logger.warn("{}国政通数据源厂商返回申请人户籍照片不存在", new String[] { prefix});
								if(flag.equals(CHANNEL_NO)){//判断是否走无照片输出通道
									rets.clear();
									rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_POLICE_PHOTO_NOTEXISTS);
									rets.put(Conts.KEY_RET_MSG, "公安数据源厂商返回申请人户籍照片不存在!");
								}else{
									retdata.put("xp", "");
									retdata.put("xp_id", "");	
									rets.clear();
									rets.put(Conts.KEY_RET_DATA, retdata);
									rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
									rets.put(Conts.KEY_RET_MSG, "采集成功!");
								}
								guozt_multcheck.setError_mesg(POLICE_PHOTO_NO);
							}							
							guozt_multcheck.setStatus(STATUS_CHECK_EQUAL);
							guozt_multcheck.setCard_check(POLICE_STATUS_SUSSES);
							guozt_multcheck.setName_check(POLICE_STATUS_SUSSES);
						}else if("02".equals(code)){//不一致
							logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
							logObj.setBiz_code1(DXCODE_NOEQUAL);
							resource_tag = Conts.TAG_UNMATCH;
							cardNo_check = POLICE_STATUS_SUSSES;
							name_check = POLICE_STATUS_FAIL;
							guozt_multcheck.setCard_check(cardNo_check);
							guozt_multcheck.setName_check(name_check);
							guozt_multcheck.setStatus(STATUS_CHECK_NO);
							rets.clear();
							rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_POLICE_INVALID);
							rets.put(Conts.KEY_RET_MSG, "公安数据源厂商返回身份证号码，姓名校验不一致!");
						}else if("01".equals(code)){//库无
							logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
							logObj.setBiz_code1(DXCODE_NOEXIST);
							resource_tag = Conts.TAG_UNFOUND;
							guozt_multcheck.setStatus(STATUS_CHECK_NULL);
							guozt_multcheck.setError_mesg(message);
							rets.clear();
							rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_POLICE_NOTEXISTS);
							rets.put(Conts.KEY_RET_MSG, message);						
						}else{
							logger.info("{}国政通返回其他信息!", new String[] { prefix});
							logObj.setBiz_code1(DXCODE_NOCHECK);
							rets.clear();
							rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_PARAM_INVALID);
							rets.put(Conts.KEY_RET_MSG, "输入参数不正确!");	
							rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
							return rets;
						}
						guozt_multcheck.setWybs_number(wybs);
						guoZTMultCheckService.add(guozt_multcheck);
						rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
						return rets;
					}else {
						logger.info("{}国政通返回其他信息!", new String[] { prefix});
						rets.clear();
						rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_POLICE_EXCEPTION);
						rets.put(Conts.KEY_RET_MSG, "数据源处理时异常! 详细信息:"+messStatDesc);
						rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
						return rets;
					}	
				}
			}else{
				Guozt_Mult_Check_Result inCached  = guoZTMultCheckService.inCached(name, enCardNo);
				logObj.setIncache("1");
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
				logObj.setBiz_code1(DXCODE_EQUAL);
				resource_tag = Conts.TAG_INCACHE_MATCH;
				logger.info("{}数据中存在此公安查询数据,开始国政通渠道查询！", new String[] { prefix});
				cardNo_check = inCached.getCard_check();
				name_check  = inCached.getName_check();
				String photoId = inCached.getImage_file();
				String status = inCached.getStatus();
				if(status.equals(STATUS_CHECK_EQUAL)){
					if(StringUtils.isNotEmpty(inCached.getSex())){
						retdata.put("xb", inCached.getSex().toString());
					}else{
						retdata.put("xb", "");
					}
					if(StringUtils.isNotEmpty(inCached.getBirth_day())){
						retdata.put("csrq", inCached.getBirth_day().toString());
					}else{
						retdata.put("csrq", "");
					}
					retdata.put("whcd", "");
					if(StringUtils.isNotEmpty(inCached.getAddress())){
						retdata.put("zz", inCached.getAddress().toString());
					}else{
						retdata.put("zz", "");
					}
					if(StringUtils.isNotEmpty(inCached.getCity())){
						retdata.put("city", inCached.getCity().toString());
					}else{
						retdata.put("city", "");
					}
					if(StringUtils.isNotEmpty(inCached.getNations())){
						retdata.put("mz", inCached.getNations().toString());
					}else{
						retdata.put("mz", "");
					}
				}
				if(status.equals(STATUS_CHECK_NULL)){
					logObj.setBiz_code1(DXCODE_NOEXIST);
					String errMsg = inCached.getError_mesg();
					rets.clear();
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_POLICE_NOTEXISTS);
					rets.put(Conts.KEY_RET_MSG, errMsg);
					rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
					return rets;
				}
				if("不一致".equals(cardNo_check) || 
						"不一致".equals(name_check)){
					logObj.setBiz_code1(DXCODE_NOEQUAL);
					resource_tag = Conts.TAG_INCACHE_UNMATCH;
					rets.clear();
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_POLICE_INVALID);
					rets.put(Conts.KEY_RET_MSG, "公安数据源厂商返回身份证号码，姓名校验不一致!");
					rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
					return rets;
				}				
				if(StringUtils.isNotEmpty(photoId)){
					logger.info("{} 从征信存储根据图片ID获取照片开始...", prefix);
					String photoImages = fileService.download(photoId,trade_id);// 根据ID从征信存储区下载照片
					if(StringUtil.isEmpty(photoImages)){
						logger.warn("{} 从征信存储根据图片ID获取照片失败", prefix);
					}else{
						image = photoImages;
						logger.info("{} 从征信存储根据图片ID获取照片成功,照片id为 : {}", prefix,photoId);
					}					
				}else{
					if(!flag.equals(CHANNEL_NO)){
						image="";
						photoId="";
					}					
				}					
				fileId = photoId;
				if(flag.equals(CHANNEL_NO)){
					if(image==null || "".equals(image)){
						rets.clear();
						rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_POLICE_PHOTO_NOTEXISTS);
						rets.put(Conts.KEY_RET_MSG, "公安数据源厂商返回申请人户籍照片不存在!");
						rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
						return rets;
					}	
				}				
			}
			
			retdata.put("resultGmsfhm", cardNo_check);
			retdata.put("resultXm", name_check);
			retdata.put("xp", image);
			retdata.put("xp_id", fileId);
			rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
			rets.put(Conts.KEY_RET_DATA, retdata);
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
			rets.put(Conts.KEY_RET_MSG, "采集成功!");
		}catch(Exception ex){
			resource_tag = Conts.TAG_SYS_ERROR;
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, "数据源处理时异常! 详细信息:"+ex.getMessage());
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
			DataSourceLogEngineUtil.writeLog(trade_id, logObj);
			DataSourceLogEngineUtil.writeParamIn(trade_id, reqparam, logObj);
		}
		return rets;
	}
}
