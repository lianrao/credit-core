package com.wanda.credit.ds.client.guoztCredit;

import java.sql.Timestamp;
import java.util.HashMap;
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

import com.wanda.credit.api.dto.DataSource;
import com.wanda.credit.api.enums.FileArea;
import com.wanda.credit.api.enums.FileType;
import com.wanda.credit.api.iface.IExecutorSecurityService;
import com.wanda.credit.base.Conts;
import com.wanda.credit.base.annotation.DataSourceClass;
import com.wanda.credit.base.enums.CRSStatusEnum;
import com.wanda.credit.base.util.CardNoValidator;
import com.wanda.credit.base.util.ExceptionUtil;
import com.wanda.credit.base.util.StringUtil;
import com.wanda.credit.common.file.FileEngine;
import com.wanda.credit.common.log.ds.DataSourceLogEngineUtil;
import com.wanda.credit.common.log.ds.vo.DataSourceLogVO;
import com.wanda.credit.common.log.ds.vo.LoggingEvent;
import com.wanda.credit.common.props.DynamicConfigLoader;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.common.util.ParamUtil;
import com.wanda.credit.ds.dao.domain.Guozt_Mult_Check_Result;
import com.wanda.credit.ds.dao.domain.Nciic_Check_Result;
import com.wanda.credit.ds.dao.iface.IGuoZTMultCheckService;
import com.wanda.credit.ds.dao.iface.INciicCheckService;
import com.wanda.credit.ds.iface.IDataSourceRequestor;
@DataSourceClass(bindingDataSourceId="ds_guozt_mult_3_0")
public class GuoZTMultSimpleRequestor_2_0 extends BaseGuoZTDataSourcesRequestor
implements IDataSourceRequestor {
	private final  Logger logger = LoggerFactory.getLogger(GuoZTMultSimpleRequestor_2_0.class);
	private final  String CHANNEL_NO = "01";/**带照片*/
	private final  String POLICE_PHOTO_NO = "POLICE_PHOTO_NOTEXISTS";
	private final  String POLICE_STATUS_SUSSES = "一致";
	private final  String POLICE_STATUS_FAIL = "不一致";
	private final  String STATUS_CHECK_EQUAL = "00"; /**一致*/
	private final  String STATUS_CHECK_NO = "01";/**不一致*/
	private final  String STATUS_CHECK_NULL = "02";/**库中无此号*/
	private final  String SOURCE_ID = "04";
	@Autowired
	private INciicCheckService nciicCheckService;
	@Autowired
	private IGuoZTMultCheckService guoZTMultCheckService;
	@Autowired
	private FileEngine fileEngines;
	@Autowired
	private IExecutorSecurityService synchExecutorService;
	@Autowired
	public IPropertyEngine propertyEngine;
	@Override
	public Map<String, Object> request(String trade_id, DataSource ds){
		final String prefix = trade_id + " " + Conts.KEY_SYS_AGENT_HEADER;
		logger.info("{}国账通多项数据源调用开始...", prefix);
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
			if ("1".equals(DynamicConfigLoader.get("ds_guozt_request_method"))) {//1走最新client调用方式
				logObj.setReq_url(police_url);
				logger.info("{}国账通调用连接:{}", prefix,police_url);
			}else {
				logObj.setReq_url(guoztUrl);
				logger.info("{}国账通调用连接:{}", prefix,guoztUrl);
			}
			logObj.setDs_id(ds.getId());
			
			logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
			rets = new HashMap<String, Object>();
	 		String name = ParamUtil.findValue(ds.getParams_in(), paramIds[0]).toString();   //姓名 
			String cardNo = ParamUtil.findValue(ds.getParams_in(), paramIds[1]).toString(); //身份证号码
			String flag = ParamUtil.findValue(ds.getParams_in(), paramIds[2]).toString(); //是否忽略照片01
			String acct_id = ParamUtil.findValue(ds.getParams_in(), paramIds[3]).toString();//账户号
			String params = name+","+cardNo+","+trade_id;
			String enCardNo = synchExecutorService.encrypt(cardNo);
			reqparam.put("name", name);
			reqparam.put("cardNo", cardNo);
			if(StringUtils.isNotEmpty(CardNoValidator.validate(cardNo)) || StringUtil.isEmpty(name)){
				logger.warn("{}入参格式不符合要求!", prefix);
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
			retdata.put("server_idx", "04");
			
			String cardNo_check = null;
			String name_check = null;
			String image = null;
			String fileId = null;
			logger.info("{} 查询是否存在数据!", prefix);
			boolean ds_incache = dsIncache(acct_id,ds.getId());
			if(!nciicCheckService.inCachedCount(name, enCardNo,incache_days) || ds_incache){
				logObj.setIncache("0");
				if("03".equals(flag)){
					logger.info("{} 查询不到数据", prefix);
					rets.clear();
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_POLICE_LOCAL_NOTEXISTS);
					rets.put(Conts.KEY_RET_MSG, "申请人本地未查得");
					rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
					return rets;
				}
				logger.info("{}国账通多项数据源采集开始......", prefix);
				
				if ("1".equals(DynamicConfigLoader.get("ds_guozt_request_method"))) {//1走最新client调用方式
					respXml = httpClientQuery(params,prefix,police_url);
				}else {
					respXml = singQurey(params,prefix,police_url);
				}
				//----------------------
				DataSourceLogEngineUtil.writeLog2LogSys(new LoggingEvent(trade_id, respXml, new String[] { trade_id }));
				if(respXml == null || respXml.length()==0){
					logObj.setState_msg("国账通查询失败：返回结果内容为空");
					logger.error("{} {}",logObj.getState_msg(),prefix);
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_POLICE_EXCEPTION);
					rets.put(Conts.KEY_RET_MSG, "数据源返回异常!");	
					rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
					return rets;
				}else{
					logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
					Nciic_Check_Result nciic_check = new Nciic_Check_Result();
					nciic_check.setTrade_id(trade_id);
					nciic_check.setCardno(enCardNo);
					nciic_check.setName(name);
					nciic_check.setSourceid(SOURCE_ID);
					nciic_check.setStatus(STATUS_CHECK_NULL);
					
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
						Node nameIdInfos = rspDoc.selectSingleNode("//data/policeCheckInfos");
						Node nameIdInfo = nameIdInfos.selectSingleNode("policeCheckInfo");
						Node inMessNode = nameIdInfo.selectSingleNode("message");
						String inStatus = inMessNode.selectSingleNode("status")!=null?inMessNode.selectSingleNode("status").getStringValue():"2";
						String inMess = inMessNode.selectSingleNode("value")!=null?inMessNode.selectSingleNode("value").getStringValue():"";
						logObj.setBiz_code2(inStatus);
						logObj.setState_msg(inMess);
						if("0".equals(inStatus)){
							String wybs = nameIdInfo.selectSingleNode("wybs")!=null?
									nameIdInfo.selectSingleNode("wybs").getStringValue() : "";
							String code = adaptCodeForV1_0(nameIdInfo.selectSingleNode("compStatus").getStringValue());
							String message = nameIdInfo.selectSingleNode("compResult")!=null?
									nameIdInfo.selectSingleNode("compResult").getStringValue() : "";
							logObj.setBiz_code3(code);
							//查询成功
							if("00".equals(code)){//一致							
								logObj.setBiz_code1(DXCODE_EQUAL);
								resource_tag = Conts.TAG_MATCH;
								String xb = nameIdInfo.selectSingleNode("sex2").getStringValue();//性別
								String mz = nameIdInfo.selectSingleNode("mz1").getStringValue();//民族
								String csrq = nameIdInfo.selectSingleNode("birthday1")!=null?nameIdInfo.selectSingleNode("birthday1").getStringValue():null;//出生日期
								String ssssxq = nameIdInfo.selectSingleNode("ssssqx1")!=null?nameIdInfo.selectSingleNode("ssssqx1").getStringValue():null;//所属省市县区
								String zz = nameIdInfo.selectSingleNode("address1").getStringValue();//住所
								image = nameIdInfo.selectSingleNode("checkPhoto").getStringValue();//相片
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
									logger.info("{}照片上传征信存储开始...", prefix);
									fileId  = fileEngines.store("ds_nciic_photo",FileArea.DS, FileType.JPG, image,trade_id);
									logger.info("{}照片上传征信存储结束!", prefix);	
									
									retdata.put("xp_content", image);
									retdata.put("xp_id", fileId);
									rets.clear();
									rets.put(Conts.KEY_RET_DATA, retdata);
									rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
									rets.put(Conts.KEY_RET_MSG, "采集成功!");
								}else{
									 retdata.put("xp_content", "");
									 retdata.put("xp_id", "");
									logger.warn("{}国政通数据源厂商返回申请人户籍照片不存在", prefix);
									if(flag.equals(CHANNEL_NO)){//判断是否走无照片输出通道
										rets.clear();
										rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_POLICE_PHOTO_NOTEXISTS);
										rets.put(Conts.KEY_RET_MSG, "公安数据源厂商返回申请人户籍照片不存在!");
									}else{	
										rets.clear();
										rets.put(Conts.KEY_RET_DATA, retdata);
										rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
										rets.put(Conts.KEY_RET_MSG, "采集成功!");
									}
									guozt_multcheck.setError_mesg(POLICE_PHOTO_NO);
									nciic_check.setError_mesg(POLICE_PHOTO_NO);
								}														
								guozt_multcheck.setStatus(STATUS_CHECK_EQUAL);
								guozt_multcheck.setCard_check(POLICE_STATUS_SUSSES);
								guozt_multcheck.setName_check(POLICE_STATUS_SUSSES);
								cardNo_check = POLICE_STATUS_SUSSES;
								name_check = POLICE_STATUS_SUSSES;
								nciic_check.setStatus(STATUS_CHECK_EQUAL);
							}else if("02".equals(code)){//不一致
								logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
								logObj.setBiz_code1(DXCODE_NOEQUAL);
								resource_tag = Conts.TAG_UNMATCH;
								cardNo_check = POLICE_STATUS_SUSSES;
								name_check = POLICE_STATUS_FAIL;
								guozt_multcheck.setCard_check(cardNo_check);
								guozt_multcheck.setName_check(name_check);
								guozt_multcheck.setStatus(STATUS_CHECK_NO);
								nciic_check.setStatus(STATUS_CHECK_NO);
								rets.clear();
								rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_POLICE_INVALID);
								rets.put(Conts.KEY_RET_MSG, "身份证号码，姓名校验不一致!");
							}else if("01".equals(code)){//库中无此号
								logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
								logObj.setBiz_code1(DXCODE_NOEXIST);
								resource_tag = Conts.TAG_UNFOUND;
								guozt_multcheck.setStatus(STATUS_CHECK_NULL);
								guozt_multcheck.setError_mesg(message);
								nciic_check.setStatus(STATUS_CHECK_NULL);
								nciic_check.setError_mesg(message);
								rets.clear();
								rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_POLICE_NOTEXISTS);
								rets.put(Conts.KEY_RET_MSG, message);						
							}else{
								logger.info("{}国政通返回其他信息!", prefix);
								logObj.setBiz_code1(DXCODE_NOCHECK);
								rets.clear();
								rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_PARAM_INVALID);
								rets.put(Conts.KEY_RET_MSG, "输入参数不正确!");	
								rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
								return rets;
							}
							nciic_check.setCard_check(cardNo_check);
							nciic_check.setName_check(name_check);
							nciic_check.setImage_file(fileId);
							guozt_multcheck.setWybs_number(wybs);
							guoZTMultCheckService.add(guozt_multcheck);
							nciicCheckService.add(nciic_check);
							rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
							return rets;
						}else{
							logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
							logger.error("{}国政通返回失败: {} ", new Object[] { prefix,inMess});
							rets.clear();
							rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_POLICE_EXCEPTION);
							rets.put(Conts.KEY_RET_MSG, "数据源处理时异常!");
							rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
							return rets;
						}
						
					}else {
						logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_FAIL);
//						logObj.setState_msg(messStatDesc.getStringValue());
						logger.error("{}国政通返回失败: {} ", new Object[] { prefix,messStatDesc.getStringValue()});
						rets.clear();
						rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_DS_POLICE_EXCEPTION);
						rets.put(Conts.KEY_RET_MSG, "数据源处理时异常!");
						rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
						return rets;
					}	
				}
			}else{
				logger.info("{}数据中存在此公安查询数据,开始简项渠道查询！", prefix);
				logObj.setIncache("1");
				logObj.setState_code(DataSourceLogEngineUtil.TRADE_STATE_SUCC);
				logObj.setBiz_code1(CODE_EQUAL);
				resource_tag = Conts.TAG_INCACHE_MATCH;
				Map<String,Object> getResultMap = nciicCheckService.inCached(name, enCardNo);
				String photoId = "";
				if(getResultMap.get("CARD_CHECK") != null){
					cardNo_check = getResultMap.get("CARD_CHECK").toString();
				}
				if(getResultMap.get("NAME_CHECK") != null){
					name_check  = getResultMap.get("NAME_CHECK").toString();
				}
				if(getResultMap.get("IMAGE_FILE") != null){
					photoId = getResultMap.get("IMAGE_FILE").toString();
				}
				if("不一致".equals(cardNo_check) || 
						"不一致".equals(name_check)){
					logObj.setBiz_code1(CODE_NOEQUAL);
					resource_tag = Conts.TAG_INCACHE_UNMATCH;
					rets.clear();
					rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_WARN_DS_POLICE_INVALID);
					rets.put(Conts.KEY_RET_MSG, "公安数据源厂商返回身份证号码，姓名校验不一致!");
					rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
					return rets;
				}				
				if(StringUtils.isNotEmpty(photoId)){
					logger.info("{} 从征信存储根据图片ID获取照片开始...", prefix);
					String photoImages = fileEngines.getBase64ById(photoId);// 根据ID从征信存储区下载照片
					image = photoImages;
					logger.info("{} 从征信存储根据图片ID获取照片成功,照片id为 : {}", prefix,photoId);					
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
			retdata.put("xp_content", image);
			retdata.put("xp_id", fileId);
			rets.put(Conts.KEY_RET_TAG, new String[]{resource_tag});
			rets.put(Conts.KEY_RET_DATA, retdata);
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_SUCCESS);
			rets.put(Conts.KEY_RET_MSG, "采集成功!");
		}catch(Exception ex){
			resource_tag = Conts.TAG_SYS_ERROR;
			rets.put(Conts.KEY_RET_STATUS, CRSStatusEnum.STATUS_FAILED_SYS_DS_EXCEPTION);
			rets.put(Conts.KEY_RET_MSG, "数据源处理时异常! 详细信息:"+ex.getMessage());
			logger.error(prefix+" 数据源处理时异常：{}",ExceptionUtil.getTrace(ex));
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
	private String adaptCodeForV1_0(String code) {
		if("3".equals(code))return "00";
		if("2".equals(code))return "02";
		if("1".equals(code))return "01";
		return "";
	}
}
